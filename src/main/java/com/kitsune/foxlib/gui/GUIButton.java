package com.kitsune.foxlib.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public abstract class GUIButton {

    /**
     * @return Whether to cancel the click event that triggered this button. Can be overridden
     */
    public boolean cancelClickEvent() {
        return true;
    }

    /**
     * Get the material of the icon itemstack that represents this GUIButton in the GUI.
     *
     * @param player - the player to get the material of the icon itemstack for
     *
     * @return - the material of the icon itemstack that represents this GUIButton in the GUI.
     */
    public abstract Material getMaterial (Player player);

    /**
     * Get the amount of the icon itemstack that represents this GUIButton in the GUI.
     *
     * @param player - the player to get the amount of the icon itemstack for
     *
     * @return - the amount of the icon itemstack that represents this GUIButton in the GUI.
     */
    public abstract int getAmount (Player player);

    /**
     * Get the display name of the icon itemstack that represents this GUIButton in the GUI.
     *
     * @param player - the player to get the display name of the icon itemstack for
     *
     * @return - the display name of the icon itemstack that represents this GUIButton in the GUI.
     */
    public abstract String getDisplayName (Player player);

    /**
     * Get the lore of the icon itemstack that represents this GUIButton in the GUI.
     *
     * @param player - the player to get the lore of the icon itemstack for
     *
     * @return - the lore of the icon itemstack that represents this GUIButton in the GUI.
     */
    public abstract List<String> getLore (Player player);

    /**
     * This method is called whenever this button is clicked.
     * Use this method for the actual code that this button will execute whenever its pressed.
     *
     * @param player - the player that clicked the button
     * @param event - the InventoryClickEvent that fired when the button was clicked
     */
    public abstract void onClick (Player player, InventoryClickEvent event);

    /**
     * Get the icon {@link ItemStack} that represents this button in the GUI.
     * <p>
     *     Note: This method can be overwritten if you need to add special item meta
     *     to the item, but it builds the item stack based of the other methods in this class.
     * </p>
     * @param player - the player to build the icon itemstack for
     *
     * @return - an itemstack that will represent this button in a gui.
     */
    public ItemStack getItemStack (Player player) {
        ItemStack item = new ItemStack(this.getMaterial(player), this.getAmount(player));
        ItemMeta itemMeta = item.getItemMeta();

        if(itemMeta != null){
            itemMeta.setDisplayName(this.getDisplayName(player));
            itemMeta.setLore(this.getLore(player));

            item.setItemMeta(itemMeta);
        }

        return item;
    }

}