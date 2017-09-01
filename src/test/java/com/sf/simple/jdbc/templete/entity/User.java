package com.sf.simple.jdbc.templete.entity;

import java.util.List;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.sf.simple.jdbc.templete.annotation.GetList;
import com.sf.simple.jdbc.templete.annotation.Operator;

/**
* @author 作者 史锋
* @version 创建时间：2017年6月12日 下午5:13:08
*/
@Table(name= "t_user")
public class User extends BaseEntity  {
	private static final long serialVersionUID = 4038269228293334137L;
	
	public interface EnrollGroup {}
	public interface VerifyGroup {}
	public interface PasswordGroup {}
	
	protected String login;
	protected String name;
	protected Byte gender;
	protected String email;	
	protected String mobile;
	protected Long userTypeId;
	protected String plainPassword;
	protected String password;
	protected String salt;
	protected String headerPic;
	protected String language;
	protected List<Role> roles;
	
	@Operator(value="LIKE")
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Byte getGender() {
		return gender;
	}

	public void setGender(Byte gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Long getUserTypeId() {
		return userTypeId;
	}

	public void setUserTypeId(Long userTypeId) {
		this.userTypeId = userTypeId;
	}

	@Transient
	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getHeaderPic() {
		return headerPic;
	}

	public void setHeaderPic(String headerPic) {
		this.headerPic = headerPic;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Transient
	@GetList(clazz=Role.class,referenceColumn="id",
		sql="select r.* from t_user_role ur,t_role r where ur.user_id = ? and r.id = ur.role_id")
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}