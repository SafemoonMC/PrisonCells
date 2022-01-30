package gg.mooncraft.minecraft.prisoncells.furnace;

import lombok.Getter;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class AbstractFurnaceRecipe implements Keyed {

    /*
    Fields
     */
    private final @NotNull NamespacedKey key;
    private final @NotNull Material result;

    /*
    Constructor
     */
    AbstractFurnaceRecipe(@NotNull NamespacedKey key, @NotNull Material result) {
        this.key = key;
        this.result = result;
    }
}