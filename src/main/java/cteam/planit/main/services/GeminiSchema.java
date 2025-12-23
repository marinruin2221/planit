package cteam.planit.main.services;

import java.util.*;
import java.lang.reflect.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class GeminiSchema {

  public static Map<String, Object> generateSchema(Class<?> clazz) {
    return generateSchemaInternal(clazz, new HashSet<>());
  }

  private static Map<String, Object> generateSchemaInternal(Type type, Set<Type> visited) {
    if (visited.contains(type)) {
      return Map.of("type", "OBJECT");
    }
    visited.add(type);

    if (type instanceof Class<?>) {
      Class<?> clazz = (Class<?>) type;

      if (isBasicType(clazz)) {
        return Map.of("type", toSchemaType(clazz));
      }

      if (clazz.isArray()) {
        Type componentType = clazz.getComponentType();
        return Map.of(
            "type", "ARRAY",
            "items", generateSchemaInternal(componentType, visited));
      }

      if (Collection.class.isAssignableFrom(clazz)) {
        return Map.of(
            "type", "ARRAY",
            "items", Map.of("type", "OBJECT")
        );
      }

      if (Map.class.isAssignableFrom(clazz)) {
        return Map.of(
            "type", "OBJECT");
      }

      return createObjectSchema(clazz, visited);
    }

    if (type instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) type;
      Type raw = pt.getRawType();

      if (raw instanceof Class<?> && Collection.class.isAssignableFrom((Class<?>) raw)) {
        Type itemType = pt.getActualTypeArguments()[0];
        return Map.of(
            "type", "ARRAY",
            "items", generateSchemaInternal(itemType, visited));
      }

      if (raw instanceof Class<?> && Map.class.isAssignableFrom((Class<?>) raw)) {
        Type valueType = pt.getActualTypeArguments()[1];
        return Map.of(
            "type", "OBJECT",
            "additionalProperties", generateSchemaInternal(valueType, visited));
      }

      return createObjectSchema((Class<?>) raw, visited);
    }

    return Map.of("type", "STRING");
  }

  private static Map<String, Object> createObjectSchema(Class<?> clazz, Set<Type> visited) {
    Map<String, Object> properties = new LinkedHashMap<>();
    List<String> ordering = new ArrayList<>();

    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);

      Type fieldType = field.getGenericType();
      Map<String, Object> fieldSchema = generateSchemaInternal(fieldType, visited);

      properties.put(field.getName(), fieldSchema);
      ordering.add(field.getName());
    }

    return Map.of(
        "type", "OBJECT",
        "properties", properties,
        "propertyOrdering", ordering);
  }

  private static boolean isBasicType(Class<?> type) {
    return type.isPrimitive()
        || type == String.class
        || Number.class.isAssignableFrom(type)
        || type == Boolean.class
        || type == Character.class
        || Date.class.isAssignableFrom(type)
        || type.getName().startsWith("java.time");
  }

  private static String toSchemaType(Class<?> type) {
    if (type == String.class)
      return "STRING";
    if (Number.class.isAssignableFrom(type)
        || (type.isPrimitive() && type != boolean.class && type != char.class))
      return "NUMBER";
    if (type == boolean.class || type == Boolean.class)
      return "BOOLEAN";
    return "STRING";
  }
}

