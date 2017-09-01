-- ----------------------------
-- Table structure for t_permission
-- ----------------------------
DROP TABLE IF EXISTS t_permission;
CREATE TABLE t_permission (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  description varchar(255) NOT NULL,
  url varchar(255) ,
  code varchar(255) ,
  parent_id varchar(50) ,
  display_order int(11) ,
  created_by_id varchar(50) ,
  modified_by_id varchar(50) ,
  create_time datetime ,
  update_time datetime ,
  status tinyint(4) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_permission
-- ----------------------------

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS t_role;
CREATE TABLE t_role (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  component varchar(128) ,
  description varchar(255) NOT NULL,
  created_by_id bigint ,
  modified_by_id bigint ,
  create_time datetime ,
  update_time datetime ,
  status tinyint(4) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO t_role VALUES (1, 'admin', null, '管理员角色', 1, null, '2017-06-15 10:16:55', null, 1);
INSERT INTO t_role VALUES (2, 'user', null, '普通用户角色', 1, null, '2017-06-15 10:16:55', null, 1);
INSERT INTO t_role VALUES (3, 'manager', null, '管理用户角色', 1, null, '2017-06-15 10:16:55', null, 1);

-- ----------------------------
-- Table structure for t_role_permission
-- ----------------------------
DROP TABLE IF EXISTS t_role_permission;
CREATE TABLE t_role_permission (
  id bigint NOT NULL AUTO_INCREMENT,
  role_id bigint NOT NULL,
  permission_id bigint NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
  id bigint NOT NULL AUTO_INCREMENT,
  login varchar(100) NOT NULL,
  name varchar(128) ,
  gender varchar(2) ,
  language varchar(20) ,
  email varchar(128) ,
  mobile varchar(20) ,
  user_type_id bigint ,
  password varchar(255) ,
  salt varchar(64) ,
  headerPic varchar(255) ,
  created_by_id bigint ,
  modified_by_id bigint ,
  create_time datetime ,
  update_time datetime ,
  status tinyint(4) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE t_user ADD INDEX index_create_time (create_time) ;


-- ----------------------------
-- Table structure for t_user_permission
-- ----------------------------
DROP TABLE IF EXISTS t_user_permission;
CREATE TABLE t_user_permission (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  permission_id bigint NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS t_user_role;
CREATE TABLE t_user_role (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

