package com.sf.simple.jdbc.templete.entity;

import java.util.List;

import javax.persistence.Table;

/**
* @author 作者 史锋
* @version 创建时间：2017年6月12日 下午7:19:21
*/
@Table(name="t_role")
public class Role extends BaseEntity  {
	private static final long serialVersionUID = 210332014635544290L;
	
	private String name;
	private String component;
	private String description;	
	
	private List<Permission> permissions;

	public String getComponent() {
		return this.component;
	}

	public void setComponent(String component) {
		this.component = component;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	
}