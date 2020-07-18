package com.kitsune.foxlib.command.parser;

import com.kitsune.foxlib.command.ArgumentParser;

public class IntegerParser implements ArgumentParser<Integer> {

    @Override
    public Integer parse(String argument) {

        try {
            return Integer.parseInt(argument);
        }
        catch (Exception e) {
            return null;
        }
    }

}
