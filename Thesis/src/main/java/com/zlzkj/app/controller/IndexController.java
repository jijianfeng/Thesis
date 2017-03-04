package com.zlzkj.app.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.zlzkj.app.model.Result;
import com.zlzkj.app.service.ActionNodeService;
import com.zlzkj.core.base.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 首页控制器
 */
@Controller
public class IndexController extends BaseController {
	
	@Autowired
	ActionNodeService actionNodeService;
	
	@RequestMapping(value={"/"})
	public String index(Model model,HttpServletRequest request,HttpServletResponse response) throws IOException {
		if(request.getSession().getAttribute("loginName") != null){
			model.addAttribute("topMenuList",actionNodeService.getTopNodeList(Integer.valueOf(request.getSession().getAttribute("roleId").toString())));
		}else{
//			System.out.println("返回登录");
			return "public/login";
		}
		return "index/index";
	}
	
	/**
	 * 左侧菜单
	 */
	@RequestMapping(value = "/left_menu/{topMenuId}")
	public String leftMenu(Model model, @PathVariable(value="topMenuId") int topMenuId) {
		
		model.addAttribute("leftMenuList",actionNodeService.getLeftNodeList(topMenuId));
		
		return "public/left_menu";
	}
	
	
	/**
	 * 网站概况
	 */
	@RequestMapping(value = "/index/dashboard")
	public String dashboard(Model model) {
		return "index/dashboard";
	}
	
}
