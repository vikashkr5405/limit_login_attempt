package com.briztech.limit_login_attempt;



public interface PersonService 
{

	Person saveUsers(Person person,String url);
	
	public void removeSessionMessage();

	public void sendEmail(Person person,String path);
	
	public boolean verifyAccount(String verificationCode);
	
	public void increaseFailedAttempt(Person person);
	
	public void resetAttempt(String email);
	
	public void lock(Person person);
	
	public boolean unlockAccountTimeExpired(Person person);
}
