package com.zlzkj.app.mapper;

import com.zlzkj.app.model.Result;

public interface ResultMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Result record);

    Result selectByPrimaryKey(Integer id);

    int updateByPrimaryKeyWithBLOBs(Result record);

    int updateByPrimaryKey(Result record);
}