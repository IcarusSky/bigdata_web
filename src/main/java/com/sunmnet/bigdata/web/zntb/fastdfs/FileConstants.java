package com.sunmnet.bigdata.web.zntb.fastdfs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description file模块常量
 * @author wm
 * @create 2019-01-15 
 */
@Component
public class FileConstants {

	/**
	 * fastDFSAgentUrl
	 */
	public static String FASTDFS_AGENT_URL;
	
	
	@Value("${fastdfs.agent.url}")
    public void setFastDFSAgentUrl( String fastDFSAgentUrl) {
		FileConstants.FASTDFS_AGENT_URL = fastDFSAgentUrl;
    }
}
