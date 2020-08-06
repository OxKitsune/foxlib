package com.kitsune.foxlib.command;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.kitsune.foxlib.FoxLib;
import com.kitsune.foxlib.util.Log;
import com.kitsune.foxlib.util.ReflectionUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FoxCommandTree {

    private FoxCommandNode<?> root;

    public FoxCommandTree() {
        this.root = new FoxCommandNode<>(null, null, null, null);
    }

    public void addFoxCommand(RegisteredCommand registeredCommand) {

        // Get the fox command
        FoxCommand foxCommand = registeredCommand.getFoxCommand();

        // Determine paths and add them to the tree
        List<String[]> requiredPaths = new ArrayList<>(Collections.singleton(foxCommand.path().split(" ")));

        // Add aliases to required args list
        for (String alias : foxCommand.aliases()) {
            requiredPaths.add(alias.split(" "));
        }

        // Add all the paths to the command tree
        for (String[] requiredPath : requiredPaths) {

            // Store previous parent
            FoxCommandNode<?> previousParent = null;

            // Loop through all sub arguments in command
            for (int j = 0; j < requiredPath.length; j++) {

                String arg = requiredPath[j];
                FoxCommandNode<?> parent = (j == 0) ? root : previousParent;

                // Check whether the node already exists, if so set the previous parent to it and return
                FoxCommandNode<?> childNode = parent.getChild(arg).orElse(null);
                if (childNode != null) {

                    // Set the previous parent
                    previousParent = childNode;
                } else {

                    // Set the previous parent
                    previousParent = parent.addChild(new FoxCommandNode<>(parent, null, arg, null));
                }


                // If this is the last node in the path, add the required parameters
                if (j == requiredPath.length - 1) {

                    // Add the parameters to the command tree
                    for (Parameter parameter : registeredCommand.getMethod().getParameters()) {

                        // Skip the first parameter, since this is the command sender
                        if (parameter.equals(registeredCommand.getMethod().getParameters()[0])) continue;

                        // Make sure the type isn't already registered
                        childNode = previousParent.getChild(parameter.getType()).orElse(null);

                        if (childNode != null) {
                            previousParent = childNode;
                            continue;
                        }

                        previousParent = previousParent.addChild(FoxCommandNode.newInstance(previousParent, null, null, parameter.getType()));
                    }

                    // Set the registered command
                    previousParent.setRegisteredCommand(registeredCommand);
                }
            }
        }
    }


    public FoxCommandResult execute(CommandSender commandSender, String name, String[] args) {

        // Get the command node
        FoxCommandNode<?> commandNode = root.getChild(name).orElse(null);

        // Make sure the command node isn't null
        if (commandNode == null) return FoxCommandResult.INVALID_COMMAND;

        // Match the arguments
        if (commandNode.getRegisteredCommand() != null && args.length == 0) {
            try {

                String permission = commandNode.getRegisteredCommand().getFoxCommand().permission();

                // Add op permission check
                if ((permission.equalsIgnoreCase("op") && commandSender.isOp()) || permission.equalsIgnoreCase("") || commandSender.hasPermission(permission)) {
                    commandNode.getRegisteredCommand().getMethod().invoke(commandNode.getRegisteredCommand().getInstance(), commandSender);
                    return FoxCommandResult.SUCCESS;
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandNode.getRegisteredCommand().getFoxCommand().noPermissionsMessage()));
                    return FoxCommandResult.INSUFFICIENT_PERMISSIONS;
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {

            Log.info("Command Tree", "Traversing tree...");
            Log.info("Command Tree", "Command: /" + commandNode.getRequiredArg() + " " + String.join(" ", args));
            List<Object> parsedArgs = new ArrayList<>();
            parsedArgs.add(commandSender);

            return traverseTree(commandSender, commandNode, Lists.newLinkedList(Arrays.asList(args)), parsedArgs);
        }

        return FoxCommandResult.INVALID_COMMAND;
    }

    /**
     * Traverse the command tree
     */
    private FoxCommandResult traverseTree(CommandSender commandSender, FoxCommandNode<?> node, LinkedList<String> args, List<Object> parsedArgs) {

        // Check if all args have been parsed
        if (args.isEmpty()) {

            if(node.getRegisteredCommand() == null){
                return FoxCommandResult.INVALID_COMMAND;
            }

            // Execute command
            try {

                // Get the permission
                String permission = node.getRegisteredCommand().getFoxCommand().permission();

                // Add op permission check
                if ((permission.equalsIgnoreCase("op") && commandSender.isOp()) || permission.equalsIgnoreCase("") || commandSender.hasPermission(permission)) {

                    // Make sure the command can be run by the command sender's type
                    if(ReflectionUtil.canBeCastTo(commandSender, node.getRegisteredCommand().getMethod().getParameterTypes()[0])) {
                        commandSender.sendMessage(ChatColor.RED + "Command cannot be run by " + commandSender.getClass().getSimpleName());
                        return FoxCommandResult.INVALID_SENDER_TYPE;
                    }

                    // Execute the command with the parsed arguments
                    node.getRegisteredCommand().getMethod().invoke(node.getRegisteredCommand().getInstance(), parsedArgs.toArray());
                    return FoxCommandResult.SUCCESS;
                } else {

                    // Send the no permission message to the player
                    if(!node.getRegisteredCommand().getFoxCommand().noPermissionsMessage().equals("")) commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', node.getRegisteredCommand().getFoxCommand().noPermissionsMessage()));
                    return FoxCommandResult.INSUFFICIENT_PERMISSIONS;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // Get the arg that needs to be parsed
        String arg = args.getFirst();

        // Loop through all the children
        for (FoxCommandNode<?> child : node.getChildren()) {

            // Create copy of these objects to modify them further down the recursion
            List<Object> localParsedArgs = new ArrayList<>(parsedArgs);
            LinkedList<String> localArgs = new LinkedList<>(args);

            // Try to match the required arg first
            // This is a constant string so it's got a higher priority than
            // the parsed arguments
            if (child.getRequiredArg() != null && child.getRequiredArg().equalsIgnoreCase(arg)) {

                Log.info("Command Tree", "Matches required argument: " + child.getRequiredArg());

                // Argument matches, use the child to continue in the tree
                localArgs.remove();

                FoxCommandResult result = traverseTree(commandSender, child, localArgs, localParsedArgs);
                if(result == FoxCommandResult.INVALID_COMMAND) continue;
                return result;
            }

            // Try to parse the argument
            if (child.getRequiredClass() == null) continue;
            Object parsedArgument = FoxCommandAPI.getInstance().getArgumentParser(child.getRequiredClass()).parse(arg);

            // If the argument doesn't match, continue
            if (parsedArgument == null) continue;

            // Argument matches, add the parsed argument to the list and continue using the child
            localArgs.remove();
            localParsedArgs.add(parsedArgument);
            FoxCommandResult result = traverseTree(commandSender, child, localArgs, localParsedArgs);
            if(result == FoxCommandResult.INVALID_COMMAND) continue;
            return result;
        }

        // Return invalid command
        return FoxCommandResult.INVALID_COMMAND;
    }


    public FoxCommandNode<?> getRoot() {
        return root;
    }
}
