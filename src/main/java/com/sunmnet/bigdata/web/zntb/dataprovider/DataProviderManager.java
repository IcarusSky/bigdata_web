package com.sunmnet.bigdata.web.zntb.dataprovider;

import com.sunmnet.bigdata.web.zntb.dataprovider.annotation.ProviderName;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class DataProviderManager implements ApplicationContextAware {
    private static Map<String, Class<? extends DataProvider>> providers = new HashMap<>();
    private static ApplicationContext applicationContext;

    static {
        Set<Class<?>> classSet = new Reflections("com.sunmnet.bigdata.web.zntb.dataprovider").getTypesAnnotatedWith(ProviderName.class);
        for (Class c : classSet) {
            if (!c.isAssignableFrom(DataProvider.class)) {
                providers.put(((ProviderName) c.getAnnotation(ProviderName.class)).name(), c);
            }
        }
    }

    public static List<Map<String, Object>> getProviderList() {
        Map<String, List<Class<? extends DataProvider>>> collect = providers.values().stream().collect(
                Collectors.groupingBy(
                        e -> {
                            ProviderName annotation = e.getAnnotation(ProviderName.class);
                            return annotation.parent() + "_" + annotation.order();
                        }
                )
        );

        return collect.entrySet().stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            String key = e.getKey();
            String[] split = key.split("_");
            map.put("name", split[0]);
            map.put("order", split[1]);
            map.put("children", e.getValue().stream().map(pro -> pro.getAnnotation(ProviderName.class).name()).collect(Collectors.toList()));
            return map;
        }).sorted((a, b) -> {
            int a_order = Integer.parseInt((String) a.get("order"));
            int b_order = Integer.parseInt((String) b.get("order"));
            return a_order - b_order;
        }).collect(Collectors.toList());
    }

    public static DataProvider getDataProvider(
            String type, Map<String, String> dataSource,
            Map<String, String> query) throws Exception {
        return getDataProvider(type, dataSource, query, false);
    }

    public static DataProvider getDataProvider(
            String type, Map<String, String> dataSource,
            Map<String, String> query,
            boolean isUseForTest) throws Exception {
        Class c = providers.get(type);
        ProviderName providerName = (ProviderName) c.getAnnotation(ProviderName.class);
        if (providerName.name().equals(type)) {
            DataProvider provider = (DataProvider) c.newInstance();
            provider.setQuery(query);
            provider.setDataSource(dataSource);
            provider.setUsedForTest(isUseForTest);
            if (provider instanceof Initializing) {
                ((Initializing) provider).afterPropertiesSet();
            }
            applicationContext.getAutowireCapableBeanFactory().autowireBean(provider);
            return provider;
        }
        return null;
    }

    protected static Class<? extends DataProvider> getDataProviderClass(String type) {
        return providers.get(type);
    }

    @SuppressWarnings("static-access")
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
