package com.core.controller;

import com.core.base.ApiResponse;
import com.core.base.QiNiuPutRet;
import com.core.base.TokenCache;
import com.core.service.IQiNiuService;
import com.core.service.UserSevice;
import com.core.util.JsonUtils;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import ch.qos.logback.classic.Logger;

import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author 星志
 *
 */
@Controller
public class UploadController {

	private org.slf4j.Logger logger = LoggerFactory.getLogger(UploadController.class);
    @Autowired
    IQiNiuService qiNiuService;

    @Autowired
    UserSevice userSevice;

    @Autowired
    Gson gson;

    @Value("${qiniu.cdn.prefix}")
    private String qiniu_cdn_prefix;

    @Autowired
    ModelMapper modelMapper;

    /**
     * 上传图片接口
     * @param file
     * @return
     */
    @PostMapping(value = "/upload/photo",  produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
    @ResponseBody
    public Object uploadPhoto(@RequestParam("uploadFile") MultipartFile file) {
        //判断上传的文件是否为空
        if (file.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        //得到上传图片的源信息
        String fileName = file.getOriginalFilename();

        try {
            //将file转为inputStream
            InputStream inputStream = file.getInputStream();
            //使用qiniu接口进行上传
            Response response = qiNiuService.uploadFile(inputStream);
            //验证是否上传成功
            if (response.isOK()) {
                //将qiniu返回的数据格式包装为QiNiuPutRet对象
              QiNiuPutRet ret = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
               //成功返回
              logger.info("图片的key为:{}",ret.getKey());
            //  long currentTimeMillis = System.currentTimeMillis();
              //TokenCache.setKey("profile"+currentTimeMillis, ret.getKey());
                //补充为完整的url
                String url = qiniu_cdn_prefix ; //url;
                url+=ret.getKey();
                //封装到map中返回,kindEditor要求返回的数据格式.
                Map result = new HashMap<>();
                result.put("error", 0);
                result.put("url", url);
                result.put("key",ret.getKey());
                return JsonUtils.objectToJson(result);
            } else {
                //失败返回
                Map result = new HashMap<>();
                result.put("error", 1);
                result.put("message", "图片上传失败");
                return JsonUtils.objectToJson(result);
            }

        } catch (QiniuException e) {
            Response response = e.response;
            try {
                return ApiResponse.ofMessage(response.statusCode, response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
                return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }


   
}
