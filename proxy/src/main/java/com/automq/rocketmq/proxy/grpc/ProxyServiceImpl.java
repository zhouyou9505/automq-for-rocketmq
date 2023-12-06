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

package com.automq.rocketmq.proxy.grpc;

import apache.rocketmq.common.v1.Code;
import apache.rocketmq.proxy.v1.ProducerClientConnection;
import apache.rocketmq.proxy.v1.ProducerClientConnectionReply;
import apache.rocketmq.proxy.v1.ProducerClientConnectionRequest;
import apache.rocketmq.proxy.v1.ProxyServiceGrpc;
import apache.rocketmq.proxy.v1.QueueStats;
import apache.rocketmq.proxy.v1.ResetConsumeOffsetByTimestampRequest;
import apache.rocketmq.proxy.v1.ResetConsumeOffsetReply;
import apache.rocketmq.proxy.v1.ResetConsumeOffsetRequest;
import apache.rocketmq.proxy.v1.Status;
import apache.rocketmq.proxy.v1.TopicStatsReply;
import apache.rocketmq.proxy.v1.TopicStatsRequest;
import com.automq.rocketmq.proxy.service.ExtendMessageService;
import com.google.protobuf.TextFormat;
import io.grpc.stub.StreamObserver;
import io.netty.channel.Channel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.rocketmq.broker.client.ClientChannelInfo;
import org.apache.rocketmq.broker.client.ProducerManager;
import org.apache.rocketmq.common.MQVersion;
import org.apache.rocketmq.common.utils.NetworkUtil;
import org.apache.rocketmq.proxy.grpc.v2.channel.GrpcClientChannel;
import org.apache.rocketmq.proxy.processor.channel.ChannelProtocolType;
import org.slf4j.Logger;

public class ProxyServiceImpl extends ProxyServiceGrpc.ProxyServiceImplBase {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProxyServiceImpl.class);

    private final ExtendMessageService messageService;

    private final ProducerManager producerManager;

    public ProxyServiceImpl(ExtendMessageService messageService, ProducerManager producerManager) {
        this.messageService = messageService;
        this.producerManager = producerManager;
    }

    @Override
    public void resetConsumeOffset(ResetConsumeOffsetRequest request,
        StreamObserver<ResetConsumeOffsetReply> responseObserver) {
        LOGGER.info("Reset consume offset request received: {}", TextFormat.shortDebugString(request));
        messageService.resetConsumeOffset(request.getTopic(), request.getQueueId(), request.getGroup(), request.getNewConsumeOffset())
            .whenComplete((v, e) -> {
                if (e != null) {
                    responseObserver.onError(e);
                    return;
                }
                ResetConsumeOffsetReply reply = ResetConsumeOffsetReply.newBuilder()
                    .setStatus(Status
                        .newBuilder()
                        .setCode(Code.OK)
                        .build())
                    .build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            });
    }

    @Override
    public void resetConsumeOffsetByTimestamp(ResetConsumeOffsetByTimestampRequest request,
        StreamObserver<ResetConsumeOffsetReply> responseObserver) {
        LOGGER.info("Reset consume offset by timestamp request received: {}", TextFormat.shortDebugString(request));
        messageService.resetConsumeOffsetByTimestamp(request.getTopic(), request.getQueueId(), request.getGroup(), request.getTimestamp())
            .whenComplete((v, e) -> {
                if (e != null) {
                    responseObserver.onError(e);
                    return;
                }
                ResetConsumeOffsetReply reply = ResetConsumeOffsetReply.newBuilder()
                    .setStatus(Status
                        .newBuilder()
                        .setCode(Code.OK)
                        .build())
                    .build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            });
    }

    @Override
    public void topicStats(TopicStatsRequest request, StreamObserver<TopicStatsReply> responseObserver) {
        messageService.getTopicStats(request.getTopic(), request.getQueueId(), request.getGroup())
            .whenComplete((pair, e) -> {
                if (e != null) {
                    responseObserver.onError(e);
                    return;
                }

                Long topicId = pair.getLeft();
                List<QueueStats> queueStatsList = pair.getRight();
                TopicStatsReply reply = TopicStatsReply.newBuilder()
                    .setStatus(Status
                        .newBuilder()
                        .setCode(Code.OK)
                        .build())
                    .setId(topicId)
                    .setName(request.getTopic())
                    .addAllQueueStats(queueStatsList)
                    .build();

                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            });
    }

    @Override
    public void producerClientConnection(ProducerClientConnectionRequest request,
        StreamObserver<ProducerClientConnectionReply> responseObserver) {
        ConcurrentHashMap<Channel, ClientChannelInfo> map = producerManager.getGroupChannelTable().get(request.getProductionGroup());
        if (map == null) {
            responseObserver.onNext(ProducerClientConnectionReply.newBuilder()
                .setStatus(Status
                    .newBuilder()
                    .setCode(Code.BAD_REQUEST)
                    .setMessage("Producer group not found: " + request.getProductionGroup())
                    .build())
                .build());
            responseObserver.onCompleted();
            return;
        }
        ProducerClientConnectionReply.Builder builder = ProducerClientConnectionReply.newBuilder();
        for (ClientChannelInfo info : map.values()) {
            String protocolType = ChannelProtocolType.REMOTING.name();
            if (info.getChannel() instanceof GrpcClientChannel) {
                protocolType = ChannelProtocolType.GRPC_V2.name();
            }
            builder.addConnection(ProducerClientConnection.newBuilder()
                .setClientId(info.getClientId())
                .setProtocol(protocolType)
                .setAddress(NetworkUtil.socketAddress2String(info.getChannel().remoteAddress()))
                .setLanguage(info.getLanguage().name())
                .setVersion(MQVersion.getVersionDesc(info.getVersion()))
                .setLastUpdateTime(info.getLastUpdateTimestamp())
                .build());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
