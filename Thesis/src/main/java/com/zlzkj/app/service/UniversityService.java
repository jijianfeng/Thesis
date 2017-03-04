package com.zlzkj.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zlzkj.app.mapper.UniversityMapper;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Thesis;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.util.CheckData;
import com.zlzkj.app.util.CommonUtil;
import com.zlzkj.app.util.ExcelTransport;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;
import com.zlzkj.core.util.Fn;

@Service
@Transactional
public class UniversityService {

	@Autowired
	private UniversityMapper mapper;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	
	public Integer delete(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}
	
	public Integer update(University entity) throws Exception{
		
		return mapper.updateByPrimaryKey(entity);
	}
	
	public Integer save(University entity) throws Exception{
		return mapper.insert(entity);
	}
	
	public University findById(Integer id){
		return mapper.selectByPrimaryKey(id);
	}
	
	public String findNameById(Integer id){
		return mapper.selectByPrimaryKey(id).getUniversityName();
	}
	
	/**
	 * 大学列表
	 * @param where
	 * @return
	 */
	public List<Row> getList(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(University.class);
		String sql = sqlBuilder
				.fields("id,university_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.order("id", "asc")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Row node = new Row();
		node.put("id", 0);
		node.put("text", "不指定");
		list.add(0,node);
		return list;
	}
	
	/**
	 * 大学列表无默认值
	 * @param where
	 * @return
	 */
	public List<Row> getListNoDefault(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(University.class);
		String sql = sqlBuilder
				.fields("id,university_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
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
	 * UI大学列表
	 * @param where
	 * @return
	 */
	public Map<String, Object> getUIGridData(Map<String, Object> where,
			Map<String, String> pageMap) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(University.class);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.parseUIPageAndOrder(pageMap).order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
//		for (Row row : list) {
//			Area area = areaService.findById(Integer.valueOf(row.get("areaId").toString()));
//			row.put("area", area.getAreaName());
//		}
		String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	public Map<String, Object> getUniversityGridData(Map<String, Object> where,
			Map<String, String> pageMap) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(University.class);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.parseUIPageAndOrder(pageMap)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		//将ID数据转化为文字
		//获取总条数
		String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	/**
	 * 通过代码找学校
	 * @param where
	 * @return
	 */
	public int getUniverstiyByCode(int code) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(University.class);
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.order("id", "asc")
				.where("university_code = "+code)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		int i=0;
		for(Row row :list){
			i = row.getInt("id");
		}
		return i;
	}
	
	/**
	 * 注册学院名称
	 * @param where
	 * @return
	 */
	public String getNewCollegeName(int universityId) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(User.class);
		Map<String, Object> where = new HashMap<String,Object>();
		where.put("university_id", universityId);
		University university = findById(universityId);
		String sql = sqlBuilder
				.fields("*")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		if(list.size()>9){
			return "ipy"+university.getUniversityCode()+String.valueOf(list.size());
		}
		else{
			return "ipy"+university.getUniversityCode()+"0"+list.size();
		}
	}
}
