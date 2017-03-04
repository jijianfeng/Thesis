package com.zlzkj.app.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zlzkj.app.mapper.MajorMapper;
import com.zlzkj.app.model.College;
import com.zlzkj.app.model.Major;
import com.zlzkj.app.model.University;
import com.zlzkj.app.model.User;
import com.zlzkj.app.util.CheckData;
import com.zlzkj.app.util.CommonUtil;
import com.zlzkj.app.util.UIUtils;
import com.zlzkj.core.mybatis.SqlRunner;
import com.zlzkj.core.sql.Row;
import com.zlzkj.core.sql.SQLBuilder;

@Service
@Transactional
public class MajorService {

	@Autowired
	private MajorMapper mapper;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	@Autowired
	private UniversityService universityService;
	
	@Autowired
	private CollegeService collegeService;
	
	public Integer delete(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}
	
	public Integer update(Major entity) throws Exception{
		
		return mapper.updateByPrimaryKey(entity);
	}
	
	public Integer save(Major entity) throws Exception{
		return mapper.insert(entity);
	}
	
	public Major findById(Integer id){
		return mapper.selectByPrimaryKey(id);
	}
	
	public String findNameById(Integer id){
		return mapper.selectByPrimaryKey(id).getMajorName();
	}
	
	/**
	 * 专业列表
	 * @param where
	 * @return
	 */
	public List<Row> getList(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Major.class);
		String sql = sqlBuilder
				.fields("id,major_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
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
	 * 专业列表无默认值
	 * @param where
	 * @return
	 */
	public List<Row> getListNoDefault(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Major.class);
		String sql = sqlBuilder
				.fields("id,major_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
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
	 * UI专业列表
	 * @param where
	 * @return
	 */
	public Map<String, Object> getUIGridData(Map<String, Object> where,Map<String, String> pageMap) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Major.class);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.parseUIPageAndOrder(pageMap).order("id", "asc")
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		//--删除多余数据（开始）
		int max = 0;
		if(max==0){
			max = list.size();
		}
		int i=0;
		for (int j=0;j<max;j++) {
			Row row = list.get(i);
			int kai=1;
			//删除多余数据--（末）
			String majorTwoCode = row.getString("majorCode");
			if(majorTwoCode.length()==5){
				majorTwoCode = "0" +majorTwoCode;
			}
			String majorOneCode = majorTwoCode.substring(0,4);
			String majorCode = majorTwoCode.substring(0,2);
			row.put("majorOneCode", majorOneCode);
			row.put("majorTwoCode", majorTwoCode);
			row.put("majorOneName", findMajorNameByCode(Integer.valueOf(majorOneCode)));
			row.put("majorTwoName", findMajorNameByCode(Integer.valueOf(majorTwoCode)));
			row.put("majorName",findMajorNameByCode(Integer.valueOf(majorCode)));
			//--删除多余数据（开始）
			if(kai==0){
				list.remove(row);
				if(list.size()==0){
					break;
				}
			}
			else{
				i++;
			}
			//删除多余数据--（末）
		}
		String countSql = sqlBuilder.fields("count(*)").where(where).buildSql();
		Integer count = sqlRunner.count(countSql);
		return UIUtils.getGridData(count, list);
	}
	
	/**
	 * 通过专业代码寻找专业名字
	 * @param where
	 * @return
	 */
	public String findMajorNameByCode(int majorCode){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Major.class);
		String sql = sqlBuilder
				.fields("id")
				.where("major_code = "+majorCode)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		Major major = findById(id);
		return major.getMajorName();
	}
	
	/**
	 * 一级专业列表
	 * @param where
	 * @return
	 */
	public List<Row> getMajorOneList(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Major.class);
		String sql = sqlBuilder
				.fields("id,major_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
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
	 * 通过专业名称寻找专业
	 * @param where
	 * @return
	 */
	public Major findMajorByCode(String majorCode){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Major.class);
		String sql = sqlBuilder
				.fields("id")
				.where("major_code = "+majorCode)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		Major major = findById(id);
		return major;
	}
	
	/**
	 * 二级专业列表
	 * @param where
	 * @return
	 */
	public List<Row> getMajorTwoList(Map<String, Object> where ,String code) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Major.class);
		String sql = sqlBuilder
				.fields("id,major_name as text,major_code as majorCode")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.order("id", "asc")
				.where(where)
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
			//判断
			//System.out.println(row.get("majorCode").toString().subSequence(0, code.length())+"哈哈哈哈哈哈哈");
			if(!code.equals("0")){
				if(!row.get("majorCode").toString().subSequence(0, code.length()).equals(code)){
					kai=0;
				}
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
		Row node = new Row();
		node.put("id", 0);
		node.put("text", "不指定");
		list.add(0,node);
		return list;
	}
	
	/**
	 * 通过专业名称寻找专业Id
	 * @param where
	 * @return
	 */
	public int findMajorIdByCode(String majorCode){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(Major.class);
		String sql = sqlBuilder
				.fields("id")
				.where("major_code = "+majorCode)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Integer id = 0;
		for(Row row : list){
			id = Integer.valueOf(row.get("id").toString());
		}
		return id;
	}
}
