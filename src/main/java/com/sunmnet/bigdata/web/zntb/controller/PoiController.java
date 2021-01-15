package com.sunmnet.bigdata.web.zntb.controller;

import com.sunmnet.bigdata.web.core.exception.ServiceException;
import com.sunmnet.bigdata.web.core.model.dto.PageResult;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewForm;
import com.sunmnet.bigdata.web.zntb.model.dto.ViewFormWriteStatus;
import com.sunmnet.bigdata.web.zntb.model.po.FormWidget;
import com.sunmnet.bigdata.web.zntb.model.po.FormWriteStatus;
import com.sunmnet.bigdata.web.zntb.service.FormDataService;
import com.sunmnet.bigdata.web.zntb.service.FormService;
import com.sunmnet.bigdata.web.zntb.service.FormWidgetService;
import com.sunmnet.bigdata.web.zntb.service.FormWriteStatusService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/poi")
public class PoiController {

    @Autowired
    private FormWriteStatusService formWriteStatusService;
    @Autowired
    private FormDataService formDataService;
    @Autowired
    private FormService formService;
    @Autowired
    private FormWidgetService formWidgetService;

    @SuppressWarnings({ "resource", "deprecation", "unchecked" })
	@ResponseBody
    @RequestMapping("/createExcel")
    public void createExcel(@RequestParam(name = "formId") Integer formId,
                            HttpServletRequest req,
                            HttpServletResponse resp) throws Exception {
        ViewForm info = formService.info(formId);

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/octet-stream;charset=utf-8");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = format.format(new Date());

        String filedisplay = info.getFormName() + ".xls";
        //防止文件名含有中文乱码
        filedisplay = new String(filedisplay.getBytes("UTF-8"), "ISO8859-1");
        resp.setHeader("Content-Disposition", "attachment;filename=" + filedisplay);

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(strDate);
        // 第三步，在sheet中添加表头第0行
        HSSFRow hssfRow = hssfSheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//设置一种居中的格式

        HSSFCell cell = hssfRow.createCell(0);


//
//        String json = req.getParameter("json");
//
//        JSONObject jsonObject = JSONObject.parseObject(json);
//        JSONArray topJsonArray = jsonObject.getJSONArray("topJsonList");
//        JSONArray datoJsonArray = jsonObject.getJSONArray("datoJsonList");
//        if (topJsonArray.size() > 0) {
//            for (int i = 0; i < topJsonArray.size(); i++) {
//                JSONObject obj = topJsonArray.getJSONObject(i);
//                cell.setCellValue(obj.getString("name"));
//                cell.setCellStyle(style);
//                cell = hssfRow.createCell(i + 1);
//            }
//        } else {
//            throw new ServiceException("EXCEL头信息不允许为空");
//        }
//
//        if (datoJsonArray.size() <= 0) {
//            throw new ServiceException("EXCEL的body内信息不允许为空");
//        }
//        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
//        for (int i = 0; i < datoJsonArray.size(); i++) {
//            hssfRow = hssfSheet.createRow(i + 1);
//            for (int j = 0; j < topJsonArray.size(); j++) {
//                hssfRow.createCell(j).setCellValue(datoJsonArray.getJSONObject(i).getString("obj" + j));
//            }
//        }

        Integer auditorType = FormWriteStatus.AUDITORSTATUS.AUDITORY.getValue();
        PageResult<ViewFormWriteStatus> byFormId = formWriteStatusService.getWriterUserList(auditorType,null, formId, "", null, null, null, 1, 1,null);
        Map<String, Object> o = formDataService.dataDetail(formId, null, auditorType, null,null, null, null, null, 1, byFormId.getTotal().intValue());
        List<String> title = (List<String>) o.get("title");
        PageResult<Map<String, Object>> data = (PageResult<Map<String, Object>>) o.get("data");
        List<Map<String, Object>> rows = data.getRows();

        if (CollectionUtils.isEmpty(title)) {
            throw new ServiceException("EXCEL头信息不允许为空");
        }
        if (CollectionUtils.isEmpty(rows)) {
            throw new ServiceException("EXCEL的body内信息不允许为空");
        }
        for (int i = 0; i < title.size(); i++) {
            cell.setCellValue(title.get(i));
            cell.setCellStyle(style);
            cell = hssfRow.createCell(i + 1);
        }

        for (int i = 0; i < rows.size(); i++) {
            hssfRow = hssfSheet.createRow(i + 1);
            for (int j = 0; j < title.size(); j++) {
                String s = title.get(j);
                Map<String, Object> objectMap = rows.get(i);
                String value = objectMap.get(s) == null ? "" : objectMap.get(s).toString();
                hssfRow.createCell(j).setCellValue(value);
            }
        }

        Map<String, Object> objectMap = formDataService.dataTotal(info);
        if (objectMap != null) {
            int lastRowNum = hssfSheet.getLastRowNum();
            hssfRow = hssfSheet.createRow(lastRowNum + 1);
            hssfRow.createCell(0).setCellValue("合计:");
            for (int j = 1; j < title.size(); j++) {
                String s = title.get(j);
                String value = objectMap.get(s) == null ? "" : objectMap.get(s).toString();
                hssfRow.createCell(j).setCellValue(value);
            }
        }


        OutputStream stream = resp.getOutputStream();
        hssfWorkbook.write(stream);
        stream.close();
    }
    
    @SuppressWarnings({ "resource", "deprecation" })
	@RequestMapping("/template")
    public void template(@RequestParam(name = "formId") Integer formId,
            HttpServletRequest req,
            HttpServletResponse resp) throws Exception {
    	ViewForm info = formService.info(formId);

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/octet-stream;charset=utf-8");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = format.format(new Date());

        String filedisplay = info.getFormName() + ".xls";
        //防止文件名含有中文乱码
        filedisplay = new String(filedisplay.getBytes("UTF-8"), "ISO8859-1");
        resp.setHeader("Content-Disposition", "attachment;filename=" + filedisplay);

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(strDate);
        // 第三步，在sheet中添加表头第0行
        HSSFRow hssfRow = hssfSheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//设置一种居中的格式
        HSSFCell cell = hssfRow.createCell(0);
        
        //标题栏
        List<FormWidget> formWidgetList = formWidgetService.getListByFormId(formId);
        List<String> title = formWidgetList.stream().map(FormWidget::getLabelName).collect(Collectors.toList());
        List<String> formatList = formWidgetList.stream().map(FormWidget::getDateFormat).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(title)) {
            throw new ServiceException("EXCEL头信息不允许为空");
        }
        for (int i = 0; i < title.size(); i++) {
            cell.setCellValue(title.get(i));
            cell.setCellStyle(style);
            cell = hssfRow.createCell(i + 1);
        }

        //创建一千行
        HSSFDataFormat dataFormat= hssfWorkbook.createDataFormat();
        HSSFCellStyle style2 = hssfWorkbook.createCellStyle();
        style2.setDataFormat(dataFormat.getFormat("@"));
        for (int i = 0; i < 1000; i++) {
            HSSFRow hssfSheetRow = hssfSheet.createRow(i + 1);
            for (int j = 0; j < title.size(); j++) {
                HSSFCell hssfCell = hssfSheetRow.createCell(j);
                hssfCell.setCellValue("");
                hssfCell.setCellStyle(style2);
            }
        }
        OutputStream stream = resp.getOutputStream();
        hssfWorkbook.write(stream);
        stream.close();
    }
    
}
