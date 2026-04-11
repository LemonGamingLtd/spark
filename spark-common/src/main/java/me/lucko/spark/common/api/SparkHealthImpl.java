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

import me.lucko.spark.api.health.HealthReportResult;
import me.lucko.spark.api.health.SparkHealth;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.platform.SparkMetadata;
import me.lucko.spark.common.sampler.Sampler;
import me.lucko.spark.common.util.MediaTypes;
import me.lucko.spark.proto.SparkProtos;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class SparkHealthImpl implements SparkHealth {
    private final SparkPlatform platform;

    public SparkHealthImpl(SparkPlatform platform) {
        this.platform = platform;
    }

    @Override
    public @NonNull CompletableFuture<HealthReportResult> createHealthReport() {
        return CompletableFuture.supplyAsync(() -> {
            SparkProtos.HealthMetadata.Builder metadata = SparkProtos.HealthMetadata.newBuilder();

            CommandSender.Data creatorData = new CommandSender.Data("API", null);
            SparkMetadata.gather(this.platform, creatorData, this.platform.getStartupGcStatistics())
                    .writeTo(metadata);

            SparkProtos.HealthData.Builder data = SparkProtos.HealthData.newBuilder()
                    .setMetadata(metadata);

            Sampler activeSampler = this.platform.getSamplerContainer().getActiveSampler();
            if (activeSampler != null) {
                data.putAllTimeWindowStatistics(activeSampler.exportWindowStatistics());
            }

            SparkProtos.HealthData builtData = data.build();
            byte[] rawData = builtData.toByteArray();

            try {
                String key = this.platform.getBytebinClient()
                        .postContent(builtData, MediaTypes.SPARK_HEALTH_MEDIA_TYPE)
                        .key();
                String url = this.platform.getViewerUrl() + key;
                return new HealthReportResultImpl(url, key, rawData);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload health report", e);
            }
        }, this.platform.getPlugin()::executeAsync);
    }
}