package com.sf.simple.jdbc.templete.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于自动生成SQL语句
 * 标注该注解后会根据sql参数进行查询,查询条件可用reference_column参数
 * <ul>
 * <li>2017年4月20日 | 史锋 | 新增</li>
 * </ul>
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface GetList {
    
    String sql() default "";
    Class<?> clazz();
    String referenceColumn() default " id ";
}
