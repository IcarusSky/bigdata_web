package com.sunmnet.bigdata.web.zntb.service;

import com.google.common.collect.Lists;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewDashboardCategory;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardCategory;
import com.sunmnet.bigdata.web.zntb.persistent.CategoryDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private DatasetService datasetService;

    @Autowired
    private StandardService standardService;

    @Autowired
    private FormService formService;

    public void save(DashboardCategory category) {
        if (existCategoryName(category)) {
            throw new ServiceException("文件夹名称已存在");
        }
        categoryDao.save(category);
    }

    public void update(DashboardCategory category) {
        if (existCategoryName(category)) {
            throw new ServiceException("文件夹名称已存在");
        }
        categoryDao.update(category);
    }

    public void updateName(DashboardCategory category) {
        if (existCategoryName(category)) {
            throw new ServiceException("文件夹名称已存在");
        }
        categoryDao.updateName(category);
    }

    private boolean existCategoryName(DashboardCategory category) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", category.getId());
        paramMap.put("name", category.getName());
        paramMap.put("type", category.getType());
        paramMap.put("userId", category.getUserId());
        return categoryDao.countExistCategoryName(paramMap) > 0;
    }

    public String delete(Integer id, String type) {
        if (id == null || id <= 0 || StringUtils.isEmpty(type)) {
            return "0";
        }

        if (DashboardCategory.Type.DATASET.getValue().equalsIgnoreCase(type)) {
            if (datasetService.getDatasetCountByCategoryId(id) == 0) {
                categoryDao.delete(id);
            } else {
                throw new ServiceException("文件夹不为空，无法删除");
            }
        } else if (DashboardCategory.Type.DATA_STANDARD.getValue().equalsIgnoreCase(type)) {
            if (standardService.countByCategoryId(id) == 0) {
                categoryDao.delete(id);
            } else {
                throw new ServiceException("分类不为空，无法删除");
            }
        } else if (DashboardCategory.Type.FORM.getValue().equalsIgnoreCase(type)) {
            if (formService.countByCategoryId(id) == 0) {
                categoryDao.delete(id);
            } else {
                throw new ServiceException("分类不为空，无法删除");
            }
        }

        return "1";
    }

    @SuppressWarnings("unchecked")
	public List<ViewDashboardCategory> getCategoryListByParentId(Integer userId, String type, Integer parentId) {
        return Lists.transform(categoryDao.getCategoryListByParentId(userId, type, parentId), ViewDashboardCategory.TO);
    }

    public void changeCategory(Integer id, Integer parentId, String type) {
        if (DashboardCategory.Type.DATASET.getValue().equalsIgnoreCase(type)) {
            datasetService.changeCategory(id,parentId);
        }
//        categoryDao.updateParentId(id, parentId);
    }
}
