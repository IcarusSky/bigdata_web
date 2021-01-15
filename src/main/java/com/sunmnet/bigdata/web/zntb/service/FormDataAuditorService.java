package com.sunmnet.bigdata.web.zntb.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.zntb.model.po.FormDataAuditor;
import com.sunmnet.bigdata.web.zntb.model.po.SecUserExt;
import com.sunmnet.bigdata.web.zntb.persistent.FormDataAuditorDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class FormDataAuditorService {

    @Autowired
    private FormDataAuditorDao formDataAuditorDao;

    public void saveBatch(List<FormDataAuditor> FormDataAuditors) {
        formDataAuditorDao.saveBatch(FormDataAuditors);
    }

    public List<FormDataAuditor> getListByFormId(Integer id) {
        return formDataAuditorDao.getListByFormId(id);
    }

    public void deleteByFormId(Integer formId) {
        formDataAuditorDao.deleteByFormId(formId);
    }
    
    
    public PageResult<SecUserExt> getWriterListByCondition(int pageNum, int pageSize, Integer formId, Integer departmentId, Integer positionId, Integer academyCode, String academyName, Integer majorCode, String majorName) {

        PageInfo<SecUserExt> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> formDataAuditorDao.getDataAuditorListByCondition(formId, departmentId, positionId, academyCode, academyName, majorCode, majorName));

        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(),
                CollectionUtils.isEmpty(pageInfo.getList()) ? Collections.emptyList() : pageInfo.getList());
    }
}
