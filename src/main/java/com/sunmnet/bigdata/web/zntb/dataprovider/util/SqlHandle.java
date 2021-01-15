package com.sunmnet.bigdata.web.zntb.dataprovider.util;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * mysql语法 sql语句拼接工具简类
 * 使用场景：需要快速书写sql语句时用到，主要使用的是mysql中的sql语法
 * 该版本的select语句只支持单表查询，不支持多表查询，待更新
 * where 语句中关联多个字段默认使用And关键字,暂不可更改，待更新
 * @author 小川
 * @date 2015-12-7
 * @version 0.5
 *
 */
public class SqlHandle {
	/**
	 * 排序操作符：顺序 "ASC","DESC"
	 */
	public static final String ASC = "ASC";
	public static final String DESC = "DESC";
	/**
	 * 数据库操作符：顺序 "select","insert","update","delete"
	 */
	public static final String SELECT="select";
	public static final String INSERT="insert";
	public static final String UPDATE="update";
	public static final String DELETE="delete";
	public static final String CREATE="create";
	/**
	 * sql语句的String类型
	 */
	private StringBuffer sql=new StringBuffer();;
	/**
	 * 要操作的字段,用在update insert中的Set之后,字段名称和值进行对应
	 */
	private Map<String, Object> operateFields =new HashMap<String, Object>();
	
	/**
	 * 要操作的值 ,同于insert语句中的values,不需要跟字段名称,只需要值就OK了
	 */
	private ArrayList<Object> operateValues =new ArrayList<Object>();
	/**
	 * select语句中用于select 后面的字段名称
	 */
	private ArrayList<String> fields = new ArrayList<String>();
	
	/**
	 * 条件集合,用于where语句后面 形式例如 field1=value1
	 */
	private ArrayList<String> conditions =new ArrayList<String>();

	/**
	 * 建表语句
	 */
	private List<SqlColumn> columnList = new ArrayList<>();

	/**
	 * 操作符,使用select update delete insert者四种操作符
	 */
	private String operate="";
	/**
	 * 操作表 ,要操作的表
	 */
	private String table="";
	/**
	 * 限制 要限制的长度
	 */
	private String limit="";
	/**
	 * 排序规则,定制排序规则
	 */
	private String order="";
	/**
	 *
	 */
	private String primaryKey = "";
	/**
	 *
	 */
	private String comment = "";
	
	public SqlHandle(String operate,String table){
		this.operate=operate;
		this.table=table;
	}
	
	/**
	 * 默认构造方法使用select语句操作
	 */
	public SqlHandle(){
		this.operate= SELECT;
	}
	/**
	 * 初始化构造数据库操作,默认使用select查询
	 * @param
	 */
	public SqlHandle(String table){
		this.table=table;
		this.operate= SELECT;
	}
	
	public SqlHandle OPERATE(String operate){
		this.operate=operate;
		return this;
	}

	public SqlHandle TABLE(String table) {
		this.table = table;
		return this;
	}



	public SqlHandle COMMENT(String comment) {
		this.comment = comment;
		return this;
	}


	public SqlHandle PRIMARYKEY(String primaryKey) {
		this.primaryKey = primaryKey;
		return this;
	}

	public SqlHandle COLUMNLIST(List<SqlColumn> columnList) {
		this.columnList.addAll(columnList);
		return this;
	}

	public SqlHandle COLUMN(SqlColumn column) {
		this.columnList.add(column);
		return this;
	}

	/**
	 * 主要应用update和insert的数据库操作添加sql
	 * @param properName
	 * @param properValue
	 * @return
	 */
	public SqlHandle OPERATEFIELD(String properName, Object properValue){
		operateFields.put(properName, properValue);
		return this;
	}
	
	public SqlHandle OPERATEFIELD(Map<String, Object> fields){
		operateFields.putAll(fields);
		return this;
	}
	
	public SqlHandle OPERATEFIELD(Object properValue){
		operateValues.add(properValue);
		return this;
	}
	
	/**
	 * 主要应用于select语句中，用于添加抬头字段
	 * @param
	 * @return
	 */
	public SqlHandle SELECTFIELD(String field){
		this.fields.add(field);
		return this;
	}

	/**
	 * 主要应用于select语句中，用于添加抬头字段
	 *
	 * @param
	 * @return
	 */
	public SqlHandle SELECTFIELD(List<String> fields) {
		this.fields.addAll(fields);
		return this;
	}

	/**
	 * 添加字段并可以给字段加别名
	 * @param field
	 * @param alias
	 * @return
	 */
	public SqlHandle SELECTFIELD(String field,String alias){
		this.fields.add(field+" as "+alias);
		return this;
	}

	
	public SqlHandle CONDITION(String field,String operator,Object value){
		String conditionStr=field+" "+operator+" ";
		//如果是String类型，需要加上单引号。
		conditionStr+=filterValue(value);
		this.conditions.add(conditionStr);
		return this;
	}
	
	/**
	 * 一种条件场景 where id in (1,2,3) or where id not in (1,2,3)等
	 * @param field
	 * @param values
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public SqlHandle CONDITION(String field, List values){
		String conditionStr=field+" in (";
		if(values.size()>0){
			for(int i = 0; i< values.size()-1; i++){
				conditionStr+=filterValue(values.get(i))+",";
			}
			conditionStr+=filterValue(values.get(values.size() - 1));
		}
		conditionStr += ")";
		this.conditions.add(conditionStr);
		return this;
	}
	/**
	 * 过滤值方法，如果是String类型添加单引号，不是直接返回原值
	 * @param value
	 * @return
	 */
	private String filterValue(Object value){
		if(value instanceof String){
			return "'"+value+"'";
		}
		else if(value instanceof Date){
			return "'"+value+"'";
		}
		else{
			return value.toString();
		}
	}
	
