package com.zenyte.plugins;

/**
 * @author Jire
 */
public interface Plugin {

    default Class<?> annotationType() {
        return null;
    }

}
