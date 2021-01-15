package com.sunmnet.bigdata.web.zntb.model.po;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.zntb.dataprovider.util.SqlColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormWidget {

    private Integer columnId;
    private Integer formId;
    private String labelName;
    private String labelType;
    private String columnName;
    private String columnDesc;
    private String columnType;
    private Integer columnLength;
    private Integer required;
    private String promptMessage;// 自定义提示信息
    private Integer dataPrefill;
    private Long datasetId;
    private String prefillJson;
    private String widgetJson;
    private Integer standardId;// 数据标准id
    private String isAutomation;// 是否自动 yes-从数据标准中选择  no-手动创建数据标准项
    private String formula;// 计算公式
    private Integer datasetCategoryId;
    private String dateFormat;// 日期格式 date_yyyy(日期，年份)、date_yyyy_mm(日期，年月)、date_yyyy_mm_dd(日期，年月日)
    private Integer fileSize;//附件个数
    private String isEdit;//是否可编辑 yes-是 no-否


    public static List<FormWidget> translateJson(String json) throws Exception {
        List<FormWidget> list = new ArrayList<>();

        JSONObject jsonObject = JSONObject.parseObject(json);

        JSONObject layoutJson = jsonObject.getJSONObject("layoutJson");
        JSONArray rows = (JSONArray) layoutJson.get("rows");
//        for (Object row : rows) {
//            JSONArray columns = (JSONArray) ((JSONObject) row).get("columns");
//            for (int i = 0; i < columns.size(); i++) {
//                Object column = columns.get(i);
//                JSONObject widgetJson = (JSONObject) ((JSONObject) column).get("widget");
//                if (widgetJson == null) {
//                    continue;
//                }
//                list.add(translateJson((JSONObject) column, widgetJson, i + 1));
//            }
//        }
        for (Object row : rows) {
            JSONArray columns = (JSONArray) ((JSONObject) row).get("columns");
            for (Object column : columns) {
                JSONObject widgetJson = (JSONObject) ((JSONObject) column).get("widget");
                if (widgetJson == null) {
                    continue;
                }
                
                FormWidget temp = translateJson((JSONObject) column, widgetJson);
                if (list.contains(temp)) {
					throw new ServiceException("不能添加英文名称重复的列");
				}else {
					list.add(temp);
				}
            }
        }
        return list;
    }

    private static FormWidget translateJson(JSONObject column, JSONObject widgetJson) {
        FormWidget formWidget = new FormWidget();
        formWidget.setFormId(column.getInteger("formId"));
        formWidget.setLabelName(column.getString("label_name"));// 控件名称
        formWidget.setLabelType(column.getString("type"));// 控件类型

        LABELTYPE labeltype = LABELTYPE.valueOf(column.getString("type"));
        formWidget.setColumnType(labeltype.getColumnType());
        formWidget.setColumnLength(labeltype.getColumnLength());
        
//        formWidget.setColumnName("column_" + i);
        formWidget.setColumnName(widgetJson.getString("columnName"));
        formWidget.setColumnDesc(column.getString("label_name"));
        formWidget.setRequired(widgetJson.getInteger("required"));
        formWidget.setPromptMessage(widgetJson.getString("promptMessage"));// 控件提示信息
        formWidget.setFormula(widgetJson.getString("formula"));// 计算公式
        Integer dataPrefill = widgetJson.getInteger("dataPrefill");
        formWidget.setDataPrefill(dataPrefill);
        // 数据是否预填：0不预填，1预填，2计算公式
        if (null != dataPrefill && (dataPrefill == 0 || dataPrefill == 2 )) {
            formWidget.setDatasetId(0L);
            formWidget.setPrefillJson("{}");
        } else {
        	String isEdit = widgetJson.getString("isEdit");
        	if (StringUtils.isNotBlank(isEdit)) {
        		formWidget.setIsEdit(isEdit);
        		formWidget.setDatasetId(widgetJson.getLong("datasetId"));
        		JSONObject prefillJson = (JSONObject) widgetJson.get("prefillJson");
        		formWidget.setPrefillJson(prefillJson.toString());
			} else {
				throw new ServiceException("针对填写方式为数据集填写的控件设置时是否可编辑必填");
			}
        }
        
        // 是否自动 yes-从数据标准中选择  no-手动创建数据标准项
        String isAutomation = widgetJson.getString("isAutomation");
        
        //判断控件选择项是否是自动从数据标准管理中获取的。如果是，则取出数据标准ID并保存；如果不是，则取出自定义信息
        if (StringUtils.isNotBlank(isAutomation) && "yes".equals(isAutomation)) {
        	// 数据标准id
        	Integer standardId = widgetJson.getInteger("standardId");
			if (null == standardId) {
				throw new ServiceException("没有正确选择数据标准");
			} else {
                JSONObject widget = (JSONObject)  widgetJson.get("widgetJson");
				formWidget.setIsAutomation(isAutomation);
				formWidget.setStandardId(standardId);
	        	formWidget.setWidgetJson("{}");
                //formWidget.setWidgetJson(widget.toString());
			}
		} else if (StringUtils.isNotBlank(isAutomation) && "no".equals(isAutomation)) {
			JSONObject widget = (JSONObject)  widgetJson.get("widgetJson");
			if (widget.size() == 0) {
				throw new ServiceException("没有手动填写数据标准");
			} else {
				formWidget.setIsAutomation(isAutomation);
				formWidget.setWidgetJson(widget.toString());
			}
		} else {
			formWidget.setWidgetJson("{}");
		}
        
        //控件类型--附件  附件个数
        if(column.getString("type").equals("file")){
            formWidget.setFileSize(column.getInteger("fileSize"));
        }
        
        //控件类型--日期  日期格式
        if(column.getString("type").equals("date")){
        	formWidget.setDateFormat(widgetJson.getString("dateFormat"));
        }
        
        
        
        return formWidget;
    }
    /**
     * 组件类型
     */
    public enum LABELTYPE {
        title(false, "varchar", 128),
        input(false, "varchar", 128),
        radio(true, "varchar", 64),
        phone(false, "varchar", 11),
        number(false, "varchar", 64),// 数字输入框
        idcard(false, "varchar", 18),
        date(true, "varchar", 30),// 日期
        select(true, "varchar", 64),
        checkbox(true, "varchar", 64),
        textarea(false, "text", 0),
        email(false, "varchar", 64),
        file(true, "text", 0) // 附件
        ;

        LABELTYPE(Boolean valueTrans, String columnType, Integer columnLength) {
            this.valueTrans = valueTrans;
            this.columnType = columnType;
            this.columnLength = columnLength;
        }

        private Boolean valueTrans;
        private String columnType;
        private Integer columnLength;

        public Boolean valueTrans() {
            return valueTrans;
        }

        public String getColumnType() {
            return columnType;
        }

        public Integer getColumnLength() {
            return columnLength;
        }
    }

    public static String keyTrans(FormWidget formWidget, String rawKey) {
        if (formWidget == null || StringUtils.isEmpty(rawKey)) {
            return "";
        }

        Map<String, Object> widgetJson = JSONObject.parseObject(formWidget.getWidgetJson());
        JSONArray options = (JSONArray) widgetJson.get("options");

        if (formWidget.getLabelType().equals(FormWidget.LABELTYPE.radio.name()) ||
                formWidget.getLabelType().equals(LABELTYPE.select.name())) {
            if (null != options) {
                for (Object o : options) {
                    Object key = ((JSONObject) o).get("key");
                    String value = ((JSONObject) o).get("value").toString();
                    if (key.equals(rawKey)) {
                        return value;
                    }
                }
            }
        }

        if (formWidget.getLabelType().equals(LABELTYPE.checkbox.name())) {
            if (null != options){
                String[] split = rawKey.split(",");
                String v = "";
                for (Object o : options) {
                    Object key = ((JSONObject) o).get("key");
                    String value = ((JSONObject) o).get("value").toString();
                    for (String s : split) {
                        if (s.equals(key)) {
                            v += value + ",";
                        }
                    }
                }
                return v.substring(0, v.length() - 1);
            }
        }

        return rawKey;
    }

    public static Object valueTrans(FormWidget formWidget, Object object) {
        if (object == null) {
            return null;
        }
        /*if (formWidget.getLabelType().equals(FormWidget.LABELTYPE.date.name())) {
            return DateUtils.formatDateTime((Timestamp) object);
        }*/
        Map<String, Object> widgetJson= JSONObject.parseObject(formWidget.getWidgetJson());
        JSONArray operation = (JSONArray) widgetJson.get("options");

        if (formWidget.getLabelType().equals(FormWidget.LABELTYPE.radio.name())||
            formWidget.getLabelType().equals(LABELTYPE.select.name())) {
            if (null != operation){
                for (Object o : operation) {
                    Object key = ((JSONObject) o).get("key");
                    String value = ((JSONObject) o).get("value").toString();
                    if (value.equals(object.toString())) {
                        return key;
                    }
                }
            }
        }

        if (formWidget.getLabelType().equals(LABELTYPE.checkbox.name())) {
            String[] split = object.toString().split(",");
            String v = "";
            if (null != operation){
                for (Object o : operation) {
                    Object key = ((JSONObject) o).get("key");
                    String value = ((JSONObject) o).get("value").toString();
                    for (String s : split) {
                        if (s.equals(value)) {
                            v += key + ",";
                        }
                    }
                }
            }
            if (v.length() == 0) {
                return v;
            }
            return v.substring(0, v.length() - 1);
        }

        return object;
    }

    public SqlColumn toSqlColumn() {
        return new SqlColumn(this.columnName)
                .TYPE(this.columnType)
                .LENGTH(this.columnLength)
                .COMMENT(this.columnDesc)
                .NOTNULL(required == 1);
    }
    public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnDesc() {
        return columnDesc;
    }

    public void setColumnDesc(String columnDesc) {
        this.columnDesc = columnDesc;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Integer getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(Integer columnLength) {
        this.columnLength = columnLength;
    }

    public Integer getRequired() {
        return required;
    }

    public void setRequired(Integer required) {
        this.required = required;
    }

    public String getPromptMessage() {
		return promptMessage;
	}

	public void setPromptMessage(String promptMessage) {
		this.promptMessage = promptMessage;
	}

	public Integer getDataPrefill() {
        return dataPrefill;
    }

    public void setDataPrefill(Integer dataPrefill) {
        this.dataPrefill = dataPrefill;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getPrefillJson() {
        return prefillJson;
    }

    public void setPrefillJson(String prefillJson) {
        this.prefillJson = prefillJson;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public String getWidgetJson() {
        return widgetJson;
    }

	public Integer getStandardId() {
		return standardId;
	}

	public String getIsAutomation() {
		return isAutomation;
	}

	public void setIsAutomation(String isAutomation) {
		this.isAutomation = isAutomation;
	}

	public void setStandardId(Integer standardId) {
		this.standardId = standardId;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public Integer getDatasetCategoryId() {
        return datasetCategoryId;
    }

    public void setDatasetCategoryId(Integer datasetCategoryId) {
        this.datasetCategoryId = datasetCategoryId;
    }

    public void setWidgetJson(String widgetJson) {
        this.widgetJson = widgetJson;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormWidget other = (FormWidget) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		return true;
	}
    
    public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Integer getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

	public String getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(String isEdit) {
		this.isEdit = isEdit;
	}

    @Override
    public String toString() {
        return "FormWidget{" +
                "columnId=" + columnId +
                ", formId=" + formId +
                ", labelName='" + labelName + '\'' +
                ", labelType='" + labelType + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnDesc='" + columnDesc + '\'' +
                ", columnType='" + columnType + '\'' +
                ", columnLength=" + columnLength +
                ", required=" + required +
                ", promptMessage='" + promptMessage + '\'' +
                ", dataPrefill=" + dataPrefill +
                ", datasetId=" + datasetId +
                ", prefillJson='" + prefillJson + '\'' +
                ", widgetJson='" + widgetJson + '\'' +
                ", standardId=" + standardId +
                ", isAutomation='" + isAutomation + '\'' +
                ", formula='" + formula + '\'' +
                ", datasetCategoryId=" + datasetCategoryId +
                ", dateFormat='" + dateFormat + '\'' +
                ", fileSize=" + fileSize +
                ", isEdit='" + isEdit + '\'' +
                '}';
    }
}
