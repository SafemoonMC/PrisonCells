package gg.mooncraft.minecraft.prisoncells;

import lombok.Getter;

import me.eduardwayland.mooncraft.waylander.command.LiteralCommand;
import me.eduardwayland.mooncraft.waylander.command.wrapper.Brigadier;
import me.eduardwayland.mooncraft.waylander.database.Credentials;
import me.eduardwayland.mooncraft.waylander.database.Database;
import me.eduardwayland.mooncraft.waylander.database.connection.hikari.impl.MariaDBConnectionFactory;
import me.eduardwayland.mooncraft.waylander.database.scheme.db.NormalDatabaseScheme;
import me.eduardwayland.mooncraft.waylander.database.scheme.file.NormalSchemeFile;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gg.mooncraft.minecraft.prisoncells.scheduler.BukkitScheduler;
import gg.mooncraft.minecraft.prisoncells.utilities.BukkitDatabaseUtilities;
import gg.mooncraft.minecraft.prisoncells.utilities.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Getter
public class PrisonCellsMain extends JavaPlugin {

    /*
    Fields
     */
    private @Nullable Brigadier brigadier;
    private @Nullable Database database;
    private final @NotNull BukkitScheduler scheduler;

    /*
    Constructor
     */
    public PrisonCellsMain() {
        super();
        // Load configuration
        saveDefaultConfig();

        // Load WaylanderScheduler
        this.scheduler = new BukkitScheduler(this);
    }

    /*
    Override Methods
     */
    @Override
    public void onLoad() {
        try {
            ConfigurationSection mysqlSection = getConfig().getConfigurationSection("mysql");
            if (mysqlSection != null) {
                this.database = createDatabase(BukkitDatabaseUtilities.fromBukkitConfig(mysqlSection));
            } else {
                throw new IllegalStateException("The config.yml doesn't contain mysql section.");
            }
        } catch (Exception e) {
            setEnabled(false);
            getLogger().severe("The plugin cannot be loaded. Connection to the database cannot be created. Error: ");
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    /*
    Methods
     */

    /**
     * It closes all tasks of any kind, including Database
     */
    protected void shutdown() {
        // Close the database
        if (this.database != null) this.database.shutdown();

        // Close the scheduler
        this.scheduler.shutdownExecutor();
        this.scheduler.shutdownScheduler();
    }

    /**
     * Registers the command into Brigadier instance
     *
     * @param literalCommand the command to register
     */
    protected void registerCommand(@NotNull LiteralCommand<?> literalCommand) {
        // Load Brigadier instance if not loaded yet
        if (getBrigadier() == null) {
            try {
                this.brigadier = new Brigadier(this);
            } catch (Exception e) {
                shutdown();
                getLogger().severe("The plugin cannot work properly without Brigadier.");
                return;
            }
        }
        getBrigadier().getBrigadierCommandWrapper().register(literalCommand);
    }

    /**
     * Creates a Database instance
     */
    public @NotNull Database createDatabase(@NotNull Credentials credentials) throws Exception {
        // Load input stream
        InputStream inputStream = getResource("prisoncells-db.scheme");
        if (inputStream == null) {
            throw new IllegalStateException("prisoncells-db.scheme is not inside the jar.");
        }

        // Create temporary file
        File temporaryFile = new File(getDataFolder(), "prisoncells-db.scheme");
        if (!temporaryFile.exists() && !temporaryFile.createNewFile()) {
            throw new IllegalStateException("The temporary file prisoncells-db.scheme cannot be created.");
        }

        // Load output stream
        FileOutputStream outputStream = new FileOutputStream(temporaryFile);

        // Copy input to output
        IOUtils.copy(inputStream, outputStream);

        // Close streams
        inputStream.close();
        outputStream.close();

        // Parse database scheme and delete temporary file
        NormalDatabaseScheme normalDatabaseScheme = new NormalSchemeFile(temporaryFile).parse();
        if (!temporaryFile.delete()) {
            getLogger().warning("The temporary file prisoncells-db.scheme cannot be deleted. You could ignore this warning!");
        }
        return Database.builder()
                .identifier(getName())
                .scheduler(getScheduler())
                .databaseScheme(normalDatabaseScheme)
                .connectionFactory(new MariaDBConnectionFactory(getName(), credentials))
                .build();
    }
}