package com.kitsune.foxlib.command.parser;

import com.kitsune.foxlib.command.ArgumentParser;

public class ShortParser  implements ArgumentParser<Short> {

    @Override
    public Short parse(String argument) {

        try {
            return Short.parseShort(argument);
        }
        catch (Exception e) {
            return null;
        }
    }

}
