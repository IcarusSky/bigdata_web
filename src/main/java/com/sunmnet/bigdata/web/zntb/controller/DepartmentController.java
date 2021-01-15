package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.model.po.Department;
import com.sunmnet.bigdata.web.zntb.service.DepartmentService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/department")
public class DepartmentController extends BaseController {

    @Resource
    private DepartmentService departmentService;

    final String SUCC_NAME = "success";

    @ResponseBody
    @PostMapping("/save")
    public Object saveDepartment(@RequestParam(value = "json") String json) {
        Department dep = Department.translateJson(json);
        dep.setUserId(authenticationHolder.getAuthenticatedUser().getId());
        Pair<Boolean, String> validateResult = dep.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }
        departmentService.saveDepartment(dep);
        return buildSuccJson(SUCC_NAME);
    }

    @ResponseBody
    @PostMapping("/update")
    public Object updateDepartment(@RequestParam(value = "json") String json) {
        Department dep = Department.translateJson(json);
        dep.setUserId(authenticationHolder.getAuthenticatedUser().getId());
        Pair<Boolean, String> validateResult = dep.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }

        return buildSuccJson(departmentService.updateDepartment(dep));
    }

    @ResponseBody
    @PostMapping("/delete")
    public Object delete(@RequestParam(value = "id") Integer id) {
        return buildSuccJson(departmentService.deleteDepartment(id));
    }

    @ResponseBody
    @PostMapping("/findAllDepList")
    public Object findAllDepList() {
        return buildSuccJson(departmentService.findAllDepList());
    }

    @ResponseBody
    @PostMapping("/findDepById")
    public Object findAllDepList(@RequestParam(value = "id") Integer id) {
        return buildSuccJson(departmentService.findDepById(id));
    }

}