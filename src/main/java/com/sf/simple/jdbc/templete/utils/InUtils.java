package com.sf.simple.jdbc.templete.utils;

import java.util.List;

/**
 * Used to deal with "IN" in sql.
 *
 */
public class InUtils {
    
    public static String getStr4SQLINParam(String[] values){

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if(i <  (values.length -1)){
                sb.append("'" + values[i] + "', ");
            }else{
                sb.append("'" + values[i] + "'");
            }
                
        }
        return sb.toString();
    }
    
    public static <T> String getStr4SQLINParam(List<T> values){

        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < values.size(); i++) {
            if(i <  (values.size() -1)){
                sb.append("'" + values.get(i) + "', ");
            }else{
                sb.append("'" + values.get(i) + "'");
            }
        }

        return sb.toString();
    }
}
