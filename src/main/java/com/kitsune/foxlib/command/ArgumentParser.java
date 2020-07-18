package com.kitsune.foxlib.command;

public interface ArgumentParser<T> {

    T parse(String argument);
}
