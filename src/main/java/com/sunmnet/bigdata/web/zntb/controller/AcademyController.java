package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.model.po.Academy;
import com.sunmnet.bigdata.web.zntb.service.AcademyService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/academy")
public class AcademyController extends BaseController {

    @Resource
    private AcademyService academyService;

    final String SUCC_NAME = "success";


    @ResponseBody
    @PostMapping("/findAllAcademies")
    public Object findAllAcademies() {
        return buildSuccJson(academyService.findAllAcademies());
    }

    @ResponseBody
    @PostMapping("/save")
    public Object saveStandard(@RequestParam(value = "json") String json) {
        Academy academy = Academy.translateJson(json);

        Pair<Boolean, String> validateResult = academy.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }
        return buildSuccJson(academyService.save(academy));
    }

    @ResponseBody
    @PostMapping("/update")
    public Object update(@RequestParam(value = "json") String json) {

        Academy academy = Academy.translateJson(json);

        Pair<Boolean, String> validateResult = academy.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }
        academyService.update(academy);
        return buildSuccJson(SUCC_NAME);
    }

    @ResponseBody
    @PostMapping("/delete")
    public Object delete(@RequestParam(value = "code") String code) {
        return buildSuccJson(academyService.delete(code));

    }
}
