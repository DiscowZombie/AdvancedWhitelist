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

package fr.discowzombie.advancedwhitelist.sponge;

import com.google.inject.Inject;
import fr.discowzombie.advancedwhitelist.common.service.AdvancedWhitelistService;
import fr.discowzombie.advancedwhitelist.common.service.WhitelistService;
import fr.discowzombie.advancedwhitelist.sponge.command.AdvancedWhitelistCommand;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "advancedwhitelist",
        name = "AdvancedWhitelist",
        description = "An advanced whitelist plugin",
        authors = {
                "DiscowZombie"
        }
)
public class AdvancedWhitelistSponge {

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> mainConfig;

    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
        Sponge.getServiceManager().setProvider(this, WhitelistService.class, new AdvancedWhitelistService());
    }

    @Listener
    public void onServerAboutToStart(GameAboutToStartServerEvent event) throws IOException {
        final Path potentialFile = new File("./config/advancedwhitelist/config.yml").toPath();
        @SuppressWarnings("OptionalGetWithoutIsPresent") final YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder()
                .setURL(Sponge.getAssetManager().getAsset("assets/advancedwhitelist/config.yml").get().getUrl())
                .setPath(potentialFile)
                .build();
        final ConfigurationNode node = loader.createEmptyNode(ConfigurationOptions.defaults());
        loader.save(node);


        // Load configuration and database
        /* CompletableFuture.supplyAsync(() -> {
            try {
                ConfigurationNode rootNode = configManager.load();

                if (!rootNode.hasMapChildren()) {
                    this.logger.warn("Configuration file doesn't exist, creating-it");

                    @SuppressWarnings("OptionalGetWithoutIsPresent")
                    URL jarConfigFile = Sponge.getAssetManager().getAsset("config.conf").get().getUrl();
                    ConfigurationLoader loader = HoconConfigurationLoader.builder().setURL(jarConfigFile).build();
                    rootNode = loader.load();
                }

                return rootNode;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(node -> {
            final DatabaseProvider provider = new MariaDatabaseProvider("localhost", 3306, "root", "root", "advancedwhitelist");
            provider.initTables().join();

            AdvancedWhitelistService.awaitReady(new DatabaseBackward(provider));
            this.logger.info("Database has been setup successfully!");
        }); */
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Sponge.getCommandManager().register(this, AdvancedWhitelistCommand.advancedWhitelist, "advancedwhitelist", "awl");
    }
}
