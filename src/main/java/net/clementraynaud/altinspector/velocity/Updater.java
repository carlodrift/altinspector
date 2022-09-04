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

package net.clementraynaud.altinspector.velocity;

import com.velocitypowered.api.scheduler.ScheduledTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Scanner;
import java.util.function.Consumer;

public class Updater {

    private static final long MINUTES_BETWEEN_VERSION_CHECKING = 600L;
    private static final long MINUTES_BEFORE_VERSION_CHECKING = 5L;
    private static final String VERSION_URL = "https://clementraynaud.net/files/%s-latest/version";
    private final Altinspector plugin;
    private final String pluginName;
    private final ScheduledTask task;

    public Updater(Altinspector plugin, String pluginName) {
        this.plugin = plugin;
        this.pluginName = pluginName;
        this.task = this.plugin.proxy().getScheduler()
                .buildTask(this.plugin, this::checkVersion)
                .delay(Duration.ofMinutes(Updater.MINUTES_BEFORE_VERSION_CHECKING))
                .repeat(Duration.ofMinutes(Updater.MINUTES_BETWEEN_VERSION_CHECKING))
                .schedule();
    }

    private void checkVersion() {
        this.getVersion(version -> {
            if (version != null && !this.plugin.version().equals(version)) {
                this.plugin.setOutdatedVersion(true);
                this.task.cancel();
            }
        });
    }

    private void getVersion(final Consumer<String> consumer) {
        this.plugin.proxy().getScheduler().buildTask(this.plugin, () -> {
            try (InputStream inputStream = new URL(String.format(
                    Updater.VERSION_URL,
                    this.pluginName.toLowerCase()))
                    .openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException ignored) {
            }
        }).schedule();
    }
}