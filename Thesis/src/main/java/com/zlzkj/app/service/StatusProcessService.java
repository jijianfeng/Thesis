package com.zlzkj.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zlzkj.app.mapper.StatusProcessMapper;
import com.zlzkj.app.model.Major;
import com.zlzkj.app.model.Result;
import com.zlzkj.app.model.StatusProcess;
import com.zlzkj.app.model.Thesis;
import com.zlzkj.app.model.User;
import com.zlzkj.app.util.CheckData;
import com.zlzkj.app.util.CommonUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;

@Service
@Transactional
public class StatusProcessService {

	@Autowired
	private StatusProcessMapper mapper;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	@Autowired
	private ResultService resultService;
	
	@Autowired
	private ThesisService thesisService;
	
	public Integer delete(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}
	
	public Integer update(StatusProcess entity) throws Exception{
		
		return mapper.updateByPrimaryKey(entity);
	}
	
	public Integer save(StatusProcess entity) throws Exception{
		return mapper.insert(entity);
	}
	
	public StatusProcess findById(Integer id){
		return mapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 获得最后操作记录并删除
	 * @param where
	 * @return
	 */
	public int getLastStatusIdAndDelete(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(StatusProcess.class);
		String sql = sqlBuilder
				.fields("id,last_status,result_id")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.order("id", "asc")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		int processId = 0;
		int lastStatus = 0;
		int resultId = 0;
		for(Row row :list){
			processId = Integer.valueOf(row.get("id").toString());
			lastStatus =Integer.valueOf(row.get("lastStatus").toString());
			resultId = Integer.valueOf(row.get("resultId").toString());
		}
		Result result = resultService.findById(resultId);
		if(lastStatus!=3){
			//针对公共平台的保护
			delete(processId);
		}
		else{
			//来自一键送审的也可以删
			Thesis thesis = thesisService.findById(result.getThesisId());
			if(thesis.getIsShortcut()==1){
				delete(processId);
			}
		}
//		System.out.println("deleteId:"+processId+"LastStatus:"+lastStatus);
		return lastStatus;
	}
	
	public Integer find(int thesisId,int resultId,int nowStatus){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(StatusProcess.class);
		Map<String, Object> where = new HashMap<String,Object>();
		if(thesisId!= 0){
			where.put("thesis_id", thesisId);
		}
		if(resultId!= 0){
			where.put("result_id", resultId);
		}
		if(nowStatus!= 0){
			where.put("now_status", nowStatus);
		}
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for (Map<String,Object> row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		return id;
	}
	
	public Integer findSendUser(int resultId){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(StatusProcess.class);
		Map<String, Object> where = new HashMap<String,Object>();
		where.put("result_id", resultId);
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for (Map<String,Object> row : list){
			if(row.get("lastStatus").equals(1)||row.get("lastStatus").equals(2)){
				id = Integer.valueOf(row.get("userId").toString());
			}
		}
		return id;
	}
}
