package com.github.kerraway.ddfc.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@Slf4j
public class BeanUtil {

  private static final Map<Class<?>, String[]> PROPERTY_NAMES_CACHE = new ConcurrentReferenceHashMap<>(256);

  public static void copyProperties(Object source, Object target) {
    if (source == null || target == null) {
      return;
    }
    BeanUtils.copyProperties(source, target);
  }

  public static void copyProperties(Object source, Object target, String... ignoreProperties) {
    if (source == null || target == null) {
      return;
    }
    BeanUtils.copyProperties(source, target, ignoreProperties);
  }

  public static void copyPropertiesIgnoreNull(Object source, Object target) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  /**
   * If specific property of target is null, copy
   * the property from source into target.
   *
   * @param source
   * @param target
   */
  public static void copyTargetAbsentProperties(Object source, Object target) {
    if (source == null || target == null) {
      return;
    }
    BeanUtils.copyProperties(source, target, getNotNullPropertyNames(target));
  }

  /**
   * If specific property of target is null and not in ignore properties,
   * copy the property from source into target.
   *
   * @param source
   * @param target
   * @param ignoreProperties
   */
  public static void copyTargetAbsentProperties(Object source, Object target, String... ignoreProperties) {
    if (source == null || target == null) {
      return;
    }
    String[] allIgnoreProperties = Stream.of(getNotNullPropertyNames(target), ignoreProperties)
        .flatMap(Stream::of)
        .distinct()
        .toArray(String[]::new);
    BeanUtils.copyProperties(source, target, allIgnoreProperties);
  }

  /**
   * If specific property of target is null and in allowed properties,
   * copy the property from source into target.
   *
   * @param source
   * @param target
   * @param allowedProperties
   */
  public static void copyTargetAbsentAndAllowedProperties(Object source, Object target, String... allowedProperties) {
    if (source == null || target == null) {
      return;
    }
    String[] allIgnoreProperties = Arrays.stream(getNotNullPropertyNames(target))
        .filter(property -> ArrayUtils.contains(allowedProperties, property))
        .toArray(String[]::new);
    BeanUtils.copyProperties(source, target, allIgnoreProperties);
  }

  /**
   * Clone bean.
   *
   * @param bean
   * @param <T>
   * @return T
   */
  public static <T> T clone(final T bean) {
    try {
      if (bean != null) {
        return (T) org.apache.commons.beanutils.BeanUtils.cloneBean(bean);
      }
    } catch (Exception e) {
      logger.error("Clone bean error.", e);
    }
    return null;
  }

  /**
   * Get property from bean.
   *
   * @param bean      should not be {@literal null}.
   * @param fieldName should not be {@literal null}.
   * @param fieldType should not be {@literal null}.
   * @param <T>
   * @return T property
   */
  public static <T> T getProperty(final Object bean, final String fieldName, Class<T> fieldType) {
    if (!hasProperty(bean, fieldName)) {
      return null;
    }
    try {
      Object value = PropertyUtils.getProperty(bean, fieldName);
      return fieldType.isInstance(value) ? fieldType.cast(value) : null;
    } catch (Exception e) {
      logger.error("Get property error.", e);
    }
    return null;
  }

  /**
   * Get property from bean.
   *
   * @param bean      should not be {@literal null}.
   * @param fieldName should not be {@literal null}.
   * @return String property
   */
  public static String getProperty(final Object bean, final String fieldName) {
    return getProperty(bean, fieldName, String.class);
  }

  /**
   * Set property of bean.
   *
   * @param bean      should not be {@literal null}.
   * @param fieldName should not be {@literal null}.
   * @param value     may be {@literal null}.
   */
  public static void setProperty(final Object bean, final String fieldName, final Object value) {
    if (!hasProperty(bean, fieldName)) {
      return;
    }
    try {
      PropertyUtils.setProperty(bean, fieldName, value);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      logger.error("Get property error.", e);
    }
  }

  /**
   * If bean class has property, returns true.
   *
   * @param bean
   * @param fieldName
   * @return boolean
   */
  public static boolean hasProperty(final Object bean, final String fieldName) {
    if (bean == null || fieldName == null) {
      return false;
    }
    Field field = ReflectionUtils.findField(bean.getClass(), fieldName);
    return field != null
        //Filter out the constant
        && !(Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()));
  }

  /**
   * If bean has nested property, return true.
   *
   * @param bean
   * @param nestedProperty
   * @return boolean
   */
  public static boolean hasNestedProperty(final Object bean, final String nestedProperty) {
    if (bean == null || nestedProperty == null) {
      return false;
    }
    try {
      return PropertyUtils.getNestedProperty(bean, nestedProperty) != null;
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      logger.warn("{} don't has {}.", JacksonUtil.writeValue(bean), nestedProperty);
    }
    return false;
  }

  private static String[] getAllPropertyNames(Object obj) {
    if (obj == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    if (PROPERTY_NAMES_CACHE.containsKey(obj.getClass())) {
      return PROPERTY_NAMES_CACHE.get(obj.getClass());
    }
    String[] allPropertyNames = Arrays.stream(PropertyUtils.getPropertyDescriptors(obj))
        .map(FeatureDescriptor::getName)
        .toArray(String[]::new);
    PROPERTY_NAMES_CACHE.put(obj.getClass(), allPropertyNames);
    return allPropertyNames;
  }

  private static String[] getNullPropertyNames(Object obj) {
    if (obj == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return Arrays.stream(getAllPropertyNames(obj))
        .filter(propertyName -> BeanUtil.getProperty(obj, propertyName, Object.class) == null)
        .toArray(String[]::new);
  }

  private static String[] getNotNullPropertyNames(Object obj) {
    if (obj == null) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return Arrays.stream(getAllPropertyNames(obj))
        .filter(propertyName -> BeanUtil.getProperty(obj, propertyName, Object.class) != null)
        .toArray(String[]::new);
  }

}
