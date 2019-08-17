package com.core.entity;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class User implements UserDetails{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long userId;
	
	@Column(name="user_num",unique=true)
	private String userNum;
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="user_tel")
	private String usertel;
	
	@Column(name="user_password")
	private String password;
	
	@Column(name="user_profile")
	private String profile;
	
	@Column(name="user_ceateTime")
	private Date createTime;

	public Long getUserId() {
		return userId;
	}
	
	

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	

	public String getProfile() {
		return profile;
	}



	public void setProfile(String profile) {
		this.profile = profile;
	}



	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUserNum() {
		return userNum;
	}

	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}

	public String getUsertel() {
		return usertel;
	}

	public void setUsertel(String usertel) {
		this.usertel = usertel;
	}
	
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }



	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
	}



	@Override
	public String getUsername() {
		
		return this.userName;
	}
	
	

}
