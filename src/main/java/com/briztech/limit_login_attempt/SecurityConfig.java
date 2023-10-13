package com.briztech.limit_login_attempt;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class SecurityConfig
{
	@Autowired
	 private CustomAuthSuccessHandler cusuccessHandler;

	@Autowired
	public CustomFailureHandler failureHandler;

	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService getDetailsService()
	{
		return new  CustomUserDetailsService();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider daos =new DaoAuthenticationProvider();
		daos.setUserDetailsService(getDetailsService());
		daos.setPasswordEncoder(passwordEncoder());
		
		return daos;
	}
	 
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{
	/*	http.csrf().disable().authorizeHttpRequests().requestMatchers("/*").permitAll()
		.requestMatchers("/user/**").authenticated().and().formLogin()
		.loginPage("/signin").loginProcessingUrl("/userLogin")
		//.usernameParameter("email")
		.defaultSuccessUrl("/profile").permitAll(); */
		
		
    	http.csrf().disable().authorizeHttpRequests().requestMatchers("/user/**").hasRole("USER")
		.requestMatchers("/admin/**").hasRole("ADMIN")
		.requestMatchers("/*").permitAll().and()
		.formLogin().loginPage("/signin").loginProcessingUrl("/userLogin")
		.failureHandler(failureHandler)
		.successHandler(cusuccessHandler)
		.and().logout().permitAll();  
		   
		return http.build();
		
	}
}
