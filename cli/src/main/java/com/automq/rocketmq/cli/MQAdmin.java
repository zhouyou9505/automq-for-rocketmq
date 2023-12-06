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

package com.automq.rocketmq.cli;

import com.automq.rocketmq.cli.broker.DescribeCluster;
import com.automq.rocketmq.cli.broker.TerminateNode;
import com.automq.rocketmq.cli.consumer.ConsumeMessage;
import com.automq.rocketmq.cli.consumer.CreateGroup;
import com.automq.rocketmq.cli.consumer.DeleteGroup;
import com.automq.rocketmq.cli.consumer.DescribeGroup;
import com.automq.rocketmq.cli.consumer.ListGroup;
import com.automq.rocketmq.cli.consumer.ResetConsumeOffset;
import com.automq.rocketmq.cli.consumer.UpdateGroup;
import com.automq.rocketmq.cli.producer.ProduceMessage;
import com.automq.rocketmq.cli.stream.DescribeStream;
import com.automq.rocketmq.cli.topic.CreateTopic;
import com.automq.rocketmq.cli.topic.DeleteTopic;
import com.automq.rocketmq.cli.topic.DescribeTopic;
import com.automq.rocketmq.cli.topic.ListTopic;
import com.automq.rocketmq.cli.topic.UpdateTopic;
import picocli.CommandLine;

@CommandLine.Command(name = "mqadmin",
    mixinStandardHelpOptions = true,
    version = "AutoMQ for RocketMQ 1.0",
    description = "Command line tools for AutoMQ for RocketMQ",
    showDefaultValues = true,
    subcommands = {
        DescribeCluster.class,
        CreateTopic.class,
        DescribeTopic.class,
        UpdateTopic.class,
        DeleteTopic.class,
        ListTopic.class,
        DescribeStream.class,
        CreateGroup.class,
        DescribeGroup.class,
        UpdateGroup.class,
        DeleteGroup.class,
        ListGroup.class,
        ProduceMessage.class,
        ConsumeMessage.class,
        TerminateNode.class,
        ResetConsumeOffset.class
    }
)
public class MQAdmin implements Runnable {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {"-e", "--endpoint"}, description = "The access endpoint of the server", required = true)
    String endpoint;

    @CommandLine.Option(names = {"-a", "--access-key"}, description = "The authentication access key")
    String accessKey = "";

    @CommandLine.Option(names = {"-s", "--secret-key"}, description = "The authentication secret key")
    String secretKey = "";

    public String getEndpoint() {
        return endpoint;
    }


    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MQAdmin()).execute(args);
        System.exit(exitCode);
    }
}