	public SqlHandle ORDERBY(String order,String field){
		order=" "+field+" "+order;
		return this;
	}
	
	public SqlHandle LIMIT(int start,int length){
		limit="  "+start+","+length;
		return this;
	}
	/**
	 * 重写toString方法,返回sql结果
	 */
	public String toString(){
		sql.setLength(0);
		switch (operate) {
		case "select":
			return selectToSql();
		case "insert":
			return insertToSql();
		case "update":
			return updateToSql();
		case "delete":
			return deleteToSql();
		case "create":
			return createToSql();
		default:
		return null;
		}
	}

	private String createToSql() {
		sql = sql.append(CREATE).append(" table ").append(table).append(" (");
		for (SqlColumn sqlColumn : columnList) {
			sql.append(sqlColumnToString(sqlColumn));
		}
		sql.append("PRIMARY KEY (`").append(primaryKey).append("`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 ");
		if (StringUtils.isNotEmpty(comment)) {
			sql.append(" COMMENT '").append(comment).append("'");
		}
		return sql.toString();
	}


	/**
	 * select语句转sql方法
	 *
	 * @return 返回转好的字符串
	 */
	private String selectToSql() {
		sql = sql.append(SELECT);
		//字段处理，没有字段默认使用*
		addFiled();
		//表处理，暂时单表
		sql = sql.append(" from ").append(table);
		//where条件处理,有处理，无不处理,默认使用And连接符。。
		addCondition();
		//排序语句
		addOrder();
		//限制条目
		addLimit();
		return sql.toString();
	}

	/**
	 * insert语句转sql放阿飞
	 *
	 * @return 返回转好的字符串
	 */
	private String insertToSql() {
		sql = sql.append(INSERT).append(" ").append(table);
		//如果opratefileds和operatevalues只能取一个
		//operatefields注入sql语句的键值映射，operatevalues只注入值，不注入键
		if (!operateFields.isEmpty() && operateValues.isEmpty()) {
			addKeyValue();
		} else if (!operateValues.isEmpty() && operateFields.isEmpty()) {
			sql = sql.append(" values(");
			for (Object value : operateValues) {
				sql = sql.append(" ").append(filterValue(value)).append(",");
			}
			//删除最后一个逗号并添加括号括上
			sql = sql.deleteCharAt(sql.length() - 1).append(")");
		}
		return sql.toString();
	}

	private String updateToSql() {
		sql = sql.append(UPDATE).append(" ").append(table);
		//operatefields注入sql语句的键值映射
		addKeyValue();
		//写入where语句
		addCondition();
		//排序语句
		addOrder();
		//限制条目
		addLimit();
		return sql.toString();
	}

	private String deleteToSql() {
		sql = sql.append(DELETE).append(" from ").append(table);
		//写入where语句
		addCondition();
		//排序语句
		addOrder();
		//限制条目
		addLimit();
		return sql.toString();
	}

	/**
	 * 添加字段
	 * 例子：filed1,field2
	 */
	private void addFiled(){
		if(fields.size()>0){
			//批量加字段
			for (String filed : this.fields) {
				//最后一个字段不加逗号
				if(!filed.equals(fields.get(fields.size()-1))){
					sql = sql.append(" ").append(filed).append(",");
				}
				else{
					sql = sql.append(" ").append(filed);
				}
			}
		}else{
			sql=sql.append(" *");
		}
	}
	
	/**
	 * 添加条件
	 * 例子：where condition1<condition2 and condition3>condition4
	 */
	private void addCondition(){
		if(conditions.size()>0){
			sql=sql.append(" where");
			for (String condition : conditions) {
				if(!condition.equals(conditions.get(conditions.size()-1))){
					sql.append(" ").append(condition).append(" and ");
				}else{
					sql.append(" ").append(condition);
				}
			}
		}
	}
	
	/**
	 * 添加排序
	 * 例子 order by field DESC
	 */
	private void addOrder(){
		if(!order.isEmpty()){
			sql.append(" order by ").append(order);
		}
	}
	
	/**
	 * 添加限制
	 * 例子 limit 0,10
	 */
	private void addLimit(){
		if(!limit.isEmpty()){
			sql.append(" limit ").append(limit);
		}
	}
	
	/**
	 * 添加SET语句到sql变量中
	 * 例子 SET key=value,key2=value2
	 */
	private void addKeyValue(){
		if(!operateFields.isEmpty() ){
			sql=sql.append(" SET");
			for(String key : operateFields.keySet()){
				Object value= operateFields.get(key);
				sql= sql.append(" ").append(key).append(" = ").append(filterValue(value)).append(",");
			}
			//删除最后一个逗号
			sql=sql.deleteCharAt(sql.length()-1);
	    }
	}


	private String sqlColumnToString(SqlColumn sqlColumn) {
		StringBuilder columnSql = new StringBuilder();
		columnSql.append(" `").append(sqlColumn.getColumnName()).append("` ");
		String type = sqlColumn.getType();
		if (type.equals(SqlColumn.DATETIME) || type.equals(SqlColumn.TEXT)) {
			columnSql.append(type);
		} else {
			columnSql.append(type).append("(").append(sqlColumn.getLength()).append(") ");
		}
		if (sqlColumn.isNotNull()) {
			columnSql.append(" NOT NULL ");
		}
		if (sqlColumn.isIncrement()) {
			columnSql.append(" AUTO_INCREMENT ");
		}
		if (StringUtils.isNotEmpty(sqlColumn.getComment())) {
			columnSql.append(" COMMENT '").append(sqlColumn.getComment()).append("'");
		}
		return columnSql.append(",").toString();
	}

}