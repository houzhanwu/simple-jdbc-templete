# simple-jdbc-templete
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

只要执行jtt.list(user, 0, 10);这句，就会生成下面这条语句
[sql=select * from t_user where 1=1  and create_time  >=  ?  and create_time  <=  ?  ORDER BY create_time desc limit 0,10, params=[2017-09-01 12:32:08.842, 2017-09-01 12:32:10.034]]

这样基本上就能做到不写任何查询的代码，80%的情况只需要把springmvc接收的对象直接传给simple-jdbc-templete，就能查询出结果。
更多的例子请看unit test代码