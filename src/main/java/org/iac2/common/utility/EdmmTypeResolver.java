package org.iac2.common.utility;

import java.util.Collection;
import java.util.Set;

import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.support.ModelEntity;
import io.github.edmm.model.support.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdmmTypeResolver {
    private static final Logger logger = LoggerFactory.getLogger(EdmmTypeResolver.class);

    public static Class<? extends ModelEntity> resolve(String type) {
        return TypeResolver.resolve(type);
    }


    public static String resolve(Class<? extends ModelEntity> clazz) {
       return TypeResolver.resolve(clazz);
    }

    public static <T> String resolveBasicType(Class<T> clazz) {
        if(Integer.class.isAssignableFrom(clazz)) {
            return DefaultKeys.INTEGER;
        } else if (Double.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)){
            return DefaultKeys.FLOAT;
        } else if (Boolean.class.isAssignableFrom(clazz)) {
            return "boolean";
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return "list";
        } else {
            return DefaultKeys.STRING;
        }
    }

    public static Set<String> typeSet() {
        return TypeResolver.typeSet();
    }


    public static void putMapping(String name, Class<? extends ModelEntity> clazz) {
        TypeResolver.put(name, clazz);
    }


}
