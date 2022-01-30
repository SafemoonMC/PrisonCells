package gg.mooncraft.minecraft.prisoncells.database;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import me.eduardwayland.mooncraft.waylander.database.queries.Query;
import me.eduardwayland.mooncraft.waylander.database.resultset.ResultSetWrapper;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;
import gg.mooncraft.minecraft.prisoncells.database.objects.VirtualFurnace;
import gg.mooncraft.minecraft.prisoncells.utilities.Serializations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VirtualFurnaceDAO {

    /*
    Constants
     */
    private static final @NotNull String TABLE_NAME = "prisoncells_users_furnaces";

    /*
    Static Methods
     */
    public static @NotNull CompletableFuture<List<VirtualFurnace>> read(@NotNull PrisonUser prisonUser) {
        Query query = Query.single("SELECT * FROM " + TABLE_NAME + " WHERE user_unique_id = ?;")
                .with(prisonUser.getUniqueId().toString())
                .build();
        return PrisonCellsMain.getInstance().getDatabase().getDatabaseManager().executeQuery(query, resultSetIterator -> {
            if (resultSetIterator == null || !resultSetIterator.hasNext()) {
                return new ArrayList<>();
            }
            List<VirtualFurnace> furnaceList = new ArrayList<>();
            resultSetIterator.forEachRemaining(resultSet -> {
                ResultSetWrapper resultSetWrapper = new ResultSetWrapper(resultSet);
                UUID furnaceUniqueId = UUID.fromString(resultSetWrapper.get("unique_id", String.class));
                byte[] fuel = resultSetWrapper.get("fuel", byte[].class);
                byte[] input = resultSetWrapper.get("input", byte[].class);
                byte[] output = resultSetWrapper.get("output", byte[].class);
                int cookTime = resultSetWrapper.get("cook_time", Integer.class);
                int cookTimeTotal = resultSetWrapper.get("cook_time_total", Integer.class);
                int fuelTime = resultSetWrapper.get("fuel_time", Integer.class);
                int fuelTimeTotal = resultSetWrapper.get("fuel_time_total", Integer.class);
                float experience = resultSetWrapper.get("experience", Float.class);
                Timestamp timestamp = resultSetWrapper.get("timestamp", Timestamp.class);

                ItemStack[] fuelItemArray = Serializations.fromByteArray(fuel);
                ItemStack[] inputItemArray = Serializations.fromByteArray(input);
                ItemStack[] outputItemArray = Serializations.fromByteArray(output);
                furnaceList.add(new VirtualFurnace(prisonUser, furnaceUniqueId, fuelItemArray != null ? fuelItemArray[0] : null, inputItemArray != null ? inputItemArray[0] : null, outputItemArray != null ? outputItemArray[0] : null, cookTime, cookTimeTotal, fuelTime, fuelTimeTotal, experience, timestamp));
            });
            return furnaceList;
        });
    }

    public static @NotNull CompletableFuture<VirtualFurnace> create(@NotNull VirtualFurnace virtualFurnace) {
        Query query = Query.single("INSERT INTO " + TABLE_NAME + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
                .with(virtualFurnace.getUniqueId().toString())
                .with(virtualFurnace.getParent().getUniqueId().toString())
                .with(Serializations.toByteArray(virtualFurnace.getFuel()))
                .with(Serializations.toByteArray(virtualFurnace.getInput()))
                .with(Serializations.toByteArray(virtualFurnace.getOutput()))
                .with(virtualFurnace.getCookTime())
                .with(virtualFurnace.getCookTimeTotal())
                .with(virtualFurnace.getFuelTime())
                .with(virtualFurnace.getFuelTimeTotal())
                .with(virtualFurnace.getExperience())
                .with(virtualFurnace.getTimestamp())
                .build();
        return PrisonCellsMain.getInstance().getDatabase().getDatabaseManager().updateQuery(query, u -> virtualFurnace);
    }

    public static @NotNull CompletableFuture<VirtualFurnace> update(@NotNull VirtualFurnace virtualFurnace) {
        Query query = Query.single("UPDATE " + TABLE_NAME + " SET fuel = ?, input = ?, output = ?, cook_time = ?, cook_time_total = ?, fuel_time = ?, fuel_time_total = ?, experience = ?, timestamp = ? WHERE unique_id = ?;")
                .with(Serializations.toByteArray(virtualFurnace.getFuel()))
                .with(Serializations.toByteArray(virtualFurnace.getInput()))
                .with(Serializations.toByteArray(virtualFurnace.getOutput()))
                .with(virtualFurnace.getCookTime())
                .with(virtualFurnace.getCookTimeTotal())
                .with(virtualFurnace.getFuelTime())
                .with(virtualFurnace.getFuelTimeTotal())
                .with(virtualFurnace.getExperience())
                .with(virtualFurnace.getTimestamp())
                .with(virtualFurnace.getUniqueId().toString())
                .build();
        return PrisonCellsMain.getInstance().getDatabase().getDatabaseManager().updateQuery(query, u -> virtualFurnace);
    }
}