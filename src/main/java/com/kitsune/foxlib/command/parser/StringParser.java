package com.kitsune.foxlib.command.parser;

import com.kitsune.foxlib.command.ArgumentParser;

public class StringParser implements ArgumentParser<String> {

    @Override
    public String parse(String argument) {
        return argument;
    }
}
