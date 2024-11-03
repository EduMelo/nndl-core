package dev.edumelo.com.nndl_core.utils;

import java.util.Optional;

public class ClassUtils {

	@SuppressWarnings("unused")
	public static Optional<Class<?>> loadClass(String className) {
	    try {
	        return Optional.of(Class.forName(className));
	    } catch (ClassNotFoundException e) {
	        throw new RuntimeException(String.format("Cannot find class by name: %s", className), e);
	    }
	}
	
}
