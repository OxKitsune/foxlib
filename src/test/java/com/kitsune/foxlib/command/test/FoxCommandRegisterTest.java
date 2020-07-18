package com.kitsune.foxlib.command.test;

import com.kitsune.foxlib.command.FoxCommand;
import com.kitsune.foxlib.command.FoxCommandAPI;
import org.bukkit.entity.Player;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powermock.api.easymock.PowerMock;

import static org.easymock.EasyMock.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FoxCommandRegisterTest {

    @BeforeAll
    public static void initTestingEnvironment() {

        // Initialise the command api
        FoxCommandAPI.init(false);
    }

    @Test
    public void testCommandRegistration () {
        assertEquals(true, FoxCommandAPI.getInstance().registerCommandsFromClass(null, new FoxTestRegisterCommand()), "Failed to register test command!");
        assertEquals(false, FoxCommandAPI.getInstance().registerCommandsFromClass(null, new FoxTestFailRegisterCommand()), "Registered command that shouldn't be registered!");
    }


    public static class FoxTestFailRegisterCommand {
        @FoxCommand(path = "foxlib", aliases = "gamertime")
        public void invalidTestCommand (String test){

            System.out.println("do nothing");
        }
    }

    public static class FoxTestRegisterCommand {

        @FoxCommand(path = "foxlib", aliases = "foxlib test")
        public void commandTest (Player player, String test){
            System.out.println("test");
        }
    }

}
