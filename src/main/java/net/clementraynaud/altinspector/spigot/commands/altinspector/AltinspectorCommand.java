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

package net.clementraynaud.altinspector.spigot.commands.altinspector;

import net.clementraynaud.altinspector.common.Messages;
import net.clementraynaud.altinspector.common.PlayerNameRetriever;
import net.clementraynaud.altinspector.spigot.Altinspector;
import net.kyori.adventure.audience.Audience;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AltinspectorCommand implements CommandExecutor, TabCompleter, PlayerNameRetriever {

    private final Altinspector plugin;

    public AltinspectorCommand(Altinspector plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name(String playerId) {
        String name = null;
        try {
            name = this.plugin.getServer().getOfflinePlayer(UUID.fromString(playerId)).getName();
        } catch (IllegalArgumentException ignored) {
        }
        if (name == null) {
            name = playerId;
        }
        return name;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Audience source = this.plugin.adventure().sender(sender);
        if (args.length == 0) {
            source.sendMessage(Messages.NO_PLAYER_SPECIFIED.component());
            return true;
        }
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            String targetId = args[0];
            try {
                UUID.fromString(targetId);
            } catch (IllegalArgumentException e) {
                targetId = this.plugin.getServer().getOfflinePlayer(args[0]).getUniqueId().toString();
            }
            source.sendMessage(this.plugin.altManager().searchResultComponent(targetId));
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(this.plugin.getServer().getOfflinePlayers())
                    .map(OfflinePlayer::getName).filter(Objects::nonNull)
                    .filter(name -> name.toLowerCase().contains(args[0].toLowerCase()) || args[0].toLowerCase().contains(name.toLowerCase()))
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }
}
