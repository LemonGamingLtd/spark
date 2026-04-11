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

import java.time.Duration;
import java.util.Set;

/**
 * Configuration for starting a profiler session.
 */
public interface ProfilerConfiguration {

    /**
     * Creates a new builder for profiler configuration.
     *
     * @return a new builder
     */
    static @NonNull Builder builder() {
        return new ProfilerConfigurationImpl.BuilderImpl();
    }

    /**
     * Creates a default configuration for execution profiling.
     *
     * @return default execution profiler configuration
     */
    static @NonNull ProfilerConfiguration executionDefault() {
        return builder().mode(Mode.EXECUTION).build();
    }

    /**
     * Creates a default configuration for allocation profiling.
     *
     * @return default allocation profiler configuration
     */
    static @NonNull ProfilerConfiguration allocationDefault() {
        return builder().mode(Mode.ALLOCATION).build();
    }

    /**
     * Gets the profiling mode.
     *
     * @return the mode
     */
    @NonNull Mode mode();

    /**
     * Gets the sampling interval in milliseconds.
     *
     * @return the sampling interval, or -1 for default
     */
    double samplingInterval();

    /**
     * Gets the thread names to profile.
     *
     * @return the thread names, or null to use default (server thread)
     */
    @Nullable Set<String> threads();

    /**
     * Whether to profile all threads.
     *
     * @return true if all threads should be profiled
     */
    boolean allThreads();

    /**
     * Whether thread names should be matched using regex.
     *
     * @return true if regex matching should be used
     */
    boolean regexThreadMatching();

    /**
     * Gets the timeout duration after which profiling should automatically stop.
     *
     * @return the timeout duration, or null for no timeout
     */
    @Nullable Duration timeout();

    /**
     * Gets the minimum tick duration threshold for tick-based profiling.
     *
     * @return the minimum tick duration in milliseconds, or -1 if not set
     */
    int minimumTickDuration();

    /**
     * Whether sleeping threads should be ignored.
     *
     * @return true if sleeping threads should be ignored
     */
    boolean ignoreSleeping();

    /**
     * Whether the Java sampler should be used instead of async-profiler.
     *
     * @return true to force the Java sampler
     */
    boolean forceJavaSampler();

    /**
     * For allocation profiling, whether to only include live objects.
     *
     * @return true to only include live objects in allocation profiling
     */
    boolean allocLiveOnly();

    /**
     * The profiling mode.
     */
    enum Mode {
        /**
         * Profile CPU execution time.
         */
        EXECUTION,
        /**
         * Profile memory allocations.
         */
        ALLOCATION
    }

    /**
     * The thread grouping mode.
     */
    enum ThreadGrouper {
        /**
         * Group threads by their pool (e.g., "Worker Thread #1" becomes "Worker Thread").
         */
        BY_POOL,
        /**
         * Keep threads separate by their full name.
         */
        BY_NAME,
        /**
         * Combine all threads into a single group.
         */
        AS_ONE
    }

    /**
     * Builder for profiler configuration.
     */
    interface Builder {

        /**
         * Sets the profiling mode.
         *
         * @param mode the mode
         * @return this builder
         */
        @NonNull Builder mode(@NonNull Mode mode);

        /**
         * Sets the sampling interval in milliseconds.
         *
         * @param interval the interval in milliseconds
         * @return this builder
         */
        @NonNull Builder samplingInterval(double interval);

        /**
         * Sets specific thread names to profile.
         *
         * @param threadNames the thread names
         * @return this builder
         */
        @NonNull Builder threads(@NonNull Set<String> threadNames);

        /**
         * Configures the profiler to profile all threads.
         *
         * @return this builder
         */
        @NonNull Builder allThreads();

        /**
         * Enables regex matching for thread names.
         *
         * @return this builder
         */
        @NonNull Builder regexThreadMatching();

        /**
         * Sets the timeout after which profiling should automatically stop and upload.
         *
         * @param timeout the timeout duration
         * @return this builder
         */
        @NonNull Builder timeout(@NonNull Duration timeout);

        /**
         * Sets the minimum tick duration threshold in milliseconds.
         * Only ticks that exceed this duration will be profiled.
         *
         * @param milliseconds the minimum tick duration
         * @return this builder
         */
        @NonNull Builder minimumTickDuration(int milliseconds);

        /**
         * Configures the profiler to ignore sleeping threads.
         *
         * @return this builder
         */
        @NonNull Builder ignoreSleeping();

        /**
         * Forces the use of the built-in Java sampler instead of async-profiler.
         *
         * @return this builder
         */
        @NonNull Builder forceJavaSampler();

        /**
         * For allocation profiling, only include live objects.
         *
         * @return this builder
         */
        @NonNull Builder allocLiveOnly();

        /**
         * Sets the thread grouping mode.
         *
         * @param grouper the thread grouper
         * @return this builder
         */
        @NonNull Builder threadGrouper(@NonNull ThreadGrouper grouper);

        /**
         * Builds the configuration.
         *
         * @return the built configuration
         */
        @NonNull ProfilerConfiguration build();
    }
}