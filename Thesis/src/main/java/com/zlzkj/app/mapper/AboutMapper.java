package com.zlzkj.app.mapper;

import com.zlzkj.app.model.About;

public interface AboutMapper {
	
    int deleteByPrimaryKey(Integer id);

    int insert(About record);

    About selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(About record);
}