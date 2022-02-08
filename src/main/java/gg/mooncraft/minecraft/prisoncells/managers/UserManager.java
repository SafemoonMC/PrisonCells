package gg.mooncraft.minecraft.prisoncells.managers;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.database.PrisonUserDAO;
import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;
import gg.mooncraft.minecraft.prisoncells.utilities.cache.CaffeineFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class UserManager {

    /*
    Fields
     */
    private final @NotNull Map<UUID, Long> commandDelayMap = new HashMap<>();
    private final @NotNull AsyncLoadingCache<UUID, PrisonUser> prisonUserCache;

    /*
    Constructor
     */
    public UserManager() {
        this.prisonUserCache = CaffeineFactory.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
                .buildAsync((key, executor) -> PrisonUserDAO.read(key));
    }

    /*
    Methods
     */
    public @NotNull CompletableFuture<PrisonUser> readUser(@NotNull UUID uniqueId) {
        return prisonUserCache.get(uniqueId);
    }

    public boolean hasCooldown(@NotNull Player player) {
        if (this.commandDelayMap.containsKey(player.getUniqueId())) {
            long lastTime = this.commandDelayMap.get(player.getUniqueId());
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastTime) < 3) {
                return true;
            }
        }
        this.commandDelayMap.put(player.getUniqueId(), System.currentTimeMillis());
        return false;
    }

    public void delCooldown(@NotNull Player player) {
        this.commandDelayMap.remove(player.getUniqueId());
    }
}