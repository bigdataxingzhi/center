package com.core.controller;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.core.entity.User;
import com.core.exception.UserException;
import com.core.service.UserService;
@Controller
@RequestMapping(value="user")
public class UserController {

@Autowired
UserService userService;

@Autowired
PasswordEncoder passwordEncoder;

@RequestMapping(value="register",method=RequestMethod.POST)
public String addUser(User user,String token){
	
	
	user.setProfile(token);
	try {
		String password = user.getPassword();
		String[] split = StringUtils.split(password, ",");
		 password = passwordEncoder.encode(split[0]);
		  user.setPassword(password);
		  
		userService.registerUser(user);
		return "login";
	} catch (UserException e) {
	}
	return "register";
	
	
};

}
