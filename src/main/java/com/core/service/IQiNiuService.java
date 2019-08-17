package com.core.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * 七牛云服务
 * @author 星志
 *
 */

public interface IQiNiuService {
    //以文件的形式上传
    Response uploadFile(File file) throws QiniuException;

    //以流的形式上传
    Response uploadFile(InputStream inputStream) throws QiniuException;

    //删除文件
    Response delete(String key) throws QiniuException;
}
