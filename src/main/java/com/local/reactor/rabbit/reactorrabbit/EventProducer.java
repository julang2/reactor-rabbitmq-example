package com.local.reactor.rabbit.reactorrabbit;

import static com.local.reactor.rabbit.reactorrabbit.EventConfig.QUEUE;
import static reactor.rabbitmq.ResourcesSpecification.binding;
import static reactor.rabbitmq.ResourcesSpecification.exchange;
import static reactor.rabbitmq.ResourcesSpecification.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;
import reactor.rabbitmq.Sender;

@Component
public class EventProducer {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventProducer.class);
  private static final String BINDING = "picnic";
  private static final String TYPE = "topic";

  @Value("${rabbit.exchange}")
  private String exchange;

  private Sender sender;

  @Autowired
  EventProducer(Sender sender) {
    this.sender = sender;
  }

  Flux<OutboundMessageResult> produce() {
    int messageCount = 10;
    LOGGER.info("Sending messages...");

    Flux<OutboundMessageResult> messages =
        sender.sendWithPublishConfirms(
            Flux.range(1, messageCount)
                .map(
                    i ->
                        new OutboundMessage(exchange, BINDING, ("Message_" + i + " ").getBytes())));
    return sender
        .declareExchange(exchange(exchange).type(TYPE))
        .then(sender.declareQueue(queue(QUEUE)))
        .then(
            sender.bind(
                BindingSpecification.binding().exchange(exchange).routingKey(BINDING).queue(QUEUE)))
        .thenMany(messages);
  }

  void distroy() {
    sender
        .unbind(binding(exchange, BINDING, QUEUE))
        .then(sender.delete(exchange(exchange)))
        .then(sender.delete(queue(QUEUE)))
        .subscribe(r -> LOGGER.info("Exchange and queue unbound and deleted"));
  }
}
