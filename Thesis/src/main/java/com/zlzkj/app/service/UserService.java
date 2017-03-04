package com.zlzkj.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.zlzkj.core.util.Fn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zlzkj.app.mapper.UserMapper;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Diy;
import com.zlzkj.app.model.Major;
//import com.zlzkj.app.model.Department;
import com.zlzkj.app.model.Role;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.util.CheckData;
import com.zlzkj.app.util.CommonUtil;
import com.zlzkj.app.util.JsonUtil;
import com.zlzkj.app.util.StringUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;
import com.zlzkj.app.service.UniversityService;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserMapper mapper;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	@Autowired
	private UniversityService universityService ;
	
	@Autowired
	private CollegeService collegeService ;
	
	@Autowired
	private MajorService majorService ;

	@Autowired
	private RoleService roleService;
	
	public Integer delete(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}
	
	public Integer update(User entity) throws Exception{
		
		return mapper.updateByPrimaryKey(entity);
	}
	
	public Integer save(User entity) throws Exception{
		
		return mapper.insert(entity);
	}
	
	public Integer add(User entity) throws Exception{
		
		mapper.insert(entity);
		return (Integer)entity.getId();
	}
	
	
	public User findById(Integer id){
		return mapper.selectByPrimaryKey(id);
	}
	
	public Integer find(String name){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where("user_phone ='"+name+"'")
				.order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for (Map<String,Object> row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		return id;
	}
	
	public boolean deleteWhere(Map<String, Object> where){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		int i = 0;
		for (Map<String,Object> row : list){
			delete(Integer.valueOf(row.get("id").toString()));
			i++;
		}
		if(i==list.size()){
			return true;
		}
		else{
			return false;
		}
	}
	
	public Map<String, Object> getUIGridData(Map<String, Object> where,
			Map<String, String> pageMap,int userType) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
//		.fields("ProjectProgress.id As id,ProjectProgress.project_id AS projectId,ProjectProgress.ptime AS ptime,ProjectProgress.content As content,ProjectProgress.object As object,ProjectProgress.schedule As schedule,ProjectProgress.problem As problem,ProjectProgress.solution As solution,ProjectProgress.remark As remark,Project.enterprise_id As enterpriseId")   //这里约定前端grid需要显示多少个具体列，也可以全部*
//		.join(ProjectProgress.class, "Project.id = ProjectProgress.project_id","Left")
		String sql = sqlBuilder
				.fields("distinct(User.university_id) AS universityId,User.id AS id , University.university_name AS universityName ,User.login_time As loginTime ,"
						+ "User.user_name AS userName ,User.user_password AS userPassword ,User.nick_name AS nickName ,User.user_remark AS userRemark,"
						+ "User.send_code AS sendCode, User.is_value AS isValue ,User.is_admin AS isAdmin ,User.user_sex AS userSex ,User.role_id AS roleId,"
						+ "User.user_phone AS userPhone")
				.join(University.class, "User.university_id = University.id","left")
//				.where("University.university_name like '%"+universityName+"%'")
				//.where("University.university_name like '%"+universityName+"%'")
				.where(where)
				.parseUIPageAndOrder(pageMap)
				.buildSql();
//		System.out.println(sql+"决定及案件达");
		List<Row> list = sqlRunner.select(sql);
//		System.out.println("积极急急急"+list.toString());
		for (Row row :list) {
			//删除多余的数据
			if(row.get("universityId").toString().equals("0")){
				row.put("university","无");
			}
			else{
				University university = universityService.findById(Integer.valueOf(row.get("universityId").toString()));
				row.put("universityName", university.getUniversityName());
			}
			if(row.getString("userSex").equals("1")){
				row.put("userSex", "男");
			}
			else{
				row.put("userSex", "女");
			}
//			System.out.println(row.get("id")+"dadada");
			if(!StringUtil.isEmpty(row.get("loginTime"))){
				row.put("loginTime", Fn.date(Integer.valueOf(row.get("loginTime").toString()), "yyyy-MM-dd"));
			}
			else{
				row.put("loginTime", "尚无登录信息");
			}
			Role role = roleService.findById(Integer.valueOf(row.getString("roleId")));
			row.put("roleId", role.getName());
			if(userType!=3){
				row.remove("userPassword");
			}
		}
		String countSql = sqlBuilder.fields("count(*)").join(University.class, "User.university_id = University.id","left").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	public Map<String, Object> getDetailUIGridData(Integer id){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("*")   
						//这里约定前端grid需要显示多少个具体列，也可以全部*
				/*.where("is_verify = 1")*/
				.where("User.id = " +id)
//				.order("id", "asc")
//				.join(AdminRole.class, "AdminRole.user_id = User.id","LEFT")
//				.join(Role.class, "Role.id = AdminRole.role_id","LEFT")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for (Map<String,Object> row : list) {
			//Area area
			//University university = universityService.findById(Integer.valueOf(row.get("universityId").toString()));
			if(row.get("userType").toString().equals("0")){
				//研究生部
				//Area area = areaService.findById(Integer.valueOf(row.get("areaId").toString()));
				University university = universityService.findById(Integer.valueOf(row.get("universityId").toString()));
				row.put("detail", university.getUniversityName()+"-研究生部");
			} else if (row.get("userType").toString().equals("1")){
				//学院
				//Area area = areaService.findById(Integer.valueOf(row.get("areaId").toString()));
				University university = universityService.findById(Integer.valueOf(row.get("universityId").toString()));
				College college = collegeService.findById(Integer.valueOf(row.get("collegeId").toString()));
				row.put("detail", university.getUniversityName()+"-"+college.getCollegeName()+"部");
			} else if (row.get("userType").toString().equals("2")){
				//老师
				//Area area = areaService.findById(Integer.valueOf(row.get("areaId").toString()));
				University university = universityService.findById(Integer.valueOf(row.get("universityId").toString()));
				College college = collegeService.findById(Integer.valueOf(row.get("collegeId").toString()));
				Major major;// = majorService.findById(Integer.valueOf(row.get("majorId").toString()));
				if(Integer.valueOf(row.get("majorTwo").toString())==0){
					major = majorService.findById(Integer.valueOf(row.get("majorOne").toString()));
				}
				else{
					major = majorService.findById(Integer.valueOf(row.get("majorTwo").toString()));
				}
				User user = findById(Integer.valueOf(row.get("id").toString()));
				row.put("detail", university.getUniversityName()+"-"+college.getCollegeName()
						+"-"+user.getNickName()+"老师");
			} else if (row.get("userType").toString().equals("3")){
				//超级管理员
				row.put("detail","系统超级管理员");
			} else if (row.get("userType").toString().equals("4")){
				//普通管理员
				row.put("detail","普通管理员");
			}
			//处理登录时间信息
			if(!StringUtil.isEmpty(row.get("loginTime"))){
				row.put("loginTime", Fn.date(Integer.valueOf(row.get("loginTime").toString()), "yyyy-MM-dd"));
			}
			else{
				row.put("loginTime", "尚无登录信息");
			}
			if(row.get("userPhone")==null){
				row.put("userPhone", "");
			}
			if(row.get("isValue").toString().equals("0")){
				row.put("isValue", "尚未激活");
			}
			else{
				row.put("isValue", "已经激活");
			}
		}
		String[] nameArray = {"详细信息","姓名","手机号码","所在银行","卡号","最后登录时间","有无激活"};
		String[] valueArray = {"detail","nickName","userPhone","visaBank","identifyNo","loginTime","isValue"};
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		for(int i = 0; i < valueArray.length; i++){
			Row map = new Row();
			map.put("name", nameArray[i]);
			map.put("value", list.get(0).getString(valueArray[i]));
			list2.add(map);
		}
		String countSql = sqlBuilder.fields("count(*)").buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(list2.size(),list2);
	}
	
	public List<Row> getList(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("id,name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where("type = 3 or type = 4")
				.order("type", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		System.out.println(where);
		return list;
	}
	
	public User getObjByUserName(String userName){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where("name = '"+userName+"'")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		User user = this.findById(list.get(0).getInt("id"));
		return user;
	}
	
	
	public User getObjByProperties(HashMap<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder.fields("*").where(where).buildSql();
		List<Row> list = sqlRunner.select(sql);
		if (list.size() == 0)
			return null;
		else
			return this.findById(list.get(0).getInt("id"));
	}
	
	public int checkLogin(String loginName, String loginPwd){
		//登录检查
		HashMap<String, Object> where  = new HashMap<String,Object>();
		where.put("user_phone", loginName);
		where.put("user_password",loginPwd);
		SQLBuilder sb = SQLBuilder.getSQLBuilder(User.class);
		String sql = sb.fields("*").where(where).buildSql();
//		List<Row> list = sqlRunner.select(sql,loginName,DigestUtils.md5Hex(loginPwd));
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		return id;
	}
	
	public int checkLoginByPhone(String phone, String loginPwd){
		//登录检查
		HashMap<String, Object> where  = new HashMap<String,Object>();
		where.put("user_phone", phone);
		where.put("user_password",loginPwd);
		SQLBuilder sb = SQLBuilder.getSQLBuilder(User.class);
		String sql = sb.fields("*").where(where).buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		return id;
	}
	
	public Integer loginId(String loginName, String loginPwd){
		String sql = SQLBuilder.getSQLBuilder(User.class)
				.fields("id")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where("user_phone ='"+loginName+"' and user_password ='"+loginPwd+"'")
				.order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		System.out.println("#######"+id);
		return id;
	}
	
	/**
	 * 寻找超级管理员
	 * @param where
	 * @return
	 */
	public Integer findAdmin(){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("id")
				.where("user_type = 3")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		return id;
	}
	
	public int findByName(String name){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("id")
				.where("user_phone = '"+name+"'")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		return id;
	}
	
	/**
	 * 老师列表
	 * @param where
	 * @return
	 */
	public List<Row> getTeacherList(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("id,nick_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.order("id", "asc")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Row node = new Row();
		node.put("id", 0);
		node.put("text", "不指定");
		list.add(0,node);
		//System.out.println(list.toString()+"积极剑锋");
		return list;
		//System.out.println(list.toString()+"积极剑锋");
	}
	
	/**
	 * 老师列表无默认值
	 * @param where
	 * @return
	 */
	public List<Row> getTeacherListNoDefault(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		String sql = sqlBuilder
				.fields("id,nick_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.order("id", "asc")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Row node = new Row();
//		node.put("id", 0);
//		node.put("text", "不指定");
		list.add(0,node);
		return list;
	}
	
	/**
	 * 寻找院校负责人 
	 */
	public Integer findUser(String universityId,String collegeId){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> where = new HashMap<String,Object>();
//		where.put("user_type",new String[]{"!=","2"});
		if(StringUtil.isNumeric(universityId)&&(!StringUtil.isEmpty(universityId))){
			where.put("university_id", universityId);
		}
		else{
			return 0;
		}
		if(StringUtil.isNumeric(collegeId)&&(!StringUtil.isEmpty(collegeId))){
			where.put("college_id", collegeId);
		}
		else{
			return 0;
		}
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
//			System.out.println(row.get("userType")+"大大大");
			if(row.get("userType").toString().equals("0")||row.get("userType").toString().equals("1")){
				id = Integer.valueOf(row.get("id").toString());
			}
		}
		return id;
	}
	
	/**
	 * 寻找管理员列表 
	 */
	public int [] findNormalAdmin(){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> where = new HashMap<String,Object>();
		String sql = sqlBuilder
				.fields("*")
				.where("user_type = 4")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		int numbers[] =new int[list.size()];
		int i = 0;
		for(Row row : list){
			numbers[i]  = row.getInt("id");
			i++;
		}
		return numbers;
	}
	
	/**
	 * 检查手机号码有无重复
	 */
	public boolean checkPhone(String phone){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> where = new HashMap<String,Object>();
		where.put("user_phone", phone);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		if(list.size()>0){
			return true;
		}
		return false;
	}
	
	public Map<String, Object> getDiyUIGridData(Map<String, Object> where,
			Map<String, String> pageMap) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Diy.class);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for (Row row : list) {
			row.put("uploadTime", Fn.date(Integer.valueOf(row.get("uploadTime").toString()), "yyyy-MM-dd"));
		}
		String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	public List<Row> getDiyList(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Diy.class);
		String sql = sqlBuilder
				.fields("id,remark as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Row node = new Row();
		node.put("id", 0);
		node.put("text", "通用送审模板");
		list.add(0,node);
		return list;
	}
}
