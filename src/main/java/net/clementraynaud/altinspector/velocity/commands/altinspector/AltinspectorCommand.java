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
import com.velocitypowered.api.proxy.ProxyServer;
import net.clementraynaud.altinspector.common.AltManager;
import net.clementraynaud.altinspector.common.Messages;
import net.clementraynaud.altinspector.common.YamlFile;
import net.clementraynaud.altinspector.velocity.Altinspector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AltinspectorCommand implements SimpleCommand {

    private final ProxyServer proxy;
    private final Altinspector plugin;
    private final YamlFile data;
    private final YamlFile usernames;

    public AltinspectorCommand(ProxyServer proxy, Altinspector plugin, YamlFile data, YamlFile usernames) {
        this.proxy = proxy;
        this.plugin = plugin;
        this.data = data;
        this.usernames = usernames;
    }

    private String name(String playerId) {
        String name = this.usernames.getString(playerId);
        if (name == null) {
            name = playerId;
        }
        return name;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        String[] args = invocation.arguments();

        if (args.length == 0) {
            source.sendMessage(Messages.NO_PLAYER_SPECIFIED.component());
            return;
        }
        this.proxy.getScheduler().buildTask(this.plugin, () -> {
            String targetId = args[0];
            try {
                UUID.fromString(targetId);
            } catch (IllegalArgumentException e) {
                for (String playerId : this.usernames.getKeys(false)) {
                    if (targetId.equalsIgnoreCase(this.usernames.getString(playerId))) {
                        targetId = playerId;
                        break;
                    }
                }
            }
            Set<String> names = new HashSet<>();
            AltManager.allAlts(targetId, this.data).forEach(id -> names.add(this.name(id)));
            if (names.isEmpty()) {
                source.sendMessage(Messages.NO_ALTS_FOUND.component(this.name(targetId)));
            } else {
                source.sendMessage(Messages.ALTS_FOUND.component(this.name(targetId)).append(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + String.join("&7" + ", "
                        + "&e", names)).append(Component.text(".", NamedTextColor.GRAY))));
            }
        }).schedule();
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = this.usernames.getValues(false).values().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        if (args.length == 0) {
            return suggestions;
        }
        if (args.length == 1) {
            return suggestions.stream()
                    .filter(name -> name.toLowerCase().contains(args[0].toLowerCase()) || args[0].toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("altinspector");
    }
}
