package com.zlzkj.app.mapper;

import com.zlzkj.app.model.Title;

public interface TitleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Title record);
    
    Title selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Title record);
}