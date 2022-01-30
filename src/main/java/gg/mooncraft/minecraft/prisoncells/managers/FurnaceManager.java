package gg.mooncraft.minecraft.prisoncells.managers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.menu.FurnaceMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class FurnaceManager {

    /*
    Fields
     */
    private final @NotNull List<FurnaceMenu> furnaceMenuList = new ArrayList<>();

    /*
    Methods
     */
    public void addFurnace(@NotNull FurnaceMenu furnaceMenu) {
        this.furnaceMenuList.add(furnaceMenu);
    }

    public void delFurnace(@NotNull FurnaceMenu furnaceMenu) {
        furnaceMenu.getFurnaceTicker().stop();
        this.furnaceMenuList.remove(furnaceMenu);
    }

    public @NotNull Optional<FurnaceMenu> getFurnaceMenu(@NotNull Player player) {
        return this.furnaceMenuList.stream().filter(furnaceMenu -> furnaceMenu.getPlayer().getUniqueId().equals(player.getUniqueId())).findFirst();
    }
}