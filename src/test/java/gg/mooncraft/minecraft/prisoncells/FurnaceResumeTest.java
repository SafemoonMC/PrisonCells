package gg.mooncraft.minecraft.prisoncells;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import gg.mooncraft.minecraft.prisoncells.furnace.MockVirtualFurnace;
import gg.mooncraft.minecraft.prisoncells.utilities.MockFurnaceUtilities;

import java.sql.Timestamp;
import java.time.Instant;

public class FurnaceResumeTest {

    /*
    Mocking
     */
    public static ServerMock server;
    static ItemStack fuel;
    static ItemStack input;

    @BeforeAll
    static void start() {
        server = MockBukkit.mock();
        fuel = new ItemStack(Material.COAL, 10);
        input = new ItemStack(Material.IRON_ORE, 10);
        MockFurnaceUtilities.registerAll();
    }

    @AfterAll
    static void stop() {
        MockBukkit.unmock();
    }

    /*
    Testing
     */

    /**
     * Fuel: Coal x10
     * Input: Iron Ore x10
     *
     * Cook time: 100
     * Cook time total: 200
     * Fuel time: 1500
     * Fuel time total: 1600
     *
     * Expected result after 100 ticks:
     * - Cook time = 200
     * - Fuel time = 1400
     * - Fuel = 10
     * - Input = 9
     * - Output = 1
     */
    @Test
    void testEasyOne() {
        System.out.println("Initializing testEasyOne()");
        int ticks = 100;
        Timestamp timestamp = Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - (ticks * 20)));
        MockVirtualFurnace virtualFurnace = new MockVirtualFurnace(
                new ItemStack(Material.COAL, 10),
                new ItemStack(Material.IRON_ORE, 10),
                new ItemStack(Material.IRON_INGOT, 0),
                100,
                200,
                1500,
                1600,
                0, timestamp);

        virtualFurnace.resume();

        assert virtualFurnace.getCookTime() == 200;
        assert virtualFurnace.getFuelTime() == 1400;
        assert virtualFurnace.getFuel().getAmount() == 10;
        assert virtualFurnace.getInput().getAmount() == 9;
        assert virtualFurnace.getOutput().getAmount() == 1;
    }

    /**
     * Fuel: Coal x10
     * Input: Iron Ore x10
     *
     * Cook time: 100
     * Cook time total: 200
     * Fuel time: 1000
     * Fuel time total: 1600
     *
     * Expected result after 1450 ticks:
     * - Cook time = 150
     * - Fuel time = 1150
     * - Fuel = 9
     * - Input = 3
     * - Output = 7
     */
    @Test
    void testEasyTwo() {
        System.out.println("Initializing testEasyTwo()");
        int ticks = 1450;
        Timestamp timestamp = Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - (ticks * 20)));
        MockVirtualFurnace virtualFurnace = new MockVirtualFurnace(
                new ItemStack(Material.COAL, 10),
                new ItemStack(Material.IRON_ORE, 10),
                new ItemStack(Material.IRON_INGOT, 0),
                100,
                200,
                1000,
                1600,
                0,
                timestamp);

        virtualFurnace.resume();

        assert virtualFurnace.getCookTime() == 150;
        assert virtualFurnace.getFuelTime() == 1150;
        assert virtualFurnace.getFuel().getAmount() == 9;
        assert virtualFurnace.getInput().getAmount() == 3;
        assert virtualFurnace.getOutput().getAmount() == 7;
    }

    /**
     * Fuel: Coal x10
     * Input: Iron Ore x10
     *
     * Cook time: 100
     * Cook time total: 200
     * Fuel time: 1000
     * Fuel time total: 1600
     *
     * Expected result after 2000 ticks:
     * - Cook time = 200
     * - Fuel time = 600
     * - Fuel = 9
     * - Input = 0
     * - Output = 10
     */
    @Test
    void testEasyThree() {
        System.out.println("Initializing testEasyThree()");
        int ticks = 2000;
        Timestamp timestamp = Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - (ticks * 20)));
        MockVirtualFurnace virtualFurnace = new MockVirtualFurnace(
                new ItemStack(Material.COAL, 10),
                new ItemStack(Material.IRON_ORE, 10),
                new ItemStack(Material.IRON_INGOT, 0),
                100,
                200,
                1000,
                1600,
                0,
                timestamp);

        virtualFurnace.resume();

        assert virtualFurnace.getCookTime() == 200;
        assert virtualFurnace.getFuelTime() == 600;
        assert virtualFurnace.getFuel().getAmount() == 9;
        assert virtualFurnace.getInput().getAmount() == 0;
        assert virtualFurnace.getOutput().getAmount() == 10;
    }

    /**
     * Fuel: Acacia Wood x2
     * Input: Iron Ore x10
     *
     * Cook time: 0
     * Cook time total: 200
     * Fuel time: 0
     * Fuel time total: 1600
     *
     * Expected result after 2000 ticks:
     * - Cook time = 0
     * - Fuel time = 0
     * - Fuel = 0
     * - Input = 7
     * - Output = 3
     */
    @Test
    void testEasyFour() {
        System.out.println("Initializing testEasyFour()");
        int ticks = 2000;
        Timestamp timestamp = Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - (ticks * 20)));
        MockVirtualFurnace virtualFurnace = new MockVirtualFurnace(
                new ItemStack(Material.ACACIA_WOOD, 2),
                new ItemStack(Material.IRON_ORE, 10),
                new ItemStack(Material.IRON_INGOT, 0),
                0,
                200,
                0,
                1600,
                0,
                timestamp);

        virtualFurnace.resume();

        assert virtualFurnace.getCookTime() == 0;
        assert virtualFurnace.getFuelTime() == 0;
        assert virtualFurnace.getFuel().getAmount() == 0;
        assert virtualFurnace.getInput().getAmount() == 7;
        assert virtualFurnace.getOutput().getAmount() == 3;
    }

    /**
     * Fuel: Acacia Wood x2
     * Input: Iron Ore x10
     *
     * Cook time: 100
     * Cook time total: 200
     * Fuel time: 600
     * Fuel time total: 1600
     *
     * Expected result after 2000 ticks:
     * - Cook time = 0
     * - Fuel time = 0
     * - Fuel = 0
     * - Input = 4
     * - Output = 6
     */
    @Test
    void testEasyFive() {
        System.out.println("Initializing testEasyFive()");
        int ticks = 2000;
        Timestamp timestamp = Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - (ticks * 20)));
        MockVirtualFurnace virtualFurnace = new MockVirtualFurnace(
                new ItemStack(Material.ACACIA_WOOD, 2),
                new ItemStack(Material.IRON_ORE, 10),
                new ItemStack(Material.IRON_INGOT, 0),
                0,
                200,
                600,
                1600,
                0,
                timestamp);

        virtualFurnace.resume();

        assert virtualFurnace.getCookTime() == 0;
        assert virtualFurnace.getFuelTime() == 0;
        assert virtualFurnace.getFuel().getAmount() == 0;
        assert virtualFurnace.getInput().getAmount() == 4;
        assert virtualFurnace.getOutput().getAmount() == 6;
    }
}