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

package fr.discowzombie.advancedwhitelist.common.database;

import fr.discowzombie.advancedwhitelist.common.service.WhitelistGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DatabaseBackward {

    private final DatabaseProvider databaseProvider;

    public DatabaseBackward(DatabaseProvider databaseProvider) {
        Objects.requireNonNull(databaseProvider);

        this.databaseProvider = databaseProvider;
    }

    public CompletableFuture<Set<WhitelistGroup>> getGroups() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT name, description FROM `group`");
                ResultSet resultSet = ps.executeQuery();

                final Set<WhitelistGroup> groups = new HashSet<>();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");

                    groups.add(new WhitelistGroup(name, description));
                }

                resultSet.close();
                ps.close();
                conn.close();

                return groups;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> deleteGroup(String groupName) {
        Objects.requireNonNull(groupName);

        return CompletableFuture.runAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = prepare(conn, "DELETE FROM `group` WHERE name = ?", new HashMap<Integer, String>() {{
                    put(1, groupName);
                }});
                ps.execute();

                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> insertGroup(WhitelistGroup group) {
        Objects.requireNonNull(group);

        return CompletableFuture.runAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = prepare(conn, "INSERT INTO `group`(name, description) VALUE (?, ?)", new HashMap<Integer, String>() {{
                    put(1, group.getName());
                    put(2, group.getDescription());
                }});
                ps.execute();

                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> updateGroup(String previousName, WhitelistGroup newGroup) {
        Objects.requireNonNull(previousName);
        Objects.requireNonNull(newGroup);

        return CompletableFuture.runAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = prepare(conn, "UPDATE `group` SET name = ?, description = ? WHERE name = ?", new HashMap<Integer, String>() {{
                    put(1, newGroup.getName());
                    put(2, newGroup.getDescription());
                    put(3, previousName);
                }});
                ps.execute();

                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> deleteUser(UUID uuid) {
        Objects.requireNonNull(uuid);

        return CompletableFuture.runAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = prepare(conn, "DELETE FROM user WHERE uuid = ?", new HashMap<Integer, String>() {{
                    put(1, uuid.toString());
                }});
                ps.execute();

                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> insertUser(UUID uuid) {
        Objects.requireNonNull(uuid);

        return CompletableFuture.runAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = prepare(conn, "INSERT INTO `user`(uuid) VALUE (?)", new HashMap<Integer, String>() {{
                    put(1, uuid.toString());
                }});
                ps.execute();

                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<List<Map.Entry<String, UUID>>> getRelation() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT group_name, user_uuid FROM `group_has_user`");
                ResultSet resultSet = ps.executeQuery();

                final List<Map.Entry<String, UUID>> relation = new ArrayList<>();

                while (resultSet.next()) {
                    String groupName = resultSet.getString("group_name");
                    UUID uuid = UUID.fromString(resultSet.getString("user_uuid"));

                    relation.add(new AbstractMap.SimpleEntry<>(groupName, uuid));
                }

                resultSet.close();
                ps.close();
                conn.close();

                return relation;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> removeRelation(String groupName, UUID uuid) {
        Objects.requireNonNull(groupName);
        Objects.requireNonNull(uuid);

        return CompletableFuture.runAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = prepare(conn, "DELETE FROM group_has_user WHERE group_name = ? AND user_uuid = ?", new HashMap<Integer, String>() {{
                    put(1, groupName);
                    put(2, uuid.toString());
                }});
                ps.execute();

                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> insertRelation(String groupName, UUID uuid) {
        Objects.requireNonNull(groupName);
        Objects.requireNonNull(uuid);

        return CompletableFuture.runAsync(() -> {
            try (Connection conn = this.databaseProvider.getConnection()) {
                PreparedStatement ps = prepare(conn, "INSERT INTO `group_has_user`(group_name, user_uuid) VALUE (?, ?)", new HashMap<Integer, String>() {{
                    put(1, groupName);
                    put(2, uuid.toString());
                }});
                ps.execute();

                ps.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private PreparedStatement prepare(Connection connection, String sqlRequest, HashMap<Integer, String> values) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(sqlRequest);

        for (Map.Entry<Integer, String> integerStringEntry : values.entrySet())
            statement.setString(integerStringEntry.getKey(), integerStringEntry.getValue());

        return statement;
    }

}
