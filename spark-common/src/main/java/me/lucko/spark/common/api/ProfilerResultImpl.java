/*
 * This file is part of spark.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lucko.spark.common.api;

import me.lucko.spark.api.profiler.ProfilerResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

public final class ProfilerResultImpl implements ProfilerResult {
    private final String url;
    private final String key;
    private final Path filePath;
    private final byte[] rawData;

    public ProfilerResultImpl(@Nullable String url, @Nullable String key, @Nullable Path filePath, byte @NonNull [] rawData) {
        this.url = url;
        this.key = key;
        this.filePath = filePath;
        this.rawData = rawData;
    }

    @Override
    public @Nullable String url() {
        return this.url;
    }

    @Override
    public @Nullable String key() {
        return this.key;
    }

    @Override
    public @Nullable Path filePath() {
        return this.filePath;
    }

    @Override
    public byte @NonNull [] rawData() {
        return this.rawData;
    }
}