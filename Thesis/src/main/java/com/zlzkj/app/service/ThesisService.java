package com.zlzkj.app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSON;
import com.zlzkj.core.util.Fn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zlzkj.app.mapper.ThesisMapper;
import com.zlzkj.app.mapper.UserMapper;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Major;
import com.zlzkj.app.model.Result;
import com.zlzkj.app.model.Role;
import com.zlzkj.app.model.StatusProcess;
import com.zlzkj.app.model.Thesis;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.util.CheckData;
import com.zlzkj.app.util.CommonUtil;
import com.zlzkj.app.util.CreatePdf;
import com.zlzkj.app.util.ExcelTransport;
import com.zlzkj.app.util.JsonUtil;
import com.zlzkj.app.util.StringUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;

@Service
@Transactional
public class ThesisService {

	@Autowired
	private ThesisMapper mapper;
	
	@Autowired
	private UniversityService universityService;
	
	@Autowired
	private CollegeService collegeService;
	
	@Autowired
	private MajorService majorService;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private StatusProcessService processService;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	@PostConstruct	
	public void Out(){
		for(int i =1;i<100;i++){
			System.out.println(">>>>>>>>>。。");
		}
	}
	
	public Integer delete(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}
	
	public Integer update(Thesis entity) throws Exception{
		return mapper.updateByPrimaryKey(entity);
	}
	
	public Integer save(Thesis entity) throws Exception{
		return mapper.insert(entity);
	}
	
	public Thesis findById(Integer id){
		return mapper.selectByPrimaryKey(id);
	}
	
	
	public Map<String, Object> getUIGridData(int type,Map<String, Object> where,
			Map<String, String> pageMap ,String majorId) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Thesis.class);
		String sql = sqlBuilder
//				+ "User.user_name AS userName ,User.user_password AS userPassword ,User.nick_name AS nickName ,User.user_remark AS userRemark,"
//				+ "User.send_code AS sendCode, User.is_value AS isValue ,User.is_admin AS isAdmin ,User.user_sex AS userSex ,User.role_id AS roleId,"
//				+ "User.user_phone AS userPhone")
				.fields("Thesis.id AS id ,Thesis.thesis_code AS thesisCode ,Thesis.thesis_name AS thesisName ,Thesis.thesis_type AS thesisType,"
						+ "Thesis.college_id AS collegeId ,Thesis.major_one AS majorOne,Thesis.major_two AS majorTwo,Thesis.research_direction AS researchDirection,"
						+ "Thesis.study_type AS studyType ,Thesis.upload_time AS uploadTime,Thesis.require_number AS requireNumber,Thesis.send_number AS sendNumber,"
						+ "Thesis.send_type AS sendType ,Thesis.had_com AS hadCom")
				.where(where)
				.parseUIPageAndOrder(pageMap)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		//将ID数据转化为文字
		for (Row row : list) {
//			if(majorId!=null){
//				Major major =majorService.findById(Integer.valueOf(majorId));
//				Major majorOne  =majorService.findById(row.getInt("majorOne"));
//				if(!majorOne.getMajorCode().substring(0,2).equals(major.getMajorCode())){
//					kai = 0;
//				}
//			}
			//论文类型修改
//			row.put("hadSend", row.get("Had")));
			row.put("uploadTime", Fn.date(Integer.valueOf(row.get("uploadTime").toString()), "yyyy-MM-dd"));
//			row.put("returnTime", Fn.date(Integer.valueOf(row.get("returnTime").toString()), "yyyy-MM-dd"));
			if(row.get("thesisType").toString().equals("1")){
				row.put("thesisType", "学硕");
			}
			else if(row.get("thesisType").toString().equals("2")){
				row.put("thesisType", "专硕");
			}
			else if(row.get("thesisType").toString().equals("3")){
				row.put("thesisType", "博士");
			}
			//有无共享
			if(row.getString("sendType").equals("3")){
				row.put("sendType", 1);
			}
			else{
				row.put("sendType", 0);
			}
			//判断有无一键送审成功
			if(row.get("returnTime")==null||row.get("returnTime").equals("")){
				row.put("returnTime", "0");
			}
			else{
				row.put("returnTime", "1");
			}
			//根据ID查询出处
			College college = collegeService.findById(Integer.valueOf(row.get("collegeId").toString()));
			row.put("collegeId", college.getCollegeName());
			//Major major = majorService.findById(Integer.valueOf(row.get("majorId").toString()));
			if(row.get("majorTwo").equals("0")||row.get("majorTwo")==null){
				row.put("majorTwo", majorService.findById(Integer.valueOf(row.getInt("majorOne"))).getMajorName());
			}
			else{
				row.put("majorTwo", majorService.findById(Integer.valueOf(row.getInt("majorOne"))).getMajorName());
			}
			row.put("majorOne", majorService.findById(Integer.valueOf(row.getInt("majorOne"))).getMajorName());
//			row.remove("universityId");
//			row.remove("collegeId");
//			row.remove("majorId");
//			row.remove("sendType");
//			if(kai==0){
//				list.remove(row);
//				if(list.size()==0){
//					break;
//				}
//			}
//			else{
//				i++;
//			}
		}
		try {
			if(type==1){
				ExcelTransport.createMainExcelFile("export.xls",
				"论文统计表格","论文序号,论文名称,论文类型,所属学院,一级专业,二级专业,研究方向,攻读方式,上传日期,有无共享给研究生部",
				"thesisCode,thesisName,thesisType,collegeId,majorOne,majorTwo,researchDirection,studyType,uploadTime,sendType",list);
			}
			else if(type==2){
				ExcelTransport.createMainExcelFile("export.xls",
				"论文统计表格","论文序号,论文名称,论文类型,所属学院,一级专业,二级专业,研究方向,攻读方式,上传日期,有无共享给研究生部,需要评审次数,已经评审次数,已经送审次数",
				"thesisCode,thesisName,thesisType,collegeId,majorOne,majorTwo,researchDirection,studyType,uploadTime,sendType,requireNumber,hadSend,sendNumber",list);
			}
			else if(type==3){
				ExcelTransport.createMainExcelFile("export.xls",
				"论文统计表格","论文序号,论文名称,论文类型,所属学院,一级专业,二级专业,研究方向,攻读方式,上传日期,有无共享给研究生部,已经评审次数,已经送审次数,是否来自学院共享",
				"thesisCode,thesisName,thesisType,collegeId,majorOne,majorTwo,researchDirection,studyType,uploadTime,sendType,hadCom,sendNumber,sendType",list);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//获取总条数
		String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	//未接收论文
	public Map<String, Object> getUnReceiveIGridData(Map<String, Object> where,
			Map<String, String> pageMap ) throws Exception {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Result.class);
		String sql = sqlBuilder
				.fields("Result.id AS id ,Thesis.university_id AS universityId ,Thesis.college_id AS collegeId,Thesis.major_two AS majorTwo,"
						+ "Thesis.thesis_type AS thesisType , Result.send_user_id AS sendUser,Thesis.thesis_code AS thesisCode,Thesis.thesis_name AS thesisName,"
						+ "Thesis.research_direction AS researchDirection ,Thesis.study_type AS studyType,Thesis.upload_time AS uploadTime,Result.return_time AS returnTime,"
						+ "Result.thesis_id AS thesisId,Result.major_one AS majorOne,Result.status AS status")
				.join(Thesis.class, "Result.thesis_id = Thesis.id","left")
//				.join(University.class ,"Result.university_id = University.id","left")
				.where(where)
				.parseUIPageAndOrder(pageMap)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for (Row row : list) {
			Thesis thesis = findById(Integer.valueOf(row.get("thesisId").toString()));
			row.put("thesisName", thesis.getThesisName());
			University university = universityService.findById(thesis.getUniversityId());
			row.put("universityId", university.getUniversityName());
			College college = collegeService.findById(thesis.getCollegeId());
			row.put("collegeId", college.getCollegeName());
			if(thesis.getMajorTwo()==0){
				row.put("majorTwo", majorService.findById(thesis.getMajorOne()).getMajorName());
			}
			else{
				row.put("majorTwo", majorService.findById(thesis.getMajorTwo()).getMajorName());
			}
			row.put("researchDirection", thesis.getResearchDirection());
			row.put("studyType", thesis.getStudyType());
			row.put("thesisCode", thesis.getThesisCode());
			row.put("thesisName", thesis.getThesisName());
			row.put("uploadTime", Fn.date(Integer.valueOf(thesis.getUploadTime()), "yyyy-MM-dd"));
			//添加送审人信息
			User sendUser = userService.findById(Integer.valueOf(thesis.getUploadPeople()));
			row.put("sendUser", sendUser.getNickName());
			//添加论文的评审的方式（自定义or通用）
			//int userId  = processService.findSendUser(Integer.valueOf(row.get("id").toString()));
			//User user = userService.findById(thesis.getUploadPeople());
			//System.out.println(Integer.valueOf(row.get("sendUserId").toString())+"测试");
			if(thesis.getDiyId()==0){
				row.put("isSendUrl", 0);
			}
			else{
				row.put("isSendUrl", 1);
			}
			if(thesis.getThesisType()==1){
				row.put("thesisType", "学硕");
			}
			else if(thesis.getThesisType()==2){
				row.put("thesisType", "专硕");
			}
			else if(thesis.getThesisType()==3){
				row.put("thesisType", "博士");
			}
			//处理红字
			System.out.println("哈哈哈"+row.get("returnTime").toString());
			row.put("returnTime", Fn.date(Integer.valueOf(row.get("returnTime").toString()), "yyyy-MM-dd"));
			String times = row.getString("returnTime");
			SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
			Date time = format.parse(times);
//			System.out.println((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))+"天");
			if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<11){
				row.put("red", "1");
			}
			else if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<16){
				row.put("red", "2");
			}
			else if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<21){
				row.put("red", "3");
			}
		}
		ExcelTransport.createMainExcelFile("export.xls","论文统计表格",
		"所属大学,所属学院,所属专业,论文类型,送审人,论文序号,论文标题,研究方向,攻读方式,论文上传时间,要求返回时间",
		"universityId,collegeId,majorTwo,thesisType,sendUser,thesisCode,thesisName,researchDirection,studyType,uploadTime,returnTime",list);
		//获取总条数
//		System.out.println(list.toString());
		String countSql = sqlBuilder.fields("count(*)").where(where).join(Thesis.class, "Result.thesis_id = Thesis.id","left").buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	//未接收论文admin
		public Map<String, Object> getUnReceiveAdminGridData(Map<String, Object> where,
				Map<String, String> pageMap ) throws Exception {
			SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Thesis.class);
			String sql = sqlBuilder
					.fields("*")
					.where(where)
					.parseUIPageAndOrder(pageMap)
					.buildSql();
			List<Row> list = sqlRunner.select(sql);
			for (Row row :list) {
				Thesis thesis = findById(row.getInt("id"));
				//添加送审人信息
				//User sendUser = userService.findById(userService.findUser(row.getString("universityId"), row.getString("collegeId")));
				User sendUser = userService.findById(Integer.valueOf(thesis.getUploadPeople()));
				row.put("uploadPeople", sendUser.getNickName());
				if(thesis.getDiyId()==0){
					row.put("isSendUrl", 0);
				}
				else{
					row.put("isSendUrl", 1);
				}
//				Area area = areaService.findById(thesis.getAreaId());
//				row.put("areaId", area.getAreaName());
				University university = universityService.findById(thesis.getUniversityId());
				row.put("universityId", university.getUniversityName());
				College college = collegeService.findById(thesis.getCollegeId());
				row.put("collegeId", college.getCollegeName());
				Major major ;
				if(row.get("majorTwo").equals("")||row.get("majorTwo")==null||row.get("majorTwo").equals("0")){
					row.put("majorTwo", majorService.findById(row.getInt("majorOne")).getMajorName());
				}
				else{
					row.put("majorTwo", majorService.findById(row.getInt("majorTwo")).getMajorName());
				}
//				row.put("majorId", major.getMajorName());
				row.put("thesisCode", thesis.getThesisCode());
				row.put("thesisName", thesis.getThesisName());
				row.put("uploadTime", Fn.date(Integer.valueOf(thesis.getUploadTime()), "yyyy-MM-dd"));
//				if(row.get("returnTime")==null||row.get("returnTime").equals("")||row.getString("returnTime").equals("0")){
//					//row.put("returnTime", Fn.date(Integer.valueOf(row.get("returnTime").toString()), "yyyy-MM-dd"));
//					kai=0;
//				}
				row.put("returnTime", Fn.date(Integer.valueOf(row.get("returnTime").toString()), "yyyy-MM-dd"));
				//处理红字
				String times = row.getString("returnTime");
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
				Date time = format.parse(times);
//				System.out.println((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))+"天");
				if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<11){
					row.put("red", "1");
				}
				else if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<16){
					row.put("red", "2");
				}
				else if((((int)(time.getTime()/1000)-Fn.time())/(60*60*24))<21){
					row.put("red", "3");
				}
				if(thesis.getThesisType()==1){
					row.put("thesisType", "学硕");
				}
				else if(thesis.getThesisType()==2){
					row.put("thesisType", "专硕");
				}
				else if(thesis.getThesisType()==3){
					row.put("thesisType", "博士");
				}
			}
			ExcelTransport.createMainExcelFile("export.xls",
			"论文统计表格","所属大学,所属学院,所属专业,送审人,论文序号,论文标题,论文类型,论文上传时间,要求返回时间,应评审次数,已评审次数,已送审次数",
			"universityId,collegeId,majorTwo,uploadPeople,thesisCode,thesisName,thesisType,uploadTime,returnTime,requireNumber,hadSend,sendNumber",list);
			//获取总条数
			String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
			Integer count = sqlRunner.count(countSql);
			return UIUtils.getGridData(count, list);
		}
	
	//已收论文
	public Map<String, Object> getReceiveGridData(Map<String, Object> where,
			Map<String, String> pageMap ) throws Exception {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(StatusProcess.class);
		String sql = sqlBuilder
				.fields("distinct(StatusProcess.thesis_id) AS thesisId ,StatusProcess.id AS id ,StatusProcess.result_id AS resultId ,"
						+ "Thesis.thesis_name AS thesisName ,Result.status AS status ,Result.teacher_id AS teacherId,Thesis.university_id AS universityId ,Thesis.college_id AS collegeId,"
						+ "Thesis.major_two AS majorId ,Thesis.thesis_type AS thesisType,Thesis.upload_people AS uploadPeople,Thesis.thesis_code AS thesisCode,"
						+ "Thesis.thesis_name AS thesisName,Thesis.research_direction AS researchDirection,Thesis.study_type AS studyType ,Thesis.upload_Time AS uploadTime,"
						+ "Thesis.return_time AS returnTime")
				.join(Thesis.class, "StatusProcess.thesis_id = Thesis.id","left")
				.join(Result.class, "StatusProcess.result_id = Result.id","left")
				.where(where)
				.parseUIPageAndOrder(pageMap)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		int max = 0;
		if(max==0){
			max = list.size();
		}
		int i=0;
		for (Row row:list) {
//			Row row = list.get(i);
//			int kai=1;
			Result re = resultService.findById(Integer.valueOf(row.get("resultId").toString()));
//			row.put("status", re.getStatus());
			int teacherId = row.getInt("teacherId");
			if(teacherId==0){
				row.put("teacherId", "暂无数据");
			}
			else{
				User user = userService.findById(teacherId);
				row.put("teacherId", user.getNickName());
			}
			String result = row.getString("status");
			Thesis thesis = findById(Integer.valueOf(row.get("thesisId").toString()));
			row.put("thesisName", thesis.getThesisName());
			row.put("thesisCode", thesis.getThesisCode());
			University university = universityService.findById(thesis.getUniversityId());
			row.put("universityId", university.getUniversityName());
			College college = collegeService.findById(thesis.getCollegeId());
			row.put("collegeId", college.getCollegeName());
			Major major;
			//= majorService.findById(thesis.getMajorId());
			if(thesis.getMajorTwo()==0){
				major = majorService.findById(thesis.getMajorOne());
			}
			else{
				major = majorService.findById(thesis.getMajorTwo());
			}
			row.put("majorI", major.getMajorName());
			row.put("uploadTime", Fn.date(Integer.valueOf(thesis.getUploadTime()), "yyyy-MM-dd"));
			row.put("returnTime", Fn.date(Integer.valueOf(re.getReturnTime()), "yyyy-MM-dd"));
			if(thesis.getThesisType()==1){
				row.put("thesisType", "学硕");
			}
			else if(thesis.getThesisType()==2){
				row.put("thesisType", "专硕");
			}
			else if(thesis.getThesisType()==3){
				row.put("thesisType", "博士");
			}
			
		    if(result.equals("4")){
				row.put("status", "对方学校研究生部");
			}
			else if(result.equals("5")){
				row.put("status", "对方学院");
			}
			else if(result.equals("6")){
				row.put("status", "评阅教师");
			}
			else if(result.equals("9")){
				row.put("status", "评阅结束");
			}
		    row.put("uploadPeople", userService.findById(thesis.getUploadPeople()).getNickName());
		    row.put("researchDirection", thesis.getResearchDirection());
		    row.put("studyType", thesis.getStudyType());
		}
		ExcelTransport.createMainExcelFile("export.xls","论文统计表格",
		"所属大学,所属学院,所属专业,论文类型,送审人,接审人,论文序号,论文标题,研究方向,攻读方式,论文上传时间,要求返回时间,论文状态",
		"universityId,collegeId,majorId,thesisType,sendUser,teacherId,thesisCode,thesisName,researchDirection,studyType,uploadTime,returnTime,result",list);
		//获取总条数
		String countSql = sqlBuilder.fields("count(*)").where(where).join(Thesis.class, "StatusProcess.thesis_id = Thesis.id","left")
				.join(Result.class, "StatusProcess.result_id = Result.id","left").buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	//已收论文(admin)
		public Map<String, Object> getReceiveAdminGridData(Map<String, Object> where,
				Map<String, String> pageMap ,String universityId,String thesisCode,String thesisName,String timeStart,String timeEnd,String status) throws Exception {
			SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(StatusProcess.class);
			String sql = sqlBuilder
					.fields("distinct(StatusProcess.thesis_id) AS thesisId ,StatusProcess.id AS id ,StatusProcess.result_id AS resultId ,"
							+ "Thesis.thesis_name AS thesisName ,Result.status AS status ,Result.teacher_id AS teacherId,Thesis.university_id AS universityId ,Thesis.college_id AS collegeId,"
							+ "Thesis.major_two AS majorId ,Thesis.thesis_type AS thesisType,Thesis.upload_people AS uploadPeople,Thesis.thesis_code AS thesisCode,"
							+ "Thesis.thesis_name AS thesisName,Thesis.research_direction AS researchDirection,Thesis.study_type AS studyType ,Thesis.upload_Time AS uploadTime,"
							+ "Thesis.return_time AS returnTime")
					.join(Thesis.class, "StatusProcess.thesis_id = Thesis.id","left")
				    .join(Result.class, "StatusProcess.result_id = Result.id","left")
					.where(where)
					.parseUIPageAndOrder(pageMap)
				//	.join(Result.class, "Result.id = StatusProcess.result_id","LEFT")
					.buildSql();
			List<Row> list = sqlRunner.select(sql);
			int max = 0;
			if(max==0){
				max = list.size();
			}
			int i=0;
			for (Row row:list) {
//				Row row = list.get(i);
//				int kai=1;
				Result re = resultService.findById(Integer.valueOf(row.get("resultId").toString()));
				row.put("status", re.getStatus());
				int teacherId = re.getTeacherId();
				if(teacherId==0){
					row.put("teacherName", "暂无数据");
				}
				else{
					User user = userService.findById(teacherId);
					row.put("teacherName", user.getNickName());
				}
				String result = re.getStatus().toString();
				Thesis thesis = findById(Integer.valueOf(row.get("thesisId").toString()));
				
				//数据删除
//				if(areaId!=null){
//					if(thesis.getAreaId()!=(Integer.valueOf(areaId))){
//						kai=0;
//					}
//				}
//				if(universityId!=null){
//					if(thesis.getUniversityId()!=(Integer.valueOf(universityId))){
//						kai=0;
//					}
//				}
//				if(universityId!=null){
//					if(!String.valueOf(thesis.getUniversityId()).equals(universityId)){
//						kai=0;
//					}
//				}
//				if(thesisCode!=null){
//					if(!thesis.getThesisCode().contains(thesisCode)){
//						kai=0;
//					}
//				}
//				if(thesisName!=null){
//					if(!thesis.getThesisName().contains(thesisName)){
//						kai=0;
//					}
//				}
//				if(timeStart!=null){
//					if(Integer.valueOf(re.getReturnTime())<Integer.valueOf(timeStart)){
//						kai=0;
//					}
//				}
//				if(timeEnd!=null){
//					if(Integer.valueOf(re.getReturnTime())>Integer.valueOf(timeEnd)){
//						kai=0;
//					}
//				}
//				if(status!=null){
//					if(Integer.valueOf(status)==1){
//						if(re.getStatus()==9){
//							kai=0;
//						}
//					}
//					else if(Integer.valueOf(status)==2){
//						if(re.getStatus()!=9){
//							kai=0;
//						}
//					}
//				}
//				if(Integer.valueOf(re.getStatus())==9){
//					String times = String.valueOf(re.getStatusTime());
////					SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
////					Date time = format.parse(times);
//					//System.out.println((Fn.time()-Integer.valueOf(re.getStatusTime()))/(60*60*24)+"天");
//					if((Fn.time()-Integer.valueOf(re.getStatusTime()))/(60*60*24)>60){
//						kai=0;
//					}
//				}
				
				row.put("thesisName", thesis.getThesisName());
				row.put("thesisCode", thesis.getThesisCode());
//				Area area = areaService.findById(thesis.getAreaId());
//				row.put("area", area.getAreaName());
				University university = universityService.findById(thesis.getUniversityId());
				row.put("university", university.getUniversityName());
				College college = collegeService.findById(thesis.getCollegeId());
				row.put("college", college.getCollegeName());
				Major major;
				// = majorService.findById(thesis.getMajorId());
				if(thesis.getMajorTwo()==0){
					major = majorService.findById(thesis.getMajorOne());
				}
				else{
					major = majorService.findById(thesis.getMajorTwo());
				}
				row.put("sendUser", userService.findById(thesis.getUploadPeople()).getNickName());
				row.put("major", major.getMajorName());
				row.put("uploadTime", Fn.date(Integer.valueOf(thesis.getUploadTime()), "yyyy-MM-dd"));
				row.put("returnTime", Fn.date(Integer.valueOf(re.getReturnTime()), "yyyy-MM-dd"));
				if(thesis.getThesisType()==1){
					row.put("thesisType", "学硕");
				}
				else if(thesis.getThesisType()==2){
					row.put("thesisType", "专硕");
				}
				else if(thesis.getThesisType()==3){
					row.put("thesisType", "博士");
				}				
			    if(result.equals("4")){
					row.put("result", "对方学校研究生部");
				}
				else if(result.equals("5")){
					row.put("result", "对方学院");
				}
				else if(result.equals("6")){
					row.put("result", "评阅教师");
				}
				else if(result.equals("9")){
					row.put("result", "评阅结束");
				}
//			    if(kai==0){
//					list.remove(row);
//					if(list.size()==0){
//						break;
//					}
//				}
//				else{
//					i++;
//				}
			}
			ExcelTransport.createMainExcelFile("export.xls","论文统计表格",
			"所属大学,所属学院,所属专业,送审人,论文类型,论文序号,论文标题,论文上传时间,要求返回时间,应评审次数,已评审次数,已送审次数",
			"universityId,collegeId,majorTwo,sendUser,thesisType,thesisCode,thesisName,uploadTime,returnTime,requireNumber,hadSend,sendNumber",list);
			//获取总条数
			String countSql = sqlBuilder.fields("count(*)").join(Thesis.class, "StatusProcess.thesis_id = Thesis.id","left")
				    .join(Result.class, "StatusProcess.result_id = Result.id","left").where(where).buildSql();
			Integer count = sqlRunner.count(countSql);
			return UIUtils.getGridData(count, list);
		}
	//公共论文
	public Map<String, Object> getPublicGridData(Map<String, Object> where,
			Map<String, String> pageMap ) throws Exception {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Result.class);
		String sql = sqlBuilder
				.fields("Result.id AS id,Result.thesis_id AS thesisId ,Result.return_time AS returnTime,Thesis.university_id AS universityId,Thesis.college_id AS collegeId,"
						+ "Thesis.major_two AS majorTwo ,Thesis.thesis_code AS thesisCode,Thesis.thesis_name AS thesisName, Thesis.thesis_type AS thesisType,Thesis.research_direction AS researchDirection,"
						+ "Thesis.study_type AS studyType ,Thesis.upload_time AS uploadTime,Thesis.is_shortcut AS isShortcut")
				.join(Thesis.class, "Result.thesis_id = Thesis.id","left")
				.where(where)
				.parseUIPageAndOrder(pageMap)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
//		System.out.println(list.toString());
		int max = 0;
		if(max==0){
			max = list.size();
		}
		int i=0;
		for (Row row:list) {
//			Row row = list.get(i);
//			int kai=1;
			Thesis thesis = findById(Integer.valueOf(row.get("thesisId").toString()));
			//数据删除
//			if(isNine==0&&(thesis.getIsNine()==1)){
//				kai=0;
//			}
//			if(isTwo==0&&(thesis.getIsTwo()==1)){
//				kai=0;
//			}
//			if(univeristiyExcept == thesis.getUniversityId()){
//				kai=0;
//			}
//			if(universityId!=null){
//				if(thesis.getUniversityId()!=(Integer.valueOf(universityId))){
//					kai=0;
//				}
//			}
//			if(universityId!=null){
//				if(!String.valueOf(thesis.getUniversityId()).equals(universityId)){
//					kai=0;
//				}
//			}
//			if(thesisCode!=null){
//				if(!thesis.getThesisCode().contains(thesisCode)){
//					kai=0;
//				}
//			}
//			if(thesisName!=null){
//				if(!thesis.getThesisName().contains(thesisName)){
//					kai=0;
//				}
//			}
//			if(timeStart!=null){
//				if(Integer.valueOf(thesis.getUploadTime())<Integer.valueOf(timeStart)){
//					kai=0;
//				}
//			}
//			if(timeEnd!=null){
//				if(Integer.valueOf(thesis.getUploadTime())>Integer.valueOf(timeEnd)){
//					kai=0;
//				}
//			}
			row.put("thesisName", thesis.getThesisName());
//			Area area = areaService.findById(thesis.getAreaId());
//			row.put("area", area.getAreaName());
			University university = universityService.findById(thesis.getUniversityId());
			row.put("universityId", university.getUniversityName());
			College college = collegeService.findById(thesis.getCollegeId());
			row.put("collegeId", college.getCollegeName());
			if(thesis.getMajorTwo()==0){
				row.put("majorTwo", majorService.findById(thesis.getMajorOne()).getMajorName());
			}
			else{
				row.put("majorTwo", majorService.findById(thesis.getMajorTwo()).getMajorName());
			}
			row.put("thesisCode", thesis.getThesisCode());
			row.put("isShortcut", thesis.getIsShortcut());
			row.put("uploadTime", Fn.date(Integer.valueOf(thesis.getUploadTime()), "yyyy-MM-dd"));
			//添加返回颜色
			if(((Integer.valueOf(row.get("returnTime").toString())-Fn.time())/(60*60*24))<11){
				row.put("red", "1");
			}
			else if(((Integer.valueOf(row.get("returnTime").toString())-Fn.time())/(60*60*24))<16){
				row.put("red", "2");
			}
			else if(((Integer.valueOf(row.get("returnTime").toString())-Fn.time())/(60*60*24))<21){
				row.put("red", "3");
			}
			row.put("returnTime", Fn.date(Integer.valueOf(row.get("returnTime").toString()), "yyyy-MM-dd"));
			row.put("researchDirection", thesis.getResearchDirection());
			row.put("studyType", thesis.getStudyType());
			if(thesis.getThesisType()==1){
				row.put("thesisType", "学硕");
			}
			else if(thesis.getThesisType()==2){
				row.put("thesisType", "专硕");
			}
			else if(thesis.getThesisType()==3){
				row.put("thesisType", "博士");
			}
//			if(kai==0){
//				list.remove(row);
//				if(list.size()==0){
//					break;
//				}
//			}
//			else{
//				i++;
//			}
		}
//		System.out.println(list.toString());
		ExcelTransport.createMainExcelFile("export.xls","论文统计表格",
		"所属大学,所属学院,所属专业,论文序号,论文标题,研究方向,攻读方式,论文类型,论文上传时间,要求返回时间,是否一键送审",
		"university,college,majorTwo,thesisCode,thesisName,researchDirection,studyType,thesisType,uploadTime,returnTime,isShortcut",list);
		//获取总条数
		String countSql = sqlBuilder.fields("count(*)").join(Thesis.class, "Result.thesis_id = Thesis.id","left").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	//论文流程
	public Map<String, Object> getProcessUIGridData(Map<String, Object> where,
			Map<String, String> pageMap) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Result.class);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.parseUIPageAndOrder(pageMap)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for (Row row : list) {
			//论文状态
			String status = row.get("status").toString();
			row.put("isfinish", "0");
			if(status.equals("1")){
				row.put("status", "被退回");
			}
			else if(status.equals("2")){
				row.put("status", "被退回");
			}
			else if(status.equals("3")){
				row.put("status", "公共平台");
			}
			else if(status.equals("4")){
				row.put("status", "对方学校研究生部");
			}
			else if(status.equals("5")){
				row.put("status", "对方学院");
			}
			else if(status.equals("6")){
				row.put("status", "评阅教师");
			}
			else if(status.equals("9")){
				row.put("status", "评阅结束");
				row.put("isfinish", "1");
			}
			//判断有无经过公共平台
			Map<String, Object> wheres = new HashMap<String,Object>();
			wheres.put("result_id", row.get("id"));
			wheres.put("thesis_id", row.get("thesisId"));
			SQLBuilder sb = SQLBuilder.getSQLBuilder(StatusProcess.class);
			String sqls = sb
					.fields("*")
					.where(wheres)
					.parseUIPageAndOrder(pageMap)
					.buildSql();
			List<Row> lists = sqlRunner.select(sqls);
			row.put("public", 0);
			for(Row rows:lists){
//				if(rows.getString("lastStatus").equals("1")||rows.getString("lastStatus").equals("2")){
//					User user = userService.findById(rows.getInt("userId"));
//					row.put("sendUserName", user.getNickName());
//					//row.put("hiddenUserId",user.getId() );
//				}
//				System.out.println(rows.getString("nowStatus")+"公共平台的问题");
//				System.out.println(rows.getString("nowStatus").equals("3"));
				if(rows.getString("nowStatus").equals("3")){
					row.put("public", 1);
				}
//				else{
//					row.put("public", 0);
//				}
			}
			User sendUser = userService.findById(Integer.valueOf(row.getString("sendUserId")));
			row.put("sendUserName", sendUser.getNickName());
			//研究生部
			if(!row.getString("universityId").equals("0")){
				University university = universityService.findById(Integer.valueOf(row.getString("universityId")));
				row.put("university", university.getUniversityName());
			}
			else{
				row.put("university", "暂无数据");
			}
			if(!row.getString("collegeId").equals("0")){
				College college = collegeService.findById(Integer.valueOf(row.getString("collegeId")));
				row.put("college", college.getCollegeName());
			}
			else{
				row.put("college", "暂无数据");
			}
			if(!row.getString("teacherId").equals("0")){
				User user  = userService.findById(Integer.valueOf(row.getString("teacherId")));
				row.put("teacher", user.getNickName());
			}
			else{
				row.put("teacher", "暂无数据");
			}
			if(row.getString("thesisResult").equals("0")){
				row.put("thesisResult", "暂无数据");
			}
			else if(row.getString("thesisResult").equals("1")){
				row.put("thesisResult", "优");
			}
			else if(row.getString("thesisResult").equals("2")){
				row.put("thesisResult", "良");
			}
			else if(row.getString("thesisResult").equals("3")){
				row.put("thesisResult", "中");
			}
			else if(row.getString("thesisResult").equals("4")){
				row.put("thesisResult", "合");
			}
			else if(row.getString("thesisResult").equals("5")){
				row.put("thesisResult", "差");
			}
		}
//		System.out.println(list);
		//获取总条数
		String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	/**
	 * 检查论文曾经送审的学校
	 */
	public String checkHad(int thesisId,String had){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Result.class);
		Map<String, Object> where = new HashMap<String,Object>();
		where.put("thesis_id", thesisId);
		String sql = sqlBuilder
				.fields("Result.university_id AS universityId , Result.college_id AS collegeId")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for(Row row : list){
			if(!had.equals("")){
				had =had +"||"+ thesisId+","+row.getString("universityId")+","+row.getString("collegeId");
			}
			else{
				had =had + thesisId+","+row.getString("universityId")+","+row.getString("collegeId");
			}
		}
		return had;
	}
	
	/**
	 * 计算论文已经被评审多少次
	 */
//	public int findSendNumber(int thesisId){
//		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Result.class);
//		Map<String, Object> where = new HashMap<String,Object>();
//		where.put("thesis_id", thesisId);
//		where.put("status", "9");
//		String sql = sqlBuilder
//				.fields("id")
//				.where(where)
//				.buildSql();
//		List<Row> list = sqlRunner.select(sql);
//		//System.out.println("已经成功评审次"+list.size());
//		return list.size();
//	}
	
	/**
	 * 计算论文已经被admin送审次数
	 */
	public int findSendNumberAdmin(int thesisId,int status){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(StatusProcess.class);
		Map<String, Object> where = new HashMap<String,Object>();
		where.put("thesis_id", thesisId);
		where.put("last_status", status);
//		where.put("last_status", "7");
		String sql = sqlBuilder
				.fields("id")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		return list.size();
	}
	/**
	 * 计算论文已经被admin送审次数
	 */
	public Map<String, Object> getDetailUIGridData(Integer id){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Result.class);
		String sql = sqlBuilder
				.fields("*")   
						//这里约定前端grid需要显示多少个具体列，也可以全部*
				/*.where("is_verify = 1")*/
				.where("Result.id = " +id)
//				.order("id", "asc")
				.buildSql();
		System.out.println("result"+id);
		List<Row> list = sqlRunner.select(sql);
		for (Map<String,Object> row : list) {
			int universityId = Integer.valueOf(row.get("universityId").toString());
			int collegeId = Integer.valueOf(row.get("collegeId").toString());
			int teacherId = Integer.valueOf(row.get("teacherId").toString());
			if(universityId==0){
				row.put("university", "暂无数据");
			}
			else{
				University university = universityService.findById(universityId);
				row.put("university", university.getUniversityName());
			}
			if(collegeId==0){
				row.put("college", "暂无数据");
			}
			else{
				College college = collegeService.findById(collegeId);
				row.put("college", college.getCollegeName());
			}
			if(teacherId==0){
				row.put("teacher", "暂无数据");
			}
			else{
				User teacher = userService.findById(teacherId);
				row.put("teacher", teacher.getNickName());
			}
		}
		System.out.println(list.toString());
		String[] nameArray = {"收方研究生部","收方学院","收方老师"};
		String[] valueArray = {"university","college","teacher"};
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		for(int i = 0; i < valueArray.length; i++){
			Row map = new Row();
			map.put("name", nameArray[i]);
			map.put("value", list.get(0).getString(valueArray[i]));
			list2.add(map);
		}
		return UIUtils.getGridData(list2.size(),list2);
	}
	
	/**
	 * 获取论文评阅书
	 */
	public String[] getCommentsFile(int thesisId){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Result.class);
		Map<String, Object> where = new HashMap<String,Object>();
		where.put("thesis_id",thesisId);
		where.put("status", "9");
		String sql = sqlBuilder
				.where(where)
				.fields("*")   
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		String files[] = new String[list.size()];
		int i = 0;
		for (Map<String,Object> row : list) {
			Result result = resultService.findById(Integer.valueOf(row.get("id").toString()));
			Thesis thesis = findById(result.getThesisId());
			Major major;
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
			try {
				CreatePdf.create3(thesis.getThesisName(), major.getMajorName(), String.valueOf(result.getThesisResultOne()),
						String.valueOf(result.getThesisResultTwo()),String.valueOf(result.getThesisResultThree()),
						String.valueOf(result.getThesisResultFour()),mainResult,
						result.getThesisRemark(),"D://export"+i+".pdf",userService.findById(result.getTeacherId()).getUserImage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			files[i] ="D://export"+i+".pdf";
			//files[i+list.size()/2] = result.getAttachment();
			i++;
		}
//		for(int j=0;j<files.length;j++){
//			System.out.println(files[j]+"经济积极急急急急急急急急急");
//		}
		return files;
	}
}
