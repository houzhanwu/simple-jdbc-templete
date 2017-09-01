package com.sf.simple.jdbc.templete.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
* @author 作者 史锋
* @version 创建时间：2017年6月12日 下午7:19:21
*/
@Table(name="t_user_role")
public class UserRole implements Serializable{
	private static final long serialVersionUID = 4405068293658708523L;
	
	private Long id;
	private Long userId;
	private Long roleId;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}