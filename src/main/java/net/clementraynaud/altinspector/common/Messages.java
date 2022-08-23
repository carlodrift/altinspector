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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public enum Messages {

    PREFIX(Component.text("", NamedTextColor.GRAY)
            .append(Component.text("Altinspector", NamedTextColor.LIGHT_PURPLE))
            .append(Component.text(" • ", NamedTextColor.DARK_GRAY))),
    NO_PLAYER_SPECIFIED(Component.text("Specify a player name or UUID.", NamedTextColor.RED)),
    NO_ALTS_FOUND(Component.text("No other account found for ")
            .append(Component.text("%s", NamedTextColor.YELLOW))
            .append(Component.text(".", NamedTextColor.GRAY))),

    ALTS_FOUND(Component.text("Other accounts found for ")
            .append(Component.text("%s", NamedTextColor.YELLOW))
            .append(Component.text(": ", NamedTextColor.GRAY)));


    private final Component message;

    Messages(Component message) {
        this.message = message;
    }

    public Component component(String... args) {
        return Messages.PREFIX.message.append(GsonComponentSerializer.gson().deserialize(String.format(GsonComponentSerializer.gson().serialize(
                        this.message),
                args
        )));
    }
}
