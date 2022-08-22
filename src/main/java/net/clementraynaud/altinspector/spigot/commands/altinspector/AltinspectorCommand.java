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

import net.clementraynaud.altinspector.common.AltManager;
import net.clementraynaud.altinspector.common.Messages;
import net.clementraynaud.altinspector.common.YamlFile;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AltinspectorCommand implements CommandExecutor, TabCompleter {

    private final YamlFile data;
    private final JavaPlugin plugin;

    public AltinspectorCommand(YamlFile data, JavaPlugin plugin) {
        this.data = data;
        this.plugin = plugin;
    }

    private String name(String playerId) {
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
        if (args.length == 0) {
            sender.sendMessage("" + Messages.PREFIX + Messages.MISSING_ARGUMENT);
            return true;
        }
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            String targetId = args[0];
            try {
                UUID.fromString(targetId);
            } catch (IllegalArgumentException e) {
                targetId = this.plugin.getServer().getOfflinePlayer(args[0]).getUniqueId().toString();
            }
            Set<String> names = new HashSet<>();
            AltManager.allAlts(targetId, this.data).forEach(id -> names.add(this.name(id)));
            if (names.isEmpty()) {
                sender.sendMessage(Messages.PREFIX + "No other account found for " + ChatColor.YELLOW + this.name(targetId)
                        + ChatColor.GRAY + "."
                );
            } else {
                sender.sendMessage(Messages.PREFIX + "Other accounts found for " + ChatColor.YELLOW + this.name(targetId)
                        + ChatColor.GRAY + ": " + ChatColor.YELLOW + String.join(ChatColor.GRAY + ", "
                        + ChatColor.YELLOW, names) + ChatColor.GRAY + "."
                );
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(this.plugin.getServer().getOfflinePlayers())
                    .map(OfflinePlayer::getName).filter(Objects::nonNull)
                    .filter(name -> name.toLowerCase().contains(args[0].toLowerCase()) || args[0].toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}