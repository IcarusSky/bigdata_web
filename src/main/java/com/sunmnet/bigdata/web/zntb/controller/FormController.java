package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.controller.BaseController;
import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewForm;
import com.sunmnet.bigdata.web.zntb.model.po.*;
import com.sunmnet.bigdata.web.zntb.service.FormService;
import com.sunmnet.bigdata.web.zntb.service.FormWriterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/form")
public class FormController extends BaseController {

    @Autowired
    private FormService formService;
    
    @Autowired
    private FormWriterService formWriterService;

    @RequestMapping(value = "/getlist")
    public Object getList(
            @RequestParam(name = "publishStatus",required = false) Integer publishStatus,
            @RequestParam(name = "auditorStatus", required = false) Integer auditorStatus,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "formName",defaultValue = "") String formName,
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Integer userId = authenticationHolder.getAuthenticatedUser().getId();
        return buildSuccJson(formService.getList(publishStatus, auditorStatus, categoryId, formName, userId, pageNum, pageSize));
    }

    // 新增表单和编辑表单都调用该接口
    @RequestMapping(value = "/save")
    public Object save(@RequestParam(name = "json") String json,
    		@RequestParam(name = "saveType", defaultValue = "false") boolean saveType) {
        Form form = Form.translateJson(json);
        form.setUserId(authenticationHolder.getAuthenticatedUser().getId());

        List<FormWidget> formWidgets = null;
		try {
			formWidgets = FormWidget.translateJson(json);
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("格式布局信息[layoutJson中的内容]解析失败");
		}
        List<FormWriter> formWriters = null;
		try {
			formWriters = FormWriter.translateJson(json);
		}catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("填表人信息[formWriter中的内容]解析失败");
		}
        List<FormDataAuditor> formDataAuditors = null;
		try {
            formDataAuditors = FormDataAuditor.translateJson(json);
		}catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("填表人信息[formDataAuditor中的内容]解析失败");
		}
		if(form.getFormId() == null) {
			formService.save(form, formWidgets, formWriters, formDataAuditors, saveType);
		} else {
			formService.update(form, formWidgets, formWriters, formDataAuditors, saveType);
		}
        return buildSuccJson("success");
    }

    // 将编辑表单方法合并到保存表单方法中
    @RequestMapping(value = "/update")
    public Object update(@RequestParam(name = "json") String json,
    		@RequestParam(name = "saveType", defaultValue = "false") boolean saveType) {
        Form form = Form.translateJson(json);
        form.setUserId(authenticationHolder.getAuthenticatedUser().getId());
        List<FormWidget> formWidgets = null;
		try {
			formWidgets = FormWidget.translateJson(json);
		} catch (Exception e) {
			throw new ServiceException("格式布局信息[layoutJson中的内容]解析失败");
		}
        List<FormWriter> formWriters = null;
		try {
			formWriters = FormWriter.translateJson(json);
		} catch (Exception e) {
			throw new ServiceException("填表人信息[formWriter中的内容]解析失败");
		}
        List<FormDataAuditor> formDataAuditors = null;
        try {
            formDataAuditors = FormDataAuditor.translateJson(json);
        }catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        } catch (Exception e) {
            throw new ServiceException("填表人信息[formWriter中的内容]解析失败");
        }
        formService.update(form, formWidgets, formWriters, formDataAuditors, saveType);
        return buildSuccJson("success");
    }

    //修改审核人 其他信息都不会修改
    @RequestMapping(value = "/updateAuditor")
    public Object updateAuditor(@RequestParam(name = "json") String json,
        @RequestParam(name = "saveType", defaultValue = "false") boolean saveType) {
        Form form = Form.translateJson(json);
        form.setUserId(authenticationHolder.getAuthenticatedUser().getId());

        List<FormWidget> formWidgets = null;
        try {
            formWidgets = FormWidget.translateJson(json);
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        } catch (Exception e) {
            throw new ServiceException("格式布局信息[layoutJson中的内容]解析失败");
        }
        List<FormWriter> formWriters = null;
        try {
            formWriters = FormWriter.translateJson(json);
        }catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        } catch (Exception e) {
            throw new ServiceException("填表人信息[formWriter中的内容]解析失败");
        }
        List<FormDataAuditor> formDataAuditors = null;
        try {
            formDataAuditors = FormDataAuditor.translateJson(json);
        }catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        } catch (Exception e) {
            throw new ServiceException("填表人信息[formDataAuditor中的内容]解析失败");
        }
