/*
 *     AdvancedWhitelist - An advanced whitelist plugin for Sponge and Spigot
 *     Copyright (C) 2019  Mathéo CIMBARO
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.discowzombie.advancedwhitelist.spigot;

import fr.discowzombie.advancedwhitelist.common.database.DatabaseBackward;
import fr.discowzombie.advancedwhitelist.common.database.DatabaseProvider;
import fr.discowzombie.advancedwhitelist.common.database.provider.H2DatabaseProvider;
import fr.discowzombie.advancedwhitelist.common.database.provider.MariaDatabaseProvider;
import fr.discowzombie.advancedwhitelist.common.service.AdvancedWhitelistService;
import fr.discowzombie.advancedwhitelist.spigot.command.AdvancedWhitelistCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings("SpongeLogging")
public class AdvancedWhitelistSpigot extends JavaPlugin {

    private static final List<String> ALLOWED_DRIVER = Arrays.asList("h2", "mysql");
    private java.util.logging.Logger logger = this.getLogger();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.getCommand("advancedwhitelist").setExecutor(new AdvancedWhitelistCommand());

        final Supplier<Optional<DatabaseProvider>> supplier = () -> {
            final FileConfiguration configuration = this.getConfig();
            final String databaseDriver = configuration.getString("database.driver");

            if (databaseDriver == null || !ALLOWED_DRIVER.contains(databaseDriver.toLowerCase())) {
                this.logger.fine(String.format("Invalid driver \"%s\"", databaseDriver));
                return Optional.empty();
            }

            if (databaseDriver.equalsIgnoreCase("h2")) {
                String filePath = configuration.getString("database.file");

                if (filePath == null) {
                    this.logger.fine("File invalid !");
                    return Optional.empty();
                }

                return Optional.of(new H2DatabaseProvider(filePath));
            } else {
                String url = configuration.getString("database.url"),
                        username = configuration.getString("database.username"),
                        password = configuration.getString("database.password"),
                        database = configuration.getString("database.database");
                int port = configuration.getInt("database.port");

                return Optional.of(new MariaDatabaseProvider(url, port, username, password, database));
            }
        };

        CompletableFuture.supplyAsync(supplier).thenAccept(databaseProvider -> {
            if (!databaseProvider.isPresent()) {
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            DatabaseProvider provider = databaseProvider.get();
            provider.initTables().join();

            AdvancedWhitelistService.awaitReady(new DatabaseBackward(provider));

            this.logger.info("Database has been setup successfully!");
        }).exceptionally(exc -> {
            this.logger.fine(String.format("Internal error: %s", exc.getMessage()));
            return null;
        });
    }
}
