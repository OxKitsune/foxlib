package com.kitsune.foxlib.util;

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


            Log.info("Reflection Util", "interface: " + anInterface.getName() + " required: " +interfaze.getName());

            // Compare the interfaces
            if(anInterface.equals(interfaze)){
                return true;
            }
        }

        return false;
    }

}
