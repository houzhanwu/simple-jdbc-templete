package com.sf.simple.jdbc.templete;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.sf.simple.jdbc.templete.annotation.GetList;
import com.sf.simple.jdbc.templete.utils.CamelNameUtils;
import com.sf.simple.jdbc.templete.utils.IdUtils;
import com.sf.simple.jdbc.templete.utils.ModelSqlUtils;
import com.sf.simple.jdbc.templete.utils.ReflectUtils;

@Component
public class JdbcTemplateTool {
	private final Logger logger = LoggerFactory.getLogger(ModelSqlUtils.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}	
	
    public int execute(String sql, Object[] params) throws Exception {
    	return jdbcTemplate.update(sql, params);
    }
    
	private void logError(String sql, Object[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(Object p:params){
			sb.append(p + " | ");
		}
		sb.append("]");
		logger.error("Error SQL: " + sql + " Params: " + sb.toString());
	}

    public int save(Object po,boolean getId) throws Exception {
    	SqlParamsPairs sqlAndParams = ModelSqlUtils.getInsertFromObject(po);
    	if(getId){
    		int update = 0;
    		String autoGeneratedColumnName = IdUtils.getAutoGeneratedId(po);
    		if(autoGeneratedColumnName!=null){
    			ReturnIdPreparedStatementCreator psc = 
    					new ReturnIdPreparedStatementCreator(sqlAndParams.getSql(), sqlAndParams.getParams(),autoGeneratedColumnName);
    			KeyHolder keyHolder = new GeneratedKeyHolder();
    			try{
    				update = jdbcTemplate.update(psc, keyHolder);
    			}catch(DataAccessException e){
    				logError(sqlAndParams.getSql(), sqlAndParams.getParams());
    				throw e;
    			}
    			IdUtils.setAutoIncreamentIdValue(po,autoGeneratedColumnName,keyHolder);
    		}
    		return update;
    	}else{
	    	return jdbcTemplate.update(sqlAndParams.getSql(), sqlAndParams.getParams());
		}

    }
    
    public int save(Object po) throws Exception {
    	return save(po,false);
    }

    public int update(Object po) throws Exception {
    	SqlParamsPairs sqlAndParams = ModelSqlUtils.getUpdateFromObject(po);
    	return jdbcTemplate.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }
    
    public int delete(Object po) throws Exception {
    	SqlParamsPairs sqlAndParams = ModelSqlUtils.getDeleteFromObject(po);
    	return jdbcTemplate.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> list(T po,long startRow ,int size,String sortBy,String sortDir,boolean getObjectList) throws Exception{
		SqlParamsPairs sqlAndParams = ModelSqlUtils.getSearchFromObject(po,startRow ,size,sortBy,sortDir);
		List<T> list = jdbcTemplate.query(sqlAndParams.getSql(), sqlAndParams.getParams(),
                new BeanPropertyRowMapper(po.getClass()));
		if(list!=null && list.size()>0){
        	if(getObjectList){
        		setListFromObject(list);
        	}
            return list;
        } else {
            return null;
        }
	}
	
	public <T> List<T> list(T po)  throws Exception{
		return list(po, 0, 0, null, null,false);
	}
	
	public <T> List<T> list(T po,long startRow ,int size)  throws Exception{
		return list(po, startRow, size, null, null,false);
	}
	
	public <T> List<T> list(T po,boolean getObjectList)  throws Exception{
		return list(po, 0, 0, null, null,getObjectList);
	}
	
	public <T> List<T> list(T po,long startRow ,int size,boolean getObjectList)  throws Exception{
		return list(po, startRow, size, null, null,getObjectList);
	}
	
	public long count(String sql, Object[] params) {
		
		long rowCount = 0;
		try{
			Map<String, Object> resultMap = null;
			if (params == null || params.length == 0) {
				resultMap = jdbcTemplate.queryForMap(sql);
			} else {
				resultMap = jdbcTemplate.queryForMap(sql, params);
			}
			Iterator<Map.Entry<String, Object>> it = resultMap.entrySet().iterator();
			if(it.hasNext()){
				Map.Entry<String, Object> entry = it.next();
				rowCount = ((Long)entry.getValue()).intValue();
			}
		}catch(EmptyResultDataAccessException e){
			
		}
		
		return rowCount;
	}
	
	public long count(Object po) throws Exception {
        SqlParamsPairs sqlAndParams = ModelSqlUtils.getCountFromObject(po);
		long rowCount = 0;
		try{
			Map<String, Object> resultMap = null;
			if (sqlAndParams.getParams() == null || sqlAndParams.getParams().length == 0) {
				resultMap = jdbcTemplate.queryForMap(sqlAndParams.getSql());
			} else {
				resultMap = jdbcTemplate.queryForMap(sqlAndParams.getSql(), sqlAndParams.getParams());
			}
			Iterator<Map.Entry<String, Object>> it = resultMap.entrySet().iterator();
			if(it.hasNext()){
				Map.Entry<String, Object> entry = it.next();
				rowCount = ((Long)entry.getValue()).intValue();
			}
		}catch(EmptyResultDataAccessException e){
			
		}
		
		
		return rowCount;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> list(String sql, Object[] params, Class<T> clazz) {
		List<T> list = null;
		if (params == null || params.length == 0) {
			list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(clazz));
		} else {
			list = jdbcTemplate.query(sql, params, new BeanPropertyRowMapper(clazz));
		}
		
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T get(Class clazz, Object id,boolean getObjectList) throws Exception {
		SqlParamsPairs sqlAndParams = ModelSqlUtils.getGetFromObject(clazz, id);
		return (T) get(sqlAndParams.getSql(), sqlAndParams.getParams(), clazz, getObjectList);
	}
	
	public <T> T get(String sql, Object[] params, Class<T> clazz,boolean getObjectList) throws Exception {
		List<T> list = list(sql, params, clazz);
		if (list.size() > 0) {
			T t = list.get(0);
			if(getObjectList){
        		setListFromObject(t);
        	}
			return t;
		} else {
			return null;
		}
	}
	
	public <T> T get(Class<T> clazz, Object id) throws Exception {
		return get(clazz, id, false);
	}
	
	public <T> T get(String sql, Object[] params, Class<T> clazz) throws Exception {
		return (T) get(sql,params, clazz, false);
	}
	
	public <T> void setListFromObject(List<T> list) throws Exception{
		for (T t : list) {
			setListFromObject(t);
		}
	}
	
	public void setListFromObject(Object po) throws Exception{
		List<Field> fieldList = ReflectUtils.getFieldList(po.getClass());
		for (Field f : fieldList) {
			Method getter = ReflectUtils.getMethod(po.getClass(), f);
			if (getter == null) {
				continue;
			}
			GetList getListAnno = getter.getAnnotation(GetList.class);
			if (getListAnno == null) {
				continue;
			}
			String referenceColumn = getListAnno.referenceColumn();
			String referenceValueName = "get" + CamelNameUtils.capitalize(referenceColumn);
			Method referenceValueMethod = ReflectUtils.getMethod(po.getClass(), referenceValueName);
			
			String sql = getListAnno.sql();
			Class<?> clazz = getListAnno.clazz();
			String setterName = "set" + CamelNameUtils.capitalize(f.getName());
			Method referenceMethod = ReflectUtils.getMethod(po.getClass(), setterName);
			
			Object invoke = referenceValueMethod.invoke(po);
			
			List<?> list = list(sql,new Object[] { invoke }, clazz);
			referenceMethod.invoke(po, list);
			logger.debug(po.getClass().getName()+"set:"+getter.getName()+" "+list);
		}
	}
}
