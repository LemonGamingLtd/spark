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

import java.nio.file.Path;

/**
 * The result of a profiler operation (start, stop, upload).
 */
public interface ProfilerResult {

    /**
     * Gets the URL to view the profile.
     * This will be null if the profile was saved to file instead of uploaded.
     *
     * @return the viewer URL, or null if saved to file
     */
    @Nullable String url();

    /**
     * Gets the bytebin key for the uploaded profile.
     * This will be null if the profile was saved to file instead of uploaded.
     *
     * @return the bytebin key, or null if saved to file
     */
    @Nullable String key();

    /**
     * Gets the file path where the profile was saved.
     * This will be null if the profile was uploaded instead of saved.
     *
     * @return the file path, or null if uploaded
     */
    @Nullable Path filePath();

    /**
     * Gets the raw profile data as bytes.
     *
     * @return the profile data bytes
     */
    byte @NonNull [] rawData();

    /**
     * Whether the result was from an upload.
     *
     * @return true if the profile was uploaded
     */
    default boolean wasUploaded() {
        return url() != null;
    }

    /**
     * Whether the result was saved to a file.
     *
     * @return true if the profile was saved to file
     */
    default boolean wasSavedToFile() {
        return filePath() != null;
    }
}