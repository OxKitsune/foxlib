package com.kitsune.foxlib.command;

import com.kitsune.foxlib.command.parser.DoubleParser;
import com.kitsune.foxlib.command.parser.IntegerParser;
import com.kitsune.foxlib.command.parser.MaterialParser;
import com.kitsune.foxlib.command.parser.ShortParser;
import com.kitsune.foxlib.command.parser.StringParser;
import com.kitsune.foxlib.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class FoxCommandAPI {

    /**
     * The singleton instance
     */
    private static FoxCommandAPI instance;

    /**
     * The command map
     */
    private final SimpleCommandMap commandMap;

    /**
     * The registered commands
     */
    private final Map<Class<?>, ArgumentParser<?>> argumentParsers;

    /**
     * The command tree
     */
    private final FoxCommandTree commandTree;

    /**
     * The command executor
     */
    private final FoxCommandExecutor commandExecutor;

    /**
     * Whether to inject the commands into the command map, this is used to run unit tests
     */
    private boolean injectCommands = true;

    /**
     * Private constructor because singletons don't need a public one
     */
    private FoxCommandAPI(SimpleCommandMap commandMap, boolean injectCommands) {
        this.argumentParsers = new HashMap<>();
        this.commandTree = new FoxCommandTree();
        this.commandExecutor = new FoxCommandExecutor(this);
        this.commandMap = commandMap;
        this.injectCommands = injectCommands;
    }

    /**
     * Initialise the {@link FoxCommandAPI}.
     *
     * @param injectCommands - whether to inject commands into the command map
     */
    public static void init(boolean injectCommands) {

        // Make sure the command container hasn't been initialised yet!
        if (instance != null && injectCommands) throw new IllegalStateException("CommandContainer has already been initialised!");

        // Return if inject commands is false and the instance already exists
        if(instance != null) {
            Log.info("FoxCommandAPI", "Instance isn't null!");
            return;
        }

        try {


            if(injectCommands){
                // Get commandmap field and make sure it's accessible
                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);

                // Initialise instance
                instance = new FoxCommandAPI((SimpleCommandMap) commandMapField.get(Bukkit.getPluginManager()), true);
            }
            else {

                // Initialise without the command map, as we're not using it
                instance = new FoxCommandAPI(null, false);
            }


            // Register base argument parsers
            instance.registerArgumentParser(String.class, new StringParser());
            instance.registerArgumentParser(Material.class, new MaterialParser());

            DoubleParser doubleParser = new DoubleParser();
            instance.registerArgumentParser(double.class, doubleParser);
            instance.registerArgumentParser(Double.class, doubleParser);

            IntegerParser integerParser = new IntegerParser();
            instance.registerArgumentParser(int.class, integerParser);
            instance.registerArgumentParser(Integer.class, integerParser);

            ShortParser shortParser = new ShortParser();
            instance.registerArgumentParser(short.class, shortParser);
            instance.registerArgumentParser(Short.class, shortParser);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            Log.error("Fox Command API", "Failed to get CommandMap!");
            e.printStackTrace();
        }
    }

    /**
     * Register all the commands in a class
     *
     * @param plugin - the plugin that's registering the command
     * @param instance - the class to register the commands from
     */
    public boolean registerCommandsFromClass(JavaPlugin plugin, Object instance) {

        for (Method method : instance.getClass().getDeclaredMethods()) {

            // Get the fox command annotation
            FoxCommand foxCommand = method.getAnnotation(FoxCommand.class);

            // Make sure a fox command is present
            if (foxCommand == null) continue;

            // Make sure method has a command executor as first param
            if (method.getParameters().length < 1 || !CommandSender.class.isAssignableFrom(method.getParameters()[0].getType())) {
                Log.error("Fox Command API", instance.getClass().getName() + "#" + method.getName() + " doesn't have a valid first param. (Make sure it implements CommandSender!)");
                return false;
            }

            for (Parameter parameter : method.getParameters()) {

                // Ignore first parameter because that's the command sender
                if(parameter.equals(method.getParameters()[0])) continue;

                if(!hasArgumentParser(parameter.getType())){
                    Log.warn("Fox Command API", "Argument Parser for type \"" + parameter.getType() + "\" required by " + instance.getClass().getName() + "#" + method.getName() + " hasn't been registered!");
                    return false;
                }

            }
            
            RegisteredCommand commandToRegister = new RegisteredCommand(foxCommand, method, instance);

            // Add to command tree
            commandTree.addFoxCommand(commandToRegister);

            // Inject command into command map
            if(injectCommands) {
                injectCommand(plugin, foxCommand);
            }

            // Log update
            Log.info("Fox Command API", "Registered \"/" + foxCommand.path() + "\" to " + commandToRegister.toString());
        }

        return true;
    }

    /**
     * Register an argument parser for the specified type.
     *
     * @param type - the type
     * @param argumentParser - the argument parser
     *
     * @return - {@code true} if the argument parser has been registered successfully or else {@code false}
     */
    public boolean registerArgumentParser (Class<?> type, ArgumentParser<?> argumentParser){

        // Make sure argument parser hasn't been registered yet
        if(argumentParsers.containsKey(type)){
            Log.warn("Fox Command API", "Argument Parser for type \"" + type.getName() + "\" is already registered to " + argumentParsers.get(type).getClass().getName());
            return false;
        }

        // Register the type
        Log.info("Fox Command API", "Registered Argument Parser for type " + type.getName() + " to " + argumentParser.getClass().getName());
        return argumentParsers.put(type, argumentParser) == null;
    }

    /**
     * Get whether an {@link ArgumentParser} has been registered for the specified type.
     *
     * @param type - the type
     *
     * @return - {@code true} if an {@link ArgumentParser} has been registered for the specified type or else {@code false}
     */
    public boolean hasArgumentParser (Class<?> type){
        return argumentParsers.containsKey(type);
    }

    /**
     * Get the {@link ArgumentParser} for the specified type.
     *
     * @param type - the type
     *
     * @return - the argument parser
     */
    public ArgumentParser<?> getArgumentParser (@NotNull Class<?> type){
        if(!hasArgumentParser(type)) throw new IllegalArgumentException("Argument Parser for type " + type.getName() + " isn't registered!");

        return argumentParsers.get(type);
    }

    /**
     * Inject the {@link FoxCommand} into the command map.
     *
     * @param plugin - the {@link JavaPlugin} that's registering the command
     * @param foxCommand - the fox command that's being injected
     */
    private void injectCommand(@NotNull JavaPlugin plugin, @NotNull FoxCommand foxCommand) {

        // Create the command label using the fox command
        String commandLabel = foxCommand.path().split(" ")[0];

        PluginCommand pluginCommand = plugin.getCommand(commandLabel);

        // Make sure command isn't registered already
        if (pluginCommand != null) {
            return;
        }

        // Add the command to the command map
        try {

            // Get the plugin command constructor and make sure it's accessible
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);

            // Create new plugin command instance
            PluginCommand command = constructor.newInstance(commandLabel, plugin);

            // Set the command's properties
            command.setDescription(foxCommand.description());
            command.setUsage(foxCommand.usage());
            command.setExecutor(commandExecutor);

            // Register the command to the command map
            commandMap.register(plugin.getName(), command);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {

            // Log error
            Log.error("Fox Command API", "Failed to inject command " + foxCommand.path() + " into command map!");
            e.printStackTrace();
        }
    }

    /**
     * Get the {@link FoxCommandTree}
     *
     * @return - the fox command tree
     */
    public FoxCommandTree getCommandTree () {
        return commandTree;
    }

    public static FoxCommandAPI getInstance() {
        return instance;
    }
}
