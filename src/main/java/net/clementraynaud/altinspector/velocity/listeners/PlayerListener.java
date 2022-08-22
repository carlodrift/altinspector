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

package net.clementraynaud.altinspector.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import net.clementraynaud.altinspector.common.AltManager;
import net.clementraynaud.altinspector.common.YamlFile;

public class PlayerListener {

    private final YamlFile data;
    private final YamlFile usernames;

    public PlayerListener(YamlFile data, YamlFile usernames) {
        this.data = data;
        this.usernames = usernames;
    }

    @Subscribe
    public void onGameProfileRequest(GameProfileRequestEvent event) {
        String playerId = event.getGameProfile().getId().toString();
        String playerName = event.getGameProfile().getName();
        String playerIp = event.getConnection().getRemoteAddress().getAddress().getHostAddress();

        AltManager.savePlayerIp(this.data, playerId, playerIp);

        this.usernames.set(playerId, playerName);
    }

}
