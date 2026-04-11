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

import me.lucko.spark.api.profiler.ProfilerConfiguration;
import me.lucko.spark.api.profiler.ProfilerInfo;
import me.lucko.spark.common.sampler.Sampler;
import me.lucko.spark.common.sampler.SamplerMode;
import me.lucko.spark.common.sampler.async.AsyncSampler;
import org.jspecify.annotations.Nullable;

public final class ProfilerInfoImpl implements ProfilerInfo {
    private final long startTime;
    private final long autoEndTime;
    private final boolean isBackground;
    private final ProfilerConfiguration.Mode mode;
    private final String samplerType;

    public ProfilerInfoImpl(Sampler sampler) {
        this.startTime = sampler.getStartTime();
        this.autoEndTime = sampler.getAutoEndTime();
        this.isBackground = sampler.isRunningInBackground();
        this.mode = sampler.getMode() == SamplerMode.ALLOCATION
                ? ProfilerConfiguration.Mode.ALLOCATION
                : ProfilerConfiguration.Mode.EXECUTION;
        this.samplerType = sampler instanceof AsyncSampler ? "async" : "java";
    }

    @Override
    public long startTime() {
        return this.startTime;
    }

    @Override
    public long autoEndTime() {
        return this.autoEndTime;
    }

    @Override
    public boolean isBackground() {
        return this.isBackground;
    }

    @Override
    public ProfilerConfiguration.@Nullable Mode mode() {
        return this.mode;
    }

    @Override
    public @Nullable String samplerType() {
        return this.samplerType;
    }
}