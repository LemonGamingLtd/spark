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

package me.lucko.spark.api.heap;

import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Programmatic access to Spark's heap analysis functionality.
 */
public interface SparkHeap {

    /**
     * Creates a heap summary and uploads it.
     *
     * @return a future that completes with the heap summary result
     */
    @NonNull CompletableFuture<HeapSummaryResult> createHeapSummary();

    /**
     * Creates a heap summary with options.
     *
     * @param runGcBefore if true, runs garbage collection before creating the summary
     * @param saveToFile if true, saves to file instead of uploading
     * @return a future that completes with the heap summary result
     */
    @NonNull CompletableFuture<HeapSummaryResult> createHeapSummary(boolean runGcBefore, boolean saveToFile);

    /**
     * Creates a heap dump and saves it to a file.
     *
     * @return a future that completes with the heap dump result
     */
    @NonNull CompletableFuture<HeapDumpResult> createHeapDump();

    /**
     * Creates a heap dump with options.
     *
     * @param runGcBefore if true, runs garbage collection before creating the dump
     * @param includeLiveOnly if true, only includes live objects in the dump
     * @return a future that completes with the heap dump result
     */
    @NonNull CompletableFuture<HeapDumpResult> createHeapDump(boolean runGcBefore, boolean includeLiveOnly);
}