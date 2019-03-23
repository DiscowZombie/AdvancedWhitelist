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

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface WhitelistService {

    Set<WhitelistGroup> getGroups();

    Optional<WhitelistGroup> getGroup(String name);

    CompletableFuture<Void> addGroup(WhitelistGroup group);

    CompletableFuture<Void> removeGroup(String groupName);

    Set<UUID> getUsersOnGroup(String groupName);

    HashMap<WhitelistGroup, Set<UUID>> getUsersOnGroups();

    CompletableFuture<Void> addUserToGroup(WhitelistGroup group, UUID uuid);

    CompletableFuture<Void> removeUserFromGroup(WhitelistGroup group, UUID uuid);

}
