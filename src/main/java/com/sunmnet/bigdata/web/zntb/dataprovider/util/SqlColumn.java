package com.sunmnet.bigdata.web.zntb.dataprovider.util;

public class SqlColumn {


	public static final String VARCHAR = "varchar";
	public static final String DATETIME = "datetime";
	public static final String TEXT = "text";
	public static final String INTEGER = "integer";
	public static final String BOOLEAN = "boolean";
	public static final String LONG = "bigint";
	public static final String BLOB = "blob";
	public static final String FLOAT = "float";
	public static final String DOUBLE = "double";
	public static final String DATE = "date";
	public static final String TIME = "time";


	private String columnName;
	private String type;
	private int length;
	private boolean notNull;
	private String comment;
	private boolean increment;

	public static SqlColumn defaultSqlColumn() {
		return new SqlColumn("id").TYPE("int").LENGTH(20).NOTNULL(true).INCREMENT(true).COMMENT("主键");
	}
	/**
	 * 初始化构造数据库操作,默认使用select查询
	 * @param
	 */
	public SqlColumn(String columnName){
		this.columnName = columnName;
	}
	
	public SqlColumn LENGTH(int length){
		this.length = length;
		return this;
	}

	public SqlColumn TYPE(String type) {
		this.type = type;
		return this;
	}

	public SqlColumn NOTNULL(boolean notNull) {
		this.notNull = notNull;
		return this;
	}

	public SqlColumn COMMENT(String comment) {
		this.comment = comment;
		return this;
	}

	public SqlColumn INCREMENT(boolean increment) {
		this.increment = increment;
		return this;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isIncrement() {
		return increment;
	}

	public void setIncrement(boolean increment) {
		this.increment = increment;
	}
}