package com.kitsune.foxlib.command;

import com.kitsune.foxlib.util.Log;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FoxCommandNode<T> {

    @Nullable
    private String requiredArg;
    @Nullable
    private Class<T> requiredClass;

    private FoxCommandNode<?> parent;
    private List<FoxCommandNode<?>> children;

    /** The registered command */
    @Nullable
    private RegisteredCommand registeredCommand;

    /**
     * Construct a new {@link FoxCommandNode}.
     *
     * @param parent - the parent of this node
     */
    public FoxCommandNode(FoxCommandNode<?> parent, @Nullable RegisteredCommand registeredCommand, @Nullable String requiredArg, @Nullable Class<T> requiredClass) {
        this.parent = parent;
        this.children = new ArrayList<>();

        this.registeredCommand = registeredCommand;
        this.requiredArg = requiredArg;
        this.requiredClass = requiredClass;
    }

    /**
     * Construct a new {@link FoxCommandNode} of the specified type
     * @param parent - the parent of this node
     */
    public static FoxCommandNode<?> newInstance (FoxCommandNode<?> parent, @Nullable RegisteredCommand registeredCommand, @Nullable String requiredArg, @Nullable Class<?> requiredClass){
        return new FoxCommandNode<>(parent, registeredCommand, requiredArg, requiredClass);
    }

    public Optional<FoxCommandNode<?>> getChild (String requiredArg) {

        // Loop through all child nodes
        for (FoxCommandNode<?> child : children) {

            // Check whether the child node is a required arg and if it equals the specified argument
            if(child.getRequiredArg() != null && child.getRequiredArg().equals(requiredArg)){
                return Optional.of(child);
            }

        }

        return Optional.empty();
    }

    public Optional<FoxCommandNode<?>> getChild (Class<?> requiredClass) {

        // Loop through all the child nodes
        for(FoxCommandNode<?> child : children){

            // Check whether the child node is the required class
            if(child.getRequiredClass() != null && child.getRequiredClass().equals(requiredClass)){
                return Optional.of(child);
            }
        }

        return Optional.empty();
    }

    public FoxCommandNode<?> addChild (FoxCommandNode<?> node) {

        // Make sure this node doesn't contain the child
        if(children.contains(node)) {
            throw new IllegalStateException("Node already contains child " + node);
        }

        // Add the node
        children.add(node);

        return node;
    }

    public void print (String spacing) {

        Log.info("Fox Command Node", spacing + toString());

        for (FoxCommandNode<?> child : children) {
            child.print(spacing + "   ");
        }
    }

    @Override
    public String toString() {
        return ((requiredArg == null) ? (requiredClass == null ? "Root" : requiredClass.getSimpleName()) : requiredArg) + " (" + children.size() + " child nodes)";
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof FoxCommandNode)) return false;

        FoxCommandNode<?> other = (FoxCommandNode<?>) obj;

        if(requiredArg != null && other.getRequiredArg() != null && requiredArg.equals(other.getRequiredArg())) return true;

        if(requiredClass != null && other.getRequiredClass() != null && requiredClass.equals(other.getRequiredClass())) return true;

        return false;
    }

    public String getRequiredArg() {
        return requiredArg;
    }

    public Class<T> getRequiredClass() {
        return requiredClass;
    }

    public FoxCommandNode<?> getParent() {
        return parent;
    }

    @Nullable
    public RegisteredCommand getRegisteredCommand() {
        return registeredCommand;
    }

    public List<FoxCommandNode<?>> getChildren() {
        return children;
    }

    public void setRegisteredCommand(RegisteredCommand registeredCommand) {
        this.registeredCommand = registeredCommand;
    }
}
