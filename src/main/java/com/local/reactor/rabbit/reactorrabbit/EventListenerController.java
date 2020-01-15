package com.local.reactor.rabbit.reactorrabbit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.OutboundMessageResult;

@RestController
public class EventListenerController {

  private EventConsumer eventListener;

  private EventProducer eventProducer;

  @Autowired
  EventListenerController(EventProducer eventProducer, EventConsumer eventListener) {
    this.eventProducer = eventProducer;
    this.eventListener = eventListener;
  }

  @RequestMapping("/consume")
  public Flux<String> consume() {
    return eventListener.consume();
  }

  @RequestMapping("/produce")
  public Flux<OutboundMessageResult> produce() {
    return eventProducer.produce();
  }
}
