package com.zlzkj.app.mapper;

import com.zlzkj.app.model.StatusProcess;

public interface StatusProcessMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StatusProcess record);
    
    StatusProcess selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(StatusProcess record);
}