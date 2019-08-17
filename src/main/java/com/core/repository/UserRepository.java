package com.core.repository;

import com.core.entity.User;

public interface UserRepository extends BaseRepository<User>{

	User findUserByUserNumOrUserName(String userNum, String userName);

}
