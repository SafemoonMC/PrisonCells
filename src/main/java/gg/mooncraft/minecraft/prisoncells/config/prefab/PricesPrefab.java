package gg.mooncraft.minecraft.prisoncells.config.prefab;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class PricesPrefab {

    /*
    Fields
     */
    private final @NotNull Map<Integer, Integer> furnacesMap;
    private final @NotNull Map<Integer, Integer> storageRowsMap;

    /*
    Constructor
     */
    public PricesPrefab(@NotNull ConfigurationSection configurationSection) {
        this.furnacesMap = new HashMap<>();
        for (String key : configurationSection.getConfigurationSection("furnaces").getKeys(false)) {
            int level = Integer.parseInt(key);
            int cost = configurationSection.getInt("furnaces." + key);
            this.furnacesMap.put(level, cost);
        }

        this.storageRowsMap = new HashMap<>();
        for (String key : configurationSection.getConfigurationSection("storage-rows").getKeys(false)) {
            int level = Integer.parseInt(key);
            int cost = configurationSection.getInt("storage-rows." + key);
            this.storageRowsMap.put(level, cost);
        }
    }

    /*
    Methods
     */
    public int getFurnaceCost(int level) {
        return this.furnacesMap.getOrDefault(level, 0);
    }

    public int getStorageRowCost(int level) {
        return this.storageRowsMap.getOrDefault(level, 0);
    }
}