package com.iceteasoftware.user.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ReflectionUtil {

    private static final String[] FIELD_NAME_SKIP = {"serialVersionUID"};

    public static boolean hasProperty(Class<?> c, String name) {
        List<Field> fields = FieldUtils.getAllFieldsList(c);

        return fields.stream().anyMatch(f -> Validator.equals(f.getName(), name));
    }

    public static Object getFieldValue(Object ob, String name) {
        try {
            Field field = ob.getClass().getDeclaredField(name);

            field.setAccessible(true);

            return field.get(ob);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            log.error("Cannot get field value {} ", name);
        }

        return null;
    }

    public static <T extends Serializable> T clone(final T entity) {
        return (T) SerializationUtils.clone(entity);
    }

    public static boolean isNullInputObject(Object object) {
        Class<?> clazz = object.getClass();

        try {
            List<Field> allFields = FieldUtils.getAllFieldsList(clazz);

            for (Field field : allFields) {
                // set accessible by file private to get value
                field.setAccessible(true);

                // skip if type is static final
                String fieldName = field.getName();

                if (Arrays.stream(FIELD_NAME_SKIP).anyMatch(fieldName::equals)) {
                    continue;
                }

                Object valueOfAttribute = field.get(object);

                if (valueOfAttribute != null) {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Error get field: ", e);
        }

        return true;
    }

}
