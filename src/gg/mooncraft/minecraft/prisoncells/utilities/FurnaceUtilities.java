package gg.mooncraft.minecraft.prisoncells.utilities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.furnace.VirtualFurnaceRecipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FurnaceUtilities {

    /*
    Constants
     */
    private static final @NotNull String FURNACE_KEY_FORMAT = "mc_furnace_%s";
    private static final @NotNull Map<NamespacedKey, VirtualFurnaceRecipe> VANILLA_FURNACE_RECIPES;

    /*
    Static
     */
    static {
        Map<NamespacedKey, VirtualFurnaceRecipe> virtualFurnaceRecipeMap = new HashMap<>();
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                VirtualFurnaceRecipe newFurnaceRecipe = new VirtualFurnaceRecipe(PrisonCellsMain.createKey(String.format(FURNACE_KEY_FORMAT, furnaceRecipe.getKey().getKey())), furnaceRecipe.getInput().getType(), furnaceRecipe.getResult().getType(), furnaceRecipe.getCookingTime(), furnaceRecipe.getExperience());
                virtualFurnaceRecipeMap.put(PrisonCellsMain.createKey(String.format(FURNACE_KEY_FORMAT, furnaceRecipe.getKey().getKey())), newFurnaceRecipe);
            }
        });
        VANILLA_FURNACE_RECIPES = Collections.unmodifiableMap(virtualFurnaceRecipeMap);
    }

    /*
    Static Methods
     */
    public static @Nullable VirtualFurnaceRecipe getByKey(@NotNull NamespacedKey key) {
        return getVanillaFurnaceRecipes().get(key);
    }

    public static @Nullable VirtualFurnaceRecipe getByIngredient(@NotNull Material ingredient) {
        for (VirtualFurnaceRecipe virtualFurnaceRecipe : getVanillaFurnaceRecipes().values()) {
            if (virtualFurnaceRecipe.getIngredientType() == ingredient) {
                return virtualFurnaceRecipe;
            }
        }
        return null;
    }

    public static @NotNull @UnmodifiableView Map<NamespacedKey, VirtualFurnaceRecipe> getVanillaFurnaceRecipes() {
        return VANILLA_FURNACE_RECIPES;
    }
}