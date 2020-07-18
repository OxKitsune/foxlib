package com.kitsune.foxlib.command;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RegisteredCommand {

    private final FoxCommand foxCommand;
    private final Method method;
    private final Object instance;

    public RegisteredCommand(@NotNull FoxCommand foxCommand, @NotNull Method method, @NotNull Object instance) {
        this.foxCommand = foxCommand;
        this.method = method;
        this.instance = instance;
    }

    public FoxCommand getFoxCommand() {
        return foxCommand;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return instance.getClass().getName() + "#" + method.getName() + "(" + Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(", ")) + ")";
    }
}
