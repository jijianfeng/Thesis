package com.zlzkj.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zlzkj.app.mapper.TitleMapper;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Major;
import com.zlzkj.app.model.Title;
import com.zlzkj.app.model.University;
import com.zlzkj.app.util.CheckData;
import com.zlzkj.app.util.CommonUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;

@Service
@Transactional
public class TitleService {

	@Autowired
	private TitleMapper mapper;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	public Integer delete(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}
	
	public Integer update(Title entity) throws Exception{
		
		return mapper.updateByPrimaryKey(entity);
	}
	
	public Integer save(Title entity) throws Exception{
		return mapper.insert(entity);
	}
	
	public Title findById(Integer id){
		return mapper.selectByPrimaryKey(id);
	}
	
	/**
	 * UI职称列表
	 * @param where
	 * @return
	 */
	public Map<String, Object> getUIGridData(Map<String, Object> where,Map<String, String> pageMap) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Title.class);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.parseUIPageAndOrder(pageMap).order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	/**
	 * 职称列表无默认值
	 * @param where
	 * @return
	 */
	public List<Row> getList(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Title.class);
		String sql = sqlBuilder
				.fields("id,titles_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
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
}
