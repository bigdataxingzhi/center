package com.core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 
 * @author 星志
 *
 */
@SpringBootApplication
@EnableScheduling
@EnableRedisHttpSession  
@EnableAspectJAutoProxy
public class CenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CenterApplication.class, args);	
		
		
	}


}