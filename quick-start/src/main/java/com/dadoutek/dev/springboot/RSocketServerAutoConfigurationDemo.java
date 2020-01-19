package com.dadoutek.dev.springboot;

/**
 * Spring Boot allows exposing RSocket over WebSocket from a WebFlux server,
 * or standing up an independent RSocket server. This depends on the type of application and its configuration.
 *
 * For WebFlux application (i.e. of type WebApplicationType.REACTIVE),
 * the RSocket server will be plugged into the Web Server only if the following properties match:
 * 1.spring.rsocket.server.mapping-path=/rsocket # a mapping path is defined (websocket endpoint?)
 * 2.spring.rsocket.server.transport=websocket # websocket is chosen as a transport
 * 3.#spring.rsocket.server.port= # no port is defined. (use server.port http config?)
 * Pay attention! Plugging RSocket into a web server is only supported with Reactor Netty, as RSocket itself is built with that library.
 *
 * Alternatively, an RSocket TCP or websocket server is started as an independent, embedded server.
 * Besides the dependency requirements, the only required configuration is to define a port for that server:
 * 1.spring.rsocket.server.port=9898 # the only required configuration
 * 2.spring.rsocket.server.transport=tcp # you're free to configure other properties
 *
 * @see org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration
 *
 */
public class RSocketServerAutoConfigurationDemo {
}
