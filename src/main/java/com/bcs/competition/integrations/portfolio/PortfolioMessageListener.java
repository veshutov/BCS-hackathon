package com.bcs.competition.integrations.portfolio;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bcs.competition.integrations.portfolio.processing.PortfolioMessageProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author veshutov
 **/
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.mode}' == 'single'")
public class PortfolioMessageListener {
    private final ObjectMapper objectMapper;
    private final PortfolioMessageProcessor portfolioMessageProcessor;
    private final Cache<String, Instant> portfolioLastSnapshotAt = Caffeine.newBuilder()
            .maximumSize(10000)
            .build();

    @KafkaListener(id = "limits0", topicPartitions = @TopicPartition(topic = "security-limits", partitions = "0"))
    public void consume0(List<String> messages, Acknowledgment ack) {
        consumePortfolio(messages, ack);
    }

    @KafkaListener(id = "limits1", topicPartitions = @TopicPartition(topic = "security-limits", partitions = "1"))
    public void consume1(List<String> messages, Acknowledgment ack) {
        consumePortfolio(messages, ack);
    }

    @KafkaListener(id = "limits2", topicPartitions = @TopicPartition(topic = "security-limits", partitions = "2"))
    public void consume2(List<String> messages, Acknowledgment ack) {
        consumePortfolio(messages, ack);
    }

    @KafkaListener(id = "limits3", topicPartitions = @TopicPartition(topic = "security-limits", partitions = "3"))
    public void consume3(List<String> messages, Acknowledgment ack) {
        consumePortfolio(messages, ack);
    }

    @KafkaListener(id = "limits4", topicPartitions = @TopicPartition(topic = "security-limits", partitions = "4"))
    public void consume4(List<String> messages, Acknowledgment ack) {
        consumePortfolio(messages, ack);
    }

    @KafkaListener(id = "limits5", topicPartitions = @TopicPartition(topic = "security-limits", partitions = "5"))
    public void consume5(List<String> messages, Acknowledgment ack) {
        consumePortfolio(messages, ack);
    }

    private void consumePortfolio(List<String> messages, Acknowledgment ack) {
        log.info("received {} messages", messages.size());
        Stopwatch portfolioWatch = Stopwatch.createStarted();
        for (String message : messages) {
            try {
                PortfolioMessage p = objectMapper.readValue(message, PortfolioMessage.class);
                Instant lastSnapshotAt = portfolioLastSnapshotAt.getIfPresent(p.getClientId());
                if (lastSnapshotAt != null && !lastSnapshotAt.isBefore(p.getTimestamp())) {
                    continue;
                } else {
                    if (p.getType() == PortfolioMessageType.SNAPSHOT) {
                        portfolioLastSnapshotAt.put(p.getClientId(), p.getTimestamp());
                    }
                    portfolioMessageProcessor.process(p);
                }
            } catch (Throwable e) {
                log.error("error while processing message", e);
            }
        }
        log.info(
                "Process {} messages, time: {}",
                messages.size(), portfolioWatch.elapsed(TimeUnit.MILLISECONDS)
        );
        ack.acknowledge();
    }
}
