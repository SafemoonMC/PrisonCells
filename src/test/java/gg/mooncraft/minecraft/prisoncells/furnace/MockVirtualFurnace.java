package gg.mooncraft.minecraft.prisoncells.furnace;

import lombok.Getter;

import org.bukkit.Material;
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

        System.out.println("Ticks: " + ticks);
        System.out.println("Cook time: " + getCookTime() + " Cook time total: " + getCookTimeTotal());
        System.out.println("Fuel time: " + getFuelTime() + " Fuel time total: " + getFuelTimeTotal());

        if (this.input != null) {
            int newFuel = calculateFuelAmount(ticks);
            int newInput = calculateInputAmount(ticks);
            int newOutput = calculateOutputAmount(ticks);

            int newCookTime = calculateInputTicks(ticks);
            int newFuelTime = calculateFuelTicks(ticks);
            int necessaryFuelTime = calculateNecessaryFuelTicks(ticks);

            int fuelTicksUsed = this.fuelTime - newFuelTime;
            int fuelTicksNeeded = newOutput * this.cookTimeTotal;
            if (fuelTicksNeeded > necessaryFuelTime) {
                int threshold = fuelTicksNeeded - necessaryFuelTime;
                newFuel = 0;
                newInput += (int) Math.ceil((float) threshold / this.cookTimeTotal);
                newOutput -= (int) Math.ceil((float) threshold / this.cookTimeTotal);

                newFuelTime = 0;
                newCookTime = 0;
                System.out.println("Needs more");
            }

            this.cookTime = newCookTime;
            this.fuelTime = newFuelTime;

            if (this.fuel != null) {
                this.fuel.setAmount(newFuel);
            }
            if (this.input != null) {
                this.input.setAmount(newInput);
            }
            if (this.output != null) {
                this.output.setAmount(this.output.getAmount() + newOutput);
            } else {
                this.output = new ItemStack(Material.STONE, newOutput);
            }

            System.out.println("New fuel: " + newFuel);
            System.out.println("New input: " + newInput);
            System.out.println("New output: " + newOutput);
            System.out.println("New cook time: " + cookTime);
            System.out.println("New fuel time: " + fuelTime);
            System.out.println("Fuel: " + this.fuel);
            System.out.println("Input: " + this.input);
            System.out.println("Output: " + this.output);
        }
        this.resuming = false;
    }

    public int calculateNecessaryFuelTicks(int ticks) {
        int necessaryFuelTicks = 0;
        if (this.fuel != null) {
            Fuel fuel = MockFurnaceUtilities.getFuelByMaterial(this.fuel.getType());

            necessaryFuelTicks = this.fuelTime;
            if (fuel != null && fuel.getBurnTime() != this.fuelTimeTotal) {
                necessaryFuelTicks += fuel.getBurnTime() * this.fuel.getAmount();
                this.fuelTimeTotal = fuel.getBurnTime();
            } else {
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
        return newFuelTicks % this.fuelTimeTotal;
    }

    public int calculateInputAmount(int ticks) {
        int necessaryCookTicks = getCookTimeTotal() * this.input.getAmount();
        int cookTicks = ticks + getCookTime();
        int newCookTicks = Math.max(0, necessaryCookTicks - cookTicks);

        return (int) Math.ceil((float) newCookTicks / this.cookTimeTotal);
    }

    public int calculateInputTicks(int ticks) {
        int necessaryCookTicks = getCookTimeTotal() * this.input.getAmount();
        int cookTicks = ticks + getCookTime();
        int newCookTicks = Math.max(0, necessaryCookTicks - cookTicks);
        return this.cookTimeTotal - (newCookTicks % this.cookTimeTotal);
    }

    public int calculateOutputAmount(int ticks) {
        return this.input.getAmount() - calculateInputAmount(ticks);
    }
}