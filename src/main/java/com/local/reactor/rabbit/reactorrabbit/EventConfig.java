package com.local.reactor.rabbit.reactorrabbit;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
public class EventConfig {

  static final String QUEUE = "my.queue";

  @Value("${rabbit.host}")
  private String host;

  @Value("${rabbit.port}")
  private int port;

  @Value("${rabbit.username}")
  private String username;

  @Value("${rabbit.password}")
  private String password;

  @Value("${rabbit.connection}")
  private String connectionName;

  // Only used for non-reactor managed connections.
  // @Autowired Mono<Connection> connectionMono;

  @Autowired ConnectionFactory connectionFactory;

  @Bean
  ConnectionFactory factory() {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.useNio();
    return connectionFactory;
  }

  @Bean
  Sender sender() {
    return RabbitFlux.createSender(
        new SenderOptions()
            .connectionFactory(connectionFactory)
            .connectionSupplier(cf -> cf.newConnection("sender")));
  }

  @Bean
  Receiver receiver() {
    return RabbitFlux.createReceiver(
        new ReceiverOptions()
            .connectionFactory(connectionFactory)
            .connectionSupplier(cf -> cf.newConnection("receiver")));
  }
  // Connection created in a following way will not be managed to by reactor. Which implies that the
  // caching of the connection, the sender registration and the clean up would have to done
  // manually. This is evident by the below code.

  /**
   * @Bean Mono<Connection> connectionMono() { ConnectionFactory connectionFactory = new
   * ConnectionFactory(); connectionFactory.setHost(host); connectionFactory.setPort(port);
   * connectionFactory.setUsername(username); connectionFactory.setPassword(password); return
   * Mono.fromCallable(() -> connectionFactory.newConnection(connectionName)).cache(); } @Bean
   * Sender sender(Mono<Connection> connectionMono) { return RabbitFlux.createSender(new
   * SenderOptions().connectionMono(connectionMono)); } @Bean Receiver receiver(Mono<Connection>
   * connectionMono) { return RabbitFlux.createReceiver(new
   * ReceiverOptions().connectionMono(connectionMono)); } @PreDestroy public void close() throws
   * Exception { connectionMono.block().close(); }
   */
}
