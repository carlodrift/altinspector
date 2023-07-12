/*
 * Copyright 2022 Clément "carlodrift" Raynaud and contributors
 *
 * This file is part of Altinspector.
 *
 * Altinspector is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Altinspector is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Altinspector.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.clementraynaud.altinspector.velocity.commands.altinspector;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.clementraynaud.altinspector.common.Messages;
import net.clementraynaud.altinspector.common.PlayerNameRetriever;
import net.clementraynaud.altinspector.velocity.Altinspector;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AltinspectorCommand implements SimpleCommand, PlayerNameRetriever {

    private final Altinspector plugin;

    public AltinspectorCommand(Altinspector plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name(String playerId) {
        String name = this.plugin.usernames().getString(playerId);
        if (name == null) {
            name = playerId;
        }
        return name;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        String[] args = invocation.arguments();

        if (this.plugin.outdatedVersion()) {
            source.sendMessage(Messages.OUTDATED_VERSION.component());
        }

        if (args.length == 0) {
            source.sendMessage(Messages.NO_PLAYER_SPECIFIED.component());
            return;
        }
        this.plugin.proxy().getScheduler().buildTask(this.plugin, () -> {
            String targetId = args[0];
            try {
                UUID.fromString(targetId);
            } catch (IllegalArgumentException e) {
                for (String playerId : this.plugin.usernames().getKeys(false)) {
                    if (targetId.equalsIgnoreCase(this.plugin.usernames().getString(playerId))) {
                        targetId = playerId;
                        break;
                    }
                }
            }
            source.sendMessage(this.plugin.altManager().searchResultComponent(targetId));
        }).schedule();
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = this.plugin.usernames().getValues(false).values().stream()
                .map(Object::toString)
                .toList();
        if (args.length == 0) {
            return suggestions;
        }
        if (args.length == 1) {
            return suggestions.stream()
                    .filter(name -> name.toLowerCase().contains(args[0].toLowerCase()) || args[0].toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("altinspector");
    }
}
