package com.zlzkj.app.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.zlzkj.app.model.Question;
import com.zlzkj.app.model.User;
import com.zlzkj.app.service.QuestionService;
//import com.zlzkj.app.model.User;
import com.zlzkj.app.service.RoleService;
import com.zlzkj.app.service.UserService;
import com.zlzkj.app.util.UIUtils;
//import com.zlzkj.app.service.UserService;
//import com.zlzkj.app.util.StringUtil;
//import com.zlzkj.app.util.UIUtils;
import com.zlzkj.app.util.UploadUtils;
import com.zlzkj.core.base.BaseController;
import com.zlzkj.core.util.Fn;

/**
 * 公共控制器
 */
@Controller
@RequestMapping(value={"link"})
public class LinkController extends BaseController{
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private QuestionService questionService;
	
	@RequestMapping(value={"list"})
	public String list(Model model, HttpServletRequest request, HttpServletResponse response){
		if(request.getMethod()=="POST"){
			String title = request.getParameter("title");
			String status = request.getParameter("status");
			Map<String, Object> where = new HashMap<String,Object>();
			if(title!=null&&(!title.equals(""))){
				where.put("question_title",new String[]{"like","%"+title+"%"});
			}
			if(status!=null){
				if(status.equals("1")){
					//未解决
					where.put("is_solved",0);
				}
				if(status.equals("2")){
					//已结解决
					where.put("is_solved",1);
				}
			}
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			return ajaxReturn(response, questionService.getUIGridData(where,UIUtils.getPageParams(request),user));
		}
		else{
			return "link/list";
		}
	}
	
	@RequestMapping(value={"list_add"})
	public String listAdd(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(request.getMethod()=="POST"){
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			String sendType = request.getParameter("sendType");
			Question question = new Question();
			if(Integer.valueOf(sendType)==0){
				//全部
				question.setSendType(4);
			}
			else if(Integer.valueOf(sendType)==1){
				//研究生部
				question.setSendType(0);
			}
			else if(Integer.valueOf(sendType)==2){
				//学院
				question.setSendType(1);
			}
			else if(Integer.valueOf(sendType)==3){
				//老师
				question.setSendType(2);
			}
			else if(Integer.valueOf(sendType)==4){
				//管理员
				question.setSendType(3);
			}
			question.setQuestionTitle(title);
			question.setQuestionContent(content);
			question.setQuestionTime(Fn.time());
			question.setIsSolved(0);
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			question.setQuestionerId(user.getId());
			if(questionService.save(question)==1){
				return ajaxReturn(response, null,"添加成功",1);
			}
			else{
				return ajaxReturn(response, null,"添加失败",0);
			}
		}
		else{
			return "link/list_add";
		}
	}
	
	@RequestMapping(value={"list_delete"})
	public String listDelete(Model model, HttpServletRequest request, HttpServletResponse response){
		if(request.getMethod()=="POST"){
			int i = questionService.delete(Integer.valueOf(request.getParameter("id").toString()));
			if(i==1){
				return ajaxReturn(response, null,"删除成功",1);
			}
			else{
				return ajaxReturn(response, null,"删除失败",0);
			}
		}
		else{
			return "link/list";
		}
	}
	
	@RequestMapping(value={"list_solve"})
	public String listSolve(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(request.getMethod()=="POST"){
			String id = request.getParameter("id");
			Question question = questionService.findById(Integer.valueOf(id));
			question.setReply(request.getParameter("reply"));
			question.setIsSolved(1);
			User user = userService.findById(Integer.valueOf(request.getSession().getAttribute("userId").toString()));
			question.setSolvederId(user.getId());
			question.setSolvedTime(Fn.time());
			if(questionService.update(question)==1){
				return ajaxReturn(response, null,"回复成功",1);
			}
			else{
				return ajaxReturn(response, null,"回复失败",0);
			}
		}
		else{
			String id = request.getParameter("id");
			Question question = questionService.findById(Integer.valueOf(id));
			model.addAttribute("date1",Fn.date(question.getQuestionTime()));
			if(question.getIsSolved()==1){
				model.addAttribute("date",Fn.date(question.getSolvedTime()));
			}
			model.addAttribute("question",question);
			return "link/list_solve";
		}
	}
}
