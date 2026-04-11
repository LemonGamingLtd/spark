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
import me.lucko.spark.api.profiler.ProfilerConfigurationImpl;
import me.lucko.spark.api.profiler.ProfilerInfo;
import me.lucko.spark.api.profiler.ProfilerResult;
import me.lucko.spark.api.profiler.SparkProfiler;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.sampler.Sampler;
import me.lucko.spark.common.sampler.SamplerBuilder;
import me.lucko.spark.common.sampler.SamplerMode;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.util.MediaTypes;
import me.lucko.spark.proto.SparkSamplerProtos;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class SparkProfilerImpl implements SparkProfiler {
    private final SparkPlatform platform;

    public SparkProfilerImpl(SparkPlatform platform) {
        this.platform = platform;
    }

    @Override
    public @NonNull CompletableFuture<Void> start(@NonNull ProfilerConfiguration configuration) {
        return CompletableFuture.supplyAsync(() -> {
            Sampler previousSampler = this.platform.getSamplerContainer().getActiveSampler();
            if (previousSampler != null) {
                previousSampler.stop(true);
                this.platform.getSamplerContainer().unsetActiveSampler(previousSampler);
            }

            SamplerMode mode = configuration.mode() == ProfilerConfiguration.Mode.ALLOCATION
                    ? SamplerMode.ALLOCATION
                    : SamplerMode.EXECUTION;

            double interval = configuration.samplingInterval();
            if (interval <= 0) {
                interval = mode.defaultInterval();
            }

            Set<String> threads = configuration.threads();
            ThreadDumper threadDumper;
            if (configuration.allThreads()) {
                threadDumper = ThreadDumper.ALL;
            } else if (threads == null || threads.isEmpty()) {
                threadDumper = this.platform.getPlugin().getDefaultThreadDumper();
            } else {
                if (configuration.regexThreadMatching()) {
                    threadDumper = new ThreadDumper.Regex(threads);
                } else {
                    threadDumper = new ThreadDumper.Specific(threads);
                }
            }

            Supplier<ThreadGrouper> threadGrouper;
            if (configuration instanceof ProfilerConfigurationImpl) {
                ProfilerConfiguration.ThreadGrouper grouper = ((ProfilerConfigurationImpl) configuration).threadGrouper();
                switch (grouper) {
                    case AS_ONE:
                        threadGrouper = ThreadGrouper.AS_ONE;
                        break;
                    case BY_NAME:
                        threadGrouper = ThreadGrouper.BY_NAME;
                        break;
                    default:
                        threadGrouper = ThreadGrouper.BY_POOL;
                        break;
                }
            } else {
                threadGrouper = ThreadGrouper.BY_POOL;
            }

            int ticksOver = configuration.minimumTickDuration();
            TickHook tickHook = null;
            if (ticksOver != -1) {
                tickHook = this.platform.getTickHook();
                if (tickHook == null) {
                    throw new IllegalStateException("Tick monitoring is not supported on this platform");
                }
            }

            SamplerBuilder builder = new SamplerBuilder();
            builder.mode(mode);
            builder.threadDumper(threadDumper);
            builder.threadGrouper(threadGrouper);

            Duration timeout = configuration.timeout();
            if (timeout != null) {
                builder.completeAfter(timeout.getSeconds(), TimeUnit.SECONDS);
            }

            builder.samplingInterval(interval);
            builder.ignoreSleeping(configuration.ignoreSleeping());
            builder.forceJavaSampler(configuration.forceJavaSampler());
            builder.allocLiveOnly(configuration.allocLiveOnly());

            if (ticksOver != -1 && tickHook != null) {
                builder.ticksOver(ticksOver, tickHook);
            }

            Sampler sampler;
            try {
                sampler = builder.start(this.platform);
            } catch (UnsupportedOperationException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            this.platform.getSamplerContainer().setActiveSampler(sampler);
            sampler.getFuture().whenCompleteAsync((s, throwable) ->
                    this.platform.getSamplerContainer().unsetActiveSampler(s));

            return null;
        }, this.platform.getPlugin()::executeAsync);
    }

    @Override
    public @NonNull CompletableFuture<ProfilerResult> stop() {
        return stop(false, null);
    }

    @Override
    public @NonNull CompletableFuture<ProfilerResult> stop(boolean saveToFile) {
        return stop(saveToFile, null);
    }

    @Override
    public @NonNull CompletableFuture<ProfilerResult> stop(boolean saveToFile, @Nullable String comment) {
        return CompletableFuture.supplyAsync(() -> {
            Sampler sampler = this.platform.getSamplerContainer().getActiveSampler();
            if (sampler == null) {
                throw new IllegalStateException("There is no active profiler");
            }

            this.platform.getSamplerContainer().unsetActiveSampler(sampler);
            sampler.stop(false);

            Sampler.ExportProps exportProps = new Sampler.ExportProps()
                    .comment(comment)
                    .classSourceLookup(() -> ClassSourceLookup.create(this.platform));

            SparkSamplerProtos.SamplerData data = sampler.toProto(this.platform, exportProps);
            byte[] rawData = data.toByteArray();

            if (saveToFile) {
                Path file = this.platform.resolveSaveFile("profile", "sparkprofile");
                try {
                    Files.write(file, rawData);
                    return new ProfilerResultImpl(null, null, file, rawData);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save profile to file", e);
                }
            } else {
                try {
                    String key = this.platform.getBytebinClient()
                            .postContent(data, MediaTypes.SPARK_SAMPLER_MEDIA_TYPE)
                            .key();
                    String url = this.platform.getViewerUrl() + key;
                    return new ProfilerResultImpl(url, key, null, rawData);
                } catch (IOException e) {
                    Path file = this.platform.resolveSaveFile("profile", "sparkprofile");
                    try {
                        Files.write(file, rawData);
                        return new ProfilerResultImpl(null, null, file, rawData);
                    } catch (IOException e2) {
                        throw new RuntimeException("Failed to upload and save profile", e2);
                    }
                }
            }
        }, this.platform.getPlugin()::executeAsync);
    }

    @Override
    public void cancel() {
        Sampler sampler = this.platform.getSamplerContainer().getActiveSampler();
        if (sampler == null) {
            throw new IllegalStateException("There is no active profiler");
        }
        this.platform.getSamplerContainer().stopActiveSampler(true);
    }

    @Override
    public boolean isActive() {
        return this.platform.getSamplerContainer().getActiveSampler() != null;
    }

    @Override
    public @Nullable ProfilerInfo getActiveProfilerInfo() {
        Sampler sampler = this.platform.getSamplerContainer().getActiveSampler();
        if (sampler == null) {
            return null;
        }
        return new ProfilerInfoImpl(sampler);
    }

    @Override
    public @NonNull CompletableFuture<ProfilerResult> snapshot() {
        return CompletableFuture.supplyAsync(() -> {
            Sampler sampler = this.platform.getSamplerContainer().getActiveSampler();
            if (sampler == null) {
                throw new IllegalStateException("There is no active profiler");
            }

            Sampler.ExportProps exportProps = new Sampler.ExportProps()
                    .classSourceLookup(() -> ClassSourceLookup.create(this.platform));

            SparkSamplerProtos.SamplerData data = sampler.toProto(this.platform, exportProps);
            byte[] rawData = data.toByteArray();

            try {
                String key = this.platform.getBytebinClient()
                        .postContent(data, MediaTypes.SPARK_SAMPLER_MEDIA_TYPE, "live")
                        .key();
                String url = this.platform.getViewerUrl() + key;
                return new ProfilerResultImpl(url, key, null, rawData);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile snapshot", e);
            }
        }, this.platform.getPlugin()::executeAsync);
    }
}