package com.sunmnet.bigdata.web.security.util;

import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.util.ValidateUtils;
import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


/**
 * <p>
 * Http工具类
 * </p>
 *
 * @author sqj
 * @version 1.0
 * @since 1.0
 */
public class HttpUtils {
    private static Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

    private static final RequestConfig REQUEST_CONFIG =
        RequestConfig.custom().setSocketTimeout(8000).setConnectTimeout(5000).setConnectionRequestTimeout(200).build();


    /**
     * 发送POST请求
     */
    public static String post(String url) {
        LOG.info(String.format("Http Post start, URL[%s].", url));
        try {
            if (!ValidateUtils.isURL(url)) {
                return null;
            }

            String response = doPost(url, null);

            LOG.info(String.format("Http Post finish, URL[%s], Response[%s].", url, response));
            return response;
        } catch (Exception ex) {
            throw new ServiceException("登录超时", ex);
        }
    }



    private static String doPost(final String url, HttpEntity httpEntity) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(REQUEST_CONFIG);
        if (httpEntity != null) {
            httpPost.setEntity(httpEntity);
        }

        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            public String handleResponse(HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                String content = "";
                if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                    HttpEntity entity = response.getEntity();
                    BufferedReader reader =
                        new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8.name()));
                    String tmp;
                    while ((tmp = reader.readLine()) != null) {
                        content += tmp + "\r\n";
                    }
                }
                return content;
            }
        };

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            return httpClient.execute(httpPost, responseHandler);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
