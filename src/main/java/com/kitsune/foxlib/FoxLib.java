package com.kitsune.foxlib;

import com.kitsune.foxlib.command.FoxCommandAPI;
import com.kitsune.foxlib.command.impl.FoxLibCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoxLib extends JavaPlugin {

    /** FoxLib instance */
    private static FoxLib instance;

    @Override
    public void onEnable() {

        // Set instance
        instance = this;

        // Initialise command container
        FoxCommandAPI.init(true);

        // Register commands
        new FoxLibCommand();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Get the {@link FoxLib} Plugin instance.
     *
     * @return - the foxlib plugin instance
     */
    public static FoxLib getInstance() {
        return instance;
    }
}
