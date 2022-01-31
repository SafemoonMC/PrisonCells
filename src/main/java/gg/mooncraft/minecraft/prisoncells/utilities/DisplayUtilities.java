package gg.mooncraft.minecraft.prisoncells.utilities;

import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class DisplayUtilities {

    public static @NotNull String getDisplay(ItemStack itemStack) {
        if (itemStack == null) return "none";
        String itemName = WordUtils.capitalizeFully(itemStack.getType().name().replaceAll("_", " "));

        return String.format("%s x%d", itemName, itemStack.getAmount());
    }
}