/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.automq.rocketmq.common.config;

public class StoreConfig {
    // Fetch limitation of a single request.
    // Filtered out messages will also be counted.

    // Default is 1000
    private int maxFetchCount = 1000;

    // Default is 10MB, unit in bytes
    private long maxFetchBytes = 10L * 1024 * 1024;

    // Default is 10s, unit in nanoseconds
    private long maxFetchTimeNanos = 10L * 1000 * 1000_000;

    public int maxFetchCount() {
        return maxFetchCount;
    }

    public long maxFetchBytes() {
        return maxFetchBytes;
    }

    public long maxFetchTimeNanos() {
        return maxFetchTimeNanos;
    }
}
