package com.kitsune.foxlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FoxCommandExecutor implements CommandExecutor {

    /** Reference to the fox command api */
    private FoxCommandAPI foxCommandAPI;

    /**
     * Construct a new {@link FoxCommandAPI}.
     *
     * @param foxCommandAPI - the {@link FoxCommandAPI} instance
     */
    public FoxCommandExecutor(FoxCommandAPI foxCommandAPI) {
        this.foxCommandAPI = foxCommandAPI;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        FoxCommandTree commandTree = foxCommandAPI.getCommandTree();

        // Execute the command
        return commandTree.execute(sender, command.getName(), args) != FoxCommandResult.INVALID_COMMAND;
    }
}
