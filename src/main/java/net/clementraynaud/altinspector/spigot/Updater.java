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

package net.clementraynaud.altinspector.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.function.Consumer;

public class Updater {

    private static final long TICKS_BETWEEN_VERSION_CHECKING = 720000L;
    private static final long TICKS_BEFORE_VERSION_CHECKING = 6000L;
    private static final String VERSION_URL = "https://clementraynaud.net/files/%s-latest/version";
    private static final String AUTO_DOWNLOAD_URL = "https://clementraynaud.net/files/%s-latest/%s.jar";
    private static final String MANUAL_DOWNLOAD_URL = "https://www.spigotmc.org/resources/altinspector-beyond-ip-check.104673/";
    private final JavaPlugin plugin;
    private final String pluginPath;
    private final String pluginName;
    private String downloadedVersion;

    public Updater(JavaPlugin plugin, String pluginPath, String pluginName) {
        this.plugin = plugin;
        this.pluginPath = pluginPath;
        this.pluginName = pluginName;
        this.plugin.getServer().getScheduler().runTaskTimer(
                this.plugin,
                this::checkVersion,
                Updater.TICKS_BEFORE_VERSION_CHECKING,
                Updater.TICKS_BETWEEN_VERSION_CHECKING
        );
    }

    private void checkVersion() {
        this.getVersion(version -> {
            if (version != null && !this.plugin.getDescription().getVersion().equals(version) && !version.equals(this.downloadedVersion)) {
                this.update(version);
            }
        });
    }

    private void getVersion(final Consumer<String> consumer) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL(String.format(
                    Updater.VERSION_URL,
                    this.pluginName.toLowerCase()))
                    .openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException ignored) {
            }
        });
    }

    private void update(String version) {
        File update = new File(this.plugin.getServer().getUpdateFolderFile().getAbsolutePath() + File.separator
                + this.pluginPath.substring(this.pluginPath.lastIndexOf(File.separator) + 1));

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.getServer().getUpdateFolderFile().mkdirs();

            try (FileOutputStream outputStream = new FileOutputStream(update)) {
                outputStream.getChannel()
                        .transferFrom(Channels.newChannel(new URL(String.format(
                                Updater.AUTO_DOWNLOAD_URL,
                                this.pluginName.toLowerCase(), this.pluginName))
                                .openStream()), 0, Long.MAX_VALUE);
                this.downloadedVersion = version;
                this.plugin.getLogger().info(String.format(
                        "%s has been updated to the latest version. Please restart your Minecraft server to apply changes.",
                        this.pluginName));

            } catch (IOException e) {
                this.plugin.getLogger().warning(String.format(
                        "You are using an outdated version (%s). Download the latest version (%s) here: %s.",
                        this.plugin.getDescription().getVersion(), version, Updater.MANUAL_DOWNLOAD_URL));
                try {
                    Files.delete(update.getAbsoluteFile().toPath());
                } catch (IOException ignored) {
                }
            }
        });
    }
}