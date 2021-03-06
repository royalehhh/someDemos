package com.etekcity.vbmp.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

/**
 * @ClassName MyBeanUtils
 * @Description bean 复制类
 * @Author Ericer
 * @Date 18-9-13 下午3:24
 **/
public class MyBeanUtils {


    /**
     * @return void
     * @Author Ericer
     * @Description 忽略大小写复制类
     * @Date 下午3:25 18-9-13
     * @Param [source, target]
     **/
    public static void copyPropertiesIgnoreCase(Object source, Object target) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);

        Class<?> sourceClass = source.getClass();
        PropertyDescriptor[] sourcePds = getPropertyDescriptors(sourceClass);
        Assert.notNull(sourcePds, "sourcePds为null");
        Map<String, String> sourceFiledNameMap = new HashMap<>();
        for (int i = 0, size = sourcePds.length; i < size; i++) {
            sourceFiledNameMap.put(sourcePds[i].getName().toLowerCase(), sourcePds[i].getName());
        }

        PropertyDescriptor[] var7 = targetPds;
        int var8 = targetPds.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            PropertyDescriptor targetPd = var7[var9];
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (sourcePds == null || sourceFiledNameMap.keySet().contains(targetPd.getName().toLowerCase()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), sourceFiledNameMap.get(targetPd.getName().toLowerCase()));
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }

                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }

                            writeMethod.invoke(target, value);
                        } catch (Throwable var15) {
                            throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", var15);
                        }
                    }
                }
            }
        }
    }

    /**
     * @return void
     * @Author Ericer
     * @Description 忽略空值的，区分大小写的复制类
     * @Date 下午3:28 18-9-13
     * @Param [source, target]
     **/
    public static void copyPropertiesIgnoreNull(Object source, Object target) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();

        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        PropertyDescriptor[] var7 = targetPds;
        int var8 = targetPds.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            PropertyDescriptor targetPd = var7[var9];
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }

                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if (value == null) {
                                continue;
                            }
                            writeMethod.invoke(target, value);
                        } catch (Throwable var15) {
                            throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", var15);
                        }
                    }
                }
            }
        }
    }
}
