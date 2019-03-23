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

package fr.discowzombie.advancedwhitelist.common.database.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.discowzombie.advancedwhitelist.common.database.DatabaseProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MariaDatabaseProvider implements DatabaseProvider {

    private final HikariDataSource dataSource;

    public MariaDatabaseProvider(String ip, int port, String username, String password, String database) {
        Objects.requireNonNull(ip);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(database);

        this.dataSource = initPool(ip, port, username, password, database);
    }

    private HikariDataSource initPool(String ip, int port, String username, String password, String database) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", ip, port, database));
        config.setUsername(username);
        config.setPassword(password);
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }

    public CompletableFuture<Void> initTables() {
        return CompletableFuture.runAsync(() -> {
            try (final Connection conn = getConnection()) {
                final String[] requests = new String[]{
                        "CREATE TABLE IF NOT EXISTS `advancedwhitelist`.`group`(`name` VARCHAR(22)  NOT NULL, `description` VARCHAR(255) NOT NULL DEFAULT ' ', PRIMARY KEY (`name`), UNIQUE INDEX `name_UNIQUE` (`name` ASC))",
                        "CREATE TABLE IF NOT EXISTS `advancedwhitelist`.`group_has_user`(`group_name` VARCHAR(22) NOT NULL, `user_uuid`  VARCHAR(255) NOT NULL, PRIMARY KEY (`user_uuid`, `group_name`), INDEX `fk_group_has_user_group_idx` (`group_name` ASC), CONSTRAINT `fk_group_has_user_group` FOREIGN KEY (`group_name`) REFERENCES `advancedwhitelist`.`group` (`name`) ON DELETE NO ACTION ON UPDATE NO ACTION)"
                };

                for (String request : requests) {
                    final PreparedStatement statement = conn.prepareStatement(request);
                    statement.execute();
                    statement.close();
                }

                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

}
