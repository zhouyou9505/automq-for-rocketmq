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

package com.automq.rocketmq.store.service.api;

import com.automq.rocketmq.store.api.MessageStateMachine;
import com.automq.rocketmq.store.model.operation.AckOperation;
import com.automq.rocketmq.store.model.operation.ChangeInvisibleDurationOperation;
import com.automq.rocketmq.store.model.operation.PopOperation;
import java.util.concurrent.CompletableFuture;

public interface OperationLogService {
    /**
     * Log pop operation to WAL.
     * Each queue has its own operation log.
     *
     * @return monotonic serial number
     */
    CompletableFuture<Long> logPopOperation(PopOperation operation);

    /**
     * Log ack operation to WAL.
     * Each queue has its own operation log.
     *
     * @return monotonic serial number
     */
    CompletableFuture<Long> logAckOperation(AckOperation operation);

    /**
     * Log change invisible time operation to WAL.
     * Each queue has its own operation log.
     *
     * @return monotonic serial number
     */
    CompletableFuture<Long> logChangeInvisibleDurationOperation(ChangeInvisibleDurationOperation operation);

    /**
     * Recover.
     * Each queue has its own operation log.
     *
     * @return monotonic serial number
     */
    CompletableFuture<Void> recover(MessageStateMachine stateMachine, long operationStreamId, long snapshotStreamId);
}
