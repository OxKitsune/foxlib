package com.kitsune.foxlib.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderButton extends GUIButton {

    /** The material that this placeholder button uses */
    private final Material material;

    /**
     * Construct a new PlaceHolder button.
     *
     * @param material - the material to use
     */
    public PlaceholderButton (Material material){
        this.material = material;
    }


    @Override
    public Material getMaterial(Player player) {
        return material;
    }

    @Override
    public int getAmount(Player player) {
        return 1;
    }

    @Override
    public String getDisplayName(Player player) {
        return " ";
    }

    @Override
    public List<String> getLore(Player player) {
        return new ArrayList<>();
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {

        // Do nothing since it's a placeholder
        event.setCancelled(true);
    }
}
