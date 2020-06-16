package com.tc.entity;

import java.util.List;

public class Menu {
    private Long id;
    private String menuName;
    private String icon;
    private List<MenuInfo> menuInfoList;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public List<MenuInfo> getMenuInfoList() {
		return menuInfoList;
	}

	public void setMenuInfoList(List<MenuInfo> menuInfoList) {
		this.menuInfoList = menuInfoList;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}


	

	

	

    
}