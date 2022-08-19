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

package net.clementraynaud.altinspector;

import net.clementraynaud.altinspector.commands.altinspector.AltinspectorCommand;
import net.clementraynaud.altinspector.listeners.PlayerListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Altinspector extends JavaPlugin {
    private static final int BSTATS_ID = 16034;

    @Override
    public void onEnable() {
        FileConfiguration config = this.getConfig();
        this.getCommand("altinspector").setExecutor(new AltinspectorCommand(config, this));
        new Metrics(this, Altinspector.BSTATS_ID);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this, config), this);
        new Updater(this, this.getFile().getAbsolutePath(), this.getName());
    }
}
