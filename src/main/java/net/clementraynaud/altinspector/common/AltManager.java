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

package net.clementraynaud.altinspector.common;

import com.google.common.hash.Hashing;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AltManager {

    private final YamlFile data;
    private final PlayerNameRetriever playerNameRetriever;

    public AltManager(YamlFile data, PlayerNameRetriever playerNameRetriever) {
        this.data = data;
        this.playerNameRetriever = playerNameRetriever;
    }

    private @NotNull Set<String> directAlts(Set<String> playersId) {
        Set<String> alts = new HashSet<>();
        for (String ip : this.data.getKeys(false)) {
            List<String> playersFromIp = this.data.getStringList(ip);
            if (playersFromIp.stream().anyMatch(playersId::contains)) {
                alts.addAll(playersFromIp);
            }
        }
        return alts;
    }

    private @NotNull Set<String> allAlts(String playerId) {
        Set<String> alts = new HashSet<>(Collections.singleton(playerId));
        while (true) {
            if (!alts.addAll(this.directAlts(alts))) {
                break;
            }
        }
        alts.remove(playerId);
        return alts;
    }

    public void savePlayerIp(String playerId, String playerIp) {
        playerIp = Hashing.sha512().hashString(playerIp, StandardCharsets.UTF_8).toString();
        List<String> playersFromIp = this.data.getStringList(playerIp);
        if (!playersFromIp.contains(playerId)) {
            playersFromIp.add(playerId);
            this.data.set(playerIp, playersFromIp);
        }
    }

    public Component searchResultComponent(String targetId) {
        Set<String> names = new HashSet<>();
        this.allAlts(targetId).forEach(id -> names.add(this.playerNameRetriever.name(id)));
        if (names.isEmpty()) {
            return Messages.NO_ALTS_FOUND.component(this.playerNameRetriever.name(targetId));
        } else {
            return Messages.ALTS_FOUND.component(this.playerNameRetriever.name(targetId)).append(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + String.join("&7" + ", "
                    + "&e", names)).append(Component.text(".", NamedTextColor.GRAY)));
        }
    }
}
