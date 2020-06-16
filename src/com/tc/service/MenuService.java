package com.tc.service;

import java.util.ArrayList;

import com.tc.entity.Menu;

public interface MenuService {
    int deleteByPrimaryKey(String db,Long id);

    int insert(String db,Menu record);

    Menu selectByPrimaryKey(String db,Long id);

    int updateByPrimaryKey(String db,Menu record);
       
    ArrayList<Menu> selectAll(String db);
    
}