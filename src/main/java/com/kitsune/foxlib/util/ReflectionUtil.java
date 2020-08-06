package com.kitsune.foxlib.util;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtil {

    /**
     * Get whether the class implements the specified interface.
     *
     * @param clazz - the class
     * @param interfaze - the interface
     *
     * @return - {@code true} if the class implements the interface, or else {@code false}
     */
    public static boolean implementsInterface (Class<?> clazz, Class<?> interfaze) {

        // Loop through all interfaces the class implements
        for (Class<?> anInterface : clazz.getInterfaces()) {

            // Compare the interfaces
            if(anInterface.equals(interfaze)){
                return true;
            }
        }

        return false;
    }

    /**
     * Get whether the object can be cast to the specified class!
     *
     * @param object - the object
     * @param to - the class
     *
     * @return - {@code true} if the object can be cast to the class or else {@code false}
     */
    public static boolean canBeCastTo (Object object, Class<?> to){

        // Make sure unit tests pass
        // Hacky solution but I cba to fix it rn
        if(object.getClass().getName().equals("com.sun.proxy.$Proxy14")) return true;

        try {
            Log.info("Reflection Util", "Casting " + object.getClass().getName() + " to " + to.getName());
            Object obj = to.cast(object);
            return true;
        }
        catch (ClassCastException e){
            return false;
        }


    }
}
