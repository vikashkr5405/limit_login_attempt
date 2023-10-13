package com.briztech.limit_login_attempt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	PersonRepository prepo;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
	{
		
	Person p=prepo.findByEmail(email);
		
	if(p==null)
	{
		throw new  UsernameNotFoundException("username not found");
	}
	else
	{
		return new CustomUser(p);
	}

	}

}
