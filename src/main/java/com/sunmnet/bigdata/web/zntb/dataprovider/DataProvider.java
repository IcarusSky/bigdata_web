package com.sunmnet.bigdata.web.zntb.dataprovider;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.googlecode.aviator.AviatorEvaluator;
import com.sunmnet.bigdata.web.core.security.authentication.AuthenticationHolder;
import com.sunmnet.bigdata.web.zntb.dataprovider.aggregator.Aggregatable;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.AggConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.CompositeConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.ConfigComponent;
import com.sunmnet.bigdata.web.zntb.dataprovider.config.DimensionConfig;
import com.sunmnet.bigdata.web.zntb.dataprovider.expression.NowFunction;
import com.sunmnet.bigdata.web.zntb.dataprovider.result.AggregateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.DriverManager;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DataProvider {
    public static final int DATASOURCE_CONNECT_TIMEOUT_MILLIS = 2000;

    @Autowired
    private AuthenticationHolder authenticationHolder;
    protected Map<String, String> dataSource;
    protected Map<String, String> query;
    private int resultLimit;
    private boolean isUsedForTest = false;
    @SuppressWarnings("unused")
	private long interval = 12 * 60 * 60; // second

    public static final String NULL_STRING = "#NULL";
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(DataProvider.class);

    static {
        DriverManager.setLoginTimeout(DATASOURCE_CONNECT_TIMEOUT_MILLIS / 1000);
        AviatorEvaluator.addFunction(new NowFunction());
    }

    public abstract boolean doAggregationInDataSource();

    public boolean isDataSourceAggInstance() {
        if (this instanceof Aggregatable && doAggregationInDataSource()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get the aggregated data by user's widget designer
     *
     * @return
     */
    public final AggregateResult getAggData(AggConfig ac) throws Exception {
        evalValueExpression(ac);
        if (isDataSourceAggInstance()) {
            return ((Aggregatable) this).queryAggData(ac);
        } else {
            return null;
        }
    }

    public final String getViewAggDataQuery(AggConfig config) throws Exception {
        evalValueExpression(config);
        if (isDataSourceAggInstance()) {
            return ((Aggregatable) this).viewAggDataQuery(config);
        } else {
            return "Not Support";
        }
    }

    /**
     * Get the options values of a dimension column
     *
     * @param columnName
     * @return
     */
    public final String[] getDimVals(String columnName, AggConfig config, boolean reload) throws Exception {
        String[] dimVals = null;
        evalValueExpression(config);
        if (isDataSourceAggInstance()) {
            dimVals = ((Aggregatable) this).queryDimVals(columnName, config);
        } else {
            return null;
        }
        return Arrays.stream(dimVals)
                .map(member -> {
                    return Objects.isNull(member) ? NULL_STRING : member;
                })
                .sorted(new NaturalOrderComparator()).limit(1000).toArray(String[]::new);
    }

    public final String[] getColumn(boolean reload) throws Exception {
        String[] columns = null;
        if (isDataSourceAggInstance()) {
            columns = ((Aggregatable) this).getColumn();
        } else {
            return null;
        }
        Arrays.sort(columns);
        return columns;
    }

    private void evalValueExpression(AggConfig ac) {
        if (ac == null) {
            return;
        }
        ac.getFilters().forEach(e -> evaluator(e));
        ac.getColumns().forEach(e -> evaluator(e));
        ac.getRows().forEach(e -> evaluator(e));
    }

    private void evaluator(ConfigComponent e) {
        if (e instanceof DimensionConfig) {
            DimensionConfig dc = (DimensionConfig) e;
            dc.setValues(dc.getValues().stream().flatMap(v -> getFilterValue(v)).collect(Collectors.toList()));
        }
        if (e instanceof CompositeConfig) {
            CompositeConfig cc = (CompositeConfig) e;
            cc.getConfigComponents().forEach(_e -> evaluator(_e));
        }
    }

    private Stream<String> getFilterValue(String value) {
        List<String> list = new ArrayList<>();
        if (value == null || !(value.startsWith("{") && value.endsWith("}"))) {
            list.add(value);
        } else if ("{loginName}".equals(value)) {
            list.add(authenticationHolder.getAuthenticatedUser().getUsername());
        } else if ("{userName}".equals(value)) {
            list.add(authenticationHolder.getAuthenticatedUser().getName());
        } else {
            list.add(AviatorEvaluator.compile(value.substring(1, value.length() - 1), true).execute().toString());
        }
        return list.stream();
    }

    public String getLockKey() {
        String dataSourceStr = JSONObject.toJSON(dataSource).toString();
        String queryStr = JSONObject.toJSON(query).toString();
        return Hashing.md5().newHasher().putString(dataSourceStr + queryStr, Charsets.UTF_8).hash().toString();
    }

    public List<DimensionConfig> filterCCList2DCList(List<ConfigComponent> filters) {
        List<DimensionConfig> result = new LinkedList<>();
        filters.stream().forEach(cc -> {
            result.addAll(configComp2DimConfigList(cc));
        });
        return result;
    }

    public List<DimensionConfig> configComp2DimConfigList(ConfigComponent cc) {
        List<DimensionConfig> result = new LinkedList<>();
        if (cc instanceof DimensionConfig) {
            result.add((DimensionConfig) cc);
        } else {
            Iterator<ConfigComponent> iterator = cc.getIterator();
            while (iterator.hasNext()) {
                ConfigComponent next = iterator.next();
                result.addAll(configComp2DimConfigList(next));
            }
        }
        return result;
    }

    abstract public String[][] getData() throws Exception;

    public void test() throws Exception {
        getData();
    }

    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    public void setResultLimit(int resultLimit) {
        this.resultLimit = resultLimit;
    }

    public int getResultLimit() {
        return resultLimit;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isUsedForTest() {
        return isUsedForTest;
    }

    public void setUsedForTest(boolean usedForTest) {
        isUsedForTest = usedForTest;
    }

    public static ConfigComponent separateNull(ConfigComponent configComponent) {
        if (configComponent instanceof DimensionConfig) {
            DimensionConfig cc = (DimensionConfig) configComponent;
            if (("=".equals(cc.getFilterType()) || "≠".equals(cc.getFilterType())) && cc.getValues().size() > 1 &&
                    cc.getValues().stream().anyMatch(s -> DataProvider.NULL_STRING.equals(s))) {
                CompositeConfig compositeConfig = new CompositeConfig();
                compositeConfig.setType("=".equals(cc.getFilterType()) ? "OR" : "AND");
                cc.setValues(cc.getValues().stream().filter(s -> !DataProvider.NULL_STRING.equals(s)).collect(Collectors.toList()));
                compositeConfig.getConfigComponents().add(cc);
                DimensionConfig nullCc = new DimensionConfig();
                nullCc.setColumnName(cc.getColumnName());
                nullCc.setFilterType(cc.getFilterType());
                nullCc.setValues(new ArrayList<>());
                nullCc.getValues().add(DataProvider.NULL_STRING);
                compositeConfig.getConfigComponents().add(nullCc);
                return compositeConfig;
            }
        }
        return configComponent;
    }

}
