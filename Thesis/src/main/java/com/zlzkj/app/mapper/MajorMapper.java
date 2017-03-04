package com.zlzkj.app.mapper;

import com.zlzkj.app.model.Major;

public interface MajorMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Major record);

    Major selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Major record);
}