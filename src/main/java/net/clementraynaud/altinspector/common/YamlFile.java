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

import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlFile extends YamlConfiguration {

    private final String name;
    private final Path path;
    private File file;

    public YamlFile(String name, Path path) {
        this.name = name;
        this.path = path;
        this.load();
    }

    private void load() {
        try {
            this.path.toFile().mkdirs();
            this.file = this.path.resolve(String.format("%s.yml", this.name)).toFile();
            if (this.file.exists() && !this.file.isFile()) {
                Files.delete(this.file.toPath());
            }
            if (!this.file.createNewFile()) {
                this.load(this.file);
            }
            this.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(final String path, final Object value) {
        super.set(path, value);
        this.save();
    }
}
