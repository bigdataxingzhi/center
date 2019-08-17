package com.core.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.entity.User;
import com.core.exception.UserException;
import com.core.repository.UserRepository;

/**
 * 
 * @author 星志
 *
 */
@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Transactional
	public void registerUser(User user){
		user.setCreateTime(new Date());
		user = userRepository.save(user);
		if(user.getUserId()==null){
			throw new UserException("插入失败");
		}
	}

}
