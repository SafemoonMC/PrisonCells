package gg.mooncraft.minecraft.prisoncells.database;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import me.eduardwayland.mooncraft.waylander.database.queries.Query;
import me.eduardwayland.mooncraft.waylander.database.resultset.ResultSetWrapper;

import org.jetbrains.annotations.NotNull;

import gg.mooncraft.minecraft.prisoncells.PrisonCellsMain;
import gg.mooncraft.minecraft.prisoncells.database.objects.PrisonUser;
import gg.mooncraft.minecraft.prisoncells.utilities.Serializations;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class PrisonUserDAO {

    /*
    Constants
     */
    private static final @NotNull String TABLE_NAME = "prisoncells_users";

    /*
    Static Methods
     */
    public static @NotNull CompletableFuture<PrisonUser> read(@NotNull UUID uniqueId) {
        Query query = Query.single("SELECT * FROM " + TABLE_NAME + " WHERE unique_id = ?;")
                .with(uniqueId.toString())
                .build();
        return PrisonCellsMain.getInstance().getDatabase().getDatabaseManager().executeQuery(query, resultSetIterator -> {
            if (resultSetIterator == null || !resultSetIterator.hasNext()) {
                return create(new PrisonUser(uniqueId)).join();
            }
            ResultSetWrapper resultSetWrapper = new ResultSetWrapper(resultSetIterator.next());
            byte[] storage = resultSetWrapper.get("storage", byte[].class);
            int storageRows = resultSetWrapper.get("storage_rows", Integer.class);

            return new PrisonUser(uniqueId, Serializations.fromByteArray(storage), storageRows).withChildren().join();
        });
    }

    public static @NotNull CompletableFuture<PrisonUser> create(@NotNull PrisonUser prisonUser) {
        Query query = Query.single("INSERT INTO " + TABLE_NAME + " VALUES(?, ?, ?);")
                .with(prisonUser.getUniqueId().toString())
                .with(Serializations.toByteArray(prisonUser.getStorage()))
                .with(prisonUser.getStorageRows())
                .build();
        return PrisonCellsMain.getInstance().getDatabase().getDatabaseManager().updateQuery(query, u -> prisonUser);
    }

    public static @NotNull CompletableFuture<PrisonUser> update(@NotNull PrisonUser prisonUser) {
        Query query = Query.single("UPDATE " + TABLE_NAME + " SET storage = ?, storage_rows = ? WHERE unique_id = ?;")
                .with(Serializations.toByteArray(prisonUser.getStorage()))
                .with(prisonUser.getStorageRows())
                .with(prisonUser.getUniqueId().toString())
                .build();
        return PrisonCellsMain.getInstance().getDatabase().getDatabaseManager().updateQuery(query, u -> prisonUser);
    }
}