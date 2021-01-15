package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.fastdfs.FastDFSClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController extends BaseController {
    
    private final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Autowired
    private FastDFSClientWrapper fastDFSClientWrapper;
    
    
    //上传文件接口 返回文件名和文件远程url
    @RequestMapping(value = "/upload")
    public Object upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            String fileType = fileName.substring(fileName.lastIndexOf("."),fileName.length());
            String fileUrl = fastDFSClientWrapper.uploadFile(bytes, bytes.length,fileType);
            logger.info("文件地址 " + fileUrl);
            
            Map<String,String> fileMap = new HashMap<>();
            fileMap.put("fileName",fileName);
            fileMap.put("fileUrl",fileUrl);
            return buildSuccJson(fileMap);
        } catch (IOException e) {
            e.printStackTrace();
            return buildErrJson("文件上传失败！");
        }
    }
    
    @RequestMapping(value = "/download")
    public Object download(@RequestParam("fileUrl") String fileUrl,@RequestParam("fileName") String fileName) {
        try {
            return fastDFSClientWrapper.downloadFile(fileUrl,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return buildErrJson("文件下载失败！");
        }
    }
    
}