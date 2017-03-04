package com.zlzkj.app.quartz;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.zlzkj.app.model.Result;
import com.zlzkj.app.model.Thesis;
import com.zlzkj.app.model.User;
import com.zlzkj.app.service.ThesisService;
import com.zlzkj.app.service.UserService;
import com.zlzkj.app.util.sendsms;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;
import com.zlzkj.core.util.Fn;


public class Job {
//	private static Logger log = Logger.getLogger(Job.class);
//	private static final String CONFIG_QUARTZ = "quartz.properties";
	
	@Autowired
	private SqlRunner sqlRunner;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ThesisService thesisService;
	
	public void work() throws IOException{
		System.out.println("自动运行程序检测是否需要发送短信");
		//逐个扫描用户
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> whererr = new HashMap<String,Object>();
		whererr.put("is_value", 1);
		String sql = sqlBuilder
				.fields("*")
				.where(whererr)
				//.parseUIPageAndOrder(pageMap).order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		for(Row row :list){
			User user = userService.findById(row.getInt("id"));
			//System.out.println("现在开始检测为："+user.getNickName());
			Map<String, Object> where = new HashMap<String,Object>();
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
			SQLBuilder resultsqlBuilder = SQLBuilder.getSQLBuilder(Result.class);
			String resultsql = resultsqlBuilder
					.fields("*")
					.where(where)
					.buildSql();
			//System.out.println(sql);
			List<Row> resultlist = sqlRunner.select(resultsql);
			int max = 0;
			if(max==0){
				max = resultlist.size();
			}
			int i=0;
			for (int j=0;j<max;j++) {
				Row resultrow = resultlist.get(i);
				int kai=1;
				Thesis thesis = thesisService.findById(Integer.valueOf(resultrow.get("thesisId").toString()));
				//System.out.println(resultrow.get("thesisId").toString()+"哈哈哈哈哈");
				if(thesis.getIsShortcut()==0&&(user.getUserType()==4||user.getUserType()==3)){
					kai=0;
				}
				else{
					//启用一键评审
					int statusnumber =Integer.valueOf(resultrow.get("status").toString());
					if(statusnumber!=3&&(user.getUserType()==4||user.getUserType()==3)){
						//一键送审状态为7
						//如果没有达到送审标准就可显示
						if(thesis.getIsFinish()==1){
							kai=0;
							//System.out.println("不显示");
						}
					}
				}
				if((Integer.valueOf(resultrow.getString("returnTime"))-Fn.time()/(60*60*24))>=16){
					kai=0;
				}
				if(kai==0){
					resultlist.remove(row);
					if(resultlist.size()==0){
						break;
					}
				}
				else{
					i++;
				}
			}
			if(resultlist.size()!=0){
				//System.out.println("名字为"+user.getNickName()+"的人有"+resultlist.size()+"篇论文尚未上传");
				String content = "尊敬的"+user.getNickName()+"，您还有"+resultlist.size()+"份论文待评阅，请及时评阅！";
				sendsms.sendMessage(content, user.getUserPhone());
			}
		}
	}
}
