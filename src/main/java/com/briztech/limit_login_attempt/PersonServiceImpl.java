package com.briztech.limit_login_attempt;

import java.util.Date;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PersonServiceImpl implements PersonService
{
	@Autowired
	PersonRepository prepo;                                                          

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public Person saveUsers(Person person,String url) 
	{
		String password=passwordEncoder.encode(person.getPassword());
		person.setPassword(password);
		person.setRole("ROLE_ADMIN");
	
		person.setEnable(false);
		person.setVerificationCode(UUID.randomUUID().toString());
		
		person.setAccountNonLocked(true);
		person.setFailedAttempt(0);
		person.setLockTime(null);
		
		Person p= prepo.save(person);
		
		if(p!=null)
		{
			 sendEmail(p,url);
		}

		return p;
	}

	@Override
	public void removeSessionMessage()
	{
	  HttpSession session =	((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
		
	  session.removeAttribute("msg");
	}

	@Override
	public void sendEmail(Person person, String url)
	{
		String from="vikashkr5405@gmail.com";
		String to=person.getEmail();
		String subject="Account verification";
		String content="Dear [[name]],<br>" + "please click the link below to verify your registration:<br>"
		+ "<h3><a href=\"[[url]]\" target=\"_self\">VERIFY</a></h3>" + "Thank you,<br>" + "vikash";
		
		try {
			
			MimeMessage message=mailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(message);
			
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			
			content=content.replace("[[name]]", person.getName());
			
			String siteUrl=url + "/verify?code=" +person.getVerificationCode();
			
			content=content.replace("[[url]]",siteUrl );
			
			helper.setText(content, true);
			
			mailSender.send(message);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean verifyAccount(String verificationCode) 
	{
	
		Person person=prepo.findByVerificationCode(verificationCode);
		
		if(person==null)
		{
			return false;
		}
		else 
		{
			person.setEnable(true);
			person.setVerificationCode(null);
			
			prepo.save(person);
			
			return true;
		}
	}

	@Override
	public void increaseFailedAttempt(Person person) {
	
		int attempt=person.getFailedAttempt()+1;
		
		prepo.updateFailedAttempt(attempt, person.getEmail());
		
	}

	//private static final long lock_duration_time=24*60*60*1000;    //24 hour lock
	
	private static final long lock_duration_time=30000;     //30 seconds account lock
	
	public static final long ATTEMPT_TIME=3;
	
	@Override
	public void resetAttempt(String email) {

		prepo.updateFailedAttempt(0, email);
		
	}

	@Override
	public void lock(Person person) {
	
		person.setAccountNonLocked(false);
		person.setLockTime(new Date());
		prepo.save(person);
		
	}

	@Override
	public boolean unlockAccountTimeExpired(Person person)
	{
		long lockTimeInMillis= person.getLockTime().getTime();
		long currentTimeInMillis=System.currentTimeMillis();
		
		if(lockTimeInMillis+lock_duration_time<currentTimeInMillis)
		{
			person.setAccountNonLocked(true);
			person.setLockTime(null);
			person.setFailedAttempt(0);
			prepo.save(person);
			return true;
		}
		
		
		return false;
		
		
	}
	
	
}

