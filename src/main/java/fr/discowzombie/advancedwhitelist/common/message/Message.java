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

package fr.discowzombie.advancedwhitelist.common.message;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class Message {

    private static final String UNKNOWN_PATH = "No message found for \"%s\".";
    private final HashMap<String, String> messages;

    public Message(HashMap<String, String> messages) {
        this.messages = messages;
    }

    public Optional<String> getMessageRaw(String path) {
        return this.messages.containsKey(path) ? Optional.of(this.messages.get(path)) : Optional.empty();
    }

    public String getMessageRawSafe(String path) {
        return this.getMessageRaw(path).orElse(UNKNOWN_PATH.replaceFirst("%s", path));
    }

    public Optional<String> getMessage(String path) {
        return this.getMessageRaw(path).map(text -> {
            return text.replace("&", "§");
        });
    }

    public String getMessageSafe(String path) {
        return this.getMessage(path).orElse(UNKNOWN_PATH.replaceFirst("%s", path));
    }

    public Optional<String> getMessage(String path, List<Replacer> variables) {
        return this.getMessage(path).map(text -> {
            variables.forEach(var -> var.apply(text));

            return text;
        });
    }

    public String getMessageSafe(String path, List<Replacer> variables) {
        return this.getMessage(path, variables).orElse(UNKNOWN_PATH.replaceFirst("%s", path));
    }

    public enum MessagePath {
        COMMAND_GROUP_LIST_HEAD("command.group.list.head"),
        COMMAND_GROUP_LIST_LINE("command.group.list.line");

        private final String path;

        MessagePath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

}

