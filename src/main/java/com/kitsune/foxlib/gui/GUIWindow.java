package com.kitsune.foxlib.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;


public abstract class GUIWindow implements Listener {

    /** Whether to automatically update the gui or not */
    private final boolean autoUpdateButtons;

    /** The title of the GUI */
    private final String guiTitle;

    /** Whether the GUIWindow is currently open for the player */
    private boolean open;

    /** The player the GUIWindow is currently opened for */
    private Player player;

    /** The Inventory instance that represents this GUIWindow */
    private Inventory inventory;

    /** The runnable that is responsible for updating the gui when autoUpdateButtons is set to true */
    private BukkitRunnable updateRunnable;

    /** The plugin associated with this instance of core */
    private Plugin plugin;

    /**
     * Construct a new GUIWindow.
     *
     * @param guiTitle - the title of the GUI
     * @param plugin - the plugin that's creating the gui
     */
    public GUIWindow (String guiTitle, Plugin plugin) {
        this(guiTitle, false, plugin);
        
    }

    /**
     * Construct a new GUIWindow.
     *
     * @param guiTitle - the title of the GUI
     * @param autoUpdateButtons - whether to update the buttons automatically
     * @param plugin - the plugin that's creating the gui
     */
    public GUIWindow (String guiTitle, boolean autoUpdateButtons, Plugin plugin){
        this.guiTitle = guiTitle;
        this.autoUpdateButtons = autoUpdateButtons;
        this.plugin = plugin;

        registerListeners();
    }
    
    public abstract Map<Integer, GUIButton> getButtons (Player player);

    /**
     * Called whenever the player closes the {@link GUIWindow}
     *
     * @param player the player that closed the window
     */
    public void onWindowClose (Player player) {
        // Have this here so GUIWindows can use this.
    }

    /**
     * Opens this GUIWindow for the specified player.
     * @param player the player
     */
    public void open (Player player){

        // Set the player
        this.player = player;

        // Get the button map for the player
        Map<Integer, GUIButton> buttonMap = getButtons(player);

        // Compute the GUISize
        int guiSize = computeGUISize(buttonMap);

        // Create the GUI Inventory.
        this.inventory = Bukkit.createInventory(null, guiSize, guiTitle);

        // Add the buttons to the inventory
        buttonMap.forEach((slot, guiButton) -> {
            inventory.setItem(slot, guiButton.getItemStack(player));
        });

        // Open the GUI for the player
        player.openInventory(inventory);

        // Set the GUI to open
        this.open = true;


        // Start the auto update runnable if necessary
        if(isAutoUpdateButtons()){

            this.updateRunnable = new BukkitRunnable() {
                @Override
                public void run() {

                    // Automatically cancel the runnable if the GUI isn't open anymore.
                    if(!isOpen()) {
                        this.cancel();
                        return;
                    }

                    // Update the GUI
                    update();
                }
            };

            // Start the runnable
            updateRunnable.runTaskTimer(plugin, 10, 10);
        }
    }

    /**
     * Update the buttons in the GUI.
     */
    public void update () {

        // Don't update if the GUI isn't open!
        if(!isOpen()) return;

        // Loop through buttons, and update them in the gui
        getButtons(player).forEach((slot, guiButton) -> {
            inventory.setItem(slot, guiButton.getItemStack(player));
        });

    }

    /**
     * Register the listeners in the GUIWindow class.
     */
    private void registerListeners () {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Unregister the listeners in the GUIWindow class
     */
    private void unregisterListeners () {
        HandlerList.unregisterAll(this);
    }


    @EventHandler
    public void onInventoryClickEvent (InventoryClickEvent e){

        // Make sure GUIWindow is open
        if(!isOpen()) return;

        // Make sure it's the player that's clicking something in the GUI
        if(!e.getWhoClicked().getUniqueId().equals(player.getUniqueId())) return;

        // Make sure the clicked inventory is not null
        if(e.getClickedInventory() == null) return;

        // Make sure it's the correct inventory
        if(!e.getInventory().equals(inventory) || !e.getClickedInventory().equals(inventory)) return;

        // This GUI Library is mainly a simple click library, however this onInventoryClick event can be overridden if necessary.
        if(e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT && e.getClick() != ClickType.MIDDLE) {

            // Cancel click event
            e.setCancelled(true);
            return;
        }

        if(getButtons(player).containsKey(e.getSlot())){
            GUIButton button = getButtons(player).get(e.getSlot());
            if (button.cancelClickEvent()) e.setCancelled(true);
            button.onClick(player, e);
        }
        //Handeling click might close
        if(isOpen()){
            update();
        }
    }

    @EventHandler
    public void onInventoryCloseEvent (InventoryCloseEvent e){

        // Make sure GUIWindow is open
        if(!isOpen()) return;

        // Make sure it's the player that's closing the GUI
        if(!e.getPlayer().getUniqueId().equals(player.getUniqueId())) return;

        // Make sure its the correct inventory
        if(!e.getInventory().equals(inventory)) return;

        // Call on window close
        onWindowClose(player);

        open = false;
        player = null;
        inventory = null;

        // Unregister the listeners
        unregisterListeners();
    }

    /**
     * Get the required inventory size for the specified button map.
     *
     * @param buttonMap - the button map.
     *
     * @return - the required inventory size for the specified button map.
     */
    private int computeGUISize (Map<Integer, GUIButton> buttonMap){
        int guiSize = -1;
        for(int slot : buttonMap.keySet()){
            if(slot > guiSize){
                guiSize = slot;
            }
        }

        while (guiSize % 9 > 0) {
            guiSize++;
        }

        if(guiSize == 0){
            guiSize = 9;
        }

        return guiSize;
    }

    /**
     * Get whether this GUI is automatically updating its buttons.
     *
     * @return - whether the gui is automatically updating its buttons
     */
    public boolean isAutoUpdateButtons() {
        return autoUpdateButtons;
    }

    /**
     * Get the title of the GUI.
     *
     * @return - the title of the gui.
     */
    public String getGuiTitle() {
        return guiTitle;
    }

    /**
     * Get whether the GUI is currently open.
     *
     * @return - whether the gui is currently open.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Get the player that's opened the GUI.
     *
     * @return - the player that's opened the gui.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the inventory that represents the GUI.
     *
     * @return - the inventory that represents the gui.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the {@link Plugin} associated with this object
     *
     * @return The {@link Plugin} associated with this object
     */
    public Plugin getPlugin(){
        return plugin;
    }
}
