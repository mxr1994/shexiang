package com.tc.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.entity.MenuInfo;
import com.tc.mapper.MenuInfoMapper;
import com.tc.service.MenuInfoService;

@Service
public class MenuInfoServiceImpl implements MenuInfoService{
	
	@Autowired
	private MenuInfoMapper me;
	
	public int deleteByPrimaryKey(String db,Long id) {
		// TODO Auto-generated method stub
		return me.deleteByPrimaryKey(db, id);
	}

	public int insert(String db,MenuInfo record) {
		return  me.insert(db, record);
	}

	
	
	/**
	 * 根据指定ID查询用户
	 */
	public MenuInfo selectByPrimaryKey(String db,Long id) {
		return me.selectByPrimaryKey(db,id);
	}

	public int updateByPrimaryKey(String db,MenuInfo record) {
		// TODO Auto-generated method stub
		return me.updateByPrimaryKey(db, record);
	}



	public ArrayList<MenuInfo> selectAll(String db) {
		return me.selectAll(db);
	}

	@Override
	public ArrayList<MenuInfo> selectByMenuId(String db, Long id) {
		// TODO Auto-generated method stub
		return me.selectByMenuId(db, id);
	}


}
