package org.iac2.common.utility;

import java.util.Collection;
import java.util.Set;

import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.support.ModelEntity;
import io.github.edmm.model.support.TypeResolver;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.Java11;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.Nginx;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.RealWorldAngularApp;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.RealWorldApplicationBackendJava11Spring;
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

    /***
     * resolves a java basic type to an edmm property type.
     * @param clazz the class of the java type
     * @return a string that represents the edmm property type
     * @param <T> the java type
     */
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

    public static void initDefaultMappings() {
        putMapping("docker_engine", DockerEngine.class);
        putMapping("docker_container", DockerContainer.class);
        // putMapping("mysql_dbms", MySqlDbms.class);
        // putMapping("mysql_db", MySqlDb.class);
        putMapping("realworld_application_backend_java11_spring", RealWorldApplicationBackendJava11Spring.class);
        putMapping("java_11", Java11.class);
        putMapping("realworld_application_angular", RealWorldAngularApp.class);
        putMapping("nginx", Nginx.class);
    }
}
