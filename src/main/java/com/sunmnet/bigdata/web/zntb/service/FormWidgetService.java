package com.sunmnet.bigdata.web.zntb.service;

import com.sunmnet.bigdata.web.zntb.model.po.FormWidget;
import com.sunmnet.bigdata.web.zntb.model.po.StandardSet;
import com.sunmnet.bigdata.web.zntb.persistent.FormWidgetDao;
import com.sunmnet.bigdata.web.zntb.persistent.StandardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class FormWidgetService {

    @Autowired
    private FormWidgetDao formWidgetDao;
    @Autowired
    private StandardDao standardDao;


    public List<FormWidget> getListByFormId(Integer formId) {
    	List<FormWidget> list = formWidgetDao.getListByFormId(formId);
    	list.stream().forEach(formWidget->{
    		if (null != formWidget.getStandardId()) {
    			Integer id = formWidget.getStandardId();
    			StandardSet standardSet = standardDao.getStandardById(id);
    			String widgetJson = standardSet.getStandardValueJson();
    			formWidget.setWidgetJson(widgetJson);
			}
    	});
        return list;
    }

    public List<FormWidget> getListByDatasetId(Integer datasetId) {
        List<FormWidget> widgets = formWidgetDao.getListByDatasetId(datasetId);
        if (widgets == null) {
            widgets = Collections.emptyList();
        }
        return widgets;
    }

    public void saveBatch(List<FormWidget> formWidgets) {
        formWidgetDao.saveBatch(formWidgets);
    }

    public void deleteByFormId(Integer formId) {
        formWidgetDao.deleteByFormId(formId);
    }
}
