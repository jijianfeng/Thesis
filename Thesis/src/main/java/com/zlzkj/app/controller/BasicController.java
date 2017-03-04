package com.zlzkj.app.controller;

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

import com.alibaba.fastjson.JSON;
import com.zlzkj.app.model.About;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Major;
import com.zlzkj.app.model.StatusProcess;
import com.zlzkj.app.model.Title;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.service.AboutService;
import com.zlzkj.app.service.CollegeService;
import com.zlzkj.app.service.MajorService;
import com.zlzkj.app.service.StatusProcessService;
import com.zlzkj.app.service.TitleService;
import com.zlzkj.app.service.UniversityService;
import com.zlzkj.app.service.UserService;
//import com.zlzkj.app.model.User;
//import com.zlzkj.app.service.UserService;
import com.zlzkj.app.util.StringUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.app.util.UploadUtils;
import com.zlzkj.core.base.BaseController;
import com.zlzkj.core.util.Fn;

/**
 * 基本管理
 * @author jjf
 *
 */
@Controller
@RequestMapping(value={"basic"})
public class BasicController extends BaseController{
	
	@Autowired
	private UniversityService universityService;
	
	@Autowired
	private CollegeService collegeService;
	
	@Autowired
	private MajorService majorService;
	
	@Autowired
	private TitleService titleService;
	
	@Autowired
	private AboutService aboutService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StatusProcessService statusProcessService;

