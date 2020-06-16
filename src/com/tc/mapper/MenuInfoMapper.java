package com.tc.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

import com.tc.entity.MenuInfo;

public interface MenuInfoMapper {
    int deleteByPrimaryKey(@Param("db") String db,@Param("id") Long id);

    int insert(@Param("db") String db,@Param("menuInfo") MenuInfo record);

    int insertSelective(@Param("db") String db,@Param("menuInfo") MenuInfo record);

    MenuInfo selectByPrimaryKey(@Param("db") String db,@Param("id") Long id);

    int updateByPrimaryKey(@Param("db") String db,@Param("menuInfo") MenuInfo record);
    
    ArrayList<MenuInfo> selectAll(@Param("db") String db);
    
    ArrayList<MenuInfo> selectByMenuId(@Param("db") String db,@Param("id") Long id);
}