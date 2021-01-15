package com.sunmnet.bigdata.web.zntb.dataprovider;

import com.google.common.collect.Ordering;
import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.DatasourceParameter;
import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.QueryParameter;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class DataProviderViewManager {

    private static Logger LOG = LoggerFactory.getLogger(DataProviderViewManager.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map<String, Object>> getQueryParams(String type, String page) {
        Class clz = DataProviderManager.getDataProviderClass(type);
        Set<Field> fieldSet = ReflectionUtils.getAllFields(clz, ReflectionUtils.withAnnotation(QueryParameter.class));
        List<Field> fieldList = fieldOrdering.sortedCopy(fieldSet);
        List<Map<String, Object>> params = null;
        try {
            Object o = clz.newInstance();
            params = new ArrayList<>();
            for (Field field : fieldList) {
                field.setAccessible(true);
                QueryParameter queryParameter = field.getAnnotation(QueryParameter.class);
                Map<String, Object> param = new HashMap<>();
                param.put("label", queryParameter.label());
                param.put("type", queryParameter.type().toString());
                param.put("name", field.get(o));
                param.put("placeholder", queryParameter.placeholder());
                param.put("value", queryParameter.value());
                param.put("options", queryParameter.options());
                param.put("checked", queryParameter.checked());
                param.put("required", queryParameter.required());
                /*
                不同页面显示不同输入框
                 */
                String pageType = queryParameter.pageType();
                if (pageType.contains("all") || StringUtils.isBlank(page)) {
                    params.add(param);
                } else if (Arrays.asList("test", "dataset", "widget").contains(page) && pageType.contains(page)) {
                    params.add(param);
                }
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        return params;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map<String, Object>> getDatasourceParams(String type) {
        Class clz = DataProviderManager.getDataProviderClass(type);
        Set<Field> fieldSet = ReflectionUtils.getAllFields(clz, ReflectionUtils.withAnnotation(DatasourceParameter.class));
        List<Field> fieldList = fieldOrdering.sortedCopy(fieldSet);
        List<Map<String, Object>> params = null;
        try {
            Object o = clz.newInstance();
            params = new ArrayList<>();
            for (Field field : fieldList) {
                field.setAccessible(true);
                DatasourceParameter datasourceParameter = field.getAnnotation(DatasourceParameter.class);
                Map<String, Object> param = new HashMap<>();
                param.put("label", datasourceParameter.label());
                param.put("type", datasourceParameter.type().toString());
                param.put("name", (String) field.get(o));
                param.put("placeholder", datasourceParameter.placeholder());
                param.put("value", datasourceParameter.value());
                param.put("options", datasourceParameter.options());
                param.put("checked", datasourceParameter.checked());
                param.put("required", datasourceParameter.required());
                params.add(param);
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        return params;
    }

    private static Ordering<Field> fieldOrdering = Ordering.from(new Comparator<Field>() {
        @Override
        public int compare(Field o1, Field o2) {
            return Integer.compare(getOrder(o1), getOrder(o2));
        }

        private int getOrder(Field field) {
            field.setAccessible(true);
            DatasourceParameter datasourceParameter = field.getAnnotation(DatasourceParameter.class);
            if (datasourceParameter != null) {
                return datasourceParameter.order();
            }
            QueryParameter queryParameter = field.getAnnotation(QueryParameter.class);
            if (queryParameter != null) {
                return queryParameter.order();
            }
            return 0;
        }
    });
}
