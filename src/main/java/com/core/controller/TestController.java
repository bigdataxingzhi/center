package com.core.controller;

import com.core.es.util.IndexTemplate;
import com.core.service.ElasticSearchOptionImpl;
import com.google.common.collect.Lists;

import ch.qos.logback.classic.Logger;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Controller
public class TestController {

@Autowired
ElasticSearchOptionImpl esclient;
	@Autowired
JestClient esOption;
	private static final Logger logger = (Logger) LoggerFactory.getLogger(TestController.class);
    @RequestMapping(value = "/{page}")
    public String returnPage(@PathVariable String page,Model model){
    /*	String json="{\n" + 
    			"  \"explain\": true,\n" + 
    			"  \"size\": 20,\n" + 
    			"  \"_source\": \"item\",\n" + 
    			"  \"aggs\": {\n" + 
    			"    \"itemagg\": {\n" + 
    			"      \"terms\": {\n" + 
    			"        \"field\": \"item\",\n" + 
    			"        \"size\": 1\n" + 
    			"      }\n" + 
    			"    }\n" + 
    			"  }\n" + 
    			"}";
    	
    	Search search = new Search.Builder(json).addIndex("javaee").addType("").build();
    	try {
			SearchResult searchResult = esOption.execute(search);
			logger.info(searchResult.getJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    
/*   	 IndexTemplate indexTemplate  = new IndexTemplate();
    	   indexTemplate.setAuthor("舒强");
    	   indexTemplate.setCount(1000);
    	   indexTemplate.setItem("spring");
    	   indexTemplate.setPublishdate("2018-02-06");
    	   indexTemplate.setSubtitle("aop详解");
    	   indexTemplate.setTitle("spring高级篇");
    	   indexTemplate.setSearchId(9L);
    	   indexTemplate.setInfo("<p>" + 
    	   		"面向切面编程（AOP）通过提供另外一种思考程序结构的途经来弥补面向对象编程（OOP）的不足。在OOP中模块化的关键单元是类（classes），</p>" + 
    	   		"<p>" + 
    	   		"而在AOP中模块化的单元则是切面。切面能对关注点进行模块化，例如横切多个类型和对象的事务管理。（在AOP术语中通常称作横切（crosscutting）关注点。" + 
    	   		"</p>" + 
    	   		"<p>" + 
    	   		"<br />" + 
    	   		"</p>");
    	   indexTemplate.setLove(450);
    	   esclient.create(indexTemplate);
    	*/
    	
   
    //	esclient.suggest("spring a");
    	//esclient.getTotalHits("springboot","aop详解");
    //	esclient.queryData("spring","aop详解");

    	
//    	esclient.mapAggregate("item");
    	
        if(page.equals("userLogin")){
            return "login";
        }
        if(page.equals("login")){

            return "login";
        }
        return page;

    }
    
    
    @RequestMapping(value = "/mian")
    public String mainPage(String page,Model model){
    	
    

    	
    	
    	
   //	esclient.suggest("sri");


    	
//    	esclient.mapAggregate("item");
    	
        return page;

    }
    
    public static void main(String[] args) {
    	String[] split = StringUtils.split("Spring    Aop"," ");
    	System.out.println(split[1]);
	}
}
