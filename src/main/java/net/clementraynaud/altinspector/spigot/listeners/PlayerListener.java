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

package net.clementraynaud.altinspector.spigot.listeners;

import net.clementraynaud.altinspector.common.AltManager;
import net.clementraynaud.altinspector.common.YamlFile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {

    private final YamlFile data;

    public PlayerListener(YamlFile data) {
        this.data = data;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerId = event.getPlayer().getUniqueId().toString();
        String playerIp = event.getRealAddress().getHostAddress();

        AltManager.savePlayerIp(this.data, playerId, playerIp);
    }

}
