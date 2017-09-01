package com.sf.simple.jdbc.templete.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sf.simple.jdbc.templete.SqlParamsPairs;
import com.sf.simple.jdbc.templete.annotation.Ignore;
import com.sf.simple.jdbc.templete.annotation.Operator;
import com.sf.simple.jdbc.templete.exception.NoColumnAnnotationFoundException;
import com.sf.simple.jdbc.templete.exception.NoIdAnnotationFoundException;

public class ModelSqlUtils {
	private static final Logger logger = LoggerFactory.getLogger(ModelSqlUtils.class);

	private static String getColumnNameFromGetter(Method getter, Field f) {
		String columnName = "";
		Column columnAnno = getter.getAnnotation(Column.class);
		if (columnAnno != null) {
			// 如果是列注解就读取name属性
			columnName = columnAnno.name();
		}

		if (columnName == null || "".equals(columnName)) {
			// 如果没有列注解就用命名方式去猜
			columnName = CamelNameUtils.camel2underscore(f.getName());
		}
		return columnName;
	}

	/**
	 * 0:getter,1:value,2:columnName
	 */
	private static <T> Object[] getGetterValueColumnName(T po, Field f,boolean forSelect)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] array = new Object[3];
		// 获取具体参数值
		Method getter = ReflectUtils.getMethod(po.getClass(), f);
		if (getter == null) {
			return null;
		}
		array[0] = getter;
		
		// 如果有column注解就用注解的name
		String columnName = getColumnNameFromGetter(getter, f);
		array[2] = columnName;
		
		if(forSelect){
			Ignore ignore = getter.getAnnotation(Ignore.class);
			if (ignore != null) {
				return array;
			}	
		}else{
			Transient tranAnno = getter.getAnnotation(Transient.class);
			if (tranAnno != null) {
				return array;
			}	
		}
		
		Object value = getter.invoke(po);
		if (value == null || value.equals("")) {
			return array;
		}
		array[1] = value;

		return array;
	}

	public static <T> SqlParamsPairs getInsertFromObject(T po)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		StringBuffer insertSql = new StringBuffer();
		StringBuffer paramsSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		String tableName = ReflectUtils.getTableName(po.getClass());

		insertSql.append("insert into " + tableName + " (");

		int count = 0;
		List<Field> fieldList = ReflectUtils.getFieldList(po.getClass());
		for (Field f : fieldList) {
			Object[] getterValueColumnName = getGetterValueColumnName(po, f,false);

			if (getterValueColumnName[1] == null) {
				continue;
			}

			if (count != 0) {
				insertSql.append(",");
			}

			insertSql.append(getterValueColumnName[2]);

			if (count != 0) {
				paramsSql.append(",");
			}
			paramsSql.append("?");

			params.add(getterValueColumnName[1]);
			count++;
		}

		insertSql.append(") values (");
		insertSql.append(paramsSql + ")");

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(insertSql.toString(), params.toArray());
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;

	}

	public static <T> SqlParamsPairs getUpdateFromObject(T po) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoIdAnnotationFoundException {

		StringBuffer updateSql = new StringBuffer();
		StringBuffer whereSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		String tableName = ReflectUtils.getTableName(po.getClass());

		updateSql.append("update " + tableName + " set");

		int count = 0;
		List<Field> fieldList = ReflectUtils.getFieldList(po.getClass());

		Object idValue = null;

		for (Field f : fieldList) {
			Object[] getterValueColumnName = getGetterValueColumnName(po, f,false);

			if (getterValueColumnName[1] == null) {
				continue;
			}

			// 看看是不是主键
			Method getter = (Method) getterValueColumnName[0];
			Id idAnno = getter.getAnnotation(Id.class);
			if (idAnno != null) {
				// 如果是主键
				whereSql.append(getterValueColumnName[2] + " = ?");
				idValue = getterValueColumnName[1];
				continue;
			}

			// 如果是普通列
			params.add(getterValueColumnName[1]);

			if (count != 0) {
				updateSql.append(",");
			}
			updateSql.append(" " + getterValueColumnName[2] + " = ?");

			count++;
		}

		// 全部遍历完如果找不到主键就抛异常
		if (idValue == null) {
			throw new NoIdAnnotationFoundException(po.getClass());
		}

		updateSql.append(" where ");
		updateSql.append(whereSql);
		params.add(idValue);

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(updateSql.toString(), params.toArray());
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;

	}

	public static SqlParamsPairs getDeleteFromObject(Object po) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoIdAnnotationFoundException {

		StringBuffer deleteSql = new StringBuffer();

		String tableName = ReflectUtils.getTableName(po.getClass());

		deleteSql.append("delete from " + tableName + " where ");

		List<Field> fieldList = ReflectUtils.getFieldList(po.getClass());

		Object idValue = null;
		Id idAnno = null;

		for (Field f : fieldList) {
			Object[] getterValueColumnName = getGetterValueColumnName(po, f,false);

			if (getterValueColumnName[1] == null) {
				continue;
			}

			Method getter = (Method) getterValueColumnName[0];
			idAnno = getter.getAnnotation(Id.class);
			if (idAnno == null) {
				continue;
			}

			deleteSql.append(getterValueColumnName[2] + " = ?");

			idValue = getterValueColumnName[1];

			break;
		}

		// 全部遍历完如果找不到主键就抛异常
		if (idAnno == null) {
			throw new NoIdAnnotationFoundException(po.getClass());
		}

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(deleteSql.toString(), new Object[] { idValue });
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;

	}
	
	private static void setSqlParms(StringBuffer sql, List<Object> params,Object[] getterValueColumnName) {
		
		Method getter = (Method) getterValueColumnName[0];
		Object value = getterValueColumnName[1];
		String columnName = (String)getterValueColumnName[2];
		
		Operator OpAnno = getter.getAnnotation(Operator.class);
		if (OpAnno == null) {
			sql.append(" and " + columnName + " = ? ");
			params.add(value);
		} else {
			String annoValue = OpAnno.value();
			String targetColumn = "".equals(OpAnno.targetColumn()) ? columnName : OpAnno.targetColumn();
			
			if ("LIKE".equals(annoValue.trim().toUpperCase())) {
				sql.append(" and " + targetColumn + " " + OpAnno.value() + " ? ");
				params.add("%" + value + "%");
			} else if ("IN".equals(annoValue.trim().toUpperCase())) {
				sql.append(" and " + targetColumn + " " + OpAnno.value() + " (" + value + ")");
			} else {
				sql.append(" and " + targetColumn + " " + OpAnno.value() + " ? ");
				params.add(value);
			}
		}
	}

	public static <T> SqlParamsPairs getSearchFromObject(Object po, long startRow, int size, String sortBy,
			String sortDir) throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		String tableName = ReflectUtils.getTableName(po.getClass());

		sql.append("select * from " + tableName + " where 1=1 ");

		List<Field> fieldList = ReflectUtils.getFieldList(po.getClass());
		String orderByStr = " ORDER BY ";
		for (Field f : fieldList) {
			Object[] getterValueColumnName = getGetterValueColumnName(po, f,true);

			if (sortBy == null) {
				Method getter = (Method) getterValueColumnName[0];
				OrderBy orderByAnno = getter.getAnnotation(OrderBy.class);
				if (orderByAnno != null) {
					Object columnName = getterValueColumnName[2];
					orderByStr += (columnName + " " + orderByAnno.value() + ",");
					continue;
				}
			}
			
			if (getterValueColumnName[1] == null) {
				continue;
			}

			setSqlParms(sql, params, getterValueColumnName);
			
		}
		if (sortBy == null) {
			if (!" ORDER BY ".equals(orderByStr)) {
				sql.append(orderByStr.substring(0, orderByStr.length() - 1));// 去掉,号
			}
		} else {
			sql.append(orderByStr + sortBy + " " + sortDir);
		}

		if (size > 0) {
			sql.append(" limit " + startRow);
			sql.append("," + size);
		}

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(sql.toString(), params.toArray());
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;
	}

	public static <T> SqlParamsPairs getGetFromObject(Class<T> clazz, Object id)
			throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException {

		StringBuffer getSql = new StringBuffer();
		String tableName = ReflectUtils.getTableName(clazz);
		getSql.append("select * from " + tableName + " where ");

		Id idAnno = null;
		List<Field> fieldList = ReflectUtils.getFieldList(clazz);

		for (Field f : fieldList) {
			Method getter = ReflectUtils.getMethod(clazz, f);
			if (getter == null) {
				continue;
			}
			idAnno = getter.getAnnotation(Id.class);
			if (idAnno == null) {
				continue;
			}
			String columnName = getColumnNameFromGetter(getter, f);
			getSql.append(columnName + " = ?");
			break;
		}

		// 全部遍历完如果找不到主键就抛异常
		if (idAnno == null) {
			throw new NoIdAnnotationFoundException(clazz);
		}

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(getSql.toString(), new Object[] { id });
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;
	}

	public static <T> SqlParamsPairs getCountFromObject(Object po)
			throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		String tableName = ReflectUtils.getTableName(po.getClass());

		sql.append("select count(1) from " + tableName + " where 1=1 ");

		// 分析列
		List<Field> fieldList = ReflectUtils.getFieldList(po.getClass());

		for (Field f : fieldList) {
			Object[] getterValueColumnName = getGetterValueColumnName(po, f,true);

			if (getterValueColumnName[1] == null) {
				continue;
			}

			setSqlParms(sql, params, getterValueColumnName);
		}

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(sql.toString(), params.toArray());
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;
	}
	

}