	/**
	 * 学院列表
	 */
	@RequestMapping(value={"college"})
	public String college(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			String areaId =request.getParameter("areaId");
			String universityId =request.getParameter("universityId");
			String college = request.getParameter("college");
			Map<String, Object> where = new HashMap<String,Object>();
			if(universityId!=null&&(!universityId.equals("0"))&&(!universityId.equals(""))){
				where.put("university_id",universityId);
			}
			if(!StringUtil.isEmpty(college)){
				where.put("college_name",new String[]{"like","%"+college+"%"});
			}
			if(StringUtil.isEmpty(areaId)||areaId.equals("0")){
				areaId = null;
			}
			return ajaxReturn(response, collegeService.getUIGridData(where, UIUtils.getPageParams(request),areaId));
		}
		else{
			return "basic/college";
		}
	}
	
	/**
	 * 地区列表
	 */
	@RequestMapping(value={"list"})
	public String list(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
			String university =request.getParameter("university");
			String universitycode =request.getParameter("universityCode");
			System.out.println(universitycode+":"+university+"呵呵");
			if(universitycode!=null&&(!universitycode.equals("0"))&&(!universitycode.equals(""))){
				where.put("university_code", universitycode);
			}
			if(university!=null&&(!university.equals("0"))&&(!university.equals(""))){
				where.put("university_name",new String[]{"like","%"+university+"%"});
			}
			return ajaxReturn(response, universityService.getUIGridData(where, UIUtils.getPageParams(request)));
		}
		else{
			return "basic/list";
		}
	}
	
	/**
	 * 专业列表
	 */
	@RequestMapping(value={"major"})
	public String major(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
//			String areaId =request.getParameter("areaId");
//			String universityId =request.getParameter("universityId");
//			String collegeId = request.getParameter("collegeId");
			String major = request.getParameter("major");
//			System.out.println(areaId+":"+universityId+"："+collegeId+":"+major);
			if(major!=null&&(!major.equals("0"))&&(!major.equals(""))){
				where.put("major_name",new String[]{"like","%"+major+"%"});
			}
//			if(collegeId!=null&&(!collegeId.equals("0"))&&(!collegeId.equals(""))){
//				where.put("college_id",collegeId);
//			}
//			if(StringUtil.isEmpty(areaId)||areaId.equals("0")){
//				areaId = null;
//			}
//			if(StringUtil.isEmpty(universityId)||universityId.equals("0")){
//				universityId = null;
//			}
			where.put("major_status", "2");
			return ajaxReturn(response, majorService.getUIGridData(where, UIUtils.getPageParams(request)));
		}
		else{
			return "basic/major";
		}
	}
	
	/**
	 * 职称列表
	 */
	@RequestMapping(value={"title"})
	public String title(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
			return ajaxReturn(response, titleService.getUIGridData(where, UIUtils.getPageParams(request)));
		}
		else{
			return "basic/title";
		}
	}
	
	/**
	 * 职称添加
	 */
	@RequestMapping(value={"title_add"})
	public String titleAdd(Model model,HttpServletRequest request,HttpServletResponse response ,Title title) throws Exception {
		if(request.getMethod()=="POST"){
			int i = titleService.save(title);
			if(i==1){
				return ajaxReturn(response, null,"添加成功",1);
			}
			else{
				return ajaxReturn(response, null,"添加失败",0);
			}
		}
		else{
			return "basic/title_add";
		}
	}
	/**
	 * 职称编辑
	 */
	@RequestMapping(value={"title_edit"})
	public String titleEdit(Model model,HttpServletRequest request,HttpServletResponse response ,Title title) throws Exception {
		if(request.getMethod()=="POST"){
			int i = titleService.update(title);
			if(i==1){
				return ajaxReturn(response, null,"修改成功",1);
			}
			else{
				return ajaxReturn(response, null,"修改失败",0);
			}
		}
		else{
			String id = request.getParameter("id");
			Title ti = titleService.findById(Integer.valueOf(id));
			model.addAttribute("title",ti);
			return "basic/title_edit";
		}
	}
	/**
	 * 职称删除
	 */
	@RequestMapping(value={"title_delete"})
	public String titleDelete(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			int i = titleService.delete(Integer.valueOf(request.getParameter("id").toString()));
			if(i==1){
				return ajaxReturn(response, null,"删除成功",1);
			}
			else{
				return ajaxReturn(response, null,"删除失败",0);
			}
		}
		else{
			return "basic/title";
		}
	}
	/**
	 * 送审情况信息
	 */
	@RequestMapping(value={"master"},method=RequestMethod.GET)
	public String masterGet(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		About about  = aboutService.findById(1);
		User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
		model.addAttribute("user",user);
//		System.out.println(user.getIsSendUrl()+"呵呵");
//		System.out.println("get");
		model.addAttribute("about",about);
		return "basic/master";
	}
	
	/**
	 * 送审情况信息
	 */
	@RequestMapping(value={"master"},method=RequestMethod.POST)
	public String master(Model model,HttpServletRequest request,HttpServletResponse response
			,@RequestParam("file") MultipartFile file 
			,@RequestParam("example") MultipartFile example
			,@RequestParam("image") MultipartFile image) throws Exception {
		About about  = aboutService.findById(1);
		//判断url
		User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
//		System.out.println("准备");
		if(!file.isEmpty()){
//			Map<String, Object> zip = new HashMap<String, Object>();		
			String zipName = "";
			Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(file);
			//判断
			//System.out.println(((Integer)zipInfo.get("status")>0)+"判断");
//			System.out.println("上传成功");
			if(((Integer)zipInfo.get("status")>0)){ //上传完成		
				//网络地址
				zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
				//物理地址
				String osName = System.getProperty("os.name").toLowerCase();
				if(osName.indexOf("windows")!=-1){
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
				}
				else{
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
				}
				if(user.getUserType()==3){
					about.setSendUrl(zipName);
				}
				else{
//					user.setSendUrl(zipName);
//					userService.update(user);
				}
				//是否启用自定义模块
			}else{ //上传出错
				return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
			}
		}
		if((!example.isEmpty())&&user.getUserType()==3){
			String zipName = "";
			Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(example);
			//判断
			if(((Integer)zipInfo.get("status")>0)){ //上传完成		
				//网络地址
				zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
				//物理地址
				String osName = System.getProperty("os.name").toLowerCase();
				if(osName.indexOf("windows")!=-1){
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
				}
				else{
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
				}
				if(user.getUserType()==3){
					//about.setSendUrl(zipName);
					about.setExampleUrl(zipName);
				}
				else{
					//user.setSendUrl(zipName);
					userService.update(user);
				}
				//是否启用自定义模块
			}else{ //上传出错
				return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
			}
		}
//		System.out.println("debug1111"+image.isEmpty());
		if((!image.isEmpty())&&user.getUserType()==2){
			String zipName = "";
			Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(image);
			//判断
			if(((Integer)zipInfo.get("status")>0)){ //上传完成		
				//网络地址
				zipName = UploadUtils.parseFileUrl(zipInfo.get("saveName").toString());
				//物理地址
				String osName = System.getProperty("os.name").toLowerCase();
				if(osName.indexOf("windows")!=-1){
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"\\"+zipInfo.get("saveName").toString();
				}
				else{
					zipName = UploadUtils.getFullSavePath(zipInfo.get("saveName").toString())+"/"+zipInfo.get("saveName").toString();
				}
//				System.out.println("debug11");
				user.setUserImage(zipName);
				//是否启用自定义模块
			}else{ //上传出错
				return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
			}
		}
//		System.out.println(Integer.valueOf(request.getParameter("isSendUrl"))+"我的传说");
//		if(user.getUserType()==0||user.getUserType()==1){
//			if(1==Integer.valueOf(request.getParameter("isSendUrl"))){
//				if(StringUtil.isEmpty(user.getSendUrl())){
//					return ajaxReturn(response, null,"使用自定义模块前，请确认依据上传自定义模块！",0);
//				}
//			}
//			user.setIsSendUrl(Integer.valueOf(request.getParameter("isSendUrl")));
//		}
		userService.update(user);
		about.setSendLetter(request.getParameter("sendLetter"));
		about.setSendNumber(Integer.valueOf(request.getParameter("status")));
		int i =aboutService.update(about);
		if(i==1){
			return ajaxReturn(response, null,"修改成功",1);
		}
		else{
			return ajaxReturn(response, null,"修改失败",0); 
		}
	}
	
	/**
	 * 联系我们
	 */
	@RequestMapping(value={"link"})
	public String link(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			About about  = aboutService.findById(1);
			about.setLinkAddress(request.getParameter("linkAddress"));
			about.setLinkMail(request.getParameter("linkMail"));
			about.setLinkTel(request.getParameter("linkTel"));
			about.setLinkUser(request.getParameter("linkUser"));
			int i =aboutService.update(about);
			if(i==1){
				return ajaxReturn(response, null,"添加成功",1);
			}
			else{
				return ajaxReturn(response, null,"添加失败",0); 
			}
		}
		else{
			About about  = aboutService.findById(1);
			model.addAttribute("about",about);
			return "basic/link";
		}
	}
	
	/**
	 * 新增学校
	 */
	@RequestMapping(value={"list_add"})
	public String list_add(Model model,HttpServletRequest request,HttpServletResponse response ,University university) throws Exception {
		if(request.getMethod()=="POST"){
			int code = university.getUniversityCode();
			int id = universityService.getUniverstiyByCode(code);
			int i =0;
			if(id ==0){
				i = universityService.save(university);
				User user = new User();
				user.setUserName("ipy"+request.getParameter("universityCode"));
				int mobile_code = (int)((Math.random()*9+1)*100000);
				int mobile_code1 = (int)((Math.random()*9+1)*100000);
				user.setUserPassword(String.valueOf(mobile_code));
				user.setNickName(request.getParameter("universityName"));
				user.setIsValue(0);
				user.setUniversityId(Integer.valueOf(university.getId()));
				user.setSendCode(String.valueOf(mobile_code1));
				user.setRoleId(2);
				user.setUserType(0);
				userService.save(user);
			}
			else{
				return ajaxReturn(response, null,"已经存在相同的学校代码！",0);
			}
			if(i==1){
				return ajaxReturn(response, null,"添加成功",1);
			}
			else{
				return ajaxReturn(response, null,"添加失败",0); 
			}
		}
		return "basic/list_add";
	}
	/**
	 * 编辑学校
	 */
	@RequestMapping(value={"list_edit"})
	public String listEdit(Model model,HttpServletRequest request,HttpServletResponse response ,University university) throws Exception {
		if(request.getMethod()=="POST"){
			int i = universityService.update(university);
			if(i==1){
				return ajaxReturn(response, null,"修改成功",1);
			}
			else{
				return ajaxReturn(response, null,"修改失败",0);
			}
		}
		else{
			String id = request.getParameter("id");
			University un = universityService.findById(Integer.valueOf(id));
			model.addAttribute("university",un);
			return "basic/list_edit";
		}
	}
	/**
	 * 删除学校
	 */
	@RequestMapping(value={"list_delete"})
	public String listDelete(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		System.out.println("删除");
		if(request.getMethod()=="POST"){
			int i = universityService.delete(Integer.valueOf(request.getParameter("id").toString()));
			if(i==1){
				Map<String, Object> where = new HashMap<String,Object>();
				where.put("university_id", request.getParameter("id"));
				userService.deleteWhere(where);
				return ajaxReturn(response, null,"删除成功",1);
			}
			else{
				return ajaxReturn(response, null,"删除失败",0);
			}
		}
		else{
			return "basic/list";
		}
	}
	/**
	 * 新增学院
	 */
	@RequestMapping(value={"college_add"})
	public String collegeAdd(Model model,HttpServletRequest request,HttpServletResponse response ,College college) throws Exception {
		if(request.getMethod()=="POST"){
			int i = collegeService.save(college);
			if(i==1){
				User user = new User();
				user.setUserName(universityService.getNewCollegeName(Integer.valueOf(request.getParameter("universityId"))));
				int mobile_code = (int)((Math.random()*9+1)*100000);
				int mobile_code1 = (int)((Math.random()*9+1)*100000);
				user.setUserPassword(String.valueOf(mobile_code));
				user.setNickName(request.getParameter("collegeName"));
				user.setIsValue(0);
				user.setUniversityId(Integer.valueOf(request.getParameter("universityId")));
				user.setSendCode(String.valueOf(mobile_code1));
				user.setRoleId(3);
				user.setUserType(1);
				userService.save(user);
				return ajaxReturn(response, null,"添加成功",1);
			}
			else{
				return ajaxReturn(response, null,"添加失败",0); 
			}
		}
		return "basic/college_add";
	}
	/**
	 * 编辑学院
	 */
	@RequestMapping(value={"college_edit"})
	public String collegeEdit(Model model,HttpServletRequest request,HttpServletResponse response ,College college) throws Exception {
		if(request.getMethod()=="POST"){
			int i = collegeService.update(college);
			if(i==1){
				return ajaxReturn(response, null,"修改成功",1);
			}
			else{
				return ajaxReturn(response, null,"修改失败",0);
			}
		}
		else{
			String id = request.getParameter("id");
			College co = collegeService.findById(Integer.valueOf(id));
			University un = universityService.findById(co.getUniversityId());
			model.addAttribute("university",un);
			model.addAttribute("college",co);
			return "basic/college_edit";
		}
	}
	
	/**
	 * 删除学院
	 */
	@RequestMapping(value={"college_delete"})
	public String collegeDelete(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		if(request.getMethod()=="POST"){
			int i = collegeService.delete(Integer.valueOf(request.getParameter("id").toString()));
			if(i==1){
				Map<String, Object> where = new HashMap<String,Object>();
				where.put("college_id", request.getParameter("id"));
				userService.deleteWhere(where);
				return ajaxReturn(response, null,"删除成功",1);
			}
			else{
				return ajaxReturn(response, null,"删除失败",0);
			}
		}
		else{
			return "basic/college";
		}
	}
	/**
	 * 增加专业
	 */
	@RequestMapping(value={"major_add"})
	public String majorAdd(Model model,HttpServletRequest request,HttpServletResponse response ,Major major) throws Exception {
		if(request.getMethod()=="POST"){
			int i = majorService.save(major);
			if(i==1){
				return ajaxReturn(response, null,"添加成功",1);
			}
			else{
				return ajaxReturn(response, null,"添加失败",0); 
			}
		}
		return "basic/major_add";
	}
	/**
	 * 编辑专业
	 */
	@RequestMapping(value={"major_edit"})
	public String majorEdit(Model model,HttpServletRequest request,HttpServletResponse response ,Major major) throws Exception {
		if(request.getMethod()=="POST"){
			int i = majorService.update(major);
			if(i==1){
				return ajaxReturn(response, null,"修改成功",1);
			}
			else{
				return ajaxReturn(response, null,"修改失败",0);
			}
		}
		else{
			String id = request.getParameter("id");
			Major ma = majorService.findById(Integer.valueOf(id));
			model.addAttribute("major",ma);
//			College co = collegeService.findById(ma.getCollegeId());
//			model.addAttribute("college",co);
//			University un = universityService.findById(co.getUniversityId());
//			model.addAttribute("university",un);
			return "basic/major_edit";
		}
	}
	/**
	 * 删除专业
	 */
	@RequestMapping(value={"major_delete"})
	public String majorDelete(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			int i = majorService.delete(Integer.valueOf(request.getParameter("id").toString()));
			if(i==1){
				return ajaxReturn(response, null,"删除成功",1);
			}
			else{
				return ajaxReturn(response, null,"删除失败",0);
			}
		}
		else{
			return "basic/major";
		}
	}
	/**
	 * 通用模板的下载
	 */
	@RequestMapping(value={"download_normal"})
	public void downloadNormal(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				About about = aboutService.findById(1);
				String tempPath = about.getSendUrl();
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+about.getSendUrl());
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
	 * 当前个人自定义模板的下载
	 */
//	@RequestMapping(value={"download_user"})
//	public void downloadUser(HttpServletRequest request, HttpServletResponse response) throws IOException {  
//			try {
//				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
//				String tempPath = user.getSendUrl();
//				response.setContentType("application/msexcel");
//				response.setHeader("Content-disposition", "inline; filename="
//						+user.getSendUrl());
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
	 * 当前个人签名的下载
	 */
	@RequestMapping(value={"download_user_image"})
	public void downloadUserImage(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				String tempPath = user.getUserImage();
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+user.getUserImage());
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
}
