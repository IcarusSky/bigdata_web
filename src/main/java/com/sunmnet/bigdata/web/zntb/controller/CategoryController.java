package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewDashboardCategory;
import com.sunmnet.bigdata.web.zntb.model.po.DashboardCategory;
import com.sunmnet.bigdata.web.zntb.service.CategoryService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/getlist")
    public Object getList(
                      @RequestParam(name = "categoryId", defaultValue = "-1") int categoryId,
                      @RequestParam(name = "type") String type,
                      @RequestParam(name = "isAll",defaultValue ="false") boolean isAll) {
        Integer userId = authenticationHolder.getAuthenticatedUser().getId();
        /**
         * 如果“type”为数据标准，或者“isAll”为true，则不考虑当前登录用户
         */
        if (type.equals(DashboardCategory.Type.DATA_STANDARD.getValue()) || isAll) {
            userId = null;
        }
        List<ViewDashboardCategory> categoryListByParentId = categoryService.getCategoryListByParentId(userId, type, categoryId);

        if (!type.equals(DashboardCategory.Type.DATASET.getValue())) {
            List arrList = new ArrayList(categoryListByParentId);
            arrList.add(0, ViewDashboardCategory.getDefault());
            return buildSuccJson(arrList);
        }
        return buildSuccJson(categoryListByParentId);
    }

    @RequestMapping(value = "/save")
    public Object saveNewCategory(@RequestParam(name = "json") String json) {
        DashboardCategory category = DashboardCategory.translateJson(json);
        category.setUserId(authenticationHolder.getAuthenticatedUser().getId());

        Pair<Boolean, String> validateResult = category.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        categoryService.save(category);
        return buildSuccJson();
    }

    @RequestMapping(value = "/update")
    public Object update(@RequestParam(name = "json") String json) {
        DashboardCategory category = DashboardCategory.translateJson(json);
        category.setUserId(authenticationHolder.getAuthenticatedUser().getId());

        Pair<Boolean, String> validateResult = category.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        categoryService.update(category);
        return buildSuccJson();
    }

    @RequestMapping(value = "/delete")
    public Object delete(@RequestParam(name = "id") Integer id,
                         @RequestParam(name = "type") String type) {
        categoryService.delete(id, type);
        return buildSuccJson();
    }

    @RequestMapping(value = "/changeCategory")
    public Object changeCategory(@RequestParam(name = "id") Integer id,
                                 @RequestParam(name = "parentId") Integer parentId,
                                 @RequestParam(name = "type") String type) {
        categoryService.changeCategory(id, parentId, type);
        return buildSuccJson();
    }

    @RequestMapping(value = "/batchChangeCategory")
    public Object batchChangeCategory(@RequestParam(name = "ids") String ids,
                                      @RequestParam(name = "parentId") Integer parentId,
                                      @RequestParam(name = "type") String type) {
        String[] split = ids.split(",");
        for (String sid : split) {
            int id = Integer.parseInt(sid);
            categoryService.changeCategory(id, parentId, type);
        }
        return buildSuccJson();
    }

    @RequestMapping(value = "/updateName")
    public Object updateName(@RequestParam(name = "id") Integer id,
                             @RequestParam(name = "name") String name,
                             @RequestParam(name = "type") String type) {
        DashboardCategory category = new DashboardCategory(id, authenticationHolder.getAuthenticatedUser().getId(), name, type);

        Pair<Boolean, String> validateResult = category.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        categoryService.updateName(category);
        return buildSuccJson();
    }
}
