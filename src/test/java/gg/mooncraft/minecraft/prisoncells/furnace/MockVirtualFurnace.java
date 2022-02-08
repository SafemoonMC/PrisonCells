package gg.mooncraft.minecraft.prisoncells.furnace;

import lombok.Getter;

import org.bukkit.inventory.ItemStack;

import gg.mooncraft.minecraft.prisoncells.utilities.MockFurnaceUtilities;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Getter
public final class MockVirtualFurnace {

    /*
    Fields
     */
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
    public MockVirtualFurnace(ItemStack fuel, ItemStack input, ItemStack output, int cookTime, int cookTimeTotal, int fuelTime, int fuelTimeTotal, float experience, Timestamp timestamp) {
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
    public void resume() {
        this.resuming = true;
        int ticks = (int) (Duration.between(timestamp.toInstant(), Instant.now()).toMillis() / 20);

        // Convert fuel in ticks
        int newTotalFuel = 0;
        int oldTotalFuel = this.fuelTime + (this.fuel != null ? this.fuel.getAmount() * MockFurnaceUtilities.getFuelByMaterial(this.fuel.getType()).getBurnTime() : 0);

        // Get the considerable ticks
        int realTicks = Math.min(ticks, oldTotalFuel);

        // Convert input in ticks
        int newTotalInput = 0;
        int oldTotalInput = (this.input != null ? this.input.getAmount() * MockFurnaceUtilities.getRecipeByIngredient(this.input.getType()).getCookTime() : 0) - this.cookTime;

        if (oldTotalInput <= realTicks) {
            realTicks = oldTotalInput;
        }

        int cookTime = this.input != null ? MockFurnaceUtilities.getRecipeByIngredient(this.input.getType()).getCookTime() : 0;
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
            this.fuelTimeTotal = this.fuel != null ? MockFurnaceUtilities.getFuelByMaterial(this.fuel.getType()).getBurnTime() : 0;
            this.fuelTime = this.fuelTimeTotal != 0 ? newTotalFuel % this.fuelTimeTotal : 0;
            fuelAmount = newTotalFuel / this.fuelTimeTotal;
        }
        newTotalInput = oldTotalInput - realTicks;
        this.cookTimeTotal = this.input != null ? MockFurnaceUtilities.getRecipeByIngredient(this.input.getType()).getCookTime() : 0;
        this.cookTime = this.cookTimeTotal != 0 ? this.cookTimeTotal - (newTotalInput % this.cookTimeTotal) : 0;
        int inputAmount = (int) Math.ceil((double) newTotalInput / this.cookTimeTotal);// - (cookTimeTotal == cookTime ? 1 : 0);

        System.out.println("Double: " + ((double) newTotalInput / this.cookTimeTotal));
        System.out.println("FuelAmount: " + fuelAmount);
        System.out.println("FuelTime: " + this.fuelTime);
        System.out.println("FuelTimeTotal: " + this.fuelTimeTotal);
        System.out.println("CookAmount: " + inputAmount);
        System.out.println("CookTime: " + this.cookTime);
        System.out.println("CookTimeTotal: " + this.cookTimeTotal);

        if (this.fuel != null) {
            this.fuel.setAmount(fuelAmount);
        }
        if (this.output != null) {
            this.output.setAmount(this.output.getAmount() + (this.input.getAmount() - inputAmount));
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
        this.resuming = false;
    }

    public int calculateNecessaryFuelTicks(int ticks) {
        int necessaryFuelTicks = this.fuelTime;
        if (this.fuel != null) {
            Fuel fuel = MockFurnaceUtilities.getFuelByMaterial(this.fuel.getType());

            if (fuel != null && fuel.getBurnTime() != this.fuelTimeTotal) {
                necessaryFuelTicks += fuel.getBurnTime() * this.fuel.getAmount();
                this.fuelTimeTotal = fuel.getBurnTime();
            } else if (this.fuel != null) {
                necessaryFuelTicks += this.fuelTimeTotal * this.fuel.getAmount();
            }
        }
        return necessaryFuelTicks;
    }

    public int calculateFuelAmount(int ticks) {
        int necessaryFuelTicks = calculateNecessaryFuelTicks(ticks);
        int newFuelTicks = Math.max(0, necessaryFuelTicks - ticks);

        return (int) Math.floor((float) newFuelTicks / this.fuelTimeTotal);
    }

    public int calculateFuelTicks(int ticks) {
        int necessaryFuelTicks = calculateNecessaryFuelTicks(ticks);
        System.out.println("Necessary Fuel: " + necessaryFuelTicks);
        System.out.println("Fuel ticks: " + ticks);
        int newFuelTicks = Math.max(0, necessaryFuelTicks - ticks);
        return this.fuelTimeTotal == 0 ? 0 : newFuelTicks % this.fuelTimeTotal;
    }

    public int calculateInputAmount(int ticks) {
        if (this.cookTime == 0 || this.cookTimeTotal == 0) {
            return this.input == null ? 0 : this.input.getAmount();
        }

        int necessaryCookTicks = getCookTimeTotal() * this.input.getAmount();
        int cookTicks = ticks + getCookTime();
        int newCookTicks = Math.max(0, necessaryCookTicks - cookTicks);

        return (int) Math.ceil((float) newCookTicks / this.cookTimeTotal);
    }

    public int calculateInputTicks(int ticks) {
        int necessaryCookTicks = getCookTimeTotal() * this.input.getAmount();
        int cookTicks = ticks + getCookTime();
        int newCookTicks = Math.max(0, necessaryCookTicks - cookTicks);

        System.out.println("Necessary Cook Ticks: " + necessaryCookTicks);
        System.out.println("Cook Ticks: " + cookTicks);
        System.out.println("New Cook Ticks: " + newCookTicks);
        System.out.println("Result: " + (this.cookTimeTotal == 0 ? 0 : this.cookTime == 0 ? 0 : this.cookTimeTotal - (newCookTicks % this.cookTimeTotal)));
        return this.cookTimeTotal == 0 ? 0 : this.cookTime == 0 ? 0 : this.cookTimeTotal - (newCookTicks % this.cookTimeTotal);
    }

    public int calculateOutputAmount(int ticks) {
        return this.input.getAmount() - calculateInputAmount(ticks);
    }
}