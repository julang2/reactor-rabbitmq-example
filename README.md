# reactor-rabbitmq-example
This is an example implementation which uses reactor-rabbitmq to connect with RabbitMQ. This is a Maven based springboot app.

Follow the steps to see it in action.

- Make sure the RabbitMQ is available on localhost.
- App offers two endpoint for `/consume` and `/produce`.
- Start the springboot app using intellij or command line.
- Hit the `/produce` to produce 10 ordinary String messages to RabbitMQ.
- Hit the `/consume` to consume the messages.

- Once the `/produce` is triggered, you should be able to see new RabbitMQ resources created on your local RabbitMQ instance.
  An exchange `my.exchange` along with a Queue `my.queue` is created with the binding `picnic`. 

- You can trigger `/produce` as many time as you want as it will keep adding new messages to `my.queue`.
- Hit `/consume` to consume the messages. The class `EventConsumer` is designed to handle a message per second to showcase the back pressure.

- Properties such as  `host`, `port`, `username` and `passowrd` are configured in the `application.properties`. Feel free to change it as per your convenience.
