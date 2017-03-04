package com.zlzkj.app.mapper;

import com.zlzkj.app.model.Diy;

public interface DiyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Diy record);

    Diy selectByPrimaryKey(Integer id);
    
    int updateByPrimaryKey(Diy record);
}