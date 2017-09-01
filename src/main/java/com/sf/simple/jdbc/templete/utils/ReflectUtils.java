package com.sf.simple.jdbc.templete.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectUtils {
	private static final Logger logger = LoggerFactory.getLogger(ReflectUtils.class);
	private static Map<Class<?>,String> tableMap = new HashMap<Class<?>,String>();
	private static Map<Class<?>,List<Field>> fieldMap = new HashMap<Class<?>,List<Field>>();
	private static Map<Class<?>,Map<String,Method>> methodMap = new HashMap<Class<?>,Map<String,Method>>();
    
    public static <T> Method getMethod(Class<T> clazz, String methodName) {
        Map<String,Method> mmap = methodMap.get(clazz);
		if(mmap==null){
			mmap = new HashMap<String, Method>();
			methodMap.put(clazz, mmap);
    	}
		if(mmap.get(methodName) == null){
			try {
		        Class<? extends Object> tempClass = clazz;
		        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {
		        	List<Method> methodList = Arrays.asList(tempClass.getDeclaredMethods());
		        	for (Method m : methodList) {
		        		logger.debug(clazz.getName() + " add method "+m.getName());
		        		mmap.put(m.getName(), m);
					}
		        	methodMap.put(clazz, mmap);
		        	Method mm = mmap.get(methodName);
	        		if(mm!=null){
	        			return mm;
	        		}
		            tempClass = tempClass.getSuperclass();
		        }
            } catch (Exception e) {
                logger.error(methodName + " doesn't exist!", e);
            }
		}
        return mmap.get(methodName);
    }
    
    public static <T> List<Field> getFieldList(Class<T> clazz) {
    	List<Field> fMap = fieldMap.get(clazz);
		if(fMap!=null){
    		return fMap;
    	}
        Map<String,Field> classFieldMap = new HashMap<>();
        Class<? extends Object> tempClass = clazz;
        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {
            Field[] declaredFields = tempClass.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
				Field field = declaredFields[i];
				if(field.getName().equals("serialVersionUID")){
					continue;
				}
				if(classFieldMap.get(field.getName())==null){
					classFieldMap.put(field.getName(), field);
				}
			}
            tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
        }
        List<Field> fields = new ArrayList<Field>(classFieldMap.values());
		fieldMap.put(clazz, fields);
        return fields;
    }
    
    public static <T> Method getMethod(Class<T> clazz, Field f) {
        String methodName = "get" + CamelNameUtils.capitalize(f.getName());
        return getMethod(clazz, methodName);
    }
    
    public static <T> String getTableName(Class<T> clazz) {
    	String tableName = tableMap.get(clazz);
    	if(tableName!=null){
    		return tableName;
    	}
        Table tableAnno = clazz.getAnnotation(Table.class);
        if (tableAnno != null) {
            if (tableAnno.catalog() != null && !tableAnno.catalog().trim().equals("")) {
            	tableName = tableAnno.catalog() + "." + tableAnno.name();
            }else{
            	tableName = tableAnno.name();
            }
        }else{
            // if Table annotation is null
            String className = clazz.getName();
            tableName = CamelNameUtils.camel2underscore(className.substring(className.lastIndexOf(".") + 1));
        }
        tableMap.put(clazz, tableName);
        return tableName;
    }
}
