package gg.mooncraft.minecraft.prisoncells.furnace;

import lombok.Getter;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class Fuel implements Keyed {

    /*
    Fields
     */
    private final @NotNull NamespacedKey key;
    private final @Nullable Material material;
    private final @Nullable Tag<Material> tag;
    private final int burnTime;

    /*
    Constructor
     */
    public Fuel(@NotNull NamespacedKey key, @NotNull Material material, int burnTime) {
        this.key = key;
        this.material = material;
        this.tag = null;
        this.burnTime = burnTime;
    }

    public Fuel(@NotNull NamespacedKey key, @NotNull Tag<Material> tag, int burnTime) {
        this.key = key;
        this.material = null;
        this.tag = tag;
        this.burnTime = burnTime;
    }

    /*
    Methods
     */
    public boolean matchFuel(@NotNull Material material) {
        if (this.material != null && this.material == material) {
            return true;
        } else {
            return tag != null && tag.isTagged(material);
        }
    }
}