package com.cmcc.zysoft.schedule.controller;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cmcc.zysoft.schedule.common.BaseController;
import com.cmcc.zysoft.schedule.model.Week;
import com.cmcc.zysoft.schedule.service.LeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleLeaderService;
import com.cmcc.zysoft.schedule.service.ScheduleService;
import com.cmcc.zysoft.schedule.service.WeekService;
import com.cmcc.zysoft.schedule.util.WeekUtil;

/**
 * 导出用contoller
 * @author ahmobile
 *
 */
@Controller
@RequestMapping("/pc/export")
public class ExportContoller extends BaseController{
	
	private static Logger logger = LoggerFactory.getLogger(ExportContoller.class);
	
	@Resource
	private ScheduleService scheduleService;
	@Resource
	private ScheduleLeaderService scheduleLeaderService;
	@Resource
	private WeekService weekService;
	@Resource
	private LeaderService leaderService;
	/**
	 * 导出主方法
	 * @param response
	 * @param request
	 */
	@RequestMapping(value="/export.htm")
	public void excelExport(HttpServletResponse response,HttpServletRequest request){
		Week week = (Week)request.getSession().getAttribute("thisWeek");
		OutputStream os = null;
		try {
			Date mon = week.getWeekStart();
			SimpleDateFormat dateFormat = new SimpleDateFormat("M月d日");
			Date[] array = WeekUtil.getWeekDay(mon);
			String fileName = "领导日程（"+
					dateFormat.format(mon)+"--"+dateFormat.format(array[6])+"）";
			fileName  = new String(fileName.getBytes(), "iso8859-1");
			os = response.getOutputStream();
			response.reset();// 清空输出流  
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-disposition", "attachment; filename="+fileName+".xls");
			//选择模版
			String rootPath = request.getSession().getServletContext().getRealPath("/");
			String realpath  = rootPath + "//resources//excel//template.xls";
			HSSFWorkbook workbook =new HSSFWorkbook(new FileInputStream(realpath));
			createWorkbook(week,workbook);
			workbook.write(os);
			os.flush();
		} catch (Exception e) {
			logger.error("导出Excel出现错误", e, this.getClass());
			e.printStackTrace();
		}finally{
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 创建excel表，并设置表头值
	 * @param week
	 * @param workbook
	 * @throws ParseException
	 */
	public void createWorkbook(Week week,HSSFWorkbook workbook) throws ParseException{
		Date mon = week.getWeekStart();
		SimpleDateFormat dateFormat = new SimpleDateFormat("M月d日");
		SimpleDateFormat df = new SimpleDateFormat("yyyy年M月d日");
		Date[] array = WeekUtil.getWeekDay(mon);
		String fileName = "中国移动安徽公司管理层一周活动预安排（"+
				df.format(mon)+"--"+dateFormat.format(array[6])+"）";
		//读取工作表
	    HSSFSheet sheet = workbook.getSheetAt(0);
	    HSSFFont font = workbook.createFont();
	    HSSFCellStyle style = workbook.createCellStyle();
	    sheet.getRow(0).getCell(0).setCellValue(fileName);
	    sheet.getRow(1).getCell(1).setCellValue(dateFormat.format(array[0])+"\n星期一");
	    sheet.getRow(1).getCell(3).setCellValue(dateFormat.format(array[1])+"\n星期二");
	    sheet.getRow(1).getCell(5).setCellValue(dateFormat.format(array[2])+"\n星期三");
	    sheet.getRow(1).getCell(7).setCellValue(dateFormat.format(array[3])+"\n星期四");
	    sheet.getRow(1).getCell(9).setCellValue(dateFormat.format(array[4])+"\n星期五");
	    sheet.getRow(1).getCell(11).setCellValue(dateFormat.format(array[5])+"\n星期六");
	    sheet.getRow(1).getCell(13).setCellValue(dateFormat.format(array[6])+"\n星期天");
	    //获取所有领导
		List<Map<String,Object>> leaders = this.scheduleService.getAllLeaders();
		List<Map<String,Object>> schedules =  this.scheduleService.getThisWeekSchedule(week.getWeekId());
	    fillSchedule(sheet,style,font,leaders,schedules);
	}
	/**
	 * 插入领导日程数据
	 * @param sheet
	 * @param style
	 * @param font
	 * @param leaders
	 * @param schedules
	 * @throws ParseException
	 */
	private void fillSchedule(HSSFSheet sheet,HSSFCellStyle style,HSSFFont font,List<Map<String,Object>> leaders,List<Map<String,Object>> schedules) throws ParseException{
		HSSFRow row;
		HSSFCell cell = null;
		HSSFCellStyle style1 = sheet.getRow(1).getCell(1).getCellStyle();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setWrapText(true);
		style.setFont(font);
		for(int i =0;i<leaders.size();i++){
			String leaderName = leaders.get(i).get("leader_name").toString();
			String leaderId = leaders.get(i).get("leader_id").toString();
			row = sheet.createRow(3+i);
			row.setHeight((short) 1000);
			cell =row.createCell(0);
			cell.setCellStyle(style1);
			cell.setCellValue(leaderName);
			row.setRowStyle(style);
			for(int j=0;j<schedules.size();j++){
				String sLeadId=schedules.get(j).get("leader_id").toString();
				String start = schedules.get(j).get("start_time").toString();
				String startHourMin = formatHourMin(start);
				String end = schedules.get(j).get("end_time").toString();
				String title = schedules.get(j).get("title").toString();
				int column_start = getColumnlocation(start,true);
				int column_end = getColumnlocation(end,false);
				if(sLeadId.equals(leaderId)){
					cell=row.getCell(column_start);
					if(cell==null){
						cell=row.createCell(column_start);
						cell.setCellStyle(style);
						cell.setCellValue(startHourMin+"\n"+title);
					}else{
						String cellValue = cell.getStringCellValue();
						cell.setCellValue(cellValue+"\n"+startHourMin+"\n"+title);
					}
					 sheet.addMergedRegion(new CellRangeAddress(3+i,3+i,column_start,column_end));
				}
			}
		}
	}
	/**
	 * 转换日程格式为HH:mm
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private String formatHourMin(String dateStr) throws ParseException{
		 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
		 Date date = df.parse(dateStr);
		 return df1.format(date);
	}
	/**
	 * 根据事件事件获取事件所在列
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private int getColumnlocation(String dateStr,boolean isStart) throws ParseException{
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date date = df.parse(dateStr);
	    Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m= calendar.get(Calendar.MINUTE);
        int column=0;
        boolean h_flag=true;
		if(isStart){
			h_flag=h<12;
		}else{
			h_flag=h<12||(h==12&&m==0);
		}
        switch(day){
		case 2:
			column=h_flag?1:2;
			break;
		case 3:
			column=h_flag?3:4;
			break;
		case 4:
			column=h_flag?5:6;
			break;
		case 5:
			column=h_flag?7:8;
			break;
		case 6:
			column=h_flag?9:10;
			break;
		case 7:
			column=h_flag?11:12;
			break;
		case 1:
			column=h_flag?13:14;
			break;
		default:
			column=0;
        }
        return column;
	}
}
