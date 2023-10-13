package com.briztech.limit_login_attempt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler
{
	@Autowired
	PersonService pserv;
	
	@Autowired
	PersonRepository prepo;
	

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String email=request.getParameter("username");
		Person person=prepo.findByEmail(email);
		
		if(person!=null)
		{
			if(person.isEnable())
			{
				if(person.isAccountNonLocked())
				{
					if(person.getFailedAttempt()<PersonServiceImpl.ATTEMPT_TIME-1)
					{
						pserv.increaseFailedAttempt(person);
					} 
					else
					{
						pserv.lock(person);
						exception =new LockedException("Your Account is locked!!  failed attempt 3 ");
					}
				}else if(!person.isAccountNonLocked())
				{
					if(pserv.unlockAccountTimeExpired(person))
					{
						exception= new LockedException("Account is unlocked! please try to Login");
					}
					else
					{
						exception=new LockedException("Account is locked! Please try after sometime");
					}
				}
			}
			else
			{
				exception =new LockedException("Your Account is inactive.. please verify your account!!");
			}
		}
		
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

	
}
