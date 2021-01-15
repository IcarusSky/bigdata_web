package com.sunmnet.bigdata.web.zntb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.model.po.Position;
import com.sunmnet.bigdata.web.zntb.service.PositionService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/position")
public class PositionController extends BaseController {

    @Resource
    private PositionService positionService;

    final String SUCC_NAME = "success";

    @ResponseBody
    @PostMapping("/save")
    public Object saveStandard(@RequestParam(value = "json") String json) {
        JSONArray jsonArray = JSONArray.parseArray(json);
        List<Position> list =new ArrayList<>(jsonArray.size());
        int userId = authenticationHolder.getAuthenticatedUser().getId();
        if(jsonArray.size()>0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Position pos = new Position();
                pos.setUserId(userId);
                pos.setDepId(obj.getInteger("depId"));
                pos.setName(obj.getString("name"));
                list.add(pos);
            }
        }
        positionService.save(list);
        return buildSuccJson(SUCC_NAME);
    }

    @ResponseBody
    @PostMapping("/update")
    public Object update(@RequestParam(value = "json") String json) {
        Position pos = Position.translateJson(json);
        pos.setUserId(authenticationHolder.getAuthenticatedUser().getId());
        Pair<Boolean, String> validateResult = pos.validate();
        if (!validateResult.getKey()) {
            return buildErrJson(validateResult.getValue());
        }
        positionService.update(pos);
        return buildSuccJson(SUCC_NAME);
    }

    @ResponseBody
    @PostMapping("/delete")
    public Object delete(@RequestParam(value = "id") Integer id) {

        positionService.delete(id);
        return buildSuccJson(SUCC_NAME);

    }

    @ResponseBody
    @PostMapping("/getListByDepId")
    public Object getListByDepId(@RequestParam(value = "id") Integer id) {
        return buildSuccJson(positionService.getListByDepId(id));
    }

    @ResponseBody
    @PostMapping("/getPosByDepId")
    public Object getPosByDepId(@RequestParam(value = "id") Integer id) {
        return buildSuccJson(positionService.getPosByDepId(id));
    }
}