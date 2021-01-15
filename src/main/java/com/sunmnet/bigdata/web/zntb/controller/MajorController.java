package com.sunmnet.bigdata.web.zntb.controller;


import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.model.po.Major;
import com.sunmnet.bigdata.web.zntb.service.MajorService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/major")
public class MajorController extends BaseController {

    @Resource
    private MajorService majorService;

    final String SUCC_NAME = "success";

    @ResponseBody
    @PostMapping("/save")
    public Object saveStandard(@RequestParam(value = "json") String json) {
        Major major = Major.translateJson(json);

        Pair<Boolean, String> validateResult = major.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }
        return buildSuccJson(majorService.save(major));
    }

    @ResponseBody
    @PostMapping("/update")
    public Object update(@RequestParam(value = "json") String json) {

        Major major = Major.translateJson(json);

        Pair<Boolean, String> validateResult = major.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }
        majorService.update(major);
        return buildSuccJson(SUCC_NAME);
    }

    @ResponseBody
    @PostMapping("/delete")
    public Object delete(@RequestParam(value = "code") String code) {
        majorService.delete(code);
        return buildSuccJson(SUCC_NAME);

    }
}