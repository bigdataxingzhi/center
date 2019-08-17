package com.core.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author 星志
 *
 */
@Service
public class SmsServiceImpl implements ISmsService, InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
