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

package fr.discowzombie.advancedwhitelist.sponge.command;

import fr.discowzombie.advancedwhitelist.common.message.Message;
import fr.discowzombie.advancedwhitelist.common.message.Replacer;
import fr.discowzombie.advancedwhitelist.common.service.AdvancedWhitelistService;
import fr.discowzombie.advancedwhitelist.common.service.WhitelistGroup;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("ALL")
public final class AdvancedWhitelistCommand {

    public static final CommandSpec advancedWhitelist = CommandSpec.builder()
            .child(Group.COMMAND_SPEC, "group")
            .child(Server.COMMAND_SPEC, "server")
            .build();
    private static Message message;

    /*
        advancedwhitelist (awl) :
            group
                list
                add
                remove
                member
                    list
                    add
                    remove
            server
                info
                list
            help (auto-generated)
         */
    private static AdvancedWhitelistService whitelistService = new AdvancedWhitelistService();

    public AdvancedWhitelistCommand(final Message message) {
        this.message = message;
    }

    private static class Group {

        static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                .child(GroupList.COMMAND_SPEC, "list")
                .child(GroupAdd.COMMAND_SPEC, "add")
                .child(GroupRemove.COMMAND_SPEC, "remove")
                .child(GroupMember.COMMAND_SPEC, "member")
                .build();

        private static class GroupList implements CommandExecutor {

            static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                    .executor(new GroupList())
                    .build();

            @Override
            public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
                Text.Builder builder = Text.builder();
                builder.append(Text.of(
                        message.getMessageSafe(Message.MessagePath.COMMAND_GROUP_LIST_HEAD.getPath(), Arrays.asList(
                                new Replacer("%s", String.format("%s", whitelistService.getGroups().size()), 1)
                        ))
                ));

                for (WhitelistGroup group : whitelistService.getGroups()) {
                    LiteralText subText = Text.of(
                            message.getMessageSafe(Message.MessagePath.COMMAND_GROUP_LIST_LINE.getPath(), Arrays.asList(
                                    new Replacer("%s", group.getName(), 1),
                                    new Replacer("%s", group.getDescription(), 1)
                            ))
                    );

                    builder.append(subText);
                }

                src.sendMessage(builder.build());
                return CommandResult.success();
            }
        }

        private static class GroupAdd implements CommandExecutor {

            static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                    .executor(new GroupAdd())
                    .build();

            @Override
            public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
                return CommandResult.success();
            }
        }

        private static class GroupRemove implements CommandExecutor {

            static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                    .executor(new GroupRemove())
                    .build();

            @Override
            public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
                return CommandResult.success();
            }
        }

        private static class GroupMember {

            static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                    .child(GroupMemberList.COMMAND_SPEC, "list")
                    .child(GroupMemberAdd.COMMAND_SPEC, "add")
                    .child(GroupMemberRemove.COMMAND_SPEC, "remove")
                    .build();

            private static class GroupMemberList implements CommandExecutor {

                static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                        .executor(new GroupMemberList())
                        .build();

                @Override
                public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
                    return CommandResult.success();
                }

            }

            private static class GroupMemberAdd implements CommandExecutor {

                static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                        .executor(new GroupMemberAdd())
                        .arguments(
                                GenericArguments.choices(Text.of("group"), new HashMap<String, String>() {{
                                    put("group1", "gr1");
                                    put("group2", "gr2");
                                }}),
                                GenericArguments.uuid(Text.of("player"))
                        )
                        .build();

                @Override
                public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
                    return CommandResult.success();
                }

            }

            private static class GroupMemberRemove implements CommandExecutor {

                static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                        .executor(new GroupMemberRemove())
                        .build();

                @Override
                public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
                    return CommandResult.success();
                }

            }

        }

    }

    private static class Server {

        static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                .child(ServerInfo.COMMAND_SPEC, "info")
                .child(ServerList.COMMAND_SPEC, "list")
                .build();

        private static class ServerInfo implements CommandExecutor {

            static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                    .executor(new ServerInfo())
                    .build();

            @Override
            public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
                return CommandResult.success();
            }
        }

        private static class ServerList implements CommandExecutor {

            static final CommandSpec COMMAND_SPEC = CommandSpec.builder()
                    .executor(new ServerList())
                    .build();

            @Override
            public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
                return CommandResult.success();
            }
        }
    }
}
