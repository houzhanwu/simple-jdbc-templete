这是一个简单的ORM工具，基于Spring jdbcTemplete，可以方便的根据对象生成相应的CRUD SQL语句。结合springmvc使用可以大量的减少代码量。

先看例子，比如springmvc接收到了一个User对象，里面包含name，startCreateTime，endCreateTime等查询参数,

	@Transient//javax.persistence注解，用于标识这个字段没有对应的数据库字段
	@Operator(targetColumn="create_time",value=" >= ")//这个用于生成查询条件
	public Timestamp getStartCreateTime() {
		return startCreateTime;
	}
	
	@Transient
	@Operator(targetColumn="create_time",value=" <= ")
	public Timestamp getEndCreateTime() {
		return endCreateTime;
	}
	
	@OrderBy(value="desc")//javax.persistence注解，用于标识这个字段会被用于生成order by语句
	public Timestamp getCreateTime() {
		return createTime;
	}

只要执行jtt.list(user, 0, 10);这句，就会生成下面这条语句 [sql=select * from t_user where 1=1 and create_time >= ? and create_time <= ? ORDER BY create_time desc limit 0,10, params=[2017-09-01 12:32:08.842, 2017-09-01 12:32:10.034]]

这样基本上就能做到不写任何查询的代码，80%的情况只需要把springmvc接收的对象直接传给simple-jdbc-templete，就能查询出结果。 更多的例子请看unit test代码

    @Test
    public void save() throws Exception{
        for (int i = 0; i < 10; i++) {
            User u = new User();
            u.setLogin("user"+i);
            u.setName("user"+i);
            u.setStatus(Byte.parseByte("1"));
            jtt.save(u);
        }

    }
    
    @Test
    public void saveWithID() throws Exception{
        User maxUser = jtt.get("select max(id) id from t_user", null, User.class);
        for (long i = maxUser.getId()+1; i < maxUser.getId()+10; i++) {
            User u = new User();
            u.setId(i);
            u.setLogin("user"+i);
            u.setName("user"+i);
            u.setStatus(Byte.parseByte("1"));
            jtt.save(u);
        }

    }
    
    @Test
    public void saveGetId() throws Exception{
        for (int i = 0; i < 10; i++) {
            User u = new User();
            u.setLogin("user"+i);
            u.setName("user"+i);
            u.setStatus(Byte.parseByte("1"));
            jtt.save(u,true);
            logger.debug(String.valueOf(u.getId()));
        }

    }
    
    @Test
    public void update() throws Exception{
        User u = new User();
        u.setId(3L);
        u.setLogin("ssss");
        jtt.update(u);
    }    
    
    @Test
    public void delete() throws Exception{
        User u = new User();
        u.setId(3L);
        jtt.delete(u);
    }
    
    @Test
    public void list() throws Exception{
        User u = new User();
        u.setLogin("user");
        List<User> list = jtt.list(u,10,20,"id","desc",false);
        for (User user : list) {
            logger.debug(String.valueOf(user.getId()));
        }
    }
    
    @Test
    public void listWithObject() throws Exception{
        User u = new User();
        u.setLogin("admin");
        List<User> list = jtt.list(u);
        jtt.setListFromObject(list);
        for (User user : list) {
            for (Role r : user.getRoles()) {
                logger.debug(r.getName());
            }
        }
    }
    
    @Test
    public void get() throws Exception{
        User u1 = jtt.get(User.class, 1L);
        logger.debug(u1.getLogin());
        User u2 = jtt.get("select * from t_user where id = ?", new Object[]{2L}, User.class);
        logger.debug(u2.getLogin());
    }
    
    @Test
    public void count() throws Exception{
        User u = new User();
        u.setLogin("user1");
        logger.debug(String.valueOf(jtt.count(u)));
        long count = jtt.count("select count(1) from t_user where login like ?", new Object[]{"%user1%"});
        logger.debug(String.valueOf(count));
    }
    
    @Test
    public void testAll() throws Exception {
        for (int i = 0; i < 100; i++) {
            jtt.execute("delete from t_user", null);
            jtt.execute("delete from t_user_role", null);
            Date start = new Date();
            for (int j = 0; j < 1000; j++) {
                User u = new User();
                long currDate = new Date().getTime();
                u.setLogin(String.valueOf(currDate));
                u.setName(String.valueOf(currDate));
                u.setCreatedById(1L);
                u.setCreateTime(new Timestamp(currDate));
                u.setStatus(Byte.parseByte("1"));
                jtt.save(u, true);

                UserRole ur1 = new UserRole();
                ur1.setRoleId(1L);
                ur1.setUserId(u.getId());
                jtt.save(ur1);
                UserRole ur2 = new UserRole();
                ur2.setRoleId(2L);
                ur2.setUserId(u.getId());
                jtt.save(ur2);

                User updateUser = jtt.get(User.class, u.getId());
                updateUser.setUpdateTime(new Timestamp(new Date().getTime()));
                updateUser.setModifiedById(2L);
                jtt.update(updateUser);

                User listUser = new User();
                listUser.setStartCreateTime(new Timestamp(currDate - 1000));
                listUser.setEndCreateTime(new Timestamp(new Date().getTime()));
                jtt.list(listUser, 0, 10, true);
                jtt.list(listUser, 0, 10, "create_time", "desc", false);
                jtt.list("select * from t_user where 1=1  and create_time  >=  ?  and create_time  <=  ?  ORDER BY create_time desc limit 0,10",
                        new Object[] { new Timestamp(currDate - 1000), new Timestamp(currDate) }, User.class);

                jtt.count("select count(1) from t_user where create_time  >=  ?  and create_time  <=  ? limit 0,10",
                        new Object[] { new Timestamp(currDate - 1000), new Timestamp(currDate) });
            }

            User maxUser = jtt.get("select max(id) id from t_user", null, User.class);
            jtt.delete(maxUser);
            Date end = new Date();
            System.out.println("use time:" + (end.getTime() - start.getTime()));
        }
        
    }