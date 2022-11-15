package org.iac2.common.utility;

import java.util.HashSet;
import java.util.Set;

import io.github.edmm.model.support.ModelEntity;
import io.github.edmm.model.support.TypeResolver;
import io.reactivex.annotations.Nullable;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdmmTypeResolver {


    private static final Logger logger = LoggerFactory.getLogger(EdmmTypeResolver.class);

    private static final BidiMap<String, Class<? extends ModelEntity>> TYPE_MAPPING = new DualHashBidiMap<>();


    @Nullable
    public static Class<? extends ModelEntity> resolve(String type) {
        Class<? extends ModelEntity> clazz = TypeResolver.resolve(type);

        // this weird check is supposed to ensure that the type resolver for default types did actually find a mapping
        // for this type and not just returned a default value.
        if (!type.equals(TypeResolver.resolve(clazz))) {
            clazz = TYPE_MAPPING.get(type);
        }

        if (clazz == null) {
            final String error = String.format("Type '%s' is unknown and not supported", type);
            logger.error(error);
        }

        return clazz;
    }

    @Nullable
    public static String resolve(Class<? extends ModelEntity> clazz) {
        String type = TypeResolver.resolve(clazz);

        // this weird check is supposed to ensure that the type resolver for default types did actually find a mapping
        // for this type and not just returned a default value.
        if(!clazz.equals(TypeResolver.resolve(type))) {
            type = TYPE_MAPPING.getKey(clazz);
        }

        if (type == null) {
            final String error = String.format("Type '%s' is unknown and not supported", clazz);
            logger.error(error);
        }

        return type;
    }

    public static Set<String> typeSet() {
        Set<String> set = new HashSet<>(TYPE_MAPPING.keySet());
        set.addAll(TypeResolver.typeSet());
        return set;
    }


    public static void putMapping(String name, Class<? extends ModelEntity> clazz) {
        if (resolve(name) != null) {
            throw new IllegalArgumentException("The type '" + name + "' is already mapped!");
        }

        if (resolve(clazz) != null) {
            throw new IllegalArgumentException("The class '" + clazz + "' is already mapped!");
        }

        TYPE_MAPPING.put(name, clazz);
    }
}
