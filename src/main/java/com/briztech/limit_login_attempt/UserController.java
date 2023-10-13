package com.briztech.limit_login_attempt;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	PersonRepository erepo;
	
	@ModelAttribute
	public void commonUser(Principal p,Model m)
	{
		if(p!=null)
		{
			String email=p.getName();
			Person ps=erepo.findByEmail(email);
		    m.addAttribute("person",ps);
		}
	}
	
	@GetMapping("/profile")
	public String profile(Principal p)
	{
		String email=p.getName();
		Person ps=erepo.findByEmail(email);
		return "profile";
	}
}

