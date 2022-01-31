package gg.mooncraft.minecraft.prisoncells.config;

import lombok.Getter;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.config.prefab.PricesPrefab;

@Getter
public final class Configuration {

    /*
    Fields
     */
    private final boolean onlyStorage;
    private final @NotNull PricesPrefab pricesPrefab;

    /*
    Constructor
     */
    public Configuration(@NotNull FileConfiguration fileConfiguration) {
        this.onlyStorage = fileConfiguration.getBoolean("only-storage");
        this.pricesPrefab = new PricesPrefab(fileConfiguration.getConfigurationSection("prices"));
    }
}