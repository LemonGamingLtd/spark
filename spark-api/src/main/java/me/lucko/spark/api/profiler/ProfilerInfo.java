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

import org.jspecify.annotations.Nullable;

/**
 * Information about an active profiler session.
 */
public interface ProfilerInfo {

    /**
     * Gets the Unix timestamp (in milliseconds) when the profiler started.
     *
     * @return the start time
     */
    long startTime();

    /**
     * Gets the Unix timestamp (in milliseconds) when the profiler will automatically stop.
     *
     * @return the auto-end time, or -1 if no timeout is set
     */
    long autoEndTime();

    /**
     * Gets how long the profiler has been running in milliseconds.
     *
     * @return the running time
     */
    default long runningTimeMillis() {
        return System.currentTimeMillis() - startTime();
    }

    /**
     * Gets how long until the profiler will automatically stop in milliseconds.
     *
     * @return the remaining time, or -1 if no timeout is set
     */
    default long remainingTimeMillis() {
        long autoEnd = autoEndTime();
        if (autoEnd == -1) {
            return -1;
        }
        return autoEnd - System.currentTimeMillis();
    }

    /**
     * Whether the profiler is running as a background sampler.
     *
     * @return true if running in background
     */
    boolean isBackground();

    /**
     * Gets the profiling mode.
     *
     * @return the mode
     */
    ProfilerConfiguration.@Nullable Mode mode();

    /**
     * Gets the sampler type being used.
     *
     * @return the sampler type name (e.g., "async" or "java")
     */
    @Nullable String samplerType();
}