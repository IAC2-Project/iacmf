package org.iac2.common.utility;

import java.util.Set;

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

    public static Set<String> typeSet() {
        return TypeResolver.typeSet();
    }


    public static void putMapping(String name, Class<? extends ModelEntity> clazz) {
        TypeResolver.put(name, clazz);
    }
}
