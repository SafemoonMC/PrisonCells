package gg.mooncraft.minecraft.prisoncells.database.objects;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.database.entities.EntityChild;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
public final class VirtualFurnace implements EntityChild<PrisonUser> {

    /*
    Fields
     */
    private final @NotNull PrisonUser parent;

    private final @NotNull UUID uniqueId;
    private ItemStack fuel;
    private ItemStack input;
    private ItemStack output;
    private int cookTime;
    private int cookTimeTotal;
    private int fuelTime;
    private int fuelTimeTotal;
    private float experience;
    public Timestamp timestamp;

    /*
    Constructor
     */
    public VirtualFurnace(@NotNull PrisonUser prisonUser) {
        this.parent = prisonUser;
        this.uniqueId = UUID.randomUUID();
    }

    public VirtualFurnace(@NotNull PrisonUser prisonUser, @NotNull UUID uniqueId, @NotNull ItemStack fuel, @NotNull ItemStack input, @NotNull ItemStack output, int cookTime, int cookTimeTotal, int fuelTime, int fuelTimeTotal, float experience, @NotNull Timestamp timestamp) {
        this.uniqueId = uniqueId;
        this.parent = prisonUser;
        this.fuel = fuel;
        this.input = input;
        this.output = output;
        this.cookTime = cookTime;
        this.cookTimeTotal = cookTimeTotal;
        this.fuelTime = fuelTime;
        this.fuelTimeTotal = fuelTimeTotal;
        this.experience = experience;
        this.timestamp = timestamp;
    }

    /*
    Methods
     */

}