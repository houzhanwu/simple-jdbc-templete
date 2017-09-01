package com.sf.simple.jdbc.templete.entity;

import javax.persistence.Table;

/**
* @author 作者 史锋
* @version 创建时间：2017年6月12日 下午5:11:53
*/
@Table(name= "t_permission")
public class Permission extends BaseEntity  {
	private static final long serialVersionUID = -1041151510773546645L;
	
	private String name;
	private String description;
	private String url;
	private String code;
	private String parentId;
	private Integer displayOrder;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	

}