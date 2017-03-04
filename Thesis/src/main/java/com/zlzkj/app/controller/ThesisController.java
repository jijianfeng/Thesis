package com.zlzkj.app.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.zlzkj.app.model.About;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Diy;
import com.zlzkj.app.model.Major;
import com.zlzkj.app.model.Result;
import com.zlzkj.app.model.StatusProcess;
import com.zlzkj.app.model.Thesis;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.service.AboutService;
import com.zlzkj.app.service.CollegeService;
import com.zlzkj.app.service.DiyService;
import com.zlzkj.app.service.MajorService;
import com.zlzkj.app.service.ResultService;
import com.zlzkj.app.service.StatusProcessService;
import com.zlzkj.app.service.ThesisService;
import com.zlzkj.app.service.UniversityService;
import com.zlzkj.app.service.UserService;
import com.zlzkj.app.util.CreatePdf;
import com.zlzkj.app.util.ExcelTransport;
import com.zlzkj.app.util.FileOperate;
import com.zlzkj.app.util.FileToZip;
//import com.zlzkj.app.service.UserService;
import com.zlzkj.app.util.StringUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.app.util.UploadUtils;
import com.zlzkj.app.util.ZipFile;
import com.zlzkj.app.util.sendsms;
import com.zlzkj.core.base.BaseController;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.util.Fn;

/**
 * 论文管理
 * @author jjf
 *
 */
@Controller
@RequestMapping(value={"thesis"})
public class ThesisController extends BaseController{
//	
	@Autowired
	private ThesisService thesisService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private StatusProcessService statusProcessService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UniversityService universityService;
	
	@Autowired
	private CollegeService collegeService;
	
	@Autowired
	private MajorService majorService;
	
	@Autowired
	private AboutService aboutService;
	
	@Autowired
	private DiyService diyService;
//	diyService
	/**
	 * 一键送审论文列表
	 */
	@RequestMapping(value={"list"})
	public String list(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
			//判断条件约束
			String collegeId = request.getParameter("collegeId");
			String majorId = request.getParameter("majorId");
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			String status = request.getParameter("status");
			where.put("is_shortcut", 1);
			if((!StringUtil.isEmpty(collegeId))&&(!collegeId.equals("0"))){
				where.put("college_id",collegeId);
			}
//			System.out.println(majorId+"哈哈哈哈哈");
			if((!StringUtil.isEmpty(majorId)&&(!majorId.equals("0")&&(!majorId.equals("undefined"))))){
				//where.put("major_one",majorId);
			}
			else{
				majorId = null;
			}
//			System.out.println(majorId+"哈哈哈哈哈");
			if(!StringUtil.isEmpty(thesisCode)){
				where.put("thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(!StringUtil.isEmpty(thesisName)){
				where.put("thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			//判断身份
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			if(user.getUserType()==0){
				//研究生部 
				where.put("send_type",new String[]{"!=","2"});
				where.put("university_id", user.getUniversityId());
			}
			else if(user.getUserType()==1){
				//学院
				where.put("send_type",new String[]{"!=","1"});
				where.put("university_id", user.getUniversityId());
				where.put("college_id", user.getCollegeId());
			}
			
			if(!StringUtil.isEmpty(timeStart)){
//				timeStart=null;
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("upload_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
//				timeEnd=null;
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("upload_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			if(StringUtil.isEmpty(status)||status.equals("0")){
				status = null;
			}
			else{
				if(status.equals("1")){
					where.put("Thesis.send_number", new String[]{"!=","0"});
				}
				else{
					where.put("Thesis.send_number", "0");
				}
			}
			where.put("return_time", "0");
			return ajaxReturn(response, thesisService.getUIGridData(1,where,UIUtils.getPageParams(request),majorId));
		}
		else{
			return "thesis/list";
		}
	}
	
	/**
	 * 一键送审论文列表
	 */
	@RequestMapping(value={"list_had_shortcut"})
	public String listHadShortcut(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
			//判断条件约束
			String collegeId = request.getParameter("collegeId");
			String majorId = request.getParameter("majorId");
			//System.out.println("噢打啊"+collegeId+"::"+majorId);
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			String status = request.getParameter("status");
			where.put("is_shortcut", 1);
			if((!StringUtil.isEmpty(collegeId))&&(!collegeId.equals("0"))){
				where.put("college_id",collegeId);
			}
//			if((!StringUtil.isEmpty(majorId)&&(!majorId.equals("0")&&(!majorId.equals("undefined"))))){
//				where.put("major_id",majorId);
//			}
			if((!StringUtil.isEmpty(majorId)&&(!majorId.equals("0")&&(!majorId.equals("undefined"))))){
				//where.put("major_one",majorId);
			}
			else{
				majorId = null;
			}
			if(!StringUtil.isEmpty(thesisCode)){
				where.put("thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(!StringUtil.isEmpty(thesisName)){
				where.put("thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			//判断身份
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			if(user.getUserType()==0){
				//研究生部 
				where.put("send_type",new String[]{"!=","2"});
				where.put("university_id", user.getUniversityId());
			}
			else if(user.getUserType()==1){
				//学院
				where.put("send_type",new String[]{"!=","1"});
				where.put("university_id", user.getUniversityId());
				where.put("college_id", user.getCollegeId());
			}
			
			if(!StringUtil.isEmpty(timeStart)){
//				timeStart=null;
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("upload_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
//				timeEnd=null;
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("upload_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			if(StringUtil.isEmpty(status)||status.equals("0")){
				status = null;
			}
			else{
				if(status.equals("1")){
					where.put("Thesis.send_number", new String[]{"!=","0"});
				}
				else{
					where.put("Thesis.send_number", "0");
				}
			}
			where.put("return_time",new String[]{"!=","0"});
			return ajaxReturn(response, thesisService.getUIGridData(2,where,UIUtils.getPageParams(request),majorId));
		}
		else{
			return "thesis/list_had_shortcut";
		}
	}
	
	/**
	 * 点对点审论文列表
	 */
	@RequestMapping(value={"list_oto"})
	public String listOto(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
			//判断条件约束
			String collegeId = request.getParameter("collegeId");
			String majorId = request.getParameter("majorId");
			//System.out.println("噢打啊"+collegeId+"::"+majorId);
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			String status = request.getParameter("status");
			where.put("is_shortcut", 0);
			if((!StringUtil.isEmpty(collegeId))&&(!collegeId.equals("0"))){
				where.put("college_id",collegeId);
			}
//			if((!StringUtil.isEmpty(majorId)&&(!majorId.equals("0")&&(!majorId.equals("undefined"))))){
//				where.put("major_id",majorId);
//			}
			if((!StringUtil.isEmpty(majorId)&&(!majorId.equals("0")&&(!majorId.equals("undefined"))))){
				//where.put("major_one",majorId);
			}
			else{
				majorId = null;
			}
			if(!StringUtil.isEmpty(thesisCode)){
				where.put("thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(!StringUtil.isEmpty(thesisName)){
				where.put("thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			//判断身份
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			if(user.getUserType()==0){
				//研究生部 
				where.put("send_type",new String[]{"!=","2"});
				where.put("university_id", user.getUniversityId());
			}
			else if(user.getUserType()==1){
				//学院
				where.put("send_type",new String[]{"!=","1"});
				where.put("university_id", user.getUniversityId());
				where.put("college_id", user.getCollegeId());
			}
			
			if(!StringUtil.isEmpty(timeStart)){
//				timeStart=null;
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("upload_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
//				timeEnd=null;
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("upload_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			if(StringUtil.isEmpty(status)||status.equals("0")){
				status = null;
			}
			else{
				if(status.equals("1")){
					where.put("Thesis.send_number", new String[]{"!=","0"});
				}
				else{
					where.put("Thesis.send_number", "0");
				}
			}
			where.put("Thesis.is_shortcut", "0");
			return ajaxReturn(response, thesisService.getUIGridData(3,where,UIUtils.getPageParams(request),majorId));
		}
		else{
			return "thesis/list_oto";
		}
	}
	
//	@RequestMapping(value={"list_add"},method=RequestMethod.GET)
//	public String listAddGet(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
////		System.out.println("准备上传");
//		return "thesis/list_add";
//	}
//	/**
//	 * 单个上传
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value={"list_add"},method=RequestMethod.POST)
//	public String listAdd(Model model,HttpServletRequest request,HttpServletResponse response,
//			@RequestParam("file") MultipartFile file) throws Exception {
//			//论文文件上传
////			System.out.println("开始上传了");
////			return "thesis/list";
//			Map<String, Object> zip = new HashMap<String, Object>();		
//			String zipName = "";
//			Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(file);
//			//判断
////			System.out.println(((Integer)zipInfo.get("status")>0)+"判断");
//			if(((Integer)zipInfo.get("status")>0)){ //上传完成		
//				//网络地址
//				zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
//				//物理地址
//				String osName = System.getProperty("os.name").toLowerCase();
////				System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
//				if(osName.indexOf("windows")!=-1){
//					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
//				}
//				else{
//					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
//				}
////				System.out.println(zipName+"完整的名字");
////				System.out.println(UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"保存路径");
////				System.out.println(zipName.substring(0,zipName.length()-4));
//				Thesis thesis = new Thesis();
//				String thesisName = request.getParameter("thesisName");
//				thesis.setThesisName(thesisName);
//				About about  = aboutService.findById(1);
//				thesis.setThesisCode(about.getSendLetter()+request.getParameter("thesisCode"));
//				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
//				thesis.setAreaId(user.getAreaId());
//				thesis.setUniversityId(user.getUniversityId());
//				thesis.setCollegeId(Integer.valueOf(request.getParameter("CollegeIdWhere")));
//				thesis.setMajorId(Integer.valueOf(request.getParameter("MajorIdWhere")));
//				thesis.setUploadTime(Fn.time());
//				thesis.setThesisType(1+Integer.valueOf(request.getParameter("thesisType")));
//				thesis.setThesisUrl(zipName);
//				if(user.getUserType()==0){
//					//研究生部
//					thesis.setSendType(1);
//				}
//				else if(user.getUserType()==1){
//					//学院部
//					thesis.setSendType(2);
//				}
//				thesisService.save(thesis);
//				zip.put("url", zipName);
//				zip.put("alt", "");
//				return ajaxReturn(response,zip,"上传成功",1);
//			}else{ //上传出错
//				return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
//			}
//	}
	/**
	 * 单个论文的编辑
	 */
	@RequestMapping(value={"list_edit"},method=RequestMethod.GET)
	public String listEditGet(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Thesis thesis = thesisService.findById(Integer.valueOf(id));
		if(thesis.getSendNumber()!=0){
			return ajaxReturn(response, null,"该论文已经送审，无法修改",0);
		}
		model.addAttribute("thesis", thesis);
		return "thesis/list_edit";
	}
	/**
	 * 单个论文的编辑
	 */
	@RequestMapping(value={"list_edit"},method=RequestMethod.POST)
	public String listEdit(Model model,HttpServletRequest request,HttpServletResponse response,
			@RequestParam("file") MultipartFile file) throws Exception {
			//论文文件上传
//			System.out.println("开始上传了");
//			return "thesis/list";
			Map<String, Object> zip = new HashMap<String, Object>();		
			String zipName = "";
			
			Thesis thesis = thesisService.findById(Integer.valueOf(request.getParameter("id")));
			String thesisName = request.getParameter("thesisName");
//			thesisName = new String(thesisName.getBytes("ISO-8859-1"), "UTF-8");
			thesis.setThesisName(thesisName);
			thesis.setThesisCode(request.getParameter("thesisCode"));
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
//			thesis.setAreaId(user.getAreaId());
			thesis.setUniversityId(user.getUniversityId());
			thesis.setCollegeId(Integer.valueOf(request.getParameter("CollegeIdWhere")));
			//thesis.setMajorId(Integer.valueOf(request.getParameter("MajorIdWhere")));
			thesis.setUploadTime(Fn.time());
			thesis.setThesisType(Integer.valueOf(request.getParameter("thesisType")));
			if(user.getUserType()==0){
				//研究生部
				thesis.setSendType(1);
			}
			else if(user.getUserType()==1){
				//学院部
				thesis.setSendType(2);
			}
			if(!file.isEmpty()){
				Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(file);
				//判断
//				System.out.println(((Integer)zipInfo.get("status")>0)+"判断");
				if(((Integer)zipInfo.get("status")>0)){ //上传完成		
					//网络地址
					zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
					//物理地址
					String osName = System.getProperty("os.name").toLowerCase();
//					System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
					if(osName.indexOf("windows")!=-1){
						zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
					}
					else{
						zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
					}
//					System.out.println(zipName+"完整的名字");
//					System.out.println(UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"保存路径");
//					System.out.println(zipName.substring(0,zipName.length()-4));
					thesis.setThesisUrl(zipName);
					thesisService.update(thesis);
					zip.put("url", zipName);
					zip.put("alt", "");
					return ajaxReturn(response,zip,"上传成功",1);
				}else{ //上传出错
					return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
				}
			}
			else{
				thesisService.update(thesis);
				zip.put("url", zipName);
				zip.put("alt", "");
				return ajaxReturn(response,zip,"修改成功",1);
			}
	}
	/**
	 * 论文的删除
	 */
	@RequestMapping(value={"list_delete"})
	public String listDelete(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String ids = request.getParameter("id");
//			System.out.println("要删除的id"+id);
			String id[] = ids.split(",");
			for(int j=0;j<id.length;j++){
				Thesis thesis = thesisService.findById(Integer.valueOf(id[j]));
				if(thesis.getSendNumber()!=0){
					return ajaxReturn(response, null,"该论文已经送审，无法删除",0);
				}
				int i=0;
				if(FileOperate.deleteFile(thesis.getThesisUrl())){
					i = thesisService.delete(thesis.getId());
				}
				//int i = thesisService.delete(thesis.getId());
				if(i==1){
//					return ajaxReturn(response, null,"删除成功",1);
				}
				else{
					return ajaxReturn(response, null,"删除失败",0);
				}
			}
			return ajaxReturn(response, null,"删除成功",1);
		}
		else{
			return "thesis/list";
		}
	}
	/**
	 * 论文详情
	 */
	@RequestMapping(value={"list_detail"})
	public String listDetail(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Thesis thesis = thesisService.findById(Integer.valueOf(id));
		model.addAttribute("date",Fn.date(thesis.getUploadTime(), "yyyy-MM-dd"));
		model.addAttribute("thesis", thesis);
		return "thesis/list_detail";
	}
	
	/**
	 * 待派论文
	 */
	@RequestMapping(value={"list_unreceive"})
	public String listUnreceive(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
//			String areaId =request.getParameter("areaId");
			String universityId = request.getParameter("universityId");	
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			Map<String, Object> where = new HashMap<String,Object>();
			//同一处理数据
//			if(StringUtil.isEmpty(areaId)||areaId.equals("0")){
//				areaId = null;
//			}
			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
				universityId = null;
			}
			else{
				where.put("Thesis.university_id", universityId);
			}
			if(StringUtil.isEmpty(thesisCode)||thesisCode.equals("")){
				thesisCode = null;
			}
			else{
				where.put("Thesis.thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(StringUtil.isEmpty(thesisName)||thesisName.equals("")){
				thesisName = null;
			}
			else{
				where.put("Thesis.thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			if(!StringUtil.isEmpty(timeStart)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("Result.return_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("Result.return_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			if(user.getUserType()==0){
				//研究生部
				where.put("Result.university_id", user.getUniversityId());
				where.put("Result.status","4");
			}
			else if(user.getUserType()==1){
				//学院
				where.put("Result.university_id", user.getUniversityId());
				where.put("Result.college_id", user.getCollegeId());
				where.put("Result.status","5");
			}
			else if(user.getUserType()==2){
				//老师只能看到被指派的
				where.put("Result.teacher_id", user.getId());
				where.put("Result.status","6");
			}
			return ajaxReturn(response,thesisService.getUnReceiveIGridData(where,UIUtils.getPageParams(request)));
		}
		else{
			return "thesis/list_unreceive";
		}
	}
	
	/**
	 * 待派一键送审论文(管理员)
	 */
	@RequestMapping(value={"list_unreceive_admin"})
	public String listUnreceiveAdmin(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String universityId = request.getParameter("universityId");	
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			//同一处理数据
			Map<String, Object> where = new HashMap<String,Object>();
			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
				universityId = null;
			}
			else{
				where.put("Thesis.university_id", universityId);
			}
			if(StringUtil.isEmpty(thesisCode)||thesisCode.equals("")){
				thesisCode = null;
			}
			else{
				where.put("Thesis.thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(StringUtil.isEmpty(thesisName)||thesisName.equals("")){
				thesisName = null;
			}
			else{
				where.put("Thesis.thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			if(!StringUtil.isEmpty(timeStart)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("Thesis.return_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("Thesis.return_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
//			String id = request.getSession().getAttribute("userId").toString();
//			User user = userService.findById(Integer.valueOf(id));
			where.put("is_shortcut", "1");
			String type=request.getParameter("type");
			if(StringUtil.isEmpty(type)){
				where.put("Thesis.is_finish",new String[]{"!=","1"});
			}
			else if(Integer.valueOf(type)==1){
				where.put("Thesis.is_finish","1");
			}
			where.put("Thesis.return_time",new String[]{"!=","0"});
			return ajaxReturn(response,thesisService.getUnReceiveAdminGridData(where,UIUtils.getPageParams(request)));
		}
		else{
			return "thesis/list_unreceive_admin";
		}
	}
	
	/**
	 * 一键论文已经派送论文
	 */
	@RequestMapping(value={"list_receive"})
	public String listReceive(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
//			String areaId =request.getParameter("areaId");
			String universityId = request.getParameter("universityId");
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			String status = request.getParameter("status");
			//同一处理数据
//			if(StringUtil.isEmpty(areaId)||areaId.equals("0")){
//				areaId = null;
//			}
			if(StringUtil.isEmpty(status)||status.equals("0")){
				status = null;
			}
			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
				universityId = null;
			}
			if(StringUtil.isEmpty(thesisCode)||thesisCode.equals("")){
				thesisCode = null;
			}
			if(StringUtil.isEmpty(thesisName)||thesisName.equals("")){
				thesisName = null;
			}
			if(!StringUtil.isEmpty(timeStart)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
			}
			else{
				timeEnd = null;
			}
//			System.out.println(areaId+":"+universityId+":"+thesisName+":"+thesisCode+":"+timeStart+":"+timeEnd);
			Map<String, Object> where = new HashMap<String,Object>();
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			where.put("StatusProcess.user_id", id);
			if(user.getUserType()==0){
				//研究生部
				where.put("StatusProcess.last_status", 4);	
			}
			else if(user.getUserType()==1){
				//学院
				where.put("StatusProcess.last_status", 5);
			}
			else if(user.getUserType()==2){
				//老师
			}
			else{
				//管理员
				where.put("Thesis.is_finish", "1");
				return ajaxReturn(response, thesisService.getReceiveAdminGridData(where,UIUtils.getPageParams(request),universityId,thesisCode,thesisName,timeStart,timeEnd,status));
			}
			return ajaxReturn(response, thesisService.getReceiveAdminGridData(where,UIUtils.getPageParams(request),universityId,thesisCode,thesisName,timeStart,timeEnd,status));
			//return ajaxReturn(response, thesisService.getReceiveGridData(where,UIUtils.getPageParams(request),universityId,thesisCode,thesisName,timeStart,timeEnd,status));
		}
		else{
			return "thesis/list_receive";
		}
	}
	/**
	 * 公共平台管理员
	 */
	@RequestMapping(value={"list_public_admin"})
	public String listPublicAdmin(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
//			String areaId =request.getParameter("areaId");
			String universityId = request.getParameter("universityId");
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			//同一处理数据
//			if(StringUtil.isEmpty(areaId)||areaId.equals("0")){
//				areaId = null;
//			}
			Map<String, Object> where = new HashMap<String,Object>();
			if(StringUtil.isEmpty(thesisCode)||thesisCode.equals("")){
				thesisCode = null;
			}
			else{
				where.put("Thesis.thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(StringUtil.isEmpty(thesisName)||thesisName.equals("")){
				thesisName = null;
			}
			else{
				where.put("Thesis.thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			if(!StringUtil.isEmpty(timeStart)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("Thesis.upload_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("Thesis.upload_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			//System.out.println(areaId+":"+universityId+":"+thesisName+":"+thesisCode+":"+timeStart+":"+timeEnd);
			where.put("Result.status","3");
			//不显示本学校的论文
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			int univeristiyExcept =user.getUniversityId();
			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
				universityId = null;
				where.put("Thesis.university_id",new String[]{"!=",String.valueOf(univeristiyExcept)});
			}
			else{
				where.put("Thesis.university_id", universityId);
			}
			//不显示不符合论文要求的论文（985,211筛选）	
			int isNine = 0;
			int isTwo = 0;
			if(univeristiyExcept==0){
				//管理员
				isNine = 1;
				isTwo =1 ;
			}
			else{
				University university = universityService.findById(univeristiyExcept);
				isNine = university.getIsNine();
				isTwo = university.getIsTwo();
			}
			if(isNine==0){
				where.put("is_nine", isNine);
			}
			if(isTwo ==0){
				where.put("is_nine", isTwo);
			}
			return ajaxReturn(response, thesisService.getPublicGridData(where,UIUtils.getPageParams(request)));
		}else{
			return "thesis/list_public_admin";
		}
	}
	
	/**
	 * 公共平台论文列表
	 */
	@RequestMapping(value={"list_public"})
	public String listPublic(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
//			String areaId =request.getParameter("areaId");
			String universityId = request.getParameter("universityId");
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			//同一处理数据
//			if(StringUtil.isEmpty(areaId)||areaId.equals("0")){
//				areaId = null;
//			}
			Map<String, Object> where = new HashMap<String,Object>();
			if(StringUtil.isEmpty(thesisCode)||thesisCode.equals("")){
				thesisCode = null;
			}
			else{
				where.put("Thesis.thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(StringUtil.isEmpty(thesisName)||thesisName.equals("")){
				thesisName = null;
			}
			else{
				where.put("Thesis.thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			if(!StringUtil.isEmpty(timeStart)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("Thesis.upload_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("Thesis.upload_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			//System.out.println(areaId+":"+universityId+":"+thesisName+":"+thesisCode+":"+timeStart+":"+timeEnd);
			where.put("Result.status","3");
			//不显示本学校的论文
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			int univeristiyExcept =user.getUniversityId();
			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
				universityId = null;
				where.put("Thesis.university_id",new String[]{"!=",String.valueOf(univeristiyExcept)});
			}
			else{
				where.put("Thesis.university_id", universityId);
			}
			//不显示不符合论文要求的论文（985,211筛选）	
			int isNine = 0;
			int isTwo = 0;
			if(univeristiyExcept==0){
				//管理员
				isNine = 1;
				isTwo =1 ;
			}
			else{
				University university = universityService.findById(univeristiyExcept);
				isNine = university.getIsNine();
				isTwo = university.getIsTwo();
			}
			if(isNine==0){
				where.put("is_nine", isNine);
			}
			if(isTwo ==0){
				where.put("is_nine", isTwo);
			}
			return ajaxReturn(response, thesisService.getPublicGridData(where,UIUtils.getPageParams(request)));
		}
		else{
			return "thesis/list_public";
		}
	}
	
	/**
	 * 论文上传
	 */
	@RequestMapping(value={"list_upload"},method=RequestMethod.GET)
	public String listUploadget(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		return "thesis/list_upload";
	}
	@RequestMapping(value={"list_upload_oto"},method=RequestMethod.GET)
	public String listUploadOtoget(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		return "thesis/list_upload_oto";
	}
	/**
	 * 一键送审论文上传
	 */
	@RequestMapping(value={"list_upload"},method=RequestMethod.POST)
	public String listUpload(Model model,HttpServletRequest request,HttpServletResponse response,
			@RequestParam("excel") MultipartFile excelFile ,@RequestParam("zip") MultipartFile zipFile) throws Exception {
		//excel上传
		Map<String, Object> excel = new HashMap<String, Object>();
		String excelName = "";
		Map<String,Object> excelInfo = UploadUtils.saveMultipartFile(excelFile);
		
		//zip上传
		Map<String, Object> zip = new HashMap<String, Object>();
		String zipName = "";
		Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(zipFile);
		//判断
		if((Integer)excelInfo.get("status")>0&&(Integer)zipInfo.get("status")>0){ //上传完成
			//网络地址
			excelName = UploadUtils.parseFileUrl(excelInfo.get("saveName").toString());
			//物理地址
			String osName = System.getProperty("os.name").toLowerCase();
//			System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
			if(osName.indexOf("windows")!=-1){
				excelName = UploadUtils.getFullSavePath(excelInfo.get("saveName").toString())+"\\"+excelInfo.get("saveName").toString();
			}
			else{
				excelName = UploadUtils.getFullSavePath(excelInfo.get("saveName").toString())+"/"+excelInfo.get("saveName").toString();
			}
			//网络地址
			zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
			//物理地址
//			String osName = System.getProperty("os.name").toLowerCase();
//			System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
			if(osName.indexOf("windows")!=-1){
				zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
			}
			else{
				zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
			}
//			System.out.println(zipName+"完整的名字");
//			System.out.println(UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"保存路径");
//			System.out.println(zipName.substring(0,zipName.length()-4));
		}else{ //上传出错
			return ajaxReturn(response,null,excelInfo.get("errorMsg").toString(),0);
		}
		excel.put("url", excelName);
		List<Object> list = ExcelTransport.readExcel(excelName, 0,"collegeName,stuNumber,stuName,teacherName,"
				+ "majorOne,majorTwo,thesisName,englishTitle"
				+ ",researchDirection,keyWords,thesisType,studyType,remark,fileType");
		excel.put("alt", "");	
		//解压zip
		 List ziplist= ZipFile.getZiplist(zipName,zipName.substring(0,zipName.length()-4));
		//for(Object object:list ){
		 int error = 0;
		 for(int i=0;i<list.size();i++){
			Object object = list.get(i);
			Map th =(Map) object;
			Thesis thesis = new Thesis();
			thesis.setStuNumber(th.get("stuNumber").toString());
			thesis.setStuName(th.get("stuName").toString());
			thesis.setTeacherName(th.get("teacherName").toString());
			thesis.setThesisName(th.get("thesisName").toString());
			//System.out.println(Integer.valueOf(request.getParameter("DiyCom"))+"ddddddddeeeeeeeeeebbbbbbbgggggggg");
			if(request.getParameter("DiyCom")==null||request.getParameter("DiyCom").toString().equals("")||request.getParameter("DiyCom").toString().equals("0")){
				thesis.setDiyId(0);
			}
			else{
				thesis.setDiyId(Integer.valueOf(request.getParameter("DiyCom").toString()));
			}
			int a = 0;
			if(th.get("majorOne").toString().length()==3){
				a = majorService.findMajorIdByCode("0"+th.get("majorOne").toString());
			}
			else{
				a = majorService.findMajorIdByCode(th.get("majorOne").toString());
			}
			thesis.setMajorOne(a);
			int b =0;
			if(th.get("majorTwo").toString().length()==5){
				b = majorService.findMajorIdByCode("0"+th.get("majorTwo").toString());
			}
			else{
				b = majorService.findMajorIdByCode(th.get("majorTwo").toString());
			}
			if(a==0||b==0){
				error++;
				continue;
			}
			thesis.setMajorTwo(b);
			thesis.setEnglishTitle(th.get("englishTitle").toString());
			thesis.setResearchDirection(th.get("researchDirection").toString());
			thesis.setKeyWords(th.get("keyWords").toString());
			thesis.setStudyType(th.get("studyType").toString());
			thesis.setRemark("上传备注:"+th.get("remark").toString());
			About about  = aboutService.findById(1);
			thesis.setThesisCode(about.getSendLetter()+th.get("stuNumber").toString());
			thesis.setFileType(th.get("fileType").toString());
//			thesis.setThesisUrl(th.get("thesisUrl").toString());
			if(th.get("thesisType").equals("学硕")){
				thesis.setThesisType(1);
			}
			else if(th.get("thesisType").equals("专硕")){
				thesis.setThesisType(2);
			}
			else if(th.get("thesisType").equals("博士")){
				thesis.setThesisType(3);
			}
			else {
				error++;
				continue;
			}
			thesis.setFileType(th.get("fileType").toString());
			//thesis.setThesisType(Integer.valueOf(th.get("thesisType").toString()));
			thesis.setUploadTime(Fn.time());
			thesis.setIsShortcut(1);
			//添加文件验证
			String osName = System.getProperty("os.name").toLowerCase();
//			System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
			if(osName.indexOf("windows")!=-1){
				thesis.setThesisUrl(UploadUtils.getFullSavePath(excelInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString().substring(0,zipInfo.get("saveName").toString().length()-4)+"\\"+th.get("stuNumber")+"."+thesis.getFileType());
			}
			else{
				thesis.setThesisUrl(UploadUtils.getFullSavePath(excelInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString().substring(0,zipInfo.get("saveName").toString().length()-4)+"/"+th.get("stuNumber")+"."+thesis.getFileType());
			}
			File thesisfile = new File(thesis.getThesisUrl());
			System.out.println(thesisfile.isFile()+"文件是否存在");
			if(!thesisfile.isFile()){
				error++;
				continue;
			}
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
//			thesis.setAreaId(user.getAreaId());
			thesis.setUniversityId(user.getUniversityId());
			thesis.setSendNumber(0);
			if(th.get("collegeName")!=null&&(!th.get("collegeName").toString().equals("0"))&&(!th.get("collegeName").toString().equals(""))){
				int collegeId = collegeService.findCollegeByName(user.getUniversityId(), th.get("collegeName").toString());
				if(collegeId==0){
					error++;
					continue;
				}
				thesis.setCollegeId(Integer.valueOf(collegeId));
			}
			if(user.getUserType()==0){
				//研究生部
				thesis.setSendType(1);
			}
			else if(user.getUserType()==1){
				//学院部
				thesis.setSendType(2);
			}
			thesis.setUploadPeople(user.getId());
			thesisService.save(thesis);
		}
		if(error!=0){
			return ajaxReturn(response,excel,"有"+error+"篇论文上传失败，其余成功，请检查！",0);
		}
		else{
			return ajaxReturn(response,excel,"评审论文全部上传成功",1);
		}
	}
	
	/**
	 * 点对点论文上传
	 */
	@RequestMapping(value={"list_upload_oto"},method=RequestMethod.POST)
	public String listUploadOto(Model model,HttpServletRequest request,HttpServletResponse response,
			@RequestParam("excel") MultipartFile excelFile ,@RequestParam("zip") MultipartFile zipFile) throws Exception {
		//excel上传
				Map<String, Object> excel = new HashMap<String, Object>();
				String excelName = "";
				Map<String,Object> excelInfo = UploadUtils.saveMultipartFile(excelFile);
				
				//zip上传
				Map<String, Object> zip = new HashMap<String, Object>();
				String zipName = "";
				Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(zipFile);
				
				//判断
				if((Integer)excelInfo.get("status")>0&&(Integer)zipInfo.get("status")>0){ //上传完成
					//网络地址
					excelName = UploadUtils.parseFileUrl(excelInfo.get("saveName").toString());
					//物理地址
					String osName = System.getProperty("os.name").toLowerCase();
//					System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
					if(osName.indexOf("windows")!=-1){
						excelName = UploadUtils.getFullSavePath(excelInfo.get("saveName").toString())+"\\"+excelInfo.get("saveName").toString();
					}
					else{
						excelName = UploadUtils.getFullSavePath(excelInfo.get("saveName").toString())+"/"+excelInfo.get("saveName").toString();
					}
					
					//网络地址
					zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
					//物理地址
//					String osName = System.getProperty("os.name").toLowerCase();
//					System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
					if(osName.indexOf("windows")!=-1){
						zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
					}
					else{
						zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
					}
//					System.out.println(zipName+"完整的名字");
//					System.out.println(UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"保存路径");
//					System.out.println(zipName.substring(0,zipName.length()-4));
				}else{ //上传出错
					return ajaxReturn(response,null,excelInfo.get("errorMsg").toString(),0);
				}
				excel.put("url", excelName);
//				System.out.println(picWeb+"修改后文件名");
				List<Object> list = ExcelTransport.readExcel(excelName, 0, "collegeName,stuNumber,stuName,teacherName,"
						+ "majorOne,majorTwo,thesisName,englishTitle"
						+ ",researchDirection,keyWords,thesisType,studyType,remark,fileType");
				excel.put("alt", "");
				if(list==null){
					return ajaxReturn(response,null,"excel格式错误，请下载示例文件",0);
				}
				//解压zip
				ZipFile.getZiplist(zipName,zipName.substring(0,zipName.length()-4));
				//for(Object object:list ){
				 int error = 0;
				 for(int i=0;i<list.size();i++){
					Object object = list.get(i);
					Map th =(Map) object;
					Thesis thesis = new Thesis();
					thesis.setStuNumber(th.get("stuNumber").toString());
					thesis.setStuName(th.get("stuName").toString());
					thesis.setTeacherName(th.get("teacherName").toString());
					thesis.setThesisName(th.get("thesisName").toString());
					//System.out.println(request.getParameter("DiyCom")+"为什么啊"+request.getParameter("CollegeIdWhere"));
					if(request.getParameter("DiyCom")==null||request.getParameter("DiyCom").toString().equals("")||request.getParameter("DiyCom").toString().equals("0")){
						thesis.setDiyId(0);
					}
					else{
						thesis.setDiyId(Integer.valueOf(request.getParameter("DiyCom").toString()));
					}
					int a = 0;
					if(th.get("majorOne").toString().length()==3){
						a = majorService.findMajorIdByCode("0"+th.get("majorOne").toString());
					}
					else{
						a = majorService.findMajorIdByCode(th.get("majorOne").toString());
					}
					thesis.setMajorOne(a);
					int b =0;
					if(th.get("majorTwo").toString().length()==5){
						b = majorService.findMajorIdByCode("0"+th.get("majorTwo").toString());
					}
					else{
						b = majorService.findMajorIdByCode(th.get("majorTwo").toString());
					}
					thesis.setMajorTwo(b);
					if(a==0||b==0){
						error++;
						System.out.println("专业错误");
						//break;
						continue;
					}
					thesis.setEnglishTitle(th.get("englishTitle").toString());
					thesis.setResearchDirection(th.get("researchDirection").toString());
					thesis.setKeyWords(th.get("keyWords").toString());
					thesis.setStudyType(th.get("studyType").toString());
					thesis.setRemark("上传备注:"+th.get("remark").toString());
					thesis.setFileType(th.get("fileType").toString());
					About about  = aboutService.findById(1);
					thesis.setThesisCode(about.getSendLetter()+th.get("stuNumber").toString());
//					thesis.setThesisUrl(th.get("thesisUrl").toString());
					if(th.get("thesisType").equals("学硕")){
						thesis.setThesisType(1);
					}
					else if(th.get("thesisType").equals("专硕")){
						thesis.setThesisType(2);
					}
					else if(th.get("thesisType").equals("博士")){
						thesis.setThesisType(3);
					}
					else {
						error++;
						System.out.println("类型错误");
						//break;
						continue;
					}
					//thesis.setThesisType(Integer.valueOf(th.get("thesisType").toString()));
					thesis.setUploadTime(Fn.time());
					thesis.setIsShortcut(0);
					//添加文件验证
					String osName = System.getProperty("os.name").toLowerCase();
//					System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
					if(osName.indexOf("windows")!=-1){
						thesis.setThesisUrl(UploadUtils.getFullSavePath(excelInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString().substring(0,zipInfo.get("saveName").toString().length()-4)+"\\"+th.get("stuNumber")+"."+thesis.getFileType());
					}
					else{
						thesis.setThesisUrl(UploadUtils.getFullSavePath(excelInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString().substring(0,zipInfo.get("saveName").toString().length()-4)+"/"+th.get("stuNumber")+"."+thesis.getFileType());
					}
					File thesisfile = new File(thesis.getThesisUrl());
					System.out.println(thesis.getThesisUrl());
					System.out.println(thesisfile.isFile()+"文件是否存在");
					if(!thesisfile.isFile()){
						error++;
						System.out.println("文件错误");
						//break;
						continue;
					}
					String id = request.getSession().getAttribute("userId").toString();
					User user = userService.findById(Integer.valueOf(id));
					thesis.setUniversityId(user.getUniversityId());
					thesis.setSendNumber(0);
					if(th.get("collegeName")!=null&&(!th.get("collegeName").toString().equals("0"))&&(!th.get("collegeName").toString().equals(""))){
						int collegeId = collegeService.findCollegeByName(user.getUniversityId(), th.get("collegeName").toString());
						if(collegeId==0){
							error++;
							System.out.println("学院错误");
							//break;
							continue;
						}
						thesis.setCollegeId(Integer.valueOf(collegeId));
					}
					if(user.getUserType()==0){
						//研究生部
						thesis.setSendType(1);
					}
					else if(user.getUserType()==1){
						//学院部
						thesis.setSendType(2);
					}
					thesis.setUploadPeople(user.getId());
					thesisService.save(thesis);
				}
				 if(error!=0){
						return ajaxReturn(response,excel,"有"+error+"篇论文上传失败，其余成功，请检查！",0);
				}
				else{
						return ajaxReturn(response,excel,"一键评审论文全部上传成功",1);
				}
	}
	
	/**
	 * 论文派送
	 */
	@RequestMapping(value={"list_up"})
	public String listUp(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String ids = request.getParameter("id");
			String id[] = ids.split(",");
			for(int i=0;i<id.length;i++){
				Result result = resultService.findById(Integer.valueOf(id[i]));
				String college = request.getParameter("collegea");
				String teacher = request.getParameter("teachera");
				if(college.equals("")||college.equals("0")){
					return ajaxReturn(response, null,"请至少选择一个学院",0);
				}
				if(teacher==null||teacher.equals("")||teacher.equals("0")){
					//如果是学院
					String userid = request.getSession().getAttribute("userId").toString();
					User user = userService.findById(Integer.valueOf(userid));
					if(user.getUserType()==1){
						//是学院
						return ajaxReturn(response, null,"请至少选择一个老师",0);
					}
					//送往学院
					//result表
					result.setStatusTime(Fn.time());
					result.setCollegeId(Integer.valueOf(college));
					//result.setMajorId(Integer.valueOf(major));
					//process表
					StatusProcess process = new StatusProcess();
					process.setResultId(result.getId());
					process.setThesisId(result.getThesisId());
					process.setLastStatus(result.getStatus());
					process.setUserId(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
					result.setStatus(5);
					process.setNowStatus(5);
					process.setAddTime(Fn.time());
					if(resultService.update(result)==1&&statusProcessService.save(process)==1){
						//return ajaxReturn(response, null,"发送成功",1);
					}
					else{
						return ajaxReturn(response, null,"发送失败",0);
					}
				}
				else{
					//送往老师
					result.setStatusTime(Fn.time());
					result.setCollegeId(Integer.valueOf(college));
					//result.setMajorOne(Integer.valueOf(majora));
					//result.setMajorTwo(Integer.valueOf(majorb));
					result.setTeacherId(Integer.valueOf(teacher));
					//process表
					StatusProcess process = new StatusProcess();
					process.setThesisId(result.getThesisId());
					process.setLastStatus(result.getStatus());
					process.setResultId(result.getId());
					process.setUserId(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
					result.setStatus(6);
					process.setNowStatus(6);
					process.setAddTime(Fn.time());
					if(resultService.update(result)==1&&statusProcessService.save(process)==1){
						//return ajaxReturn(response, null,"发送成功",1);
					}
					else{
						return ajaxReturn(response, null,"发送失败",0);
					}
				}
			}
			return ajaxReturn(response, null,"发送成功",1);
		}
		else{
			//request.getSession().setAttribute("sendId", request.getParameter("id"));
			model.addAttribute("id", request.getParameter("id"));
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			model.addAttribute("collegeId", user.getCollegeId());
			return "thesis/list_up";
		}
	}
	
	/**
	 * 管理员的论文派送
	 */
	@RequestMapping(value={"list_up_admin"})
	public String listUpAdmin(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String ids = request.getSession().getAttribute("sendId").toString();
			String id[] = ids.split(",");
			for(int i=0;i<id.length;i++){
				Result result = new Result();
				Thesis thesis = thesisService.findById(Integer.valueOf(id[i]));
				String university = request.getParameter("universitya");
				String college = request.getParameter("collegea");
				String majorOne = request.getParameter("majora");
				String majorTwo  = request.getParameter("majorb");
				String teacher = request.getParameter("teachera");
				//保存数据
//				result.setAreaId(Integer.valueOf(area));
				result.setUniversityId(Integer.valueOf(university));
				result.setCollegeId(Integer.valueOf(college));
				//result.setMajorId(Integer.valueOf(major));
				if(majorOne!=null&&(!majorOne.equals(""))){
					result.setMajorOne(Integer.valueOf(majorOne));
				}
				if(majorTwo!=null&&(!majorTwo.equals(""))){
					result.setMajorTwo(Integer.valueOf(majorTwo));
				}
				result.setTeacherId(Integer.valueOf(teacher));
				result.setStatusTime(Fn.time());
				result.setThesisId(thesis.getId());
				result.setReturnTime(thesis.getReturnTime());
//				if(thesis.getIsShortcut()==1){
//					result.setReturnTime(thesis.getReturnTime());
//				}
				result.setSendUserId(thesis.getUploadPeople());
				//process表
				StatusProcess process = new StatusProcess();
				process.setResultId(result.getId());
				process.setThesisId(result.getThesisId());
				User user =userService.findById(result.getSendUserId());
				if(user.getUserType()==1){
					process.setLastStatus(1);
				}
				else{
					process.setLastStatus(2);
				}
				process.setUserId(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				process.setAddTime(Fn.time());
				if(teacher!=null&&(!teacher.equals(""))&&(!teacher.equals("0"))){
					result.setStatus(6);
					process.setNowStatus(6);
					
				}
				else if(college!=null&&(!college.equals(""))&&(!college.equals("0"))){
					result.setStatus(5);
					process.setNowStatus(5);
				}
				else if(university!=null&&(!university.equals(""))&&(!university.equals("0"))){
					result.setStatus(4);
					process.setNowStatus(4);
				}
				thesis.setSendNumber(thesis.getSendNumber()+1);
				thesisService.update(thesis);
				resultService.save(result);
				process.setResultId(result.getId());
				process.setThesisId(result.getThesisId());
				if(statusProcessService.save(process)==1){
					//return ajaxReturn(response, null,"发送成功",1);
				}
				else{
					return ajaxReturn(response, null,"发送失败",0);
				}
			}
			return ajaxReturn(response, null,"发送成功",1);
		}
		else{
			request.getSession().setAttribute("sendId", request.getParameter("id"));
			return "thesis/list_up_admin";
		}
	}
	
	/**
	 * 管理员的论文派送
	 */
	@RequestMapping(value={"list_up_admin_public"})
	public String listUpAdminPublic(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String ids = request.getSession().getAttribute("sendId").toString();
			String id[] = ids.split(",");
			for(int i=0;i<id.length;i++){
				//Thesis thesis = thesisService.findById(Integer.valueOf(id[i]));
				Result result = resultService.findById(Integer.valueOf(id[i]));
				Thesis thesis = thesisService.findById(result.getThesisId());
				//String area = request.getParameter("areaa");
				String university = request.getParameter("universitya");
				String college = request.getParameter("collegea");
				String majorOne = request.getParameter("majora");
				String majorTwo = request.getParameter("majorb");
				String teacher = request.getParameter("teachera");
				//保存数据
//				result.setAreaId(Integer.valueOf(area));
				result.setUniversityId(Integer.valueOf(university));
				result.setCollegeId(Integer.valueOf(college));
				//result.setMajorId(Integer.valueOf(majorOne));
				if(majorOne!=null&&(!majorOne.equals(""))){
					result.setMajorOne(Integer.valueOf(majorOne));
				}
				if(majorTwo!=null&&(!majorTwo.equals(""))){
					result.setMajorTwo(Integer.valueOf(majorTwo));
				}
				result.setTeacherId(Integer.valueOf(teacher));
				result.setStatusTime(Fn.time());
				//result.setThesisId(Integer.valueOf(id[i]));
				result.setReturnTime(thesis.getReturnTime());
				result.setSendUserId(userService.findUser(String.valueOf(thesis.getUniversityId()), String.valueOf(thesis.getCollegeId())));
				//process表
				StatusProcess process = new StatusProcess();
				process.setResultId(result.getId());
				process.setThesisId(result.getThesisId());
				//User user =userService.findById(result.getSendUserId());
//				if(user.getUserType()==1){
//					process.setLastStatus(1);
//				}
//				else{
//					process.setLastStatus(2);
//				}
				process.setLastStatus(3);
				process.setUserId(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				process.setAddTime(Fn.time());
				if(teacher!=null&&(!teacher.equals(""))&&(!teacher.equals("0"))){
					result.setStatus(6);
					process.setNowStatus(6);
					
				}
				else if(college!=null&&(!college.equals(""))&&(!college.equals("0"))){
					result.setStatus(5);
					process.setNowStatus(5);
				}
				else if(university!=null&&(!university.equals(""))&&(!university.equals("0"))){
					result.setStatus(4);
					process.setNowStatus(4);
				}
				thesis.setSendNumber(thesis.getSendNumber()+1);
				thesisService.update(thesis);
				resultService.update(result);
				process.setResultId(result.getId());
				process.setThesisId(result.getThesisId());
				if(statusProcessService.save(process)==1){
					//return ajaxReturn(response, null,"发送成功",1);
				}
				else{
					return ajaxReturn(response, null,"发送失败",0);
				}
			}
			return ajaxReturn(response, null,"发送成功",1);
		}
		else{
			request.getSession().setAttribute("sendId", request.getParameter("id"));
			return "thesis/list_up_admin_public";
		}
	}
	
	/**
	 * 老师的评审
	 */
	@RequestMapping(value={"list_comments"},method=RequestMethod.POST)
	public String listComments(Model model,HttpServletRequest request,HttpServletResponse response 
			,@RequestParam("file") MultipartFile commfile) throws Exception {
		//System.out.println(request.getSession().getAttribute("commentsId").toString()+"评论的resultID");
		Result result = resultService.findById(Integer.valueOf(request.getParameter("id").toString()));
		if(commfile.isEmpty()){
			return ajaxReturn(response, null,"请上传附件 ",0);
		}
		else{
			Map<String, Object> zip = new HashMap<String, Object>();		
			String zipName = "";
			Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(commfile);
			//判断
			System.out.println(((Integer)zipInfo.get("status")>0)+"判断");
			if(((Integer)zipInfo.get("status")>0)){ //上传完成		
				//网络地址
				zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
				//物理地址
				String osName = System.getProperty("os.name").toLowerCase();
//				System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
				if(osName.indexOf("windows")!=-1){
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
				}
				else{
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
				}
//				System.out.println(zipName+"完整的名字");
//				System.out.println(UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"保存路径");
//				System.out.println(zipName.substring(0,zipName.length()-4));
				zip.put("url", zipName);
				zip.put("alt", "");
				result.setAttachment(zipName);
//				System.out.println(zipName+"为什么设定不了");
			}else{ //上传出错
				return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
			}
		}
		String id = request.getSession().getAttribute("userId").toString();
		User user = userService.findById(Integer.valueOf(id));
		//判断时间是否正确
		if(Fn.time()<result.getReturnTime()){
			user.setThesisNumber(user.getThesisNumber()+1);
		}
		else{
			user.setThesisNumber(user.getThesisNumber()-1);
		}
		result.setTeacherId(Integer.valueOf(id));
		result.setThesisRemark(request.getParameter("thesisRemark"));
//		result.setAttachment(request.getParameter("attachment"));
		result.setStatusTime(Fn.time());
		//结果的评价
		//result.setThesisResult(Integer.valueOf(request.getParameter("thesisResult")));
		result.setThesisResultOne(Integer.valueOf(request.getParameter("thesisResultOne").toString()));
		result.setThesisResultTwo(Integer.valueOf(request.getParameter("thesisResultTwo").toString()));
		result.setThesisResultThree(Integer.valueOf(request.getParameter("thesisResultThree").toString()));
		result.setThesisResultFour(Integer.valueOf(request.getParameter("thesisResultFour").toString()));
		if(Integer.valueOf(request.getParameter("thesisResult"))==0){
			if(request.getParameter("thesisResultNumber").toString().equals("")||request.getParameter("thesisResultNumber").toString()==null){
				return ajaxReturn(response, null,"请至少填一个分数 ",0);
			}
			//保存百分分数
			result.setThesisResult(Integer.valueOf(request.getParameter("thesisResultNumber")));
		}
		else{
			//保存非百分数
			result.setThesisResult(Integer.valueOf(request.getParameter("thesisResult")));
			result.setIsHundred(0);
		}
		StatusProcess process = new StatusProcess();
		process.setAddTime(Fn.time());
		process.setLastStatus(result.getStatus());
		result.setStatus(9);
		process.setUserId(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
		process.setNowStatus(9);
		process.setResultId(result.getId());
		process.setThesisId(result.getThesisId());
		Thesis thesis = thesisService.findById(process.getThesisId());
		thesis.setHadCom(thesis.getHadCom()+1);
		if(thesis.getHadCom()>=thesis.getRequireNumber()){
			thesis.setIsFinish(1);
		}
		thesisService.update(thesis);
		if(statusProcessService.save(process)==1&&resultService.update(result)==1){
			return ajaxReturn(response, null,"评审成功",1);
		}
		else{
			return ajaxReturn(response, null,"评审失败",1);
		}
	}
	
	/**
	 * 老师的评审
	 */
	@RequestMapping(value={"list_comments"},method=RequestMethod.GET)
	public String listCommentsGet(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Result result = resultService.findById(Integer.valueOf(id));
		Thesis thesis = thesisService.findById(result.getThesisId());
		model.addAttribute("thesis", thesis);
		Major major ;
		//= majorService.findById(thesis.getMajorId());
		if(thesis.getMajorTwo()==0){
			major = majorService.findById(thesis.getMajorOne());
		}
		else{
			major = majorService.findById(thesis.getMajorTwo());
		}
		model.addAttribute("majorTwo", major.getMajorName());
		return "thesis/list_comments";
	}
	
	/**
	 * 老师的评审（自定义模板）
	 */
	@RequestMapping(value={"list_comments_diy"},method=RequestMethod.GET)
	public String listCommentsDiyGet(Model model,HttpServletRequest request,HttpServletResponse response
			) throws Exception {
//		System.out.println("获取网页");
		String id = request.getParameter("id");
		Result result = resultService.findById(Integer.valueOf(id));
		Thesis thesis = thesisService.findById(result.getThesisId());
		model.addAttribute("result", result);
		Major major;
		if(thesis.getMajorTwo()==0){
			major = majorService.findById(thesis.getMajorOne());
		}
		else{
			major = majorService.findById(thesis.getMajorTwo());
		}
		model.addAttribute("major", major.getMajorName());
		return "thesis/list_comments_diy";
	}
	
	/**
	 * 老师的评审（自定义模板）
	 */
	@RequestMapping(value={"list_comments_diy"},method=RequestMethod.POST)
	public String listCommentsDiyPost(Model model,HttpServletRequest request,HttpServletResponse response 
			,@RequestParam("file") MultipartFile commfile) throws Exception {
//		System.out.println("上传自定义模块");
		String id = request.getParameter("resultId");
		if(commfile.isEmpty()){
			return ajaxReturn(response, null,"请上传附件 ",0);
		}
		else{
			Map<String, Object> zip = new HashMap<String, Object>();		
			String zipName = "";
			Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(commfile);
			//判断
			System.out.println(((Integer)zipInfo.get("status")>0)+"判断");
			if(((Integer)zipInfo.get("status")>0)){ //上传完成		
				//网络地址
				zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
				//物理地址
				String osName = System.getProperty("os.name").toLowerCase();
//				System.out.println(osName+"我的系统"+osName.indexOf("windowdas"));
				if(osName.indexOf("windows")!=-1){
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
				}
				else{
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
				}
				zip.put("url", zipName);
				zip.put("alt", "");
				Result result = resultService.findById(Integer.valueOf(id));
				result.setAttachment(zipName);
				result.setTeacherId(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				result.setStatusTime(Fn.time());
				//resultService.update(result);
				//流程表
				StatusProcess process = new StatusProcess();
				process.setAddTime(Fn.time());
				process.setLastStatus(result.getStatus());
				result.setStatus(9);
				process.setUserId(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				process.setNowStatus(9);
				process.setResultId(result.getId());
				process.setThesisId(result.getThesisId());
				Thesis thesis = thesisService.findById(process.getThesisId());
				thesis.setHadCom(thesis.getHadCom()+1);
				if(thesis.getHadCom()>=thesis.getRequireNumber()){
					thesis.setIsFinish(1);
				}
				thesisService.update(thesis);
				if(resultService.update(result)==1&&statusProcessService.save(process)==1){
					return ajaxReturn(response, null,"评审成功",1);
				}
				else{
					return ajaxReturn(response, null,"评审失败",0);
				}
			}else{ //上传出错
				return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
			}
		}
	}
	
	/**
	 * 送方送审
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"list_send_to"})
	public String listSendTo(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String ids = request.getParameter("id");
			String id[] = ids.split(",");
			for(int j=0;j<id.length;j++){
//				System.out.println(id[j]+"论文id");
				Thesis thesis = thesisService.findById(Integer.valueOf(id[j]));
				//验证送审最大数
				About about = aboutService.findById(1);
				if(about.getSendNumber()!=0){
					if(about.getSendNumber()>=thesis.getSendNumber()){
						return ajaxReturn(response, null,"送审失败,编号为"+thesis.getThesisCode()+"的论文已经达到最大送审数量",0);
					}
				}
				Result result = new Result();
				result.setThesisId(thesis.getId());
//				result.setAreaId(Integer.valueOf(request.getParameter("area").toString()));
				result.setUniversityId(Integer.valueOf(request.getParameter("universityaaa").toString()));
				String times = request.getParameter("returnTime");
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(times);
//				System.out.println((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))+"天");
				if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<0){
					return ajaxReturn(response, null,"截止日期必须大于或等于今天",0);
				}
				result.setReturnTime((int)(time.getTime()/1000));
				if(request.getParameter("collegea").toString().equals("")||request.getParameter("collegea").toString().equals("0")){
					//未指定学院
					result.setStatus(4);
					int i = userService.findUser(request.getParameter("universityaaa").toString(), "0");
					User user = userService.findById(i);
					if(user.getIsValue()==1){
						if(!user.getSendCode().equals(request.getParameter("sendCode"))){
							//System.out.println("未指定院系");
							return ajaxReturn(response, null,"送审码与对方不符",0);
						}
					}
					else{
						String code = request.getParameter("sendCode").toString();
						if(code.length()!=11){
							return ajaxReturn(response, null,"手机格式错误",0);
						}
//						int mobile_code = (int)((Math.random()*9+1)*100000);
						String content ="您的账户已经开通，账号为："+user.getUserName()+
								" 密码为："+user.getUserPassword()+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
						int jl = sendsms.sendMessage(content, code);
						if(jl!=1){
							return ajaxReturn(response,null,"手机验证错误，请重试！",0);
						}
						if(jl==1){
							user.setIsValue(1);
							user.setUserPhone(code);
							user.setSendCode(code);
							userService.update(user);
						}
					}
				}
				else{
					//指定学院
					result.setStatus(5);
					result.setCollegeId(Integer.valueOf(request.getParameter("collegea").toString()));
					int i = userService.findUser(request.getParameter("universityaaa").toString(), request.getParameter("collegea"));
					User user = userService.findById(i);
					if(user.getIsValue()==1){
						if(!user.getSendCode().equals(request.getParameter("sendCode"))){
							return ajaxReturn(response, null,"送审码与对方不符",0);
						}
					}
					else{
						String code = request.getParameter("sendCode").toString();
						if(code.length()!=11){
							return ajaxReturn(response, null,"手机格式错误",0);
						}
//						int mobile_code = (int)((Math.random()*9+1)*100000);
						String content ="您的账户已经开通，账号为："+user.getUserName()+
								" 密码为："+user.getUserPassword()+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
						int jl = sendsms.sendMessage(content, code);
						if(jl!=1){
							return ajaxReturn(response,null,"手机验证错误，请重试！",0);
						}
						if(jl==1){
							user.setIsValue(1);
							user.setUserPhone(code);
							user.setSendCode(code);
							userService.update(user);
						}
					}
				}
				result.setStatusTime(Fn.time());
				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				result.setSendUserId(user.getId());
				resultService.save(result);
				StatusProcess process = new StatusProcess();
				process.setThesisId(thesis.getId());
				process.setResultId(result.getId());
				//User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				process.setUserId(user.getId());
				process.setAddTime(Fn.time());
				if(user.getUserType()==1){
					//送方：研究生部
					process.setLastStatus(1);
				}
				else{
					//送方：学院部
					process.setLastStatus(2);
				}
				if(request.getParameter("collegea").toString().equals("")||request.getParameter("collegea").toString().equals("0")){
					process.setNowStatus(4);
				}
				else{
					process.setNowStatus(5);
				}
				if(statusProcessService.save(process)==1){
					thesis.setSendNumber(thesis.getSendNumber()+1);
					thesisService.update(thesis);
					//return ajaxReturn(response, null,"送审成功",1);
				}
				else{
					return ajaxReturn(response, null,"送审失败",0);
				}
			}
			return ajaxReturn(response, null,"送审成功",1);
		}
		else{
			//request.getSession().setAttribute("sendTo", request.getParameter("id"));
			String ids = request.getParameter("id");
			String id[] = ids.split(",");
			String had ="";
			for(int j=0;j<id.length;j++){
				had = thesisService.checkHad(Integer.valueOf(id[j]), had);
			}
//			System.out.println(had+"哈哈哈哈哈");
			model.addAttribute("had",had);
			model.addAttribute("ids", ids);
			return "thesis/list_send_to";
		}
	}
	
	/**
	 * 论文拒绝
	 */
	@RequestMapping(value={"list_refuse"})
	public String listRefuse(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String ids = request.getParameter("id");
		String id[] = ids.split(",");
		for(int i=0;i<id.length;i++){
			Result result = resultService.findById(Integer.valueOf(id[i]));
			Map<String, Object> where = new HashMap<String,Object>();
			where.put("result_id", result.getId());
	//		User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			int status = statusProcessService.getLastStatusIdAndDelete(where);
			result.setStatus(status);
			Thesis thesis = thesisService.findById(result.getThesisId());
			if(status==5){
				result.setTeacherId(0);
				result.setMajorOne(0);
				result.setMajorTwo(0);
				resultService.update(result);
			}
			if(status==4){
				result.setCollegeId(0);
				result.setTeacherId(0);
				result.setMajorOne(0);
				result.setMajorTwo(0);
				resultService.update(result);
			}
			if(status==3&&thesis.getIsShortcut()==0){
				return ajaxReturn(response, null,"来自公共平台的论文不能退",0);
			}
			if(status==2||status==1){
//				result.setAreaId(0);
				result.setUniversityId(0);
				result.setCollegeId(0);
				result.setTeacherId(0);
				result.setMajorOne(0);
				result.setMajorTwo(0);
				//resultService.update(result);
			}
			resultService.update(result);
		}
		return ajaxReturn(response, null,"退回成功",1);
	}
	
	/**
	 * 论文接收
	 */
	@RequestMapping(value={"accept"})
	public String accept(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
//		System.out.println(request.getParameter("id"));
		String ids = request.getParameter("id");
		String id[] = ids.split(",");
		for(int i=0;i<id.length;i++){
			Result result = resultService.findById(Integer.valueOf(id[i]));
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			if(user.getUserType()==0){
				//研究生部
				result.setStatus(4);
//				result.setAreaId(user.getAreaId());
				result.setUniversityId(user.getUniversityId());
			}
			else if(user.getUserType()==1){
				//学院
				result.setStatus(5);
//				result.setAreaId(user.getAreaId());
				result.setUniversityId(user.getUniversityId());
				result.setCollegeId(user.getCollegeId());
			}
			else if(user.getUserType()==2){
				//老师
				result.setStatus(6);
//				result.setAreaId(user.getAreaId());
				result.setUniversityId(user.getUniversityId());
				result.setCollegeId(user.getCollegeId());
				//result.setMajorId(user.getMajorId());
				result.setMajorOne(user.getMajorOne());
				result.setMajorTwo(user.getMajorTwo());
				result.setTeacherId(user.getId());
			}
			result.setStatusTime(Fn.time());
			resultService.update(result);
			StatusProcess process = new StatusProcess();
			process.setAddTime(Fn.time());
			process.setLastStatus(3);
			process.setNowStatus(result.getStatus());
			process.setResultId(result.getId());
			process.setThesisId(result.getThesisId());
			process.setUserId(user.getId());
			if(statusProcessService.save(process)==1){
				//return ajaxReturn(response, null,"操作成功",1);
			}
			else{
				return ajaxReturn(response, null,"操作失败",0);
			}
		}
		return ajaxReturn(response, null,"操作成功",1);
	}
	
	/**
	 * 论文共享
	 */
	@RequestMapping(value={"list_shared"})
	public String listShared(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String ids = request.getParameter("id");
		String id[] = ids.split(",");
		for(int i=0;i<id.length;i++){
			Thesis thesis = thesisService.findById(Integer.valueOf(id[i]));
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			if(user.getUserType()!=1){
				return ajaxReturn(response, null,"对不起，您没有权限",0);
			}
			thesis.setSendType(3);
			thesisService.update(thesis);
			int j=thesisService.update(thesis);
			if(j!=1){
				return ajaxReturn(response, null,"操作失败",0);
			}
		}
		return ajaxReturn(response, null,"操作成功",1);
	}
	
	/**
	 * 送审论文到公共平台(管理员)
	 */
	@RequestMapping(value={"list_send_public_admin"})
	public String listSendPublicAdmin(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String thesisId = request.getParameter("id").toString();
			String id[] = thesisId.split(",");
			for(int i=0;i<id.length;i++){
				Thesis thesis = thesisService.findById(Integer.valueOf(id[i]));
				//验证送审最大数
				About about = aboutService.findById(1);
				if(about.getSendNumber()!=0){
					if(about.getSendNumber()>=thesis.getSendNumber()){
						return ajaxReturn(response, null,"送审失败,该论文已经达到最大送审数量",0);
					}
				}
				Result result = new Result();
				result.setThesisId(thesis.getId());
				result.setStatus(3);
				result.setStatusTime(Fn.time());
//				String haha  = Fn.date(thesis.getReturnTime(), "yyyy-MM-dd");
//				String times = haha;
//				System.out.println(haha);
//				System.out.println(times);
//				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
//				Date time = format.parse(times);
//				if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<0){
//					return ajaxReturn(response, null,"截止日期必须大于或等于今天",0);
//				}
//				result.setReturnTime((int)(time.getTime()/1000));
				result.setReturnTime(thesis.getReturnTime());
				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				result.setSendUserId(user.getId());
				resultService.save(result);
				StatusProcess process = new StatusProcess();
				process.setThesisId(thesis.getId());
				process.setResultId(result.getId());
				//User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				process.setUserId(user.getId());
				process.setAddTime(Fn.time());
				if(user.getUserType()==1){
					//送方：研究生部
					process.setLastStatus(1);
				}
				else if(user.getUserType()==2){
					//送方：学院部
					process.setLastStatus(2);
				}
				else{
					//管理员
					User senduser;
					if(thesis.getSendType()==1){
						//研究生部
						senduser = userService.findById(userService
								.findUser(String.valueOf(thesis.getUniversityId()),
										"0"));
					}
					else{
						//学院
						senduser = userService.findById(userService
								.findUser(String.valueOf(thesis.getUniversityId()),
										String.valueOf(thesis.getCollegeId())));
					}
					//System.out.println(senduser.getId()+"哈哈哈哈哈哈哈哈");
					if(senduser.getUserType()==0){
						//研究生部
						process.setLastStatus(1);
					}
					else{
						//学院
						process.setLastStatus(2);
					}
				}
				process.setNowStatus(3);
				if(statusProcessService.save(process)==1){
					thesis.setSendNumber(thesis.getSendNumber()+1);
					thesisService.update(thesis);
					//return ajaxReturn(response, null,"已经送往公共平台",1);
				}
				else{
					return ajaxReturn(response, null,"发送失败",0);
				}
			}
			return ajaxReturn(response, null,"已经送往公共平台",1);
		}
		else{
			return "thesis/list_send_public_admin";
		}
	}
	
	/**
	 * 待送审论文到公共平台(非管理员)
	 */
	@RequestMapping(value={"list_send_public"})
	public String listSendPublic(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String thesisId = request.getParameter("id").toString();
			String id[] = thesisId.split(",");
			for(int i=0;i<id.length;i++){
				Thesis thesis = thesisService.findById(Integer.valueOf(id[i]));
				//验证送审最大数
				About about = aboutService.findById(1);
				if(about.getSendNumber()!=0){
					if(about.getSendNumber()>=thesis.getSendNumber()){
						return ajaxReturn(response, null,"送审失败,该论文已经达到最大送审数量",0);
					}
				}
				Result result = new Result();
				result.setThesisId(thesis.getId());
				result.setStatus(3);
				result.setStatusTime(Fn.time());
				String times = request.getParameter("returnTime");
				//System.out.println(times);
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(times);
				if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<0){
					return ajaxReturn(response, null,"截止日期必须大于或等于今天",0);
				}
				result.setReturnTime((int)(time.getTime()/1000));
				//result.setReturnTime(thesis.getReturnTime());
				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				result.setSendUserId(user.getId());
				resultService.save(result);
				StatusProcess process = new StatusProcess();
				process.setThesisId(thesis.getId());
				process.setResultId(result.getId());
				//User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				process.setUserId(user.getId());
				process.setAddTime(Fn.time());
				if(user.getUserType()==1){
					//送方：研究生部
					process.setLastStatus(1);
				}
				else if(user.getUserType()==2){
					//送方：学院部
					process.setLastStatus(2);
				}
				else{
					//管理员
					User senduser;
					if(thesis.getSendType()==1){
						//研究生部
						senduser = userService.findById(userService
								.findUser(String.valueOf(thesis.getUniversityId()),
										"0"));
					}
					else{
						//学院
						senduser = userService.findById(userService
								.findUser(String.valueOf(thesis.getUniversityId()),
										String.valueOf(thesis.getCollegeId())));
					}
					//System.out.println(senduser.getId()+"哈哈哈哈哈哈哈哈");
					if(senduser.getUserType()==0){
						//研究生部
						process.setLastStatus(1);
					}
					else{
						//学院
						process.setLastStatus(2);
					}
				}
				process.setNowStatus(3);
				if(statusProcessService.save(process)==1){
					thesis.setSendNumber(thesis.getSendNumber()+1);
					thesisService.update(thesis);
					//return ajaxReturn(response, null,"已经送往公共平台",1);
				}
				else{
					return ajaxReturn(response, null,"发送失败",0);
				}
			}
			return ajaxReturn(response, null,"已经送往公共平台",1);
		}
		else{
			return "thesis/list_send_public";
		}
	}
	
	
	/**
	 * 一键送审论文
	 */
	@RequestMapping(value={"list_send_shortcut"})
	public String listSendShortcut(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			//Thesis thesis = thesisService.findById(Integer.valueOf(request.getParameter("id").toString()));
			System.out.println(request.getParameter("id")+"ids");
			String ids[] = request.getParameter("id").toString().split(",");
			for(int i=0;i<ids.length;i++){
				//System.out.println(ids[i]+"我是快乐的"+i);
				Thesis thesis = thesisService.findById(Integer.valueOf(ids[i]));
				if(thesis.getReturnTime()!=0){
					continue;
				}
				//验证送审最大数
				About about = aboutService.findById(1);
				if(about.getSendNumber()!=0){
//					System.out.println("呵呵"+about.getSendNumber()+"++"+thesis.getSendNumber());
					if(about.getSendNumber()>=thesis.getSendNumber()){
						return ajaxReturn(response, null,"送审失败,论文编号为"+thesis.getThesisCode()+"的论文已经达到最大送审数量",0);
					}
				}
				//Result result = new Result();
				//送审时间
				String times = request.getParameter("returnTime");
//				System.out.println(times);
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(times);
				if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<0){
					return ajaxReturn(response, null,"截止日期必须大于或等于今天",0);
				}
				//送审要求，985,211,普通
				if(request.getParameter("schoolLevel").toString().equals("0")){
					thesis.setIsNine(1);
					thesis.setIsTwo(1);
				}
				else if(request.getParameter("schoolLevel").toString().equals("1")){
					thesis.setIsNine(0);
					thesis.setIsTwo(1);
				}
				//希望院校的输入
				String remark = thesis.getRemark();
				//1
				System.out.println(request.getParameter("unOne"));
				if((!request.getParameter("unOne").toString().equals("0"))&&(!StringUtil.
						isEmpty(request.getParameter("unOne").toString()))){
					//加备注
					University university = universityService.findById(Integer.valueOf(request.getParameter("unOne")));
					remark += "  希望院校备注：第一希望院校为"+university.getUniversityName();
					if((!request.getParameter("collegeOne").toString().equals("0"))&&(!StringUtil.
							isEmpty(request.getParameter("collegeOne").toString()))){
						College college = collegeService.findById(Integer.valueOf(request.getParameter("collegeOne")));
						remark += college.getCollegeName();
					}
				}
				//2
				if((!request.getParameter("unTwo").toString().equals("0"))&&(!StringUtil.
						isEmpty(request.getParameter("unTwo").toString()))){
					//加备注
					University university = universityService.findById(Integer.valueOf(request.getParameter("unTwo")));
					remark += "；第二希望院校为"+university.getUniversityName();
					if((!request.getParameter("collegeTwo").toString().equals("0"))&&(!StringUtil.
							isEmpty(request.getParameter("collegeTwo").toString()))){
						College college = collegeService.findById(Integer.valueOf(request.getParameter("collegeTwo")));
						remark += college.getCollegeName();
					}
				}
				//3
				if((!request.getParameter("unThree").toString().equals("0"))&&(!StringUtil.
						isEmpty(request.getParameter("unThree").toString()))){
					//加备注
					University university = universityService.findById(Integer.valueOf(request.getParameter("unThree")));
					remark += "；第三希望院校为"+university.getUniversityName();
					if((!request.getParameter("collegeThree").toString().equals("0"))&&(!StringUtil.
							isEmpty(request.getParameter("collegeThree").toString()))){
						College college = collegeService.findById(Integer.valueOf(request.getParameter("collegeThree")));
						remark += college.getCollegeName();
					}
				}
				//4
				if((!request.getParameter("unFour").toString().equals("0"))&&(!StringUtil.
						isEmpty(request.getParameter("unFour").toString()))){
					//加备注
					University university = universityService.findById(Integer.valueOf(request.getParameter("unFour")));
					remark += "；第四希望院校为"+university.getUniversityName();
					if((!request.getParameter("collegeFour").toString().equals("0"))&&(!StringUtil.
							isEmpty(request.getParameter("collegeFour").toString()))){
						College college = collegeService.findById(Integer.valueOf(request.getParameter("collegeFour")));
						remark += college.getCollegeName();
					}
				}
				//5
				if((!request.getParameter("unFive").toString().equals("0"))&&(!StringUtil.
						isEmpty(request.getParameter("unFive").toString()))){
					//加备注
					University university = universityService.findById(Integer.valueOf(request.getParameter("unFive")));
					remark += "；第五希望院校为"+university.getUniversityName();
					if((!request.getParameter("collegeFive").toString().equals("0"))&&(!StringUtil.
							isEmpty(request.getParameter("collegeFive").toString()))){
						College college = collegeService.findById(Integer.valueOf(request.getParameter("collegeFive")));
						remark += college.getCollegeName();
					}
				}
				remark += "送审备注："+request.getParameter("remark");
				thesis.setRemark(remark);
				thesis.setRequireNumber(Integer.valueOf(request.getParameter("requireNumber").toString()));
				thesis.setReturnTime((int)(time.getTime()/1000));		
				//result.setIsShortcut(1);
				//User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				//thesis.setSendNumber(thesis.getSendNumber()+1);
				thesisService.update(thesis);
				if(i==ids.length-1){
					return ajaxReturn(response, null,"已经成功一键送审",1);
				}
			}
			return ajaxReturn(response, null,"已经成功一键送审",1);
		}
		else{
			return "thesis/list_send_shortcut";
		}
	}
	
	/**
	 * 论文的下载
	 */
	@RequestMapping(value={"download"})
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				if(user.getUserType()==3){
					return ;
				}
				String id = request.getParameter("id");
				Thesis thesis = thesisService.findById(Integer.valueOf(id));
				String tempPath = thesis.getThesisUrl();
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+thesis.getThesisUrl());
				// inline内嵌显示一个文件attachment提供文件下载
				InputStream is = new java.io.BufferedInputStream(
						new java.io.FileInputStream((tempPath)));
				
				int read = 0;
				byte[] bytes = new byte[2048];

				OutputStream os = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    }
	
	/**
	 * 论文的下载（result）
	 */
	@RequestMapping(value={"download_result"})
	public void downloadResult(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				if(user.getUserType()==3){
					return ;
				}
				String id = request.getParameter("id");
				Result result = resultService.findById(Integer.valueOf(id));
				Thesis thesis = thesisService.findById(result.getThesisId());
				String tempPath = thesis.getThesisUrl();
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+thesis.getThesisUrl());
				// inline内嵌显示一个文件attachment提供文件下载

				InputStream is = new java.io.BufferedInputStream(
						new java.io.FileInputStream((tempPath)));

				int read = 0;
				byte[] bytes = new byte[2048];

				OutputStream os = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
    }
	
//	@RequestMapping(value={"download_process"})
//	public void downloadProcess(HttpServletRequest request, HttpServletResponse response) throws IOException {  
//			try {
//				String id = request.getParameter("id").toString();
//				StatusProcess statusProcess = statusProcessService.findById(Integer.valueOf(id));
//				Thesis thesis = thesisService.findById(statusProcess.getThesisId());
//				String tempPath = thesis.getThesisUrl();
//				response.setContentType("application/msexcel");
//				response.setHeader("Content-disposition", "inline; filename="
//						+thesis.getThesisUrl());
//				// inline内嵌显示一个文件attachment提供文件下载
//
//				InputStream is = new java.io.BufferedInputStream(
//						new java.io.FileInputStream((tempPath)));
//
//				int read = 0;
//				byte[] bytes = new byte[2048];
//
//				OutputStream os = response.getOutputStream();
//				while ((read = is.read(bytes)) != -1) {
//					os.write(bytes, 0, read);
//				}
//				os.flush();
//				os.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//    }
	
	/**
	 * 查看论文的送审流程
	 */
	@RequestMapping(value={"list_process"})
	public String listProcess(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String id = request.getParameter("id");
//			System.out.println(id+"post");
			Map<String, Object> where = new HashMap<String,Object>();
			Thesis thesis = thesisService.findById(Integer.valueOf(id));
			where.put("thesis_id", thesis.getId());
			return ajaxReturn(response, thesisService.getProcessUIGridData(where,UIUtils.getPageParams(request)));
		}
		else{
			String id =request.getParameter("id");
			model.addAttribute("id", id);
//			request.getSession().setAttribute("list_processId", id);
//			System.out.println(id+"get");
			return "thesis/list_process";
		}
	}
	
	/**
	 * 查看论文的送审流程详细
	 */
	@RequestMapping(value={"list_process_detail"})
	public String listProcessDetail(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Result result = resultService.findById(Integer.valueOf(id));
		Thesis thesis = thesisService.findById(result.getThesisId());
		HashMap<String,Object> data =  Fn.modelToRow(thesis);
		data.put("date", Fn.date(result.getStatusTime(), "yyyy-MM-dd"));
		//评审人
		int i = statusProcessService.find(thesis.getId(), result.getId(),result.getStatus());
		User user ;
		if(i!=0){
//			StatusProcess process = statusProcessService.findById(i);
			//= userService.findById(process.getUserId());
			if(result.getStatus()==3){
				//随机选择一个普通管理员身份信息！
				int[]numbers = userService.findNormalAdmin();
				user = userService.findById(numbers[(int)(numbers.length * Math.random())]);
			}
			else if(result.getStatus()==4){
				//对方研究生部
				user = userService.findById(userService.findUser(String.valueOf(result.getUniversityId()),"0"));
			}
			else if(result.getStatus()==5){
				//对方研究生部
				user = userService.findById(userService.findUser(String.valueOf(result.getUniversityId()),String.valueOf(result.getCollegeId())));
			}
			else if(result.getStatus()==6){
				//教师处
				user = userService.findById(result.getTeacherId());
			}
			else if(result.getStatus()==7){
				//随机选择一个管理员身份
				//user = userService.findById(result.getTeacherId());
				int[]numbers = userService.findNormalAdmin();
				user = userService.findById(numbers[(int)(numbers.length * Math.random())]);
			}
			else if(result.getStatus()==8){
				//老师处（已经下载）
				user = userService.findById(result.getTeacherId());
			}
			else if(result.getStatus()==9){
				user = userService.findById(result.getTeacherId());
			}
			else{
				user = new User();
				user.setNickName("暂无数据");
				user.setId(0);
			}
			data.put("user", user.getNickName());
			data.put("hiddenUserId", user.getId());
		}
		else{
			data.put("user", "暂无");
			user = new User();
			//user.setIsSendUrl(0);
		}
		//User user = userService.findById(result.getSendUserId());
		if(thesis.getDiyId()==0){
			data.put("isSendUrl", 0);
		}
		else{
			data.put("isSendUrl", 1);
		}
		//data.put("isSendUrl", user.getIsSendUrl());
		data.put("resultOne", result.getThesisResultOne());
		data.put("resultTwo", result.getThesisResultTwo());
		data.put("resultThree", result.getThesisResultThree());
		data.put("resultFour", result.getThesisResultFour());
		data.put("remark", result.getThesisRemark());
		data.put("ishundred", result.getIsHundred());
		data.put("thesisResult", result.getThesisResult());
		data.put("status", result.getStatus());
		data.put("resultId", result.getId());
		//data.put("hiddenUserId", user.getId());
		//
//		System.out.println(result.getId()+"大大");
//		System.out.println(result.getThesisRemark()+"怎么可能");
		return ajaxReturn(response, data,"读取成功",1);
	}
	
	
	/**
	 * 导出论文列表到excel并下载
	 */
	@RequestMapping(value={"export_excel"})
	public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				String tempPath = "D:\\export.xls";
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+"export." + "xls");
				InputStream is = new java.io.BufferedInputStream(
						new java.io.FileInputStream((tempPath)));

				int read = 0;
				byte[] bytes = new byte[2048];

				OutputStream os = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    }  
	
	/**
	 * 下载论文评审附件
	 */
	@RequestMapping(value={"download_comments"})
	public void downloadComments(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				String id = request.getParameter("id");
				Result result = resultService.findById(Integer.valueOf(id));
				String tempPath = result.getAttachment();
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+result.getAttachment());
				InputStream is = new java.io.BufferedInputStream(
						new java.io.FileInputStream((tempPath)));

				int read = 0;
				byte[] bytes = new byte[2048];

				OutputStream os = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    }
	
	/**
	 * 下载示例文件
	 */
	@RequestMapping(value={"download_example"})
	public void downloadExample(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				About about = aboutService.findById(1);
				String tempPath = about.getExampleUrl();
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+about.getExampleUrl());
				InputStream is = new java.io.BufferedInputStream(
						new java.io.FileInputStream((tempPath)));

				int read = 0;
				byte[] bytes = new byte[2048];

				OutputStream os = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    }
	
	/**
	 * 导出评审情况到PDF并下载
	 */
	@RequestMapping(value={"download_pdf"})
	public void downloadPdf(HttpServletRequest request, HttpServletResponse response) throws Exception  {  
			try {
				//User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
//				if(user.getUserType()==3){
//					return ;
//				}
				String id = request.getParameter("id");
				Result result = resultService.findById(Integer.valueOf(id));
				Thesis thesis = thesisService.findById(result.getThesisId());
				Major major;
				//= majorService.findById(thesis.getMajorId());
				if(thesis.getMajorTwo()==0){
					major = majorService.findById(thesis.getMajorOne());
				}
				else{
					major = majorService.findById(thesis.getMajorTwo());
				}
				String mainResult = "暂无数据";
				if(result.getIsHundred()==0){
					//不是百分制
					if(result.getThesisResult()==1){
						mainResult = "优";
					}
					else if(result.getThesisResult()==2){
						mainResult = "良";
					}
					else if(result.getThesisResult()==3){
						mainResult = "中";
					}
					else if(result.getThesisResult()==4){
						mainResult = "合";
					}
					else if(result.getThesisResult()==5){
						mainResult = "差";
					}
				}
				else{
					//是百分比数
					mainResult = String.valueOf(result.getThesisResult());
				}
//				System.out.println(thesis.getThesisName());
//				System.out.println(major.getMajorName());
//				System.out.println(String.valueOf(result.getThesisResultOne()));
//				System.out.println(String.valueOf(result.getThesisResultTwo()));
//				System.out.println(String.valueOf(result.getThesisResultThree()));
//				System.out.println(String.valueOf(result.getThesisResultFour()));
//				System.out.println(mainResult);
//				System.out.println(result.getThesisRemark());
//				System.out.println("debug嵇建峰"+user.getUserImage());
				CreatePdf.create3(thesis.getThesisName(), major.getMajorName(), String.valueOf(result.getThesisResultOne()),
						String.valueOf(result.getThesisResultTwo()),String.valueOf(result.getThesisResultThree()),
						String.valueOf(result.getThesisResultFour()),mainResult,
						result.getThesisRemark(),"D://export.pdf",userService.findById(result.getTeacherId()).getUserImage());
				String tempPath = "D:\\export.pdf";
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+"export.pdf");
				InputStream is = new java.io.BufferedInputStream(
						new java.io.FileInputStream((tempPath)));

				int read = 0;
				byte[] bytes = new byte[2048];

				OutputStream os = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    }
	
	/**
	 * 下载某用户的自定义送审模板
	 */
	@RequestMapping(value={"download_user"})
	public void downloadUser(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				String id = request.getParameter("id");
				Result result = resultService.findById(Integer.valueOf(id));
				//int i = statusProcessService.findSendUser(result.getId());
				Thesis thesis = thesisService.findById(result.getThesisId());
				User user = userService.findById(thesis.getUploadPeople());
				String tempPath;
				if(thesis.getDiyId()==0){
					About about = aboutService.findById(1);
					tempPath =  about.getSendUrl();
				}
				else{
					Diy diy = diyService.findById(thesis.getDiyId());
					tempPath = diy.getUrl();
				}
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+tempPath);
				InputStream is = new java.io.BufferedInputStream(
						new java.io.FileInputStream((tempPath)));

				int read = 0;
				byte[] bytes = new byte[2048];

				OutputStream os = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    }
	
	/**
	 * 查看用户详细
	 */
	@RequestMapping(value={"fand_user"})
	public String fandUser(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		int i = userService.findUser(request.getParameter("universityIda"), request.getParameter("collegeIda"));
//		System.out.println(request.getParameter("universityIda")+"university");
//		System.out.println(request.getParameter("collegeIda")+"college");
//		System.out.println(i+"::::i");
//		HashMap<String,Object> data =  new HashMap<String, Object>();
//		if(i==0){
//			data.put("mess", "暂无此学院负责人信息，请联系管理员！");
//			data.put("status", "0");
//		}
//		else {
//			data.put("status", "1");
//			//data.put("mess", "c");
//			data.put("id", i);
//		}
		if(i==0){
			return ajaxReturn(response, null,"暂无此学院信息，请联系管理员！",0);
		}
		else{
			model.addAttribute("id", i);
			return "user/detail";
		}
	}
	
	/**
	 * 已送审
	 */
	@RequestMapping(value={"list_receive_oto"})
	public String fandReceiveOto(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
//			String areaId =request.getParameter("areaId");
			String universityId = request.getParameter("universityId");
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			String status = request.getParameter("status");
			Map<String, Object> where = new HashMap<String,Object>();
			if(StringUtil.isEmpty(status)||status.equals("0")){
				status = null;
			}
			else if(Integer.valueOf(status)==1){
				where.put("Result.status", new String[]{"!=","9"} );
			}
			else if(Integer.valueOf(status)==2){
				where.put("Result.status", "9");
			}
			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
				universityId = null;
			}
			else{
				where.put("Thesis.university_id", universityId);
			}
			if(StringUtil.isEmpty(thesisCode)||thesisCode.equals("")){
				thesisCode = null;
			}
			else{
				where.put("Thesis.thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(StringUtil.isEmpty(thesisName)||thesisName.equals("")){
				thesisName = null;
			}
			else{
				where.put("Thesis.thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			if(!StringUtil.isEmpty(timeStart)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("Thesis.return_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("Thesis.return_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			where.put("StatusProcess.user_id", id);
			if(user.getUserType()==0){
				//研究生部
				where.put("StatusProcess.last_status", 4);	
			}
			else if(user.getUserType()==1){
				//学院
				where.put("StatusProcess.last_status", 5);
			}
//			else if(user.getUserType()==2){
//				//老师
//				where.put("StatusProcess.now_status", 9);
//			}
			return ajaxReturn(response, thesisService.getReceiveGridData(where,UIUtils.getPageParams(request)));
		}
		else{
			return "thesis/list_receive_oto";
		}
	}
	
	/**
	 * 查看论文评审详细
	 */
	@RequestMapping(value={"list_result_detail"})
	public String listResultDetail(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			//Result result = resultService.findById(Integer.valueOf(request.getParameter("id")));
			int id = Integer.valueOf(request.getParameter("id"));
			return ajaxReturn(response,thesisService.getDetailUIGridData(id));
			//return "user/detail";
		}else{
			//System.out.println("get");
			int id = Integer.valueOf(request.getParameter("id"));
			StatusProcess process = statusProcessService.findById(id);
			model.addAttribute("id",process.getResultId());
			return "thesis/list_result_detail";
		}
	}
//	@RequestMapping(value={"list_receive_oto_teacher"})
//	public String listReceiveOtoTeacher(Model model,HttpServletRequest request,HttpServletResponse response)throws Exception {
//		return "thesis/list_receive_oto_teacher";
//	}a
	
//	@RequestMapping(value={"list_unreceive_teacher"})
//	public String listUnreceiveTeacher(Model model,HttpServletRequest request,HttpServletResponse response)throws Exception {
//		return "thesis/list_unreceive_teacher";
//	}
	
	/**
	 * 待派论文
	 */
	@RequestMapping(value={"list_unreceive_teacher"})
	public String listUnreceiveTeacher(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
//			String areaId =request.getParameter("areaId");
			String universityId = request.getParameter("universityId");	
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			Map<String, Object> where = new HashMap<String,Object>();
			//同一处理数据
//			if(StringUtil.isEmpty(areaId)||areaId.equals("0")){
//				areaId = null;
//			}
			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
				universityId = null;
			}
			else{
				where.put("Thesis.university_id", universityId );
			}
			if(StringUtil.isEmpty(thesisCode)||thesisCode.equals("")){
				thesisCode = null;
			}
			else{
				where.put("Thesis.thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(StringUtil.isEmpty(thesisName)||thesisName.equals("")){
				thesisName = null;
			}
			else{
				where.put("Thesis.thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			if(!StringUtil.isEmpty(timeStart)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("return_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("return_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			if(user.getUserType()==0){
				//研究生部
				where.put("university_id", user.getUniversityId());
				where.put("status","4");
			}
			else if(user.getUserType()==1){
				//学院
				where.put("university_id", user.getUniversityId());
				where.put("college_id", user.getCollegeId());
				where.put("status","5");
			}
			else if(user.getUserType()==2){
				//老师只能看到被指派的
				where.put("teacher_id", user.getId());
//				where.put("university_id", user.getUniversityId());
//				where.put("college_id", user.getCollegeId());
				where.put("status","6");
			}
			return ajaxReturn(response,thesisService.getUnReceiveIGridData(where,UIUtils.getPageParams(request)));
		}
		else{
			return "thesis/list_unreceive_teacher";
		}
	}
	
	/**
	 * 已送审
	 */
	@RequestMapping(value={"list_receive_oto_teacher"})
	public String listReceiveOtoTeacher(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
//			String areaId =request.getParameter("areaId");
			String universityId = request.getParameter("universityId");
			String thesisCode = request.getParameter("thesisCode");
			String thesisName = request.getParameter("thesisName");
			String timeStart = request.getParameter("timeStart");
			String timeEnd = request.getParameter("timeEnd");
			Map<String, Object> where = new HashMap<String,Object>();
//			String status = request.getParameter("status");
			//同一处理数据
//			if(StringUtil.isEmpty(status)||status.equals("0")){
//				status = null;
//			}
			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
				universityId = null;
			}
			else{
				where.put("Thesis.university_id", universityId);
			}
			if(StringUtil.isEmpty(thesisCode)||thesisCode.equals("")){
				thesisCode = null;
			}
			else{
				where.put("Thesis.thesis_code",new String[]{"like","%"+thesisCode+"%"});
			}
			if(StringUtil.isEmpty(thesisName)||thesisName.equals("")){
				thesisName = null;
			}
			else{
				where.put("Thesis.thesis_name",new String[]{"like","%"+thesisName+"%"});
			}
			if(!StringUtil.isEmpty(timeStart)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeStart);
				timeStart = String.valueOf((time.getTime()/1000));
				where.put("Thesis.return_time",new String[]{">",timeStart} );
			}
			else{
				timeStart=null;
			}
			if(!StringUtil.isEmpty(timeEnd)){
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(timeEnd);
				timeEnd = String.valueOf((time.getTime()/1000));
				where.put("Thesis.return_time",new String[]{"<",timeEnd} );
			}
			else{
				timeEnd = null;
			}
			String id = request.getSession().getAttribute("userId").toString();
			User user = userService.findById(Integer.valueOf(id));
			where.put("user_id", id);
			if(user.getUserType()==0){
				//研究生部
				where.put("last_status", 4);	
			}
			else if(user.getUserType()==1){
				//学院
				where.put("last_status", 5);
			}
			else if(user.getUserType()==2){
				//老师
				where.put("now_status", 9);
			}
			return ajaxReturn(response, thesisService.getReceiveGridData(where,UIUtils.getPageParams(request)));
		}
		else{
			return "thesis/list_receive_oto_teacher";
		}
	}
	
	@RequestMapping(value={"check_user"})
	public String checkUser(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String universityId = request.getParameter("universityId");
		String collegeId =  request.getParameter("collegeId");
		int i = userService.findUser(universityId, collegeId);
		//System.out.println(universityId+":::"+collegeId+":::"+i);
		User user = userService.findById(i);
		if(user.getIsValue()==1){
			return ajaxReturn(response, null,"该账号已经激活，请填写对方送审码！对方手机号为："+user.getUserPhone(),1);
		}
		else{
			return ajaxReturn(response, null,"该用户尚未激活，您可以输入对方手机号码邀请激活！",0);
		}
	}
	
	@RequestMapping(value={"download_commentss"})
	public void downloadComentss(HttpServletRequest request, HttpServletResponse response)  {  
			try {
				String ids = request.getParameter("id");
				String id[] = ids.split(",");
//				System.out.println(ids);
				for(int i=0;i<id.length;i++){
//					Result result = resultService.findById(Integer.valueOf(id[i]));
					Thesis thesis = thesisService.findById(Integer.valueOf(id[i]));
					String files[]  = thesisService.getCommentsFile(thesis.getId());
					File[] srcfile=new File[files.length];
					for(int j=0;j<files.length;j++){
						srcfile[j] = new File(files[j]);
//						srcfile[j+(files.length/2)] = 
					}
					File zipfile=new File("D:\\export"+i+".zip");
			        FileToZip.zipFiles(srcfile, zipfile);
				}
				File[] srcfile=new File[id.length];
				for(int j=0;j<id.length;j++){
					srcfile[j] = new File("D:\\export"+j+".zip");
				}
				File zipfile=new File("D:\\export.zip");
		        FileToZip.zipFiles(srcfile, zipfile);
				String tempPath = "D:\\export.zip";
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+tempPath);
				InputStream is = new java.io.BufferedInputStream(
						new java.io.FileInputStream((tempPath)));

				int read = 0;
				byte[] bytes = new byte[2048];

				OutputStream os = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				os.flush();
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
}
