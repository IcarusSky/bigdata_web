package com.sunmnet.bigdata.web.zntb.dataprovider.provider.jdbc;

import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.DatasourceParameter;
import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.ProviderName;

import java.util.Map;

@ProviderName(
        name = "Mysql",
        parent = "自建数据源",
        order = 1
)
public class MysqlDataProvider extends JdbcDataProvider {

    private String JDBC_URL = "jdbc:mysql://IP:PORT/DATABASES?useUnicode=true&characterEncoding=utf8&connectTimeout=" + DATASOURCE_CONNECT_TIMEOUT_MILLIS;

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
            value = "3306",
            order = 2)
    private String PORT = "port";

    @DatasourceParameter(label = "数据库",
            type = DatasourceParameter.Type.Input,
            placeholder = "请输入数据库名称",
            required = true,
            order = 3)
    private String DATABASES = "databases";

    protected String getJDBCUrl(Map<String, String> dataSource) {
        String ip = dataSource.get(IP);
        String port = dataSource.get(PORT);
        String databases = dataSource.get(DATABASES);
        return JDBC_URL.replaceAll("IP", ip).
                replaceAll("PORT", port).
                replaceAll("DATABASES", databases);
    }

    protected String getDriver(Map<String, String> dataSource) {
        return "com.mysql.jdbc.Driver";
    }

}
