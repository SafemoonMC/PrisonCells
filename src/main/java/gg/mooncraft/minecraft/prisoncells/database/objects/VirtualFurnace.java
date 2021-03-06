package gg.mooncraft.minecraft.prisoncells.database.objects;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.database.entities.EntityChild;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gg.mooncraft.minecraft.prisoncells.database.VirtualFurnaceDAO;
import gg.mooncraft.minecraft.prisoncells.furnace.Fuel;
import gg.mooncraft.minecraft.prisoncells.furnace.VirtualFurnaceRecipe;
import gg.mooncraft.minecraft.prisoncells.utilities.FurnaceUtilities;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
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
    private Timestamp timestamp;

    private boolean locked;
    private boolean resuming;

    /*
    Constructor
     */
    public VirtualFurnace(@NotNull PrisonUser prisonUser) {
        this.parent = prisonUser;
        this.uniqueId = UUID.randomUUID();
    }

    public VirtualFurnace(@NotNull PrisonUser prisonUser, @NotNull UUID uniqueId, @Nullable ItemStack fuel, @Nullable ItemStack input, @Nullable ItemStack output, int cookTime, int cookTimeTotal, int fuelTime, int fuelTimeTotal, float experience, @NotNull Timestamp timestamp) {
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
    public void update() {
        this.timestamp = Timestamp.from(Instant.now());
        VirtualFurnaceDAO.update(this);
    }

    public void resume(@NotNull FurnaceInventory furnaceInventory) {
        if (timestamp == null) return;
        this.resuming = true;
        int ticks = (int) (Duration.between(timestamp.toInstant(), Instant.now()).toMillis() / 20);

        // Convert fuel in ticks
        int newTotalFuel = 0;
        int oldTotalFuel = this.fuelTime + (this.fuel != null ? this.fuel.getAmount() * FurnaceUtilities.getFuelByMaterial(this.fuel.getType()).getBurnTime() : 0);

        // Get the considerable ticks
        int realTicks = Math.min(ticks, oldTotalFuel);

        // Convert input in ticks
        int newTotalInput = 0;
        int oldTotalInput = (this.input != null ? this.input.getAmount() * FurnaceUtilities.getRecipeByIngredient(this.input.getType()).getCookTime() : 0) - this.cookTime;

        if (oldTotalInput <= realTicks) {
            realTicks = oldTotalInput;
        }

        int cookTime = this.input != null ? FurnaceUtilities.getRecipeByIngredient(this.input.getType()).getCookTime() : 0;
        int freeOutputSpace;
        if (this.output != null) {
            freeOutputSpace = (this.output.getMaxStackSize() - this.output.getAmount()) * cookTime;
        } else {
            freeOutputSpace = this.input == null ? 0 : this.input.getMaxStackSize() * cookTime;
        }
        if (realTicks > freeOutputSpace) {
            realTicks = freeOutputSpace;
        }

        int fuelAmount;
        if (this.fuelTime >= realTicks) {
            newTotalFuel = oldTotalFuel;
            this.fuelTime -= realTicks;
            fuelAmount = this.fuel != null ? this.fuel.getAmount() : 0;
        } else {
            newTotalFuel = oldTotalFuel - realTicks;
            this.fuelTimeTotal = this.fuel != null ? FurnaceUtilities.getFuelByMaterial(this.fuel.getType()).getBurnTime() : 0;
            this.fuelTime = this.fuelTimeTotal != 0 ? newTotalFuel % this.fuelTimeTotal : 0;
            fuelAmount = newTotalFuel / this.fuelTimeTotal;
        }
        newTotalInput = oldTotalInput - realTicks;
        this.cookTimeTotal = this.input != null ? FurnaceUtilities.getRecipeByIngredient(this.input.getType()).getCookTime() : 0;
        this.cookTime = this.cookTimeTotal != 0 ? this.cookTimeTotal - (newTotalInput % this.cookTimeTotal) : 0;
        int inputAmount = (int) Math.ceil((double) newTotalInput / this.cookTimeTotal);// - (cookTimeTotal == cookTime ? 1 : 0);

        if (this.fuel != null) {
            this.fuel.setAmount(fuelAmount);
        }
        if (this.output != null) {
            if (this.input != null) {
                this.experience += (this.input.getAmount() - inputAmount) * FurnaceUtilities.getRecipeByIngredient(this.input.getType()).getExperience();
                this.output.setAmount(this.output.getAmount() + (this.input.getAmount() - inputAmount));
            }
        } else {
            if (this.input != null) {
                this.experience += (this.input.getAmount() - inputAmount) * FurnaceUtilities.getRecipeByIngredient(this.input.getType()).getExperience();
                this.output = new ItemStack(FurnaceUtilities.getRecipeByIngredient(this.input.getType()).getResult(), (this.input.getAmount() - inputAmount));
            }
        }
        if (this.input != null) {
            if (inputAmount != 0) {
                this.input.setAmount(inputAmount);
            } else {
                this.input = null;
            }
        }
        if (this.cookTime == this.cookTimeTotal) {
            this.cookTime = 0;
            if (this.input == null) {
                this.cookTimeTotal = 0;
            }
        }
        furnaceInventory.setItem(0, getInput());
        furnaceInventory.setItem(1, getFuel());
        furnaceInventory.setItem(2, getOutput());
        this.resuming = false;
    }

    public void extract(@NotNull Player player, @NotNull FurnaceInventory furnaceInventory) {
        player.giveExp((int) this.experience);
        this.experience = 0;
    }

    public void tick(@NotNull FurnaceInventory furnaceInventory) {
        if (this.isResuming()) {
            return;
        }
        if (this.fuelTime > 0) {
            this.fuelTime--;
            if (canCook()) {
                this.cookTime++;
                if (this.cookTime >= this.cookTimeTotal) {
                    this.cookTime = 0;
                    processCook(furnaceInventory);
                }
            } else {
                this.cookTime = 0;
            }
        } else if (canBurn() && canCook()) {
            processBurn(furnaceInventory);
        } else if (this.cookTime > 0) {
            if (canCook()) {
                this.cookTime -= 5;
            } else {
                this.cookTime = 0;
            }
        }
        updateInventoryView(furnaceInventory);
    }

    private void processBurn(@NotNull FurnaceInventory furnaceInventory) {
        this.locked = true;
        Fuel fuel = FurnaceUtilities.getFuelByMaterial(this.fuel.getType());

        if (this.fuel.getAmount() > 1) {
            this.fuel.setAmount(this.fuel.getAmount() - 1);
        } else {
            this.fuel = null;
        }
        this.fuelTime = fuel.getBurnTime();
        this.fuelTimeTotal = fuel.getBurnTime();
        updateInventory(furnaceInventory);
        this.locked = false;
    }

    private void processCook(@NotNull FurnaceInventory furnaceInventory) {
        this.locked = true;
        VirtualFurnaceRecipe virtualFurnaceRecipe = FurnaceUtilities.getRecipeByIngredient(this.input.getType());
        if (virtualFurnaceRecipe == null) return;
        if (this.output == null) {
            this.output = new ItemStack(virtualFurnaceRecipe.getResult());
        } else {
            this.output.setAmount(this.output.getAmount() + 1);
        }
        if (this.input.getAmount() > 1) {
            this.input.setAmount(this.input.getAmount() - 1);
        } else {
            this.input = null;
            this.cookTime = 0;
            this.cookTimeTotal = 0;
        }
        this.experience += virtualFurnaceRecipe.getExperience();
        updateInventory(furnaceInventory);
        this.locked = false;
    }

    private boolean canCook() {
        if (this.input == null) return false;
        VirtualFurnaceRecipe virtualFurnaceRecipe = FurnaceUtilities.getRecipeByIngredient(this.input.getType());
        if (virtualFurnaceRecipe == null) return false;
        this.cookTimeTotal = virtualFurnaceRecipe.getCookTime();
        if (this.output == null) return true;

        Material outputMaterial = this.output.getType();
        if (outputMaterial == virtualFurnaceRecipe.getResult()) {
            return this.output.getAmount() < outputMaterial.getMaxStackSize();
        }
        return false;
    }

    private boolean canBurn() {
        if (this.fuel == null) return false;
        return FurnaceUtilities.getFuelByMaterial(this.fuel.getType()) != null;
    }

    private void updateInventory(@NotNull FurnaceInventory furnaceInventory) {
        furnaceInventory.setFuel(this.fuel);
        furnaceInventory.setSmelting(this.input);
        furnaceInventory.setResult(this.output);
    }

    private void updateInventoryView(@NotNull FurnaceInventory furnaceInventory) {
        ItemStack input = furnaceInventory.getItem(0);
        if (!Objects.equals(this.input, input)) {
            this.input = input;
        }
        ItemStack fuel = furnaceInventory.getItem(1);
        if (!Objects.equals(this.fuel, fuel)) {
            this.fuel = fuel;
        }
        ItemStack output = furnaceInventory.getItem(2);
        if (!Objects.equals(this.output, output)) {
            this.output = output;
        }
        furnaceInventory.getViewers().forEach(humanEntity -> {
            InventoryView view = humanEntity.getOpenInventory();
            view.setProperty(InventoryView.Property.COOK_TIME, this.cookTime);
            view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_SMELTING, this.cookTimeTotal);
            view.setProperty(InventoryView.Property.BURN_TIME, this.fuelTime);
            view.setProperty(InventoryView.Property.TICKS_FOR_CURRENT_FUEL, this.fuelTimeTotal);
        });
    }

    private int calculateNecessaryFuelTicks(int ticks) {
        int necessaryFuelTicks = this.fuelTime;
        if (this.fuel != null) {
            Fuel fuel = FurnaceUtilities.getFuelByMaterial(this.fuel.getType());

            if (fuel != null && fuel.getBurnTime() != this.fuelTimeTotal) {
                necessaryFuelTicks += fuel.getBurnTime() * this.fuel.getAmount();
                this.fuelTimeTotal = fuel.getBurnTime();
            } else if (this.fuel != null) {
                necessaryFuelTicks += this.fuelTimeTotal * this.fuel.getAmount();
            }
        }
        return necessaryFuelTicks;
    }

    private int calculateFuelAmount(int ticks) {
        int necessaryFuelTicks = calculateNecessaryFuelTicks(ticks);
        int newFuelTicks = Math.max(0, necessaryFuelTicks - ticks);

        return (int) Math.floor((float) newFuelTicks / this.fuelTimeTotal);
    }

    private int calculateFuelTicks(int ticks) {
        int necessaryFuelTicks = calculateNecessaryFuelTicks(ticks);
        int newFuelTicks = Math.max(0, necessaryFuelTicks - ticks);
        return this.fuelTimeTotal == 0 ? 0 : newFuelTicks % this.fuelTimeTotal;
    }

    private int calculateInputAmount(int ticks) {
        if (this.cookTime == 0 && this.cookTimeTotal == 0) {
            return this.input == null ? 0 : this.input.getAmount();
        }

        int necessaryCookTicks = getCookTimeTotal() * this.input.getAmount();
        int cookTicks = ticks + getCookTime();
        int newCookTicks = Math.max(0, necessaryCookTicks - cookTicks);

        return (int) Math.ceil((float) newCookTicks / this.cookTimeTotal);
    }

    private int calculateInputTicks(int ticks) {
        int necessaryCookTicks = getCookTimeTotal() * this.input.getAmount();
        int cookTicks = ticks + getCookTime();
        int newCookTicks = Math.max(0, necessaryCookTicks - cookTicks);

        return this.cookTimeTotal == 0 ? 0 : this.cookTime == 0 ? 0 : this.cookTimeTotal - (newCookTicks % this.cookTimeTotal);
    }

    private int calculateOutputAmount(int ticks) {
        return this.input.getAmount() - calculateInputAmount(ticks);
    }

    public double getCookTimeLeft() {
        int timeLeft = this.cookTimeTotal - this.cookTime;
        return (double) timeLeft / 20;
    }

    public double getFuelTimeLeft() {
        return (double) this.fuelTime / 20;
    }
}