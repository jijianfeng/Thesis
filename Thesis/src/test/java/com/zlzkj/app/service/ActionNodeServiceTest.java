package com.zlzkj.app.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.zlzkj.app.model.ActionNode;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.util.CustomerException;
import com.zlzkj.core.base.BaseSpringTest;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActionNodeServiceTest extends BaseSpringTest{

	protected Log logger = LogFactory.getLog(ActionNodeServiceTest.class);
	
	@Autowired
	private ActionNodeService actionNodeService;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UniversityService universityService;

	
	@Test
	public void init() throws Exception{
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(College.class);
		Map<String, Object> where = new HashMap<String,Object>();
		//where.put("role_id", "3");
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
//				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for (Map<String,Object> row : list){
			id = Integer.valueOf(row.get("universityId").toString());
			University university = universityService.findById(id);
			//负责人
			User haduser = userService.findById(userService.findUser(String.valueOf(university.getId()), "0"));
			User user = new User();
			where.put("university_id", university.getId());
			SQLBuilder sqlBuilders = SQLBuilder.getSQLBuilder(User.class);
			String sqls = sqlBuilders
					.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
					.where(where)
					.buildSql();
			List<Row> lists = sqlRunner.select(sqls);
			if(lists.size()>9){
				user.setUserName(haduser.getUserName()+lists.size());
			}
			else{
				user.setUserName(haduser.getUserName()+"0"+lists.size());
			}
			int mobile_code = (int)((Math.random()*9+1)*100000);
			user.setUserPassword(String.valueOf(mobile_code));
			user.setNickName(row.get("collegeName").toString());
			user.setRoleId(3);
			user.setUniversityId(university.getId());
			user.setCollegeId(Integer.valueOf(row.get("id").toString()));
			user.setUserType(1);
			userService.save(user);
		}
		System.out.println("更新完成");
	}
	
	@Test
	public void delete() throws Exception{
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> where = new HashMap<String,Object>();
		//where.put("role_id", "3");
		where.put("id",new String[]{">","26921"} );
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for(Row row :list){
			int i = userService.delete(Integer.valueOf(row.getInt("id")));
			if(i!=1){
				System.out.println("失败");
			}
		}
		System.out.println("成功");
	}
	
	@Test
	public void edit() throws Exception{
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> where = new HashMap<String,Object>();
//		where.put("role_id", "3");
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for(Row row :list){
			User user= userService.findById(row.getInt("id"));
			if(user.getCheckCode().equals("")){
				user.setCheckCode("0");
			}
			else{
				System.out.println("异常");
				break;
			}
			if(user.getCheckCodeTime().equals("")){
				user.setCheckCodeTime("0");;
			}
			else{
				System.out.println("异常");
				break;
			}
			userService.update(user);
		}
		System.out.println("成功");
	}
	
	@Test
	public void editName() throws Exception{
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> where = new HashMap<String,Object>();
//		where.put("role_id", "3");
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for(Row row :list){
			User user= userService.findById(row.getInt("id"));
			String name = user.getUserName();
			name = name.replace("ipy", "");
			user.setUserName(name);
			int i =userService.update(user);
			if(i!=1){
				System.out.println("异常");
				break;
			}
		}
		System.out.println("成功");
	}
	
	@Test
	public void addSendCode() throws Exception{
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> where = new HashMap<String,Object>();
		where.put("send_code", "");
		String sql = sqlBuilder
				.fields("id AS id")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for(Row row :list){
			User user= userService.findById(row.getInt("id"));
			int mobile_code = (int)((Math.random()*9+1)*100000);
			user.setSendCode(String.valueOf(mobile_code));
			int i = userService.update(user);
			System.out.println(user.getId());
			if(i!=1){
				System.out.println("异常");
				break;
			}
		}
		System.out.println("成功");
	}
	
	//测试：增加对象
//	@Test
//	public void testAddActionNode() throws Exception {
//		try{
//			ActionNode actionNode = new ActionNode();
//
//			actionNode.setId(1);
//			actionNode.setIconClass("2");
//			actionNode.setIsMenu((byte)1);
//			actionNode.setIsShow((byte)1);
//			actionNode.setLevel((byte)1);
//			actionNode.setName("1");
//			actionNode.setPid(1);
//			actionNode.setSortId(1);
//			actionNode.setUrl("1");
//
//			try {
//				actionNodeService.save(actionNode);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				System.out.println(e.getLocalizedMessage());
//			}
//			int newId = actionNode.getId();
//			logger.info("@crud.add >>>> new insert id:"+newId);
//			Assert.assertTrue(newId>0);
//		}catch (CustomerException e)
//		{
//			System.out.println(e.getMessage());
//		}
//	}
//
//
//	//测试：更新对象
//	@Test
//	public void testUpdateActionNode() throws Exception {
//		try{
//			ActionNode actionNode = new ActionNode();
//
//			actionNode.setId(1);
//			actionNode.setIconClass("22");
//			actionNode.setIsMenu((byte)1);
//			actionNode.setIsShow((byte)1);
//			actionNode.setLevel((byte)1);
//			actionNode.setName("1");
//			actionNode.setPid(1);
//			actionNode.setSortId(1);
//			actionNode.setUrl("1");
//
//			actionNodeService.update(actionNode);
//
//			actionNode = actionNodeService.findById(actionNode.getId());
//			Assert.assertTrue(1==actionNode.getId());
//		}catch (CustomerException e)
//		{
//			System.out.println(e.getMessage());
//		}
//	}
//
//
//	//测试：按id删除对象
//	@Test
//	public void testDeleteActionNode() throws Exception {
//		try{
//			//删除
//			Integer id = 1;
//			int affected = actionNodeService.delete(id);
//			Assert.assertTrue(affected==1);
//		}catch (CustomerException e)
//		{
//			System.out.println(e.getMessage());
//		}
//	}
//
//
//	//测试：按id获取对象
//	@Test
//	public void testGetActionNodeByKey() throws Exception {
//		try{
//			//查找
//			Integer id = 1;
//			ActionNode actionNode = actionNodeService.findById(id);
//			logger.info("@crud.find >>>> "+JSON.toJSONString(actionNode));
//			Assert.assertTrue(id==actionNode.getId());
//		}catch (CustomerException e)
//		{
//			System.out.println(e.getMessage());
//		}
//	}
//
//	/**
//	 * 测试获取所有对象
//	 */
//	@Test
//	public void selectAll(){
//
//		List<ActionNode> actionNodeList = actionNodeService.findAll();
//
//		for(ActionNode actionNode:actionNodeList){
//			logger.info("@selectAll >>>> listed user id:"+actionNode.getId());
//		}
//
//		Assert.assertTrue(actionNodeList.size()>0);
//
//	}
//
//	/**
//	 * 测试获取Row
//	 */
//	@Test
//	public void select(){
//
//		SQLBuilder sb = SQLBuilder.getSQLBuilder(ActionNode.class);
//		String sql = sb.fields("*").where("id=#{0}").buildSql();
//		List<Row> list = sqlRunner.select(sql,1);
//
//		for(Row r:list){
//			logger.info("@select >>>> actionNodes:"+JSON.toJSONString(r));
//		}
//
//		Assert.assertTrue(list.size()>0);
//	}
//
//	@Test
//	public void find(){
//
//		SQLBuilder sb = SQLBuilder.getSQLBuilder(ActionNode.class);
//		String sql = sb.fields("*").where("id=#{0}").buildSql();
//		Row row = sqlRunner.find(sql, 1);
//
//		logger.info("@find >>>> actionNode:"+JSON.toJSONString(row));
//
//		Assert.assertTrue(row.getInt("id")==1);
//
//	}
//
//	@Test
//	public void count(){
//		SQLBuilder sb = SQLBuilder.getSQLBuilder(ActionNode.class);
//		String sql = sb.fields("count(*)").buildSql();
//		int count = sqlRunner.count(sql);
//
//		logger.info("@count >>>> count:"+count);
//
//		Assert.assertTrue(count>0);
//
//	}

}