//        if(form.getFormId() == null) {
//            formService.save(form, formWidgets, formWriters, formDataAuditors, saveType);
//        } else {
            formService.updateAuditor(form, formWidgets, formWriters, formDataAuditors,false);
//        }
        return buildSuccJson("success");
    }

    @RequestMapping(value = "/delete")
    public Object delete(@RequestParam(name = "id") Integer id) {
        formService.delete(id);
        return buildSuccJson("success");
    }

    @RequestMapping(value = "/info")
    public Object info(@RequestParam(name = "id") Integer id) {
        ViewForm form = formService.info(id);
        return buildSuccJson(form);
    }

    @RequestMapping(value = "/publish")
    public Object publish(@RequestParam(name = "formId") Integer formId) {
        formService.publish(formId);
        return buildSuccJson("success");
    }

    @RequestMapping(value = "/unpublish")
    public Object unPublish(@RequestParam(name = "formId") Integer formId) {
        formService.unPublish(formId);
        return buildSuccJson("success");
    }

    //待填列表
    @RequestMapping(value = "/writelist")
    public Object writeList(
            @RequestParam(name = "writeStatus", required = false) Integer writeStatus,
            @RequestParam(name = "required", required = false) Integer required,
            @RequestParam(name = "formName", defaultValue = "") String formName,
            @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Integer userId = authenticationHolder.getAuthenticatedUser().getId();
        PageResult<ViewForm> writeList = formService.getWriteList(writeStatus, required, formName, pageNum, pageSize, userId);
        return buildSuccJson(writeList);
    }

    //紧急待填
    @RequestMapping(value = "/writetop")
    public Object writeTop() {
        Integer userId = authenticationHolder.getAuthenticatedUser().getId();
        List<ViewForm> writeList = formService.getWriteTop(userId);
        return buildSuccJson(writeList);
    }

    //审核列表
    @RequestMapping(value = "/auditorlist")
    public Object auditorList(@RequestParam(name = "auditorStatus", required = false) Integer auditorStatus,
                              @RequestParam(name = "formName", defaultValue = "") String formName,
                              @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Integer userId = authenticationHolder.getAuthenticatedUser().getId();
        PageResult<ViewForm> auditorList = formService.getAuditorList(userId, auditorStatus, formName, pageNum, pageSize);
        return buildSuccJson(auditorList);
    }

    //审核
    @RequestMapping(value = "/auditor")
    public Object auditor(@RequestParam(name = "formId") Integer formId,
                          @RequestParam(name = "auditorStatus") Integer auditorStatus,
                          @RequestParam(name = "auditorDesc", defaultValue = "") String auditorDesc ) {
        formService.auditor(formId, auditorStatus,auditorDesc);
        return buildSuccJson("success");
    }
    
    //提交审核
    @RequestMapping(value="/submit")
    public Object submit(@RequestParam(name = "formId") Integer formId){
    	formService.submit(formId);
    	return buildSuccJson("success");
    }
    
    //撤销提价
    @RequestMapping(value="/revoke")
    public Object revoke(@RequestParam(name = "formId") Integer formId){
    	formService.revoke(formId);
    	return buildSuccJson("success");
    }
    
    //变更填写时间
    @RequestMapping(value="/changeTime")
    public Object changeTime(@RequestParam(name = "formId") Integer formId, 
    		@RequestParam(name = "beginTime") String beginTime, 
    		@RequestParam(name = "endTime") String endTime){
    	
    	formService.changeTime(formId, beginTime, endTime);
    	return buildSuccJson("success");
    }
    
    //获取表单填写人列表
    @RequestMapping("/getWriterListByCondition")
    public Object getWriterListByCondition(@RequestParam(name = "formId") Integer formId,
    		@RequestParam(name = "departmentId", required=false) Integer departmentId, 
    		@RequestParam(name = "positionId", required=false) Integer positionId, 
    		@RequestParam(name = "academyCode", required=false) Integer academyCode, 
    		@RequestParam(name = "academyName", defaultValue = "") String academyName, 
    		@RequestParam(name = "majorCode", required=false ) Integer majorCode,
    		@RequestParam(name = "majorName", defaultValue = "") String majorName,
    		@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
    	PageResult<SecUserExt> users = formWriterService.getWriterListByCondition(pageNum, pageSize, formId, departmentId, positionId, academyCode, academyName, majorCode, majorName);
    	return buildSuccJson(users);
    }
    
}