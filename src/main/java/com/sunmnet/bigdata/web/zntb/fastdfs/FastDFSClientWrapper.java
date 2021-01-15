package com.sunmnet.bigdata.web.zntb.fastdfs;


import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * @Description FastDFS客户端包装类
 * @author wm
 * @create 2019-01-15
 */
@Component
@Configuration
@Import(com.github.tobato.fastdfs.FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class FastDFSClientWrapper {
    private final Logger logger = LoggerFactory.getLogger(FastDFSClientWrapper.class);
    
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    

    /**
     * 文件上传
     * @param bytes     文件字节
     * @param fileSize  文件大小
     * @param extension 文件扩展名
     * @author wm
     * @create 2019-01-15
     * @return fastDfs路径
     */
    public String uploadFile(byte[] bytes, long fileSize, String extension) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        StorePath storePath = fastFileStorageClient.uploadFile(byteArrayInputStream, fileSize, extension, null);
        logger.info("文件存储路径："+storePath.getFullPath());
        String visitPath = FileConstants.FASTDFS_AGENT_URL+storePath.getFullPath();
        logger.info("文件访问路径："+visitPath);
        return visitPath;
    }

    /**
     * 下载文件
     * @param fileUrl 文件URL
     * @author wm
     * @create 2019-01-15
     * @return 文件字节
     * @throws IOException
     */
    public ResponseEntity downloadFile(String fileUrl,String fileName) throws IOException {
        //http://192.169.1.76:9270/group1/M00/00/00/wKkBTF6ZV0yARG46AAAF1Qtyuk429..txt
        String group = "group1";
        String[] groupPath = fileUrl.split(group);
        String path = groupPath[1].substring(1,groupPath[1].length());
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);
    
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentDispositionFormData("attachment",  new String(fileName.getBytes("UTF-8"),"iso-8859-1"));
    
        headers.add("Content-disposition","attachment;filename="+new String(fileName.getBytes("utf-8"),"ISO-8859-1"));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
    }
    
    
}