package com.kitsune.foxlib.command.test;

import com.kitsune.foxlib.command.FoxCommandResult;
import com.kitsune.foxlib.command.FoxCommand;
import com.kitsune.foxlib.command.FoxCommandAPI;
import com.kitsune.foxlib.util.Log;
import org.bukkit.entity.Player;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FoxCommandExecuteTest {


    @BeforeAll
    public static void initTestingEnvironment() {

        // Initialise the command api
        FoxCommandAPI.init(false);
    }

    @Test
    public void commandExecutetest () {

        // Register command
        FoxCommandAPI.getInstance().registerCommandsFromClass(null, this);

        // Mock player
        Player player = mock(Player.class);
        EasyMock.expect(player.getName()).andReturn("OxKitsune");
        EasyMock.expect(player.getName()).andReturn("OxKitsune");
        EasyMock.expect(player.getName()).andReturn("OxKitsune");
        EasyMock.expect(player.getName()).andReturn("OxKitsune");

        // Mock permissions
        EasyMock.expect(player.hasPermission("foxlib.test.permission")).andReturn(true);
        EasyMock.expect(player.hasPermission("foxlib.test.permission2")).andReturn(false);

        // Start mock
        EasyMock.expectLastCall();
        EasyMock.replay(player);

        // Make sure these values aren't null
        assertNotNull(FoxCommandAPI.getInstance());
        assertNotNull(FoxCommandAPI.getInstance().getCommandTree());
        assertNotNull(player);

        // Perform tests
        assertEquals(FoxCommandResult.SUCCESS, FoxCommandAPI.getInstance().getCommandTree().execute(player, "foxlib", new String[]{"test"}), "Failed to execute command!");
        assertEquals(FoxCommandResult.SUCCESS, FoxCommandAPI.getInstance().getCommandTree().execute(player, "foxlib", new String[]{"perms", "test"}), "Failed to execute permission check!");

        // This should call Player#sendMessage(String)
        assertEquals(FoxCommandResult.INSUFFICIENT_PERMISSIONS, FoxCommandAPI.getInstance().getCommandTree().execute(player, "foxlib", new String[]{"perms2", "test"}), "Failed to execute permission2 check!");

        // Clean up mock
        EasyMock.expectLastCall();
        EasyMock.replay(player);
    }

    @FoxCommand(path = "foxlib")
    public void testCommand (Player player, String arg){
       Log.info("Test Command", "Player: " + player.getName() + " Result: " + arg);
    }

    @FoxCommand(path = "foxlib perms", permission = "foxlib.test.permission")
    public void permsTestCommand (Player player, String arg){
        Log.info("Test Command", "Player " + player.getName() + " has permission: foxlib.test.permission");
    }

    @FoxCommand(path = "foxlib perms2", permission = "foxlib.test.permission2")
    public void permsTest2Command (Player player, String arg){
        Log.info("Test Command", "Player " + player.getName() + " has permission: foxlib.test.permission2");
    }
}
