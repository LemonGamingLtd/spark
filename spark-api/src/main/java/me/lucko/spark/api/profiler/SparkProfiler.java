/*
 * This file is part of spark, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.spark.api.profiler;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Programmatic access to Spark's profiler functionality.
 */
public interface SparkProfiler {

    /**
     * Starts a new profiler with the given configuration.
     *
     * <p>If a profiler is already active, this will stop the existing profiler first.</p>
     *
     * @param configuration the profiler configuration
     * @return a future that completes when the profiler starts
     * @throws IllegalStateException if the profiler could not be started
     */
    @NonNull CompletableFuture<Void> start(@NonNull ProfilerConfiguration configuration);

    /**
     * Starts a new profiler with default configuration (execution mode, default thread).
     *
     * <p>If a profiler is already active, this will stop the existing profiler first.</p>
     *
     * @return a future that completes when the profiler starts
     * @throws IllegalStateException if the profiler could not be started
     */
    default @NonNull CompletableFuture<Void> start() {
        return start(ProfilerConfiguration.executionDefault());
    }

    /**
     * Stops the active profiler and uploads the results.
     *
     * @return a future that completes with the profiler result when upload finishes
     * @throws IllegalStateException if there is no active profiler
     */
    @NonNull CompletableFuture<ProfilerResult> stop();

    /**
     * Stops the active profiler and optionally saves to file instead of uploading.
     *
     * @param saveToFile if true, saves the profile to a file instead of uploading
     * @return a future that completes with the profiler result
     * @throws IllegalStateException if there is no active profiler
     */
    @NonNull CompletableFuture<ProfilerResult> stop(boolean saveToFile);

    /**
     * Stops the active profiler and optionally saves to file with a comment.
     *
     * @param saveToFile if true, saves the profile to a file instead of uploading
     * @param comment optional comment to include in the profile
     * @return a future that completes with the profiler result
     * @throws IllegalStateException if there is no active profiler
     */
    @NonNull CompletableFuture<ProfilerResult> stop(boolean saveToFile, @Nullable String comment);

    /**
     * Cancels the active profiler without uploading or saving results.
     *
     * @throws IllegalStateException if there is no active profiler
     */
    void cancel();

    /**
     * Checks if a profiler is currently active.
     *
     * @return true if a profiler is active
     */
    boolean isActive();

    /**
     * Gets information about the currently active profiler.
     *
     * @return the profiler info, or null if no profiler is active
     */
    @Nullable ProfilerInfo getActiveProfilerInfo();

    /**
     * Gets the result of the current profiler session without stopping it.
     * This is useful for getting a snapshot of the current profiler state.
     *
     * @return a future that completes with the current profiler result
     * @throws IllegalStateException if there is no active profiler
     */
    @NonNull CompletableFuture<ProfilerResult> snapshot();
}