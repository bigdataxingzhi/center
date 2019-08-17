package com.core.es.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * es 构造索引信息类
 * @author 星志
 *
 */
@Component
public class IndexMessage {

    public static final String INDEX = "index";  //创建索引
    public static final String REMOVE = "remove";  //删除索引

	public static final int MAX_RETRY = 3;
    /**防止异常情况下的构建索引失败,可重试*/
    private int retry = 0;

    /**
     * 默认构造器 防止jackson序列化失败
     */
    public IndexMessage() {
    }

    public IndexMessage( int retry) {
        
        this.retry = retry;
    }


    
    
}
