package com.zlzkj.app.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Diy;
import com.zlzkj.app.model.Thesis;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.service.CollegeService;
import com.zlzkj.app.service.DiyService;
import com.zlzkj.app.service.UniversityService;
import com.zlzkj.app.service.UserService;
import com.zlzkj.app.util.ExcelTransport;
import com.zlzkj.app.util.JsonUtil;
import com.zlzkj.app.util.StringUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.app.util.UploadUtils;
import com.zlzkj.app.util.sendsms;
import com.zlzkj.core.base.BaseController;
import com.zlzkj.core.util.Fn;

/**
 * 平台用户控制器
 */
@Controller
@RequestMapping(value={"user"})
public class UserController extends BaseController{

	@Autowired
	private UserService userService;

	@Autowired
	private UniversityService universityService;
	
	@Autowired
	private CollegeService collegeService ;
	
	@Autowired
	private DiyService diyService ;
 
	@RequestMapping(value={"/"})
	public String index(Model model,HttpServletRequest request,HttpServletResponse response) {

		return "user/user";
	}

	@RequestMapping(value={"find_by_id"},method = RequestMethod.POST)
	public String findById(Integer id, HttpServletRequest request, HttpServletResponse response) {
		User user = userService.findById(id);
		return ajaxReturn(response, user);
	}
	
	@RequestMapping(value={"list"})
	public String list(Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
			String userType = request.getParameter("userType");
			if(!StringUtil.isEmpty(userType)){
				if(userType.equals("1")){
					//研究生
					where.put("User.user_type", 0);
				}
				if(userType.equals("2")){
					//学院
					where.put("User.user_type", 1);
				}
				if(userType.equals("3")){
					//老师
					where.put("User.user_type", 2);
				}
			}
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			if(user.getUserType()!=3&&user.getUserType()!=4){
				//不是管理员
				where.put("User.is_admin", "0");
			}
			if(user.getUserType()==0){
				//研究生部
				where.put("User.university_id", user.getUniversityId());
			}
			else if(user.getUserType()==1){
				//学院
				where.put("User.university_id", user.getUniversityId());
				where.put("User.college_id", user.getCollegeId());
			}
			else if(user.getUserType()==2){
				//老师
				where.put("User.university_id", user.getUniversityId());
				where.put("User.college_id", user.getCollegeId());
				where.put("User.id", user.getId());
			}
//			String universityId = request.getParameter("universityId");
//			if(!StringUtil.isEmpty(universityId)){
//				where.put("university_id", universityId);
//			}
			String universityName = request.getParameter("universityName");
			if(!StringUtil.isEmpty(universityName)){
				where.put("University.university_name", new String[]{"like","%"+universityName+"%"});
			}
			return ajaxReturn(response, userService.getUIGridData(where,UIUtils.getPageParams(request),user.getUserType()));
		}
		
		else{
			return "user/list";
		}
	}
	
	@RequestMapping(value={"invite"})
	public String invite(Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
			String userType = request.getParameter("userType");
			if(!StringUtil.isEmpty(userType)){
				if(userType.equals("1")){
					//研究生
					where.put("user_type", 0);
				}
				if(userType.equals("2")){
					//学院
					where.put("user_type", 1);
				}
				if(userType.equals("3")){
					//老师
					where.put("user_type", 2);
				}
			}
//			String universityId = request.getParameter("universityId");
//			if(!StringUtil.isEmpty(universityId)){
//				where.put("university_id", universityId);
//			}
			String universityName = request.getParameter("universityName");
			if(StringUtil.isEmpty(universityName)){
				universityName = "";
			}
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			int type = user.getUserType();
			if(type==0||type==1||type==2){
				where.put("is_admin", "0");
			}
			return ajaxReturn(response, userService.getUIGridData(where,UIUtils.getPageParams(request),1));
		}
		else{
			return "user/invite";
		}
	}

	@RequestMapping(value={"save"})
	public String save(User entity, HttpServletRequest request, HttpServletResponse response){
        Integer count = null;
			try {
				count = userService.save(entity);
			} catch (Exception e) {
				return ajaxReturn(response,null,e.getLocalizedMessage(),-1);
			}

        if(count==0)
        	return ajaxReturn(response,null,"添加失败",0);
        else
        	return ajaxReturn(response, null,"添加成功",1);
	}


	@RequestMapping(value={"update"})
	public String update(User entity, HttpServletRequest request, HttpServletResponse response) {
		Integer count = null;
		try {
			count = userService.update(entity);
		} catch (Exception e) {
			return ajaxReturn(response,null,e.getLocalizedMessage(),-1);
		}

    if(count==0)
    	return ajaxReturn(response,null,"更新失败",0);
    else
    	return ajaxReturn(response, null,"更新成功",1);

	}
	@RequestMapping(value={"delete"})
	public String delete(HttpServletRequest request, HttpServletResponse response) {
		String delIds = request.getParameter("ids");
		String[] ids = delIds.split(",");
//		System.out.println(ids[0]);
		int count = 0;
		for(String id : ids){
			User user = userService.findById(Integer.valueOf(id));
			if(user.getUserType()==0){
				universityService.delete(Integer.valueOf(user.getUniversityId()));
			}
			else if(user.getUserType()==1){
				collegeService.delete(Integer.valueOf(user.getCollegeId()));
			}
			count += userService.delete(StringUtil.stringToInteger(id));
		}

		if(count==0)
			return ajaxReturn(response,null,"删除失败",0);
		else{
			return ajaxReturn(response, count+"","删除成功",1);
		}
	}
	
	@RequestMapping(value={"detail"})
	public String detail(Model model, HttpServletRequest request, HttpServletResponse response
			,User entity,Integer id) {
		if(request.getMethod().equals("POST")){
			//System.out.println("post");
			if(id != null){
				return ajaxReturn(response,userService.getDetailUIGridData(id));
			}
			return "user/detail";
		}else{
			//System.out.println("get");
			model.addAttribute("id",id);
			return "user/detail";
		}
	}
	
	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @param entity
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value={"add"})
	public String add(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			return "user/add";
		}else{
			return "user/add";
		}
	}
	
	@RequestMapping(value={"change_pass"})
	public String change(Model model, HttpServletRequest request,HttpServletResponse response,String name,String OPass, String NPass) throws Exception {
		if(request.getMethod()=="POST"){
			int  id = Integer.valueOf(request.getSession().getAttribute("userId").toString());
			User user = userService.findById(id);
			if(String.valueOf(user.getUserPassword()).equals(OPass)){
				user.setUserPassword(NPass);
				if(userService.update(user)==1){
					return ajaxReturn(response, null,"修改成功",1);
				}
				else{
					return ajaxReturn(response, null,"修改失败",0);
				}
			}else{
				return ajaxReturn(response, null,"原密码错误",0);
			}
		}
		else{
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			model.addAttribute("user", user);
			return "user/change_pass";
		}
	}
	
	@RequestMapping(value={"myself"})
	public String myself(Model model, HttpServletRequest request,HttpServletResponse response,String name,String OPass, String NPass) throws Exception {
		if(request.getMethod()=="POST"){
			int  id = Integer.valueOf(request.getSession().getAttribute("userId").toString());
			User user = userService.findById(id);
			if((!user.getCheckCode().equals(request.getParameter("checkCode")))||(Fn.time()-Integer.valueOf(user.getCheckCodeTime())>300)){
				//验证码错误或验证时间大于五分钟
				return ajaxReturn(response, null,"修改失败,验证码错误或超时",0);
			}
			if(user.getUserType()==2){
				//老师
				user.setTitlesId(Integer.valueOf(request.getParameter("titlesId")));
				user.setUserTitle(Integer.valueOf(request.getParameter("userTitle")));
				user.setMajorOne(Integer.valueOf(request.getParameter("majora")));
				user.setMajorTwo(Integer.valueOf(request.getParameter("majorb")));
			}
			if(user.getUserType()==1||user.getUserType()==0){
				user.setLocalTel(request.getParameter("localTel"));
				user.setWorkName(request.getParameter("workName"));
			}
			user.setUserMail(request.getParameter("userMail"));
			user.setUserAddress(request.getParameter("userAddress"));
			user.setIdentifyNo(request.getParameter("identifyNo"));
			user.setNickName(request.getParameter("nickName"));
			user.setUserSex(Integer.valueOf(request.getParameter("userSex").toString()));
			user.setVisaBank(request.getParameter("visaBank"));
			user.setVisaCard(request.getParameter("visaCard"));
			user.setUserPhone(request.getParameter("userPhone"));
			if(user.getUserPhone().length()!=11){
				return ajaxReturn(response, null,"手机号码格式错误",0);
			}
			if(userService.checkPhone(user.getUserPhone())){
				return ajaxReturn(response, null,"该号码已经被注册",0);
			}
			if(userService.update(user)==1){
				return ajaxReturn(response, null,"修改成功",1);
			}
			else{
				return ajaxReturn(response, null,"修改失败",0);
			}
		}
		else{
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			model.addAttribute("user", user);
			//model.addAttribute("birth", Fn.date(Integer.valueOf(user.getUserBirthday()), "yyyy-MM-dd"));
			return "user/myself";
		}
	}
	
	/**
	 * 用户备注
	 * @param model
	 * @param request
	 * @param response
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value={"mess_edit"})
	public String messEdit(Model model, HttpServletRequest request, HttpServletResponse response,User entity) throws Exception {
		if(request.getMethod()=="POST"){
//			String id = request.getParameter("id");
//			User user = userService.findById(Integer.valueOf(id));
			int i = (userService.update(entity));
			if(i==1){
				return ajaxReturn(response, null,"修改成功",1);
			}
			else{
				return ajaxReturn(response, null,"修改失败",0);
			}
		}
		else{
			String id = request.getParameter("id");
			User user = userService.findById(Integer.valueOf(id));
			model.addAttribute("user", user);
			return "user/mess_edit";
		}
	}
	
	/**
	 * 用户备注
	 * @param model
	 * @param request
	 * @param response
	 * @param entity
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value={"edit"})
	public String edit(Model model, HttpServletRequest request, HttpServletResponse response,User entity) throws Exception {
		if(request.getMethod()=="POST"){
			String id = request.getParameter("id");
			//System.out.println(id+"jjjjjjjjjjjj");
			User user = userService.findById(Integer.valueOf(id));
			user.setUserRemark(request.getParameter("userRemark"));
			//System.out.println(request.getParameter("userRemark")+"凉快啦啦啦啦啦啦啦啦啦啦啦啦啦啦啦");
			int i = (userService.update(user));
			if(i==1){
				return ajaxReturn(response, null,"修改成功",1);
			}
			else{
				return ajaxReturn(response, null,"修改失败",0);
			}
			//return ajaxReturn(response, null,"修改成功",1);
		}
		else{
			String id = request.getParameter("id");
			User user = userService.findById(Integer.valueOf(id));
			model.addAttribute("user", user);
			return "user/edit";
		}
	}
	
	/**
	 * 删除
	 * @param model
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping(value={"deletes"})
	public String listDelete(Model model,HttpServletRequest request, HttpServletResponse response
			,Integer id) {
		if(request.getMethod().equals("POST")){
			userService.delete(id);
//			int  adminId = userService.findId(id);
//			userService.delete(adminId);
			return ajaxReturn(response, null,"删除成功",1);
		}else{
			return "user/list";
		}
	}
	
	/**
	 * 显示
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
//	@RequestMapping(value={"select"})
//	public String enterpriseSelect(Model model, HttpServletRequest request, HttpServletResponse response
//			,String types) {
//		System.out.println(types);
//		if(types != null){
//			request.getSession().setAttribute("typessss", types);
//		}
//		if(request.getMethod().equals("POST")){
//			String status = (String) request.getSession().getAttribute("typessss");
////			request.getSession().removeAttribute("types");	
////			System.out.println("ty"+ty);
//			if(status.equals("1")){
//				return ajaxReturn(response, enterpriseService.getUIGridData(null, UIUtils.getPageParams(request)));
//			} else if(status.equals("2")){
//				return ajaxReturn(response, departmentService.getGridData("> 0", UIUtils.getPageParams(request)));
//			} else {
//				return ajaxReturn(response, departmentService.getGridData("= 0", UIUtils.getPageParams(request)));
//			}
//		
//		}
//			return "user/select";
//	}
	
	
	@RequestMapping(value={"roleList"})
	public String roleList(Model model, HttpServletRequest request, HttpServletResponse response) {
		if(request.getMethod().equals("POST")){
			Map<String, Object>	userList = userService.getUIGridData(null,UIUtils.getPageParams(request),1);
			model.addAttribute("userList",userList);
			return ajaxReturn(response, userList);
		}else{
			return "user/list";
		}
	}
	
	@RequestMapping(value={"accept"})
	public String accept(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String id = request.getParameter("id");
			User user = userService.findById(Integer.valueOf(id));
			user.setIsValue(1);
			
			int i = userService.update(user);
			if(i==1){
				return ajaxReturn(response,null,"启用成功",1);
			}
			else{
				return ajaxReturn(response,null,"启用失败",0);
			}
		}else{
			return "user/list";
		}
	}
	
	@RequestMapping(value={"cancel"})
	public String cancel(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String id = request.getParameter("id");
			User user = userService.findById(Integer.valueOf(id));
			user.setIsValue(0);
			System.out.println("我的权限"+user.getUserType());
			if(user.getUserType()==3){
				return ajaxReturn(response,null,"权限不足",0);
			}
			int i = userService.update(user);
			if(i==1){
				return ajaxReturn(response,null,"禁用成功",1);
			}
			else{
				return ajaxReturn(response,null,"禁用失败",0);
			}
		}else{
			return "user/list";
		}
	}
	
	@RequestMapping(value={"restart"})
	public String restart(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String id = request.getParameter("id");
			User user = userService.findById(Integer.valueOf(id));
			if(user.getUserType()==3){
				return ajaxReturn(response,null,"你没有权限",0);
			}
			User admin = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			if(admin.getUserType()!=3){
				return ajaxReturn(response,null,"非法访问,你没有权限",0);
			}
			else{
				int mobile_code = (int)((Math.random()*9+1)*100000);
				user.setUserPassword(String.valueOf(mobile_code));
				int i = userService.update(user);
				if(i==1){
					return ajaxReturn(response,null,"修改成功，新密码为 "+mobile_code,1);
				}
				else{
					return ajaxReturn(response,null,"修改失败，请稍后再试",0);
				}
			}
		}else{
			return "user/list";
		}
	}
	
	@RequestMapping(value={"send_code"})
	public String sendCode(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String id = request.getParameter("id");
			User user = userService.findById(Integer.valueOf(id));
			int mobile_code = (int)((Math.random()*9+1)*100000);
			String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。"); 
			//int number = sendMessage(content,"17826800420");
			if(Fn.time()-Integer.valueOf(user.getCheckCodeTime())<=60){
				System.out.println("呵呵");
				return ajaxReturn(response,null,"您的请求，过于频繁，请稍后再试！",0);
			}
			if(user.getUserPhone().length()==11){
				int number = sendsms.sendMessage(content, user.getUserPhone());
				System.out.println("number"+number);
				if(number==1){
					user.setCheckCode(String.valueOf(mobile_code));
					user.setCheckCodeTime(String.valueOf(Fn.time()));
					userService.update(user);
					return ajaxReturn(response,null,"发送成功！",1);
				}
				else{
					return ajaxReturn(response,null,"发送失败！每个手机每天最多发五条短信",0);
				}
			}
			else{
				String phone = request.getParameter("userPhone");
				if(phone.length()!=11){
					return ajaxReturn(response,null,"您输入的手机号码错误！",0);
				}
				int number = sendsms.sendMessage(content,phone);
				System.out.println("number"+number);
				if(number==1){
					user.setCheckCode(String.valueOf(mobile_code));
					user.setCheckCodeTime(String.valueOf(Fn.time()));
					userService.update(user);
					return ajaxReturn(response,null,"发送成功！",1);
					//return ajaxReturn(response,null,"发送失败！",0);
				}
				else{
					return ajaxReturn(response,null,"发送失败！每个手机每天最多发五条短信",0);
				}
				
			}
		}else{
			return ajaxReturn(response,null,"非法访问！",0);
		}
	}
	
	@RequestMapping(value={"college_user_add"})
	public String collegeUserAdd(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String universityId = request.getParameter("universityId");
			String collegeName = request.getParameter("collegeName");
			String linkMan = request.getParameter("linkMan");
			String linkTel = request.getParameter("linkTel");
			String linkAddress = request.getParameter("linkAddress");
			if(linkTel.length()!=11){
				return ajaxReturn(response,null,"请输入正确的手机号",0);
			}
			if(userService.checkPhone(linkTel)){
				return ajaxReturn(response,null,"该手机号已经被注册",0);
			}
			//新建学院
			College college = new College();
			college.setCollegeName(collegeName);
			college.setLinkAddress(linkAddress);
			college.setLinkMan(linkMan);
			college.setLinkTel(linkTel);
			college.setUniversityId(Integer.valueOf(universityId));
			int mobile_code = (int)((Math.random()*9+1)*100000);
			String content ="您的账户已经开通，账号为："+universityService.getNewCollegeName(Integer.valueOf(universityId))+
					" 密码为："+mobile_code+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
			int j = sendsms.sendMessage(content, linkTel);
			if(j!=1){
				return ajaxReturn(response,null,"手机验证错误，请重试！",0);
			}
			collegeService.save(college);
			User user = new User();
			user.setUserName(universityService.getNewCollegeName(Integer.valueOf(universityId)));
			//int mobile_code = (int)((Math.random()*9+1)*100000);
			user.setUserPassword(String.valueOf(mobile_code));
			user.setNickName(collegeName);
			user.setRoleId(3);
			user.setUniversityId(Integer.valueOf(universityId));
			user.setCollegeId(college.getId());
			user.setUserType(1);
			user.setIsValue(1);
			user.setUserPhone(linkTel);
			int mobile_code1 = (int)((Math.random()*9+1)*100000);
			user.setSendCode(String.valueOf(mobile_code1));
			int i = userService.save(user);
			if(i==1){
				return ajaxReturn(response,null,"添加成功！密码已经发送给负责人手机",1);
			}
			else{
				return ajaxReturn(response,null,"添加失败！",0);
			}
		}else{
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			University university = universityService.findById(user.getUniversityId());
			model.addAttribute("university", university);
			return "user/college_user_add";
		}
	}
	
	@RequestMapping(value={"teacher_user_add"})
	public String teacherUserAdd(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String universityId = request.getParameter("universityId");
			String collegeId = request.getParameter("collegeId");
			String linkMan = request.getParameter("linkMan");
			String linkTel = request.getParameter("linkTel");
			if(linkTel.length()!=11){
				return ajaxReturn(response,null,"请输入正确的手机号",0);
			}
			int mobile_code = (int)((Math.random()*9+1)*100000);
			String content ="您的账户已经开通，账号为："+universityService.getNewCollegeName(Integer.valueOf(universityId))+
					" 密码为："+mobile_code+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
			int j = sendsms.sendMessage(content, linkTel);
			if(j!=1){
				return ajaxReturn(response,null,"手机验证错误，请重试！",0);
			}
			if(userService.checkPhone(linkTel)){
				return ajaxReturn(response,null,"该手机号已经被注册",0);
			}
			User user = new User();
			user.setUserPhone(linkTel);
			user.setUserName(universityService.getNewCollegeName(Integer.valueOf(universityId)));
			user.setUserPassword(String.valueOf(mobile_code));
			user.setNickName(linkMan);
			user.setRoleId(4);
			user.setUniversityId(Integer.valueOf(universityId));
			user.setCollegeId(Integer.valueOf(collegeId));
			int mobile_code1 = (int)((Math.random()*9+1)*100000);
			user.setSendCode(String.valueOf(mobile_code1));
			user.setUserType(2);
			user.setIsValue(1);
			int i = userService.save(user);
			if(i==1){
				return ajaxReturn(response,null,"添加成功！密码已经发送给负责人手机",1);
			}
			else{
				return ajaxReturn(response,null,"添加失败！",0);
			}
		}else{
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			University university = universityService.findById(user.getUniversityId());
			College college = collegeService.findById(user.getCollegeId());
			model.addAttribute("college", college);
			model.addAttribute("university", university);
			return "user/teacher_user_add";
		}
	}
	
	@RequestMapping(value={"reset_phone"})
	public String resetPhone(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String id = request.getParameter("id");
			User user = userService.findById(Integer.valueOf(id));
			if(user.getUserType()==3){
				return ajaxReturn(response,null,"你没有权限",0);
			}
			User admin = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			if(admin.getUserType()!=3){
				return ajaxReturn(response,null,"非法访问,你没有权限",0);
			}
			else{
				user.setUserPhone("");
				int i = userService.update(user);
				if(i==1){
					return ajaxReturn(response,null,"重置成功，请尽快绑定手机！",1);
				}
				else{
					return ajaxReturn(response,null,"重置失败，请稍后再试",0);
				}
			}
		}else{
			return "user/list";
		}
	}
	
	@RequestMapping(value={"invite_people"})
	public String invitePeople(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String id = request.getParameter("id");
			User user = userService.findById(Integer.valueOf(id));
			user.setIsValue(1);
			user.setUserPhone(request.getParameter("userTel"));
			if(user.getUserPhone().length()!=11){
				return ajaxReturn(response,null,"手机格式错误",0);
			}
			if(userService.checkPhone(user.getUserPhone())){
				return ajaxReturn(response,null,"此手机号已经被注册！",0);
			}
			String content  ="您的账户已经开通，账号为："+user.getUserName()+
					" 密码为："+user.getUserPassword()+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
			int mess = sendsms.sendMessage(content, request.getParameter("userTel").toString());
			if(mess!=1){
				return ajaxReturn(response,null,"手机格式错误",0);
			}
			int i = userService.update(user);
			if(i==1){
				return ajaxReturn(response,null,"邀请成功",1);
			}
			else{
				return ajaxReturn(response,null,"邀请失败",0);
			}
		}else{
			return "user/invite_people";
		}
	}
	
	@RequestMapping(value={"college_user_adds"},method=RequestMethod.GET)
	public String collegeUserAdds(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
		model.addAttribute("universitytId", user.getUniversityId());
		return "user/college_user_adds";
	}
	
	@RequestMapping(value={"college_user_adds"},method=RequestMethod.POST)
	public String collegeUserAdds(Model model,HttpServletRequest request,HttpServletResponse response,
			@RequestParam("file") MultipartFile file) throws Exception {
		Map<String, Object> excel = new HashMap<String, Object>();
		String excelName = "";
		Map<String,Object> excelInfo = UploadUtils.saveMultipartFile(file);
		if((Integer)excelInfo.get("status")>0){ //上传完成
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
		}else{ //上传出错
			return ajaxReturn(response,null,excelInfo.get("errorMsg").toString(),0);
		}
		excel.put("url", excelName);
		List<Object> list = ExcelTransport.readExcel(excelName, 0, "collegeName,linkMan,linkTel,linkAddress");
		System.out.println(list.toString());
		int error = 0;
		for(int i=0;i<list.size();i++){
			Object object = list.get(i);
			Map th =(Map) object;
			String universityId = request.getParameter("universityId");
			String collegeName = th.get("collegeName").toString();
			String linkMan = th.get("linkMan").toString();
			String linkTel = th.get("linkTel").toString();
			String linkAddress = th.get("linkAddress").toString();
			System.out.println(universityId+":::"+collegeName+":::"+linkMan+":::"+linkTel+":::"+linkAddress);
			if(universityId==null){
				error++;
				continue;
			}
			if(collegeName==null){
				error++;
				continue;
			}
			if(linkMan==null){
				error++;
				continue;
			}
			if(linkTel==null){
				error++;
				continue;
			}
			if(linkAddress==null){
				error++;
				continue;
			}
			if(linkTel.length()!=11){
				error++;
				continue;
				//return ajaxReturn(response,null,"请输入正确的手机号",0);
			}
			if(userService.checkPhone(linkTel)){
				error++;
				continue;
				//return ajaxReturn(response,null,"该手机号已经被注册",0);
			}
			//新建学院
			College college = new College();
			college.setCollegeName(collegeName);
			college.setLinkAddress(linkAddress);
			college.setLinkMan(linkMan);
			college.setLinkTel(linkTel);
			college.setUniversityId(Integer.valueOf(universityId));
			int mobile_code = (int)((Math.random()*9+1)*100000);
			String content ="您的账户已经开通，账号为："+universityService.getNewCollegeName(Integer.valueOf(universityId))+
					" 密码为："+mobile_code+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
			int j = sendsms.sendMessage(content, linkTel);
			if(j!=1){
				error++;
				continue;
				//return ajaxReturn(response,null,"手机验证错误，请重试！",0);
			}
			collegeService.save(college);
			User user = new User();
			user.setUserName(universityService.getNewCollegeName(Integer.valueOf(universityId)));
			//int mobile_code = (int)((Math.random()*9+1)*100000);
			user.setUserPassword(String.valueOf(mobile_code));
			user.setNickName(collegeName);
			user.setRoleId(3);
			user.setUniversityId(Integer.valueOf(universityId));
			user.setCollegeId(college.getId());
			user.setUserType(1);
			user.setIsValue(1);
			user.setUserPhone(linkTel);
			int mobile_code1 = (int)((Math.random()*9+1)*100000);
			user.setSendCode(String.valueOf(mobile_code1));
			int l = userService.save(user);
			if(l==1){
				//return ajaxReturn(response,null,"添加成功！密码已经发送给负责人手机",1);
			}
			else{
				error++;
				continue;
				//return ajaxReturn(response,null,"添加失败！",0);
			}
		}
		if(error!=0){
			return ajaxReturn(response,null,"有"+error+"条学院信息不够正确，无法输入",0);
		}
		else{
			return ajaxReturn(response,null,"上传成功,共上传"+list.size()+"条数据",1);
		}
	}
	
	@RequestMapping(value={"teacher_user_adds"},method=RequestMethod.GET)
	public String teacherUserAdds(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
		model.addAttribute("collegeId", user.getCollegeId());
		model.addAttribute("universityId", user.getUniversityId());
		return "user/teacher_user_adds";
	}
	
	@RequestMapping(value={"teacher_user_adds"},method=RequestMethod.POST)
	public String teacherUserAdds(Model model,HttpServletRequest request,HttpServletResponse response,
			@RequestParam("file") MultipartFile file) throws Exception {
		Map<String, Object> excel = new HashMap<String, Object>();
		String excelName = "";
		Map<String,Object> excelInfo = UploadUtils.saveMultipartFile(file);
		if((Integer)excelInfo.get("status")>0){ //上传完成
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
		}else{ //上传出错
			return ajaxReturn(response,null,excelInfo.get("errorMsg").toString(),0);
		}
		excel.put("url", excelName);
		List<Object> list = ExcelTransport.readExcel(excelName, 0, "linkMan,linkTel,linkAddress");
		int error = 0;
		for(int i=0;i<list.size();i++){
			Object object = list.get(i);
			Map th =(Map) object;
			String universityId = request.getParameter("universityId");
			String collegeId = request.getParameter("collegeId");
			String linkMan = th.get("linkMan").toString();
			String linkTel = th.get("linkTel").toString();
			String linkAddress = th.get("linkAddress").toString();
			System.out.println(universityId+":::"+linkMan+":::"+linkTel+":::"+linkAddress);
			if(linkTel.length()!=11){
				error++;
				continue;
				//return ajaxReturn(response,null,"请输入正确的手机号",0);
			}
			int mobile_code = (int)((Math.random()*9+1)*100000);
			String content ="您的账户已经开通，账号为："+universityService.getNewCollegeName(Integer.valueOf(universityId))+
					" 密码为："+mobile_code+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
			int j=1;// = sendsms.sendMessage(content, linkTel);
			if(j!=1){
				error++;
				continue;
				//return ajaxReturn(response,null,"手机验证错误，请重试！",0);
			}
			if(userService.checkPhone(linkTel)){
				error++;
				continue;
				//return ajaxReturn(response,null,"该手机号已经被注册",0);
			}
			User user = new User();
			user.setUserPhone(linkTel);
			user.setUserName(universityService.getNewCollegeName(Integer.valueOf(universityId)));
			user.setUserPassword(String.valueOf(mobile_code));
			user.setNickName(linkMan);
			user.setRoleId(4);
			user.setUniversityId(Integer.valueOf(universityId));
			user.setCollegeId(Integer.valueOf(collegeId));
			int mobile_code1 = (int)((Math.random()*9+1)*100000);
			user.setSendCode(String.valueOf(mobile_code1));
			user.setUserType(2);
			user.setIsValue(1);
			int L = userService.save(user);
			if(L==1){
				//return ajaxReturn(response,null,"添加成功！密码已经发送给负责人手机",1);
			}
			else{
				error++;
				continue;
				//return ajaxReturn(response,null,"添加失败！",0);
			}
		}
		if(error!=0){
			return ajaxReturn(response,null,"有"+error+"条专家信息不够正确，无法输入",0);
		}
		else{
			return ajaxReturn(response,null,"上传成功,共上传"+list.size()+"条数据",1);
		}
	}
	
	@RequestMapping(value={"diy_comments"})
	public String diyComments(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		if(request.getMethod()=="POST"){
			Map<String, Object> where = new HashMap<String,Object>();
			//User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			where.put("user_id", request.getSession().getAttribute("userId").toString());
			return ajaxReturn(response, userService.getDiyUIGridData(where,UIUtils.getPageParams(request)));
		}
		else{
			return "user/diy_comments";
		}
	}
	@RequestMapping(value={"diy_add"},method=RequestMethod.GET)
	public String diyAdd(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
//		model.addAttribute("id", request.getParameter("id"));
		return "user/diy_add";
	}
	
	@RequestMapping(value={"diy_edit"},method=RequestMethod.GET)
	public String diyEdit(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		model.addAttribute("id", request.getParameter("id"));
		Diy diy = diyService.findById(Integer.valueOf(request.getParameter("id")));
		model.addAttribute("remark",diy.getRemark());
		return "user/diy_edit";
	}
	
	@RequestMapping(value={"diy_add"},method=RequestMethod.POST)
	public String diyAdd(Model model,HttpServletRequest request,HttpServletResponse response,
			@RequestParam("file") MultipartFile file) throws Exception {
		Map<String, Object> zip = new HashMap<String, Object>();		
		String zipName = "";
		if(!file.isEmpty()){
			Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(file);
			//判断
//			System.out.println(((Integer)zipInfo.get("status")>0)+"判断");
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
				Diy diy = new Diy();
				diy.setUrl(zipName);
				diy.setUploadTime(String.valueOf(Fn.time()));
				User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
				diy.setUserId(user.getId());
				diy.setRemark(request.getParameter("remark"));
				diyService.save(diy);
				zip.put("url", zipName);
				zip.put("alt", "");
				return ajaxReturn(response,zip,"上传成功",1);
			}else{ //上传出错
				return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
			}
		}
		else{
			return ajaxReturn(response,null,"模板不能为空",0);
		}
	}
	
	@RequestMapping(value={"diy_edit"},method=RequestMethod.POST)
	public String diyEdit(Model model,HttpServletRequest request,HttpServletResponse response,
			@RequestParam("file") MultipartFile file) throws Exception {
		Map<String, Object> zip = new HashMap<String, Object>();		
		String zipName = "";
		if(!file.isEmpty()){
			Map<String,Object> zipInfo = UploadUtils.saveMultipartFile(file);
			//判断
//			System.out.println(((Integer)zipInfo.get("status")>0)+"判断");
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
				Diy diy = diyService.findById(Integer.valueOf(request.getParameter("id")));
				diy.setUrl(zipName);
				diy.setRemark(request.getParameter("remark"));
				diyService.update(diy);
				zip.put("url", zipName);
				zip.put("alt", "");
				return ajaxReturn(response,zip,"上传成功",1);
			}else{ //上传出错
				return ajaxReturn(response,null,zipInfo.get("errorMsg").toString(),0);
			}
		}
		else{
			return ajaxReturn(response,null,"模板不能为空",0);
		}
	}
	
	@RequestMapping(value={"diy_delete"})
	public String diyDelete(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		if(request.getMethod()=="POST"){
			int i = diyService.delete(Integer.valueOf(request.getParameter("id")));
			if(i==1){
				return ajaxReturn(response,null,"删除成功",1);
			}
			else{
				return ajaxReturn(response,null,"删除失败",0);
			}
		}
		else{
			return "user/diy_comments";
		}
	}
	
	/**
	 * 论文的下载
	 */
	@RequestMapping(value={"download_diy"})
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {  
			try {
				String id = request.getParameter("id");
				Diy diy = diyService.findById(Integer.valueOf(id));
				String tempPath = diy.getUrl();
				response.setContentType("application/msexcel");
				response.setHeader("Content-disposition", "inline; filename="
						+tempPath);
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
	
	@RequestMapping(value={"teacher_user_adds_admin"},method=RequestMethod.GET)
	public String teacherUserAddsAdmin(Model model,HttpServletRequest request,HttpServletResponse response ) throws Exception {
		User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
		model.addAttribute("collegeId", user.getCollegeId());
		model.addAttribute("universityId", user.getUniversityId());
		return "user/teacher_user_adds_admin";
	}
	
	@RequestMapping(value={"teacher_user_add_admin"})
	public String teacherUserAddAdmin(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(request.getMethod().equals("POST")){
			String universityId = request.getParameter("universityaaa");
			String collegeId = request.getParameter("collegeaaa");
			String linkMan = request.getParameter("linkMan");
			String linkTel = request.getParameter("linkTel");
			String isAdmin = request.getParameter("isAdmin");
			if(Fn.time()>0){
				System.out.println(isAdmin);
				System.out.println(universityId);
				System.out.println(collegeId);
				System.out.println(linkMan);
				System.out.println(linkTel);
			}
			if(linkTel.length()!=11){
				return ajaxReturn(response,null,"请输入正确的手机号",0);
			}
			int mobile_code = (int)((Math.random()*9+1)*100000);
			String content ="您的账户已经开通，账号为："+universityService.getNewCollegeName(Integer.valueOf(universityId))+
					" 密码为："+mobile_code+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
			int j = sendsms.sendMessage(content, linkTel);
			if(j!=1){
				return ajaxReturn(response,null,"手机验证错误，请重试！",0);
			}
			if(userService.checkPhone(linkTel)){
				return ajaxReturn(response,null,"该手机号已经被注册",0);
			}
			User user = new User();
			user.setIsAdmin(Integer.valueOf(isAdmin));
			user.setUserPhone(linkTel);
			user.setUserName(universityService.getNewCollegeName(Integer.valueOf(universityId)));
			user.setUserPassword(String.valueOf(mobile_code));
			user.setNickName(linkMan);
			user.setRoleId(4);
			user.setUniversityId(Integer.valueOf(universityId));
			user.setCollegeId(Integer.valueOf(collegeId));
			int mobile_code1 = (int)((Math.random()*9+1)*100000);
			user.setSendCode(String.valueOf(mobile_code1));
			user.setUserType(2);
			user.setIsValue(1);
			int i = userService.save(user);
			if(i==1){
				return ajaxReturn(response,null,"添加成功！密码已经发送给负责人手机",1);
			}
			else{
				return ajaxReturn(response,null,"添加失败！",0);
			}
		}else{
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			University university = universityService.findById(user.getUniversityId());
			College college = collegeService.findById(user.getCollegeId());
			model.addAttribute("college", college);
			model.addAttribute("university", university);
			return "user/teacher_user_add_admin";
		}
	}
	
	@RequestMapping(value={"teacher_user_adds_admin"},method=RequestMethod.POST)
	public String teacherUserAddsAdmin(Model model,HttpServletRequest request,HttpServletResponse response,
			@RequestParam("file") MultipartFile file) throws Exception {
		Map<String, Object> excel = new HashMap<String, Object>();
		String excelName = "";
		Map<String,Object> excelInfo = UploadUtils.saveMultipartFile(file);
		if((Integer)excelInfo.get("status")>0){ //上传完成
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
		}else{ //上传出错
			return ajaxReturn(response,null,excelInfo.get("errorMsg").toString(),0);
		}
		excel.put("url", excelName);
		List<Object> list = ExcelTransport.readExcel(excelName, 0, "universityCode,collegeName,linkMan,linkTel,linkAddress");
		int error = 0;
		for(int i=0;i<list.size();i++){
			Object object = list.get(i);
			Map th =(Map) object;
			int universityId =universityService.getUniverstiyByCode(Integer.valueOf(th.get("universityCode").toString()));
			int collegeId = collegeService.findCollegeByName(universityId, th.get("collegeName").toString());
			if(collegeId==0){
				error++;
				continue;
			}
			String linkMan = th.get("linkMan").toString();
			String linkTel = th.get("linkTel").toString();
			String linkAddress = th.get("linkAddress").toString();
			System.out.println(universityId+":::"+linkMan+":::"+linkTel+":::"+linkAddress);
			if(linkTel.length()!=11){
				error++;
				continue;
				//return ajaxReturn(response,null,"请输入正确的手机号",0);
			}
			int mobile_code = (int)((Math.random()*9+1)*100000);
			String content ="您的账户已经开通，账号为："+universityService.getNewCollegeName(Integer.valueOf(universityId))+
					" 密码为："+mobile_code+"，请不要把账号信息泄露给其他人。网站链接：www.epingyue.com";
			int j=1;// = sendsms.sendMessage(content, linkTel);
			if(j!=1){
				error++;
				continue;
				//return ajaxReturn(response,null,"手机验证错误，请重试！",0);
			}
			if(userService.checkPhone(linkTel)){
				error++;
				continue;
				//return ajaxReturn(response,null,"该手机号已经被注册",0);
			}
			User user = new User();
			user.setUserPhone(linkTel);
			user.setUserName(universityService.getNewCollegeName(Integer.valueOf(universityId)));
			user.setUserPassword(String.valueOf(mobile_code));
			user.setNickName(linkMan);
			user.setRoleId(4);
			user.setUniversityId(Integer.valueOf(universityId));
			user.setCollegeId(Integer.valueOf(collegeId));
			int mobile_code1 = (int)((Math.random()*9+1)*100000);
			user.setSendCode(String.valueOf(mobile_code1));
			user.setUserType(2);
			user.setIsValue(1);
			int L = userService.save(user);
			if(L==1){
				//return ajaxReturn(response,null,"添加成功！密码已经发送给负责人手机",1);
			}
			else{
				error++;
				continue;
				//return ajaxReturn(response,null,"添加失败！",0);
			}
		}
		if(error!=0){
			return ajaxReturn(response,null,"有"+error+"条专家信息不够正确，无法输入",0);
		}
		else{
			return ajaxReturn(response,null,"上传成功,共上传"+list.size()+"条数据",1);
		}
	}

}
