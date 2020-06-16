package com.tc.service;

import java.util.ArrayList;

import com.tc.entity.MenuInfo;

public interface MenuInfoService {
    int deleteByPrimaryKey(String db,Long id);

    int insert(String db,MenuInfo record);

    MenuInfo selectByPrimaryKey(String db,Long id);

    int updateByPrimaryKey(String db,MenuInfo record);
       
    ArrayList<MenuInfo> selectAll(String db);
    ArrayList<MenuInfo> selectByMenuId(String db,Long id);
    
    
}