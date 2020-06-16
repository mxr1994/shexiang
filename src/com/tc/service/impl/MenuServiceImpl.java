package com.tc.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.entity.Menu;
import com.tc.mapper.MenuMapper;
import com.tc.service.MenuService;

@Service
public class MenuServiceImpl implements MenuService{
	
	@Autowired
	private MenuMapper me;
	
	public int deleteByPrimaryKey(String db,Long id) {
		// TODO Auto-generated method stub
		return me.deleteByPrimaryKey(db, id);
	}

	public int insert(String db,Menu record) {
		return  me.insert(db, record);
	}

	
	
	/**
	 * 根据指定ID查询用户
	 */
	public Menu selectByPrimaryKey(String db,Long id) {
		return me.selectByPrimaryKey(db,id);
	}

	public int updateByPrimaryKey(String db,Menu record) {
		// TODO Auto-generated method stub
		return me.updateByPrimaryKey(db, record);
	}



	public ArrayList<Menu> selectAll(String db) {
		return me.selectAll(db);
	}


}
