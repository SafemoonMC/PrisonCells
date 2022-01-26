package gg.mooncraft.minecraft.prisoncells.furnace;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.database.objects.VirtualFurnace;

import java.util.Objects;

public class VirtualFurnaceRecipe extends AbstractFurnaceRecipe {

    /*
    Fields
     */
    private final Material ingredient;
    private final int cookTime;
    private final float experience;

    /**
     * Create a new recipe for a {@link VirtualFurnace}
     * <p>The experience for this will default to 0.0</p>
     *
     * @param key        Key for recipe
     * @param ingredient Ingredient to be put into furnace
     * @param result     The resulting item from this recipe
     * @param cookTime   Time to cook this item (in ticks)
     */
    public VirtualFurnaceRecipe(@NotNull NamespacedKey key, @NotNull Material ingredient, @NotNull Material result, int cookTime) {
        this(key, ingredient, result, cookTime, 0.0F);
    }

    /**
     * Create a new recipe for a {@link VirtualFurnace}
     *
     * @param key        Key for recipe
     * @param ingredient Ingredient to be put into furnace
     * @param result     The resulting item from this recipe
     * @param cookTime   Time to cook this item (in ticks)
     * @param experience The experience the player will receive for cooking this item
     */
    public VirtualFurnaceRecipe(@NotNull NamespacedKey key, @NotNull Material ingredient, @NotNull Material result, int cookTime, float experience) {
        super(key, result);
        this.ingredient = ingredient;
        this.cookTime = cookTime;
        this.experience = experience;
    }

    /**
     * Get the ingredient {@link Material} of this recipe
     *
     * @return Ingredient Material of this recipe
     */
    public Material getIngredientType() {
        return this.ingredient;
    }

    /**
     * Get the cook time for this recipe
     *
     * @return Cook time for this recipe
     */
    public int getCookTime() {
        return this.cookTime;
    }

    /**
     * Get the experience this recipe will yield
     *
     * @return Experience this recipe will yield
     */
    public float getExperience() {
        return experience;
    }

    /*
    Override Methods
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualFurnaceRecipe recipe = (VirtualFurnaceRecipe) o;
        return cookTime == recipe.cookTime && Float.compare(recipe.experience, experience) == 0 && ingredient == recipe.ingredient;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, cookTime, experience);
    }

    @Override
    public String toString() {
        return "VirtualFurnaceRecipe{" +
                "key=" + getKey() +
                ", ingredient=" + ingredient +
                ", result=" + getResult().name() +
                ", cookTime=" + cookTime +
                ", experience=" + experience +
                '}';
    }
}