package com.zlzkj.app.mapper;

import com.zlzkj.app.model.University;

public interface UniversityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(University record);

    University selectByPrimaryKey(Integer id);
    
    int updateByPrimaryKey(University record);
}