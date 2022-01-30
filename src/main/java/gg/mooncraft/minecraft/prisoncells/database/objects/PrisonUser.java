package gg.mooncraft.minecraft.prisoncells.database.objects;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.database.entities.EntityParent;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gg.mooncraft.minecraft.prisoncells.database.PrisonUserDAO;
import gg.mooncraft.minecraft.prisoncells.database.VirtualFurnaceDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public final class PrisonUser implements EntityParent<PrisonUser> {

    /*
    Fields
     */
    private final @NotNull UUID uniqueId;

    private final @NotNull List<VirtualFurnace> furnaceList;

    private final @NotNull AtomicInteger storageRows;
    private @NotNull ItemStack[] storage;

    /*
    Constructor
     */
    public PrisonUser(@NotNull UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.furnaceList = new ArrayList<>();
        this.storageRows = new AtomicInteger(1);
        this.storage = new ItemStack[0];
    }

    public PrisonUser(@NotNull UUID uniqueId, ItemStack[] storage, int storageRows) {
        this.uniqueId = uniqueId;
        this.furnaceList = new ArrayList<>();
        this.storage = storage;
        this.storageRows = new AtomicInteger(storageRows);
    }

    /*
    Methods
     */
    public void updateStorage(ItemStack[] storage) {
        this.storage = storage;
        PrisonUserDAO.update(this);
    }

    public void updateStorageRows() {
        this.storageRows.incrementAndGet();
        PrisonUserDAO.update(this);
    }

    public void updateFurnaceCount() {
        VirtualFurnace virtualFurnace = new VirtualFurnace(this);
        this.furnaceList.add(virtualFurnace);
        VirtualFurnaceDAO.create(virtualFurnace);
    }

    public int getStorageRows() {
        return this.storageRows.get();
    }

    public int getFurnaceCount() {
        return this.furnaceList.size();
    }

    public @Nullable ItemStack[] getStorage() {
        return this.storage;
    }

    /*
    Override Methods
     */
    @Override
    public @NotNull CompletableFuture<PrisonUser> withChildren() {
        CompletableFuture<?> furnaceFuture = VirtualFurnaceDAO.read(this).thenAccept(this.furnaceList::addAll);
        return furnaceFuture.thenApply(v -> this);
    }
}