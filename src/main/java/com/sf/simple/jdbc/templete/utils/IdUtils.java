package com.sf.simple.jdbc.templete.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.KeyHolder;


public class IdUtils {
	public static String getUUID(){
		return UUID.randomUUID().toString();
	}
	/**
	 * 根据注解获取自增主键字段名
	 * 如果没找到就返回空字符串
	 * @param po
	 * @return increamentIdFieldName
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static String getAutoGeneratedId(Object po) throws SecurityException, NoSuchMethodException{
		String autoGeneratedId = null;
		//根据注解获取自增主键字段名
		List<Field> allFields = ReflectUtils.getFieldList(po.getClass());
		for(Field f:allFields){
			//获取getter方法
			Method getter = ReflectUtils.getMethod(po.getClass(), f);
			Id idAnno = getter.getAnnotation(Id.class);
			if(idAnno == null){
				continue;
			}
			GeneratedValue generatedValueAnno = getter.getAnnotation(GeneratedValue.class);
			if(generatedValueAnno == null){
				continue;
			}
			
			if(GenerationType.IDENTITY == generatedValueAnno.strategy() || GenerationType.TABLE == generatedValueAnno.strategy()){
				autoGeneratedId = f.getName();
				break;
			}
		}
		return autoGeneratedId;
	}
	
	/**
	 * 将自增id的值设置回去
	 * @param po
	 * @param autoGeneratedId
	 * @param idValue
	 * @throws Exception
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InvalidDataAccessApiUsageException 
	 */
	public static void setAutoIncreamentIdValue(Object po,String autoGeneratedId,KeyHolder idValue) 
			throws NoSuchMethodException, InvalidDataAccessApiUsageException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException{
		String setterName = "set" + CamelNameUtils.capitalize(autoGeneratedId);
		Method setter = ReflectUtils.getMethod(po.getClass(), setterName);
		
		Class<?>[] returnType = setter.getParameterTypes();
		if(returnType[0].equals(Integer.class) ){
			setter.invoke(po, idValue.getKey().intValue());
		}
		if(returnType[0].equals(Long.class)){
			setter.invoke(po, idValue.getKey().longValue());
		}			
	}
}
