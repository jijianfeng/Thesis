package com.zlzkj.app.mapper;

import com.zlzkj.app.model.Thesis;

public interface ThesisMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Thesis record);

    Thesis selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Thesis record);
}