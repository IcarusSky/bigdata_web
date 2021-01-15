package com.sunmnet.bigdata.web.zntb.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.zntb.dataprovider.util.MD5Util;
import com.sunmnet.bigdata.web.zntb.model.dto.AccountUser;
import com.sunmnet.bigdata.web.zntb.service.SecUserExtService;

@Controller
@RequestMapping("/admin/user")
public class UserExtController extends BaseController {

    @Resource
    private SecUserExtService userExtService;

    /**
     * 获取用户扩展信息接口
     *
     * @param userId 用户ID
     * @return 扩展信息
     */
    @ResponseBody
    @GetMapping("/extinfo")
    public Object getExtInfo(@RequestParam(value = "userId", required = false) Integer userId) {
        if (userId == null || userId <= 0) {
            return buildErrJson("用户ID必须大于零");
        }
        return buildSuccJson(userExtService.selectByUserId(userId));
    }

    /**
     * 根据姓名模糊搜索用户扩展信息接口
     *
     * @param name 用户姓名
     * @return 扩展信息
     */
    @ResponseBody
    @GetMapping("/extInfoByName")
    public Object getExtInfoByName(@RequestParam(value = "name", required = false, defaultValue = "") String name, @RequestParam(value = "topN", required = false, defaultValue = "") String topN) {
        return buildSuccJson(userExtService.selectByName(name, topN));
    }

    /**
     *  统一登录--
     * @param verify 校验码
     * @param userName 用户编号；学号or教工号
     * @param strSysDatetime 时间戳
     * @param jsName 用户角色
     * @param url 指定跳转的 URL
     * @return 是否通过认证
     */
    @ResponseBody
    @GetMapping(value = "confirmUserInfo",produces = { "application/json;charset=UTF-8" })
    public Object confirmUserInfo(@RequestParam(value = "verify")String verify, @RequestParam(value = "userName")String userName,
                                  @RequestParam(value = "strSysDatetime")String strSysDatetime, @RequestParam(value = "jsName")String jsName,
                                  @RequestParam(value = "url")String url, HttpServletResponse response) {
        String zfkey = "ZFSSOKEY";
        try {
            boolean flag = MD5Util.verify(userName + zfkey + strSysDatetime + jsName, verify);
            AccountUser accountUser = userExtService.confirmUserInfo(userName);
            if (flag && accountUser!=null){
                System.out.println(accountUser.getName());
                response.sendRedirect("http://192.168.2.205:8981/#/?accountName="+accountUser.getName()+"&url="+url);
                return buildSuccJson();
            }else {
                response.sendRedirect("http://192.168.2.205:8981/#/?accountName="+"0000");
                return buildErrJson("用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildSuccJson();
    }
}
