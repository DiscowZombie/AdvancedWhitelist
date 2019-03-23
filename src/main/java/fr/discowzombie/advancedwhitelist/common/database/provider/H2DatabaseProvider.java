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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class H2DatabaseProvider implements DatabaseProvider {

    private final HikariDataSource dataSource;

    public H2DatabaseProvider(String fileName) {
        Objects.requireNonNull(fileName);

        this.dataSource = initPool(fileName);
    }

    /**
     * @param fileName Name of the file including the extension and full path (./, ~/home)
     */
    private HikariDataSource initPool(String fileName) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:h2:%s;mode=mysql", fileName));
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }

    public CompletableFuture<Void> initTables() {
        return CompletableFuture.runAsync(() -> {
            try (final Connection conn = getConnection()) {
                final StringBuilder stringBuilder = new StringBuilder();
                String s;

                final FileReader fileReader = new FileReader("");
                final BufferedReader br = new BufferedReader(fileReader);

                while ((s = br.readLine()) != null)
                    stringBuilder.append(s);
                br.close();
                fileReader.close();

                final String[] requests = stringBuilder.toString().split(";");

                for (String request : requests) {
                    System.out.println(request);
                    final PreparedStatement statement = conn.prepareStatement(request);
                    statement.execute();
                    statement.close();
                }

                conn.close();
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
}
