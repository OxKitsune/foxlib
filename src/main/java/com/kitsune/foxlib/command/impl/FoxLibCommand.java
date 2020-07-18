package com.kitsune.foxlib.command.impl;

import com.kitsune.foxlib.FoxLib;
import com.kitsune.foxlib.command.FoxCommand;
import com.kitsune.foxlib.command.FoxCommandAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class FoxLibCommand {

    public FoxLibCommand() {

        // Register the command
        FoxCommandAPI.getInstance().registerCommandsFromClass(FoxLib.getInstance(), this);
    }

    @FoxCommand(path = "foxlib info", aliases = {"fl info", "foxlib"}, description = "Base command for the FoxLib plugin", usage = "/foxlib", permission = "op")
    public void foxlibCommand(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.GOLD + "FoxLib " + ChatColor.GRAY + "v" + FoxLib.getInstance().getDescription().getVersion());
    }


    @FoxCommand(path = "foxlib info", aliases = {"fl info", "foxlib"}, description = "Base command for the FoxLib plugin", usage = "/foxlib", permission = "op")
    public void foxlibCommand2(CommandSender commandSender, double gamer) {
        commandSender.sendMessage(ChatColor.GOLD + "FoxLib " + ChatColor.GRAY + "v" + FoxLib.getInstance().getDescription().getVersion() + " res: " + (gamer-2));
    }
}
