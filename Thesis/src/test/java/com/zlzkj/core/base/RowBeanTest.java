package com.zlzkj.core.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.zlzkj.core.sql.RowBean;
import com.zlzkj.core.sql.Row;

public class RowBeanTest {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // 设置类成员属性
        Row row = new Row();
        
        row.put("id", "135");
        row.put("login_name", "simon");
        row.put("login_pass", "112233");

        // 获得bean的实体
        Object bean = RowBean.getBean(row);
        Method method = bean.getClass().getMethod("getId");
        System.out.println(method.invoke(bean));
        
        // 通过反射查看所有方法名
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            System.out.println(methods[i].getName());
        }
    }
}