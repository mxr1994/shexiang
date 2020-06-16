package com.tc.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.entity.Role;
import com.tc.mapper.RoleMapper;
import com.tc.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService{
	
	@Autowired
	private RoleMapper me;
	
	public int deleteByPrimaryKey(String db,Long id) {
		// TODO Auto-generated method stub
		return me.deleteByPrimaryKey(db, id);
	}

	public int insert(String db,Role record) {
		return  me.insert(db, record);
	}

	
	
	/**
	 * 根据指定ID查询用户
	 */
	public Role selectByPrimaryKey(String db,Long id) {
		return me.selectByPrimaryKey(db,id);
	}

	public int updateByPrimaryKey(String db,Role record) {
		// TODO Auto-generated method stub
		return me.updateByPrimaryKey(db, record);
	}



	public ArrayList<Role> selectAll(String db) {
		return me.selectAll(db);
	}

	@Override
	public ArrayList<Role> selectAllByUserId(String db, Long id) {
		// TODO Auto-generated method stub
		return me.selectAllByUserId(db,id);
	}

	@Override
	public int deleteByRidAndMid(String db, Long rid, Long mid) {
		// TODO Auto-generated method stub
		return me.deleteByRidAndMid(db, rid, mid);
	}


}
