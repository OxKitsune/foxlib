package com.kitsune.foxlib.util;

import com.google.common.base.Preconditions;
import com.kitsune.foxlib.FoxLib;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

/**
 * ItemBuilder foxlib v0.1.0
 *
 * @author kitsune
 */
public class ItemBuilder {

    /**
     * The itemstack that's modified by this item builder
     */
    private ItemStack itemStack;

    /**
     * Construct a new {@link ItemBuilder}.
     *
     * @param material - the material to use in this item builder
     * @param amount   - the amount to use in this item stack
     */
    public ItemBuilder(Material material, int amount) {

        // Make sure amount > 0
        Preconditions.checkArgument(amount > 0, "ItemStack size has to be greater than 0!");

        // Create the item stack
        this.itemStack = new ItemStack(material, amount);
    }

    /**
     * Construct a new {@link ItemBuilder}.
     *
     * @param material - the material to use in this item builder
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    /**
     * Construct a new {@link ItemBuilder}.
     */
    public ItemBuilder() {
        this(Material.AIR);
    }

    /**
     * Set the type of the item in this {@link ItemBuilder}.
     *
     * @param type - the type of the item in this item builder
     * @return - the {@link ItemBuilder}
     */
    public ItemBuilder setType(Material type) {
        itemStack.setType(type);
        return this;
    }

    /**
     * Set the name of the item in this {@link ItemBuilder}.
     *
     * @param name - the name of the item in this item builder
     * @return - the {@link ItemBuilder}
     */
    public ItemBuilder setName(String name) {

        // Get the item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set the name
        itemMeta.setDisplayName(name);

        // Set the item meta again
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Set the lore of the item in this {@link ItemBuilder}.
     *
     * @param lore - the lore of the item in this item builder
     * @return - the {@link ItemBuilder}
     */
    public ItemBuilder setLore(List<String> lore) {

        // Get the item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set the name
        itemMeta.setLore(lore);

        // Set the item meta again
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Set the amount of the item in this {@link ItemBuilder}.
     *
     * @param amount - the amount of the item in this item builder
     * @return - the {@link ItemBuilder}
     */
    public ItemBuilder setAmount(int amount) {

        // Make sure size > 0
        Preconditions.checkArgument(amount > 0, "ItemStack size has to be greater than 0!");

        // Set the amount
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * Add item flags to the item in this {@link ItemBuilder}.
     *
     * @param itemFlags - the item flags to add.
     * @return - the {@link ItemBuilder}
     */
    public ItemBuilder addItemFlag(ItemFlag... itemFlags) {

        // Get the item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Add the item flags
        itemMeta.addItemFlags(itemFlags);

        // Set the item meta
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Add the enchantment glow to the {@link ItemStack} in this item builder
     *
     * @return - this {@link ItemBuilder}
     */
    public ItemBuilder addGlow() {

        if (Enchantment.getByKey(new NamespacedKey(FoxLib.getInstance(), "glow_enchant")) == null) {

            // Register glow enchantment
            try {

                // Make sure field is accessible
                Field field = Enchantment.class.getDeclaredField("acceptingNew");
                field.setAccessible(true);

                // Set the field to true
                field.set(null, true);

                // Register the enchantment
                Enchantment.registerEnchantment(new GlowEnchant(new NamespacedKey(FoxLib.getInstance(), "glow_enchant")));

                // Make sure to reset the field
                field.set(null, false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.addEnchant(Enchantment.getByKey(new NamespacedKey(FoxLib.getInstance(), "glow_enchant")), 1, true);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    class GlowEnchant extends Enchantment {

        public GlowEnchant(@NotNull NamespacedKey key) {
            super(key);
        }

        @Override
        public String getName() {
            return "glow";
        }

        @Override
        public int getMaxLevel() {
            return 1;
        }

        @Override
        public int getStartLevel() {
            return 0;
        }

        @Override
        public EnchantmentTarget getItemTarget() {
            return null;
        }

        @Override
        public boolean isTreasure() {
            return false;
        }

        @Override
        public boolean isCursed() {
            return false;
        }

        @Override
        public boolean conflictsWith(Enchantment other) {
            return false;
        }

        @Override
        public boolean canEnchantItem(ItemStack item) {
            return true;
        }
    }

    /**
     * Build the {@link ItemStack} and return it.
     *
     * @return - the item stack built by this item builder
     */
    public ItemStack build() {
        return itemStack.clone();
    }
}

