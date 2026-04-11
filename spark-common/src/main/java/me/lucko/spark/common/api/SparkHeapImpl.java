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

import me.lucko.spark.api.heap.HeapDumpResult;
import me.lucko.spark.api.heap.HeapSummaryResult;
import me.lucko.spark.api.heap.SparkHeap;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.heapdump.HeapDump;
import me.lucko.spark.common.heapdump.HeapDumpSummary;
import me.lucko.spark.common.util.MediaTypes;
import me.lucko.spark.proto.SparkHeapProtos;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class SparkHeapImpl implements SparkHeap {
    private final SparkPlatform platform;

    public SparkHeapImpl(SparkPlatform platform) {
        this.platform = platform;
    }

    @Override
    public @NonNull CompletableFuture<HeapSummaryResult> createHeapSummary() {
        return createHeapSummary(false, false);
    }

    @Override
    public @NonNull CompletableFuture<HeapSummaryResult> createHeapSummary(boolean runGcBefore, boolean saveToFile) {
        return CompletableFuture.supplyAsync(() -> {
            if (runGcBefore) {
                System.gc();
            }

            HeapDumpSummary heapDump;
            try {
                heapDump = HeapDumpSummary.createNew();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create heap summary", e);
            }

            CommandSender.Data creatorData = new CommandSender.Data("API", null);
            SparkHeapProtos.HeapData output = heapDump.toProto(this.platform, creatorData);
            byte[] rawData = output.toByteArray();

            if (saveToFile) {
                Path file = this.platform.resolveSaveFile("heapsummary", "sparkheap");
                try {
                    Files.write(file, rawData);
                    return new HeapSummaryResultImpl(null, null, file, rawData);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save heap summary to file", e);
                }
            } else {
                try {
                    String key = this.platform.getBytebinClient()
                            .postContent(output, MediaTypes.SPARK_HEAP_MEDIA_TYPE)
                            .key();
                    String url = this.platform.getViewerUrl() + key;
                    return new HeapSummaryResultImpl(url, key, null, rawData);
                } catch (IOException e) {
                    Path file = this.platform.resolveSaveFile("heapsummary", "sparkheap");
                    try {
                        Files.write(file, rawData);
                        return new HeapSummaryResultImpl(null, null, file, rawData);
                    } catch (IOException e2) {
                        throw new RuntimeException("Failed to upload and save heap summary", e2);
                    }
                }
            }
        }, this.platform.getPlugin()::executeAsync);
    }

    @Override
    public @NonNull CompletableFuture<HeapDumpResult> createHeapDump() {
        return createHeapDump(false, true);
    }

    @Override
    public @NonNull CompletableFuture<HeapDumpResult> createHeapDump(boolean runGcBefore, boolean includeLiveOnly) {
        return CompletableFuture.supplyAsync(() -> {
            if (runGcBefore) {
                System.gc();
            }

            Path file = this.platform.resolveSaveFile("heap", HeapDump.isOpenJ9() ? "phd" : "hprof");

            try {
                HeapDump.dumpHeap(file, includeLiveOnly);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create heap dump", e);
            }

            return new HeapDumpResultImpl(file);
        }, this.platform.getPlugin()::executeAsync);
    }
}