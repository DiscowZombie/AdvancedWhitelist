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

package fr.discowzombie.advancedwhitelist.common.service;

import fr.discowzombie.advancedwhitelist.common.database.DatabaseBackward;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AdvancedWhitelistService implements WhitelistService {

    private static final HashMap<WhitelistGroup, Set<UUID>> groupUserMap = new HashMap<>();
    private static DatabaseBackward databaseBackward;

    public static void awaitReady(DatabaseBackward databaseBackward) {
        Objects.requireNonNull(databaseBackward);

        AdvancedWhitelistService.databaseBackward = databaseBackward;

        Set<WhitelistGroup> groups = AdvancedWhitelistService.databaseBackward.getGroups().join();

        for (Map.Entry<String, UUID> groupsUser : AdvancedWhitelistService.databaseBackward.getRelation().join()) {
            Optional<WhitelistGroup> matchedGroup =
                    groups.stream().filter(g -> g.getName().equalsIgnoreCase(groupsUser.getKey())).findFirst();

            // Should never append - this mean database is incorrect !
            if (!matchedGroup.isPresent()) continue;

            Set<UUID> presentUsers = groupUserMap.getOrDefault(matchedGroup.get(), new HashSet<>());
            presentUsers.add(groupsUser.getValue());

            groupUserMap.put(matchedGroup.get(), presentUsers);
        }
    }

    /**
     * Retrieve a copy of the list containing all the groups
     *
     * @return A copy of the list containing all groups
     */
    @Override
    public Set<WhitelistGroup> getGroups() {
        return new HashSet<>(AdvancedWhitelistService.groupUserMap.keySet());
    }

    /**
     * Retrieve a copy of an optional group matching the name
     *
     * @param name Name of group to match, non case-sensitive
     * @return The group or empty
     */
    @Override
    public Optional<WhitelistGroup> getGroup(String name) {
        return getGroups().stream().filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public CompletableFuture<Void> addGroup(WhitelistGroup group) {
        return AdvancedWhitelistService.databaseBackward.insertGroup(group)
                .thenRun(() -> AdvancedWhitelistService.groupUserMap.put(group, new HashSet<>()));
    }

    @Override
    public CompletableFuture<Void> removeGroup(String groupName) {
        return AdvancedWhitelistService.databaseBackward.deleteGroup(groupName).thenRun(() -> {
            Optional<WhitelistGroup> grp = getGroup(groupName);
            grp.ifPresent(AdvancedWhitelistService.groupUserMap::remove);
        });
    }

    /**
     * Retrieve a copy of the set containing the user's uuid
     *
     * @param groupName The group name to match
     * @return Set of UUIDs
     */
    @Override
    public Set<UUID> getUsersOnGroup(String groupName) {
        Optional<Map.Entry<WhitelistGroup, Set<UUID>>> grp = AdvancedWhitelistService.groupUserMap.entrySet().stream()
                .filter(g -> g.getKey().getName().equals(groupName)).findFirst();

        return grp.isPresent() ? grp.get().getValue() : new HashSet<>();
    }

    /**
     * Return a copy
     *
     * @return A copy of all groups associated with the users
     */
    @Override
    public HashMap<WhitelistGroup, Set<UUID>> getUsersOnGroups() {
        return new HashMap<>(AdvancedWhitelistService.groupUserMap);
    }

    @Override
    public CompletableFuture<Void> addUserToGroup(WhitelistGroup group, UUID uuid) {
        return AdvancedWhitelistService.databaseBackward.insertRelation(group.getName(), uuid).thenRun(() -> {
            Set<UUID> users = AdvancedWhitelistService.groupUserMap.getOrDefault(group, new HashSet<>());
            users.add(uuid);

            AdvancedWhitelistService.groupUserMap.put(group, users);
        });
    }

    @Override
    public CompletableFuture<Void> removeUserFromGroup(WhitelistGroup group, UUID uuid) {
        return AdvancedWhitelistService.databaseBackward.removeRelation(group.getName(), uuid).thenRun(() -> {
            Set<UUID> users = AdvancedWhitelistService.groupUserMap.getOrDefault(group, new HashSet<>());
            users.remove(uuid);

            AdvancedWhitelistService.groupUserMap.put(group, users);
        });
    }
}
