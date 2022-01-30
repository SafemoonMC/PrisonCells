package gg.mooncraft.minecraft.prisoncells.managers;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;

import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.database.PrisonUserDAO;
import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;
import gg.mooncraft.minecraft.prisoncells.utilities.cache.CaffeineFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class UserManager {

    /*
    Fields
     */
    private final @NotNull AsyncLoadingCache<UUID, PrisonUser> prisonUserCache;

    /*
    Constructor
     */
    public UserManager() {
        this.prisonUserCache = CaffeineFactory.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES)
                .buildAsync((key, executor) -> PrisonUserDAO.read(key));
    }

    /*
    Methods
     */
    public @NotNull CompletableFuture<PrisonUser> readUser(@NotNull UUID uniqueId) {
        return prisonUserCache.get(uniqueId);
    }
}