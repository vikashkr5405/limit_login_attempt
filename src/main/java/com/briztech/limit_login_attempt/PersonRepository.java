package com.briztech.limit_login_attempt;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PersonRepository extends JpaRepository<Person,Integer>
{

	public Person findByEmail(String email); 
	
	public Person findByVerificationCode(String code);
	
	@Modifying
	@Query("update person p set p.failedAttempt=?1 where p.email=?2")
	
	public void updateFailedAttempt(int attempt,String email);
}
