package com.kitsune.foxlib.command.parser;

import com.kitsune.foxlib.command.ArgumentParser;
import com.kitsune.foxlib.util.Log;

public class DoubleParser implements ArgumentParser<Double> {

    @Override
    public Double parse(String argument) {

        try {
            return Double.parseDouble(argument);
        }
        catch (Exception e) {
            return null;
        }
    }
}
