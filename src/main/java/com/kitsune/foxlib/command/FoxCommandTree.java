package com.kitsune.foxlib.command;

import com.kitsune.foxlib.util.Log;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FoxCommandTree {

    private FoxCommandNode<?> root;

    public FoxCommandTree() {
        this.root = new FoxCommandNode<>(null, null, null, null);
    }

    public void addFoxCommand (RegisteredCommand registeredCommand) {

        // Get the fox command
        FoxCommand foxCommand = registeredCommand.getFoxCommand();

        // Determine paths and add them to the tree
        List<String[]> requiredPaths = new ArrayList<>(Collections.singleton(foxCommand.path().split(" ")));

        // Add aliases to required args list
        for (String alias : foxCommand.aliases()) {
            requiredPaths.add(alias.split(" "));
        }

        // Add all the paths to the command tree
        for(int i = 0 ; i < requiredPaths.size(); i++) {

            // Store previous parent
            FoxCommandNode<?> previousParent = null;

            // Loop through all sub arguments in command
            for(int j = 0; j < requiredPaths.get(i).length; j++) {

                String arg = requiredPaths.get(i)[j];
                FoxCommandNode<?> parent = (j == 0) ? root : previousParent;

                // Check whether the node already exists, if so set the previous parent to it and return
                FoxCommandNode<?> childNode = parent.getChild(arg).orElse(null);
                if(childNode != null){

                    // Set the previous parent
                    previousParent = childNode;
                }
                else {

                    // Set the previous parent
                    previousParent = parent.addChild(new FoxCommandNode<>(parent, null, arg, null));
                }


                // If this is the last node in the path, add the required parameters
                if(j == requiredPaths.get(i).length-1){

                    // Add the parameters to the command tree
                    for (Parameter parameter : registeredCommand.getMethod().getParameters()) {

                        // Skip the first parameter, since this is the command sender
                        if(parameter.equals(registeredCommand.getMethod().getParameters()[0])) continue;

                        // Make sure the type isn't already registered
                        childNode = previousParent.getChild(parameter.getType()).orElse(null);

                        if(childNode != null){
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

        // Debug
        root.print("");
    }


    public FoxCommandResult execute(CommandSender commandSender, String name, String[] args) {

        // Get the command node
        FoxCommandNode<?> commandNode = root.getChild(name).orElse(null);

        // Make sure the command node isn't null
        if(commandNode == null) return FoxCommandResult.INVALID_COMMAND;

        // Match the arguments
        if(commandNode.getRegisteredCommand() != null && args.length == 0) {
            try {

                String permission = commandNode.getRegisteredCommand().getFoxCommand().permission();

                // Add op permission check
                if((permission.equalsIgnoreCase("op") && commandSender.isOp()) || permission.equalsIgnoreCase("") || commandSender.hasPermission(permission)){
                    commandNode.getRegisteredCommand().getMethod().invoke(commandNode.getRegisteredCommand().getInstance(), commandSender);
                    return FoxCommandResult.SUCCESS;
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', commandNode.getRegisteredCommand().getFoxCommand().noPermissionsMessage()));
                    return FoxCommandResult.INSUFFICIENT_PERMISSIONS;
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        else {

            Log.info("Fox Command Tree", "Traversing tree...");
            Log.info("Fox Command Tree", "Command: /" + commandNode.getRequiredArg() + " " + String.join(" ", args));
            List<Object> parsedArgs = new ArrayList<>();
            parsedArgs.add(commandSender);

            return traverseTree(commandSender, commandNode, new LinkedList<>(), parsedArgs, args, 0);
        }

        return FoxCommandResult.INVALID_COMMAND;
    }

    public FoxCommandResult traverseTree (CommandSender commandSender, FoxCommandNode<?> parent, Queue<FoxCommandNode<?>> visitedNodes, List<Object> parsedArgs, String[] args, int index) {

        Log.info("Command Tree", "parent: " + parent.toString() + " index: " + index + " args: " + args.length);

        // If the parent doesn't have children return it
        if(parent.getChildren().size() == 0 && index == args.length) {
            try {

                Log.info("Command Tree", "Using " + parent.getRegisteredCommand().toString());

                String permission = parent.getRegisteredCommand().getFoxCommand().permission();

                Log.info("Command Tree", "Permission: " + permission);

                // Add op permission check
                if((permission.equalsIgnoreCase("op") && commandSender.isOp()) || permission.equalsIgnoreCase("") || commandSender.hasPermission(permission)){
                    parent.getRegisteredCommand().getMethod().invoke(parent.getRegisteredCommand().getInstance(), parsedArgs.toArray());
                    return FoxCommandResult.SUCCESS;
                }
                else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', parent.getRegisteredCommand().getFoxCommand().noPermissionsMessage()));
                    return FoxCommandResult.INSUFFICIENT_PERMISSIONS;
                }


            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return FoxCommandResult.INVALID_COMMAND;

            }
        }

        for(FoxCommandNode<?> child : parent.getChildren()){

            // Skip this child if it's already been visited
            if(visitedNodes.contains(child)) continue;

            // Check if the required arg is met
            if(child.getRequiredArg() != null && args[index].equalsIgnoreCase(child.getRequiredArg())){

                Log.info("Command Tree", "child: " + child.getRequiredArg());
                visitedNodes.add(child);
                index++;
                return traverseTree(commandSender, child, visitedNodes, parsedArgs, args, index);
            }


            if(child.getRequiredClass() != null){

                Object parsedArg = FoxCommandAPI.getInstance().getArgumentParser(child.getRequiredClass()).parse(args[index]);

                if(parsedArg != null){
                    visitedNodes.add(child);
                    parsedArgs.add(parsedArg);
                    index++;
                    return traverseTree(commandSender, child, visitedNodes, parsedArgs, args, index);
                }
            }
        }

        // Make sure we don't visit this node again
        visitedNodes.add(parent);

        Log.info("Command Tree", "Couldn't find matching arg: " + index + ": " + (args.length > index ? args[index] : "INVALID INDEX"));
        return traverseTree(commandSender, parent.getParent(), visitedNodes, parsedArgs, args, index-1);
    }

    public FoxCommandNode<?> getRoot() {
        return root;
    }
}
