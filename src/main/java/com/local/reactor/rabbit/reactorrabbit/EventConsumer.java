package com.local.reactor.rabbit.reactorrabbit;

import static com.local.reactor.rabbit.reactorrabbit.EventConfig.QUEUE;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

@Service
public class EventConsumer {

  private Receiver receiver;

  @Autowired
  EventConsumer(Receiver receiver, Sender sender) {
    this.receiver = receiver;
  }

  Flux<String> consume() {
    return receiver
        .consumeManualAck(QUEUE)
        .limitRate(1)
        .delayElements(Duration.ofSeconds(1))
        .flatMap(delivery -> Mono.just(new String(delivery.getBody())));
  }
}
