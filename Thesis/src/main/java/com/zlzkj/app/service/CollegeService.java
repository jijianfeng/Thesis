package com.zlzkj.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zlzkj.app.mapper.CollegeMapper;
import com.zlzkj.app.model.College;
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
public class CollegeService {

	@Autowired
	private CollegeMapper mapper;
	
	@Autowired
	private SqlRunner sqlRunner;
	
	@Autowired
	private UniversityService universityService;
	
	public Integer delete(Integer id){
		return mapper.deleteByPrimaryKey(id);
	}
	
	public Integer update(College entity) throws Exception{
		
		return mapper.updateByPrimaryKey(entity);
	}
	
	public Integer save(College entity) throws Exception{
		return mapper.insert(entity);
	}
	
	public College findById(Integer id){
		return mapper.selectByPrimaryKey(id);
	}
	
	public String findNameById(Integer id){
		return mapper.selectByPrimaryKey(id).getCollegeName();
	}
	
	/**
	 * 学院列表
	 * @param where
	 * @return
	 */
	public List<Row> getList(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(College.class);
		String sql = sqlBuilder
				.fields("id,college_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
				.order("id", "asc")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		Row node = new Row();
		node.put("id", 0);
		node.put("text", "[默认]研究生部");
		list.add(0,node);
		return list;
	}
	
	/**
	 * 学院列表无默认值
	 * @param where
	 * @return
	 */
	public List<Row> getListNoDefault(Map<String, Object> where) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(College.class);
		String sql = sqlBuilder
				.fields("id,college_name as text")   //这里约定前端grid需要显示多少个具体列，也可以全部*
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
	 * UI学院列表
	 * @param where
	 * @return
	 */
	public Map<String, Object> getUIGridData(Map<String, Object> where,Map<String, String> pageMap ,String areaId) {
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(College.class);
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
			University university = universityService.findById(Integer.valueOf(row.get("universityId").toString()));
			row.put("university", university.getUniversityName());
			row.put("universityCode", university.getUniversityCode());
			if(areaId!=null){
				if(!areaId.equals(String.valueOf(university.getAreaId()))){
					kai=0;
				}
			}
//			Area area = areaService.findById(university.getAreaId());
//			row.put("area", area.getAreaName());
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
	
	public int findCollegeByName(int universityId ,String collegeName){
		SQLBuilder sqlBuilder = SQLBuilder.getSQLBuilder(College.class);
		Map<String, Object> where = new HashMap<String,Object>();
		where.put("university_id", universityId);
		where.put("college_name", collegeName);
		String sql = sqlBuilder
				.fields("*")
				.where(where)
				.buildSql();
		List<Row> list = sqlRunner.select(sql);
		int id = 0;
		for(Row row:list){
			id = Integer.valueOf(row.getString("id"));
		}
		return id;
	}
}
