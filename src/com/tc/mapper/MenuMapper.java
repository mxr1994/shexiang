package com.tc.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

import com.tc.entity.Menu;

public interface MenuMapper {
    int deleteByPrimaryKey(@Param("db") String db,@Param("id") Long id);

    int insert(@Param("db") String db,@Param("menu") Menu record);

    int insertSelective(@Param("db") String db,@Param("menu") Menu record);

    Menu selectByPrimaryKey(@Param("db") String db,@Param("id") Long id);

    int updateByPrimaryKey(@Param("db") String db,@Param("menu") Menu record);
    
    ArrayList<Menu> selectAll(@Param("db") String db);
    
    
}