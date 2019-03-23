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

package fr.discowzombie.advancedwhitelist.spigot.command;

import fr.discowzombie.advancedwhitelist.common.service.AdvancedWhitelistService;
import fr.discowzombie.advancedwhitelist.common.service.WhitelistGroup;
import fr.discowzombie.advancedwhitelist.common.service.WhitelistService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("NullableProblems")
public class AdvancedWhitelistCommand implements CommandExecutor {

    private static final HashMap<String[], SimpleCommand> subCommands = new HashMap<>();

    static {
        final WhitelistService whitelistService = new AdvancedWhitelistService();

        // group list
        AdvancedWhitelistCommand.registerSubCommand((sender, args) -> {
            final StringBuilder builder = new StringBuilder();
            builder.append(String.format("There are %s groups available: ", whitelistService.getGroups().size()));

            for (WhitelistGroup group : whitelistService.getGroups())
                builder.append(String.format("%s - %s", group.getName(), group.getDescription()));

            sender.sendMessage(builder.toString());
        }, "group", "list");
    }

    private static void registerSubCommand(SimpleCommand command, String... args) {
        AdvancedWhitelistCommand.subCommands.put(args, command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Map.Entry<String[], SimpleCommand>> matchedSubCommands = AdvancedWhitelistCommand.subCommands.entrySet().stream()
                .filter(element -> element.getKey().length <= args.length)
                .filter(element -> {
                    boolean matched = true;

                    int i = 0;
                    for (String arg : element.getKey()) {
                        if (!arg.equalsIgnoreCase(args[i])) {
                            matched = false;
                            break;
                        }

                        i++;
                    }

                    return matched;
                }).collect(Collectors.toList());

        // Display help
        if (matchedSubCommands.isEmpty()) {
            retrieveHelp(sender);
            return true;
        }

        // Match exactly
        if (matchedSubCommands.size() == 1) {
            matchedSubCommands.get(0).getValue().execute(sender, args);
            return true;
        }

        // Multiple choose
        sender.sendMessage("§cAn internal error has occurred, multiples match !");
        return true;
    }

    private void retrieveHelp(CommandSender sender) {
        sender.sendMessage("=+= Help =+=");
    }


}
