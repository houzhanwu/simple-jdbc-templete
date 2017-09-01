package com.sf.simple.jdbc.templete;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.sf.simple.jdbc.templete.entity.User;
import com.sf.simple.jdbc.templete.utils.ReflectUtils;

public class ReflectTest{
	
	public static void main(String[] args) {
		List<Field> fieldListWithClass = ReflectUtils.getFieldList(User.class);
		for (Field f : fieldListWithClass) {
			System.out.println(f.getName());
			Method method = ReflectUtils.getMethod(User.class, f);
			Annotation[] annotations = method.getDeclaredAnnotations();
			if(annotations==null){
				continue;
			}
			for (int i = 0; i < annotations.length; i++) {
				Annotation annotation = annotations[i];
				System.out.println(annotation.annotationType());
			}
		}
	}
}
