package com.zlzkj.app.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zlzkj.app.mapper.QuestionMapper;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Question;
import com.zlzkj.app.model.Result;
import com.zlzkj.app.model.StatusProcess;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.util.CheckData;
import com.zlzkj.app.util.CommonUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;
import com.zlzkj.core.util.Fn;

@Service
@Transactional
public class QuestionService {

	@Autowired
	private QuestionMapper mapper;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	@Autowired
	private UserService userService;
	
	public Integer delete(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}
	
	public Integer update(Question entity) throws Exception{
		
		return mapper.updateByPrimaryKey(entity);
	}
	
	public Integer save(Question entity) throws Exception{
		return mapper.insert(entity);
	}
	
	public Question findById(Integer id){
		return mapper.selectByPrimaryKey(id);
	}
	
	public Map<String, Object> getUIGridData(Map<String, Object> where,
			Map<String, String> pageMap,User user) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Question.class);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.parseUIPageAndOrder(pageMap).order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		int max = 0;
		if(max==0){
			max = list.size();
		}
		int i=0;
		for (int j=0;j<max;j++) {
			Row row = list.get(i);
			//删除多余的数据
			int kai=1;
			row.put("uploadTime", Fn.date(Integer.valueOf(row.get("questionTime").toString()), "yyyy-MM-dd"));
			User userName = userService.findById(row.getInt("questionerId"));
			row.put("questioner", userName.getNickName());
			if(row.getInt("sendType")!=4){
				if(row.getInt("sendType")!=user.getUserType()){
					kai=0;
				}
			}
			if(row.getInt("questionerId")==user.getId()){
				kai=1;
			}
			if(user.getUserType()==3){
				kai=1;
			}
			if(kai==0){
				list.remove(row);
				if(list.size()==0){
					break;
				}
			}
			else{
				i++;
			}
		}
		//获取总条数
		String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
}
