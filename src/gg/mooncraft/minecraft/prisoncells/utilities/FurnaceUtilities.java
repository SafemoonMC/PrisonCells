package gg.mooncraft.minecraft.prisoncells.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.FurnaceRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.furnace.Fuel;
import gg.mooncraft.minecraft.prisoncells.furnace.VirtualFurnaceRecipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FurnaceUtilities {

    /*
    Constants
     */
    private static final @NotNull String FUEL_KEY_FORMAT = "mc_fuel_%s";
    private static final @NotNull String FURNACE_KEY_FORMAT = "mc_furnace_%s";
    private static final @NotNull Map<NamespacedKey, Fuel> VANILLA_FUELS;
    private static final @NotNull Map<NamespacedKey, VirtualFurnaceRecipe> VANILLA_FURNACE_RECIPES;

    /*
    Static
     */
    static {
        // Register fuels
        Map<NamespacedKey, Fuel> vanillaFuelMap = new HashMap<>();
        register(vanillaFuelMap, "lava_bucket", Material.LAVA_BUCKET, 20000);
        register(vanillaFuelMap, "block_of_coal", Material.COAL_BLOCK, 16000);
        register(vanillaFuelMap, "dried_kelp_block", Material.DRIED_KELP_BLOCK, 4000);
        register(vanillaFuelMap, "blaze_rod", Material.BLAZE_ROD, 2400);
        register(vanillaFuelMap, "coal", Material.COAL, 1600);
        register(vanillaFuelMap, "charcoal", Material.CHARCOAL, 1600);
        register(vanillaFuelMap, "any_boat", Tag.ITEMS_BOATS, 1200);
        register(vanillaFuelMap, "any_log", Tag.LOGS, 300);
        register(vanillaFuelMap, "any_plank", Tag.PLANKS, 300);
        register(vanillaFuelMap, "any_wood_pressure_plate", Tag.WOODEN_PRESSURE_PLATES, 300);
        register(vanillaFuelMap, "any_wood_stair", Tag.WOODEN_STAIRS, 300);
        register(vanillaFuelMap, "any_wood_trapdoor", Tag.WOODEN_TRAPDOORS, 300);
        register(vanillaFuelMap, "crafting_table", Material.CRAFTING_TABLE, 300);
        register(vanillaFuelMap, "bookshelf", Material.BOOKSHELF, 300);
        register(vanillaFuelMap, "chest", Material.CHEST, 300);
        register(vanillaFuelMap, "trapped_chest", Material.TRAPPED_CHEST, 300);
        register(vanillaFuelMap, "daylight_detector", Material.DAYLIGHT_DETECTOR, 300);
        register(vanillaFuelMap, "jukebox", Material.JUKEBOX, 300);
        register(vanillaFuelMap, "note_block", Material.NOTE_BLOCK, 300);
        register(vanillaFuelMap, "mushroom_stem", Material.MUSHROOM_STEM, 300);
        register(vanillaFuelMap, "brown_mushroom_block", Material.BROWN_MUSHROOM_BLOCK, 300);
        register(vanillaFuelMap, "red_mushroom_block", Material.RED_MUSHROOM_BLOCK, 300);
        register(vanillaFuelMap, "any_banner", Tag.BANNERS, 300);
        register(vanillaFuelMap, "any_wooden_slab", Tag.WOODEN_SLABS, 150);
        register(vanillaFuelMap, "bow", Material.BOW, 300);
        register(vanillaFuelMap, "fishing_rod", Material.FISHING_ROD, 300);
        register(vanillaFuelMap, "ladder", Material.LADDER, 300);
        register(vanillaFuelMap, "any_wooden_button", Tag.WOODEN_BUTTONS, 100);
        register(vanillaFuelMap, "wooden_pickaxe", Material.WOODEN_PICKAXE, 200);
        register(vanillaFuelMap, "wooden_shovel", Material.WOODEN_SHOVEL, 200);
        register(vanillaFuelMap, "wooden_hoe", Material.WOODEN_HOE, 200);
        register(vanillaFuelMap, "wooden_axe", Material.WOODEN_AXE, 200);
        register(vanillaFuelMap, "wooden_sword", Material.WOODEN_SWORD, 200);
        register(vanillaFuelMap, "any_wooden_door", Tag.WOODEN_TRAPDOORS, 200);
        register(vanillaFuelMap, "bowl", Material.BOWL, 100);
        register(vanillaFuelMap, "any_sapling", Tag.SAPLINGS, 100);
        register(vanillaFuelMap, "stick", Material.STICK, 100);
        register(vanillaFuelMap, "any_wool", Tag.WOOL, 100);
        register(vanillaFuelMap, "any_carpet", Tag.CARPETS, 67);
        register(vanillaFuelMap, "scaffolding", Material.SCAFFOLDING, 400);
        register(vanillaFuelMap, "cartography_table", Material.CARTOGRAPHY_TABLE, 300);
        register(vanillaFuelMap, "fletching_table", Material.FLETCHING_TABLE, 300);
        register(vanillaFuelMap, "smithing_table", Material.SMITHING_TABLE, 300);
        register(vanillaFuelMap, "lectern", Material.LECTERN, 300);
        register(vanillaFuelMap, "composter", Material.COMPOSTER, 300);
        register(vanillaFuelMap, "barrel", Material.BARREL, 300);
        register(vanillaFuelMap, "loom", Material.LOOM, 300);
        register(vanillaFuelMap, "any_sign", Tag.SIGNS, 200);
        register(vanillaFuelMap, "bamboo", Material.BAMBOO, 50);
        register(vanillaFuelMap, "any_wood_fence", Tag.WOODEN_FENCES, 300);

        // Register recipes
        Map<NamespacedKey, VirtualFurnaceRecipe> virtualFurnaceRecipeMap = new HashMap<>();
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                VirtualFurnaceRecipe newFurnaceRecipe = new VirtualFurnaceRecipe(PrisonCellsMain.createKey(String.format(FURNACE_KEY_FORMAT, furnaceRecipe.getKey().getKey())), furnaceRecipe.getInput().getType(), furnaceRecipe.getResult().getType(), furnaceRecipe.getCookingTime(), furnaceRecipe.getExperience());
                virtualFurnaceRecipeMap.put(PrisonCellsMain.createKey(String.format(FURNACE_KEY_FORMAT, furnaceRecipe.getKey().getKey())), newFurnaceRecipe);
            }
        });

        // Assign unmodifiable version of the maps
        VANILLA_FUELS = Collections.unmodifiableMap(vanillaFuelMap);
        VANILLA_FURNACE_RECIPES = Collections.unmodifiableMap(virtualFurnaceRecipeMap);
    }

    /*
    Static Methods
     */
    private static void register(@NotNull Map<NamespacedKey, Fuel> map, String name, Material fuel, int burnTicks) {
        Fuel newFuel = new Fuel(PrisonCellsMain.createKey(String.format(FUEL_KEY_FORMAT, name)), fuel, burnTicks);
        map.put(newFuel.getKey(), newFuel);
    }

    private static void register(@NotNull Map<NamespacedKey, Fuel> map, String name, Tag<Material> fuelTag, int burnTicks) {
        Fuel newFuel = new Fuel(PrisonCellsMain.createKey(String.format(FUEL_KEY_FORMAT, name)), fuelTag, burnTicks);
        map.put(newFuel.getKey(), newFuel);
    }

    public static @Nullable Fuel getFuelByKey(@NotNull NamespacedKey key) {
        return getVanillaFuels().get(key);
    }

    public static @Nullable Fuel getFuelByMaterial(@NotNull Material material) {
        for (Fuel fuel : getVanillaFuels().values()) {
            if (fuel.matchFuel(material)) {
                return fuel;
            }
        }
        return null;
    }

    public static @Nullable VirtualFurnaceRecipe getRecipeByKey(@NotNull NamespacedKey key) {
        return getVanillaFurnaceRecipes().get(key);
    }

    public static @Nullable VirtualFurnaceRecipe getRecipeByIngredient(@NotNull Material ingredient) {
        for (VirtualFurnaceRecipe virtualFurnaceRecipe : getVanillaFurnaceRecipes().values()) {
            if (virtualFurnaceRecipe.getIngredientType() == ingredient) {
                return virtualFurnaceRecipe;
            }
        }
        return null;
    }

    public static @NotNull @UnmodifiableView Map<NamespacedKey, Fuel> getVanillaFuels() {
        return VANILLA_FUELS;
    }

    public static @NotNull @UnmodifiableView Map<NamespacedKey, VirtualFurnaceRecipe> getVanillaFurnaceRecipes() {
        return VANILLA_FURNACE_RECIPES;
    }
}