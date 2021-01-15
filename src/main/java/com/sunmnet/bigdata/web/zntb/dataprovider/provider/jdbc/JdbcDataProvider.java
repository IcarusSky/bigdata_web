package com.sunmnet.bigdata.web.zntb.dataprovider.provider.jdbc;

import com.sunmnet.bigdata.web.zntb.dataprovider.DataProvider;
import com.sunmnet.bigdata.web.zntb.dataprovider.Initializing;
import com.sunmnet.bigdata.web.zntb.dataprovider.aggregator.Aggregatable;
import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.DatasourceParameter;
import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.QueryParameter;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.AggConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.AggregateResult;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.ColumnMetaData;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.TableMetaData;
import com.sunmnet.bigdata.web.zntb.dataprovider.util.DPCommonUtils;
import com.sunmnet.bigdata.web.zntb.dataprovider.util.SqlHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.*;

public class JdbcDataProvider extends DataProvider implements Aggregatable, Initializing {

	private static final Logger LOG = LoggerFactory.getLogger(JdbcDataProvider.class);

	@Value("${dataprovider.resultLimit:200000}")
	private int resultLimit;

	@DatasourceParameter(label = "用户名",
			type = DatasourceParameter.Type.Input,
			placeholder = "请输入用户名",
			required = true,
			order = 97)
	private String USERNAME = "username";

	@DatasourceParameter(label = "密码",
			type = DatasourceParameter.Type.Password,
			placeholder = "请输入密码",
			required = true,
			order = 98)
	private String PASSWORD = "password";

	@QueryParameter(label = "查询语句",
			type = QueryParameter.Type.TextArea,
			required = true,
			order = 1)
	private String SQL = "sql";

	private SqlHelper sqlHelper;

	protected String getJDBCUrl(Map<String, String> dataSource) {
		return null;
	}

	protected String getDriver(Map<String, String> dataSource) {
		return null;
	}

	public List<String> getDatabases() {
		try (Connection connection = getConnection()) {
			List<String> databases = new ArrayList<>();

			DatabaseMetaData metaData = connection.getMetaData();
			ResultSet resultSet = metaData.getCatalogs();
			while (resultSet.next()) {
				databases.add(resultSet.getString("TABLE_CAT"));
			}
			return databases;
		} catch (Exception ex) {
			LOG.error("获取数据库信息出错", ex);
			return Collections.emptyList();
		}
	}

	public List<TableMetaData> getTables() {
		try (Connection connection = getConnection()) {
			List<TableMetaData> tables = new ArrayList<>();
			DatabaseMetaData metaData = connection.getMetaData();

			// 只查询用户表、视图，不包括系统表
			ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
			while (resultSet.next()) {
				TableMetaData table = new TableMetaData();
				table.setName(resultSet.getString("TABLE_NAME"));
				table.setType(resultSet.getString("TABLE_TYPE"));
				table.setDatabase(resultSet.getString("TABLE_CAT"));
				table.setRemarks(resultSet.getString("REMARKS"));
				tables.add(table);
			}
			return tables;
		} catch (Exception ex) {
			LOG.error("获取表信息出错", ex);
			return Collections.emptyList();
		}
	}

	public List<ColumnMetaData> getColumns() {
		try (Connection connection = getConnection()) {
			List<ColumnMetaData> columns = new ArrayList<>();
			DatabaseMetaData metaData = connection.getMetaData();

			// 只查询用户表、视图，不包括系统表
			ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE", "VIEW"});
			List<String> tables = new ArrayList<>();
			while (resultSet.next()) {
				tables.add(resultSet.getString("TABLE_NAME"));
			}

			resultSet = metaData.getColumns(null, null, null, null);
			while (resultSet.next()) {
				String table = resultSet.getString("TABLE_NAME");
				if (tables.contains(table)) {
					ColumnMetaData column = new ColumnMetaData();
					column.setName(resultSet.getString("COLUMN_NAME"));
					column.setType(JDBCType.valueOf(resultSet.getInt("DATA_TYPE")));
					column.setDatabase(resultSet.getString("TABLE_CAT"));
					column.setTable(table);
					column.setRemarks(resultSet.getString("REMARKS"));
					columns.add(column);
				}
			}
			return columns;
		} catch (Exception ex) {
			LOG.error("获取列信息出错", ex);
			return Collections.emptyList();
		}
	}

	@Override
	public boolean doAggregationInDataSource() {
		return true;
	}

	@Override
	public String[][] getData() throws Exception {
		return null;
	}

	@Override
	public void test() throws Exception {
		try (Connection con = getConnection()) {
		} catch (Exception e) {
			throw new Exception("ERROR:" + e.getMessage(), e);
		}
	}

	private String getAsSubQuery(String rawQueryText) {
		String deletedBlankLine = rawQueryText.replaceAll("(?m)^[\\s\t]*\r?\n", "").trim();
		return deletedBlankLine.endsWith(";") ? deletedBlankLine.substring(0, deletedBlankLine.length() - 1) : deletedBlankLine;
	}

	public Connection getConnection() throws Exception {
		String username = dataSource.get(USERNAME);
		String password = dataSource.get(PASSWORD);
		String driver = getDriver(dataSource);
		String jdbcurl = getJDBCUrl(dataSource);

		Class.forName(driver);
		Properties props = new Properties();
		props.setProperty("user", username);
		if (StringUtils.isNotBlank(password)) {
			props.setProperty("password", password);
		}
		return DriverManager.getConnection(jdbcurl, props);
	}

	@Override
	public String[] queryDimVals(String columnName, AggConfig config) throws Exception {
		String fsql = null;
		String exec = null;
		String sql = getAsSubQuery(query.get(SQL));
		List<String> filtered = new ArrayList<>();
		String whereStr = "";
		if (config != null) {
			whereStr = sqlHelper.assembleFilterSql(config);
		}
		fsql = "SELECT cb_view.%s FROM (\n%s\n) cb_view %s GROUP BY cb_view.%s";
		exec = String.format(fsql, columnName, sql, whereStr, columnName);
		LOG.info(exec);
		try (Connection connection = getConnection();
				Statement stat = connection.createStatement();
				ResultSet rs = stat.executeQuery(exec)) {
			while (rs.next()) {
				filtered.add(rs.getString(1));
			}
		} catch (Exception e) {
			LOG.error("ERROR:" + e.getMessage());
			throw new Exception("ERROR:" + e.getMessage(), e);
		}
		return filtered.toArray(new String[]{});
	}

	private ResultSetMetaData getMetaData(String subQuerySql, Statement stat) throws Exception {
		ResultSetMetaData metaData;
		try {
			stat.setMaxRows(100);
			String fsql = "\nSELECT * FROM (\n%s\n) cb_view WHERE 1=0";
			String sql = String.format(fsql, subQuerySql);
			LOG.info(sql);
			ResultSet rs = stat.executeQuery(sql);
			metaData = rs.getMetaData();
		} catch (Exception e) {
			LOG.error("ERROR:" + e.getMessage());
			throw new Exception("ERROR:" + e.getMessage(), e);
		}
		return metaData;
	}

	private Map<String, Integer> getColumnType() throws Exception {
		Map<String, Integer> result;
		try (
				Connection connection = getConnection();
				Statement stat = connection.createStatement()
				) {
			String subQuerySql = getAsSubQuery(query.get(SQL));
			ResultSetMetaData metaData = getMetaData(subQuerySql, stat);
			int columnCount = metaData.getColumnCount();
			result = new HashMap<>();
			for (int i = 0; i < columnCount; i++) {
				result.put(metaData.getColumnLabel(i + 1).toUpperCase(), metaData.getColumnType(i + 1));
			}
			return result;
		}
	}

	@Override
	public String[] getColumn() throws Exception {
		String subQuerySql = getAsSubQuery(query.get(SQL));
		try (
				Connection connection = getConnection();
				Statement stat = connection.createStatement()
				) {
			ResultSetMetaData metaData = getMetaData(subQuerySql, stat);
			int columnCount = metaData.getColumnCount();
			String[] row = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
				row[i] = metaData.getColumnLabel(i + 1);
			}
			return row;
		}
	}

	@Override
	public AggregateResult queryAggData(AggConfig config) throws Exception {
		String exec = sqlHelper.assembleAggDataSql(config);
		List<String[]> list = new LinkedList<>();
		LOG.info(exec);
		try (
				Connection connection = getConnection();
				Statement stat = connection.createStatement();
				ResultSet rs = stat.executeQuery(exec)
				) {
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (rs.next()) {
				String[] row = new String[columnCount];
				for (int j = 0; j < columnCount; j++) {
					row[j] = rs.getString(j + 1);
				}
				list.add(row);
			}
		} catch (Exception e) {
			LOG.error("ERROR:" + e.getMessage());
			throw new Exception("ERROR:" + e.getMessage(), e);
		}
		return DPCommonUtils.transform2AggResult(config, list);
	}

	@Override
	public String viewAggDataQuery(AggConfig config) throws Exception {
		return sqlHelper.assembleAggDataSql(config);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String subQuery = null;
		if (query != null) {
			subQuery = getAsSubQuery(query.get(SQL));
		}
		SqlHelper sqlHelper = new SqlHelper(subQuery, true);
		if (!isUsedForTest()) {
			Map<String, Integer> columnTypes = null;
			try {
				columnTypes = getColumnType();
			} catch (Exception e) {
				LOG.warn("getColumnType failed: {}", e.getMessage());
			}
			sqlHelper.getSqlSyntaxHelper().setColumnTypes(columnTypes);
		}
		this.sqlHelper = sqlHelper;
	}
}
