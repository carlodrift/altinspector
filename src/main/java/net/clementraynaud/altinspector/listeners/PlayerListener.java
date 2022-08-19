/*
 * Copyright 2020, 2021, 2022 Clément "carlodrift" Raynaud and contributors
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

package net.clementraynaud.altinspector.listeners;

import com.google.common.hash.Hashing;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class PlayerListener implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public PlayerListener(JavaPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String ip = Hashing.sha512().hashString(event.getRealAddress().getHostAddress(), StandardCharsets.UTF_8).toString();
        List<String> list = this.config.getStringList(ip);
        if (!list.contains(event.getPlayer().getUniqueId().toString())) {
            list.add(event.getPlayer().getUniqueId().toString());
            this.config.set(ip, list);
            this.plugin.saveConfig();
        }
    }

}
