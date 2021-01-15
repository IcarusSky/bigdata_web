package com.sunmnet.bigdata.web.zntb.dataprovider.provider.jdbc;

import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.DatasourceParameter;
import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.ProviderName;

import java.util.Map;

@ProviderName(name = "Oracle", parent = "自建数据源", order = 1)
public class OracleDataProvider extends JdbcDataProvider {

    private String SID_URL = "jdbc:oracle:thin:@IP:PORT:SID";
    private String SERVER_NAME_URL = "jdbc:oracle:thin:@//IP:PORT/SID";

    @DatasourceParameter(label = "数据库地址",
            type = DatasourceParameter.Type.Input,
            placeholder = "IP或主机名",
            required = true,
            order = 1)
    private String IP = "ip";

    @DatasourceParameter(label = "端口",
            type = DatasourceParameter.Type.Input,
            placeholder = "请输入端口",
            required = true,
            value = "1521",
            order = 2)
    private String PORT = "port";

    @DatasourceParameter(label = "服务名或SID",
            type = DatasourceParameter.Type.Input,
            placeholder = "请输入服务名或SID",
            required = true,
            order = 3)
    private String SID = "sid";

    @DatasourceParameter(label = "",
            type = DatasourceParameter.Type.Radio,
            options = {"sid", "服务名"},
            value = "sid",
            required = true,
            order = 4)
    private String SIDTYPE = "sid_type";

    protected String getJDBCUrl(Map<String, String> dataSource) {
        String ip = dataSource.get(IP);
        String port = dataSource.get(PORT);
        String sid = dataSource.get(SID);
        String sidType = dataSource.get(SIDTYPE);
        String url = sidType.equals("sid") ? SID_URL : SERVER_NAME_URL;
        return url.replaceAll("IP", ip).
                replaceAll("PORT", port).
                replaceAll("SID", sid);
    }

    protected String getDriver(Map<String, String> dataSource) {
        return "oracle.jdbc.driver.OracleDriver";
    }
}
