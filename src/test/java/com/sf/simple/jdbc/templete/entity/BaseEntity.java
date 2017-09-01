package com.sf.simple.jdbc.templete.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OrderBy;

import com.sf.simple.jdbc.templete.annotation.Operator;


/**
* @author 作者 史锋
* @version 创建时间：2017年6月15日 上午10:12:55
*/
@MappedSuperclass
public abstract class BaseEntity implements Serializable{
	private static final long serialVersionUID = -5853790348761629991L;
	private Long id;
	private Long createdById;
	private Long modifiedById;
	private Timestamp createTime;
	private Timestamp updateTime;
	private Byte status;
	
	private Timestamp startCreateTime;
	private Timestamp endCreateTime;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public Long getModifiedById() {
		return modifiedById;
	}

	public void setModifiedById(Long modifiedById) {
		this.modifiedById = modifiedById;
	}
	@OrderBy(value="desc")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}
	
	@Operator(targetColumn="create_time",value=" >= ")
	public Timestamp getStartCreateTime() {
		return startCreateTime;
	}

	public void setStartCreateTime(Timestamp startCreateTime) {
		this.startCreateTime = startCreateTime;
	}

	@Operator(targetColumn="create_time",value=" <= ")
	public Timestamp getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(Timestamp endCreateTime) {
		this.endCreateTime = endCreateTime;
	}
	
}
