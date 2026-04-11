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
import java.util.HashSet;
import java.util.Set;

public final class ProfilerConfigurationImpl implements ProfilerConfiguration {
    private final Mode mode;
    private final double samplingInterval;
    private final Set<String> threads;
    private final boolean allThreads;
    private final boolean regexThreadMatching;
    private final Duration timeout;
    private final int minimumTickDuration;
    private final boolean ignoreSleeping;
    private final boolean forceJavaSampler;
    private final boolean allocLiveOnly;
    private final ThreadGrouper threadGrouper;

    ProfilerConfigurationImpl(Mode mode, double samplingInterval, Set<String> threads,
                              boolean allThreads, boolean regexThreadMatching, Duration timeout,
                              int minimumTickDuration, boolean ignoreSleeping, boolean forceJavaSampler,
                              boolean allocLiveOnly, ThreadGrouper threadGrouper) {
        this.mode = mode;
        this.samplingInterval = samplingInterval;
        this.threads = threads;
        this.allThreads = allThreads;
        this.regexThreadMatching = regexThreadMatching;
        this.timeout = timeout;
        this.minimumTickDuration = minimumTickDuration;
        this.ignoreSleeping = ignoreSleeping;
        this.forceJavaSampler = forceJavaSampler;
        this.allocLiveOnly = allocLiveOnly;
        this.threadGrouper = threadGrouper;
    }

    @Override
    public @NonNull Mode mode() {
        return this.mode;
    }

    @Override
    public double samplingInterval() {
        return this.samplingInterval;
    }

    @Override
    public @Nullable Set<String> threads() {
        return this.threads;
    }

    @Override
    public boolean allThreads() {
        return this.allThreads;
    }

    @Override
    public boolean regexThreadMatching() {
        return this.regexThreadMatching;
    }

    @Override
    public @Nullable Duration timeout() {
        return this.timeout;
    }

    @Override
    public int minimumTickDuration() {
        return this.minimumTickDuration;
    }

    @Override
    public boolean ignoreSleeping() {
        return this.ignoreSleeping;
    }

    @Override
    public boolean forceJavaSampler() {
        return this.forceJavaSampler;
    }

    @Override
    public boolean allocLiveOnly() {
        return this.allocLiveOnly;
    }

    public ThreadGrouper threadGrouper() {
        return this.threadGrouper;
    }

    public static final class BuilderImpl implements Builder {
        private Mode mode = Mode.EXECUTION;
        private double samplingInterval = -1;
        private Set<String> threads = null;
        private boolean allThreads = false;
        private boolean regexThreadMatching = false;
        private Duration timeout = null;
        private int minimumTickDuration = -1;
        private boolean ignoreSleeping = false;
        private boolean forceJavaSampler = false;
        private boolean allocLiveOnly = false;
        private ThreadGrouper threadGrouper = ThreadGrouper.BY_POOL;

        BuilderImpl() {
        }

        @Override
        public @NonNull Builder mode(@NonNull Mode mode) {
            this.mode = mode;
            return this;
        }

        @Override
        public @NonNull Builder samplingInterval(double interval) {
            this.samplingInterval = interval;
            return this;
        }

        @Override
        public @NonNull Builder threads(@NonNull Set<String> threadNames) {
            this.threads = new HashSet<>(threadNames);
            return this;
        }

        @Override
        public @NonNull Builder allThreads() {
            this.allThreads = true;
            return this;
        }

        @Override
        public @NonNull Builder regexThreadMatching() {
            this.regexThreadMatching = true;
            return this;
        }

        @Override
        public @NonNull Builder timeout(@NonNull Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public @NonNull Builder minimumTickDuration(int milliseconds) {
            this.minimumTickDuration = milliseconds;
            return this;
        }

        @Override
        public @NonNull Builder ignoreSleeping() {
            this.ignoreSleeping = true;
            return this;
        }

        @Override
        public @NonNull Builder forceJavaSampler() {
            this.forceJavaSampler = true;
            return this;
        }

        @Override
        public @NonNull Builder allocLiveOnly() {
            this.allocLiveOnly = true;
            return this;
        }

        @Override
        public @NonNull Builder threadGrouper(@NonNull ThreadGrouper grouper) {
            this.threadGrouper = grouper;
            return this;
        }

        @Override
        public @NonNull ProfilerConfiguration build() {
            return new ProfilerConfigurationImpl(
                    this.mode, this.samplingInterval, this.threads, this.allThreads,
                    this.regexThreadMatching, this.timeout, this.minimumTickDuration,
                    this.ignoreSleeping, this.forceJavaSampler, this.allocLiveOnly,
                    this.threadGrouper
            );
        }
    }
}