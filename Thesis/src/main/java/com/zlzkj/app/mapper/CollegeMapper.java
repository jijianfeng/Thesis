package com.zlzkj.app.mapper;

import com.zlzkj.app.model.College;

public interface CollegeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(College record);

    College selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(College record);
}