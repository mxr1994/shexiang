package com.tc.entity;

public class Role {
    private Long id;
    private Integer userId;//角色名称
    private Integer menuId;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	

	

    
}