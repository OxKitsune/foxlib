package com.kitsune.foxlib.command.parser;

import com.kitsune.foxlib.command.ArgumentParser;
import org.bukkit.Material;

public class MaterialParser implements ArgumentParser<Material> {

    @Override
    public Material parse(String argument) {

        try {
            return Material.valueOf(argument);
        }
        catch (Exception e){
            return null;
        }
    }
}
