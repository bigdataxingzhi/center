package com.core.controller;

import com.google.common.collect.Lists;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class BaseController {

	
//    @RequestMapping(value = "/{page}")
//    public String returnPage(@PathVariable String page,Model model){
//    	
//    	
//        if(page.equals("userLogin")){
//            return "login";
//        }
//        if(page.equals("login")){
//
//            return "login";
//        }
//        return page;
//
//    }
	
  @RequestMapping(value = "/")
  public String returnMainPage(){
  	
      return "main";

  }
}
