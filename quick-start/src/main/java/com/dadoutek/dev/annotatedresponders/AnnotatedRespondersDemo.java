package com.dadoutek.dev.annotatedresponders;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class AnnotatedRespondersDemo {
    /**
     * To use annotated responders on the server side, add RSocketMessageHandler
     * to your Spring configuration to detect(发现) @Controller beans with @MessageMapping and @ConnectMapping methods
     * <p>
     * 简单看了一下源码。由MessagingRSocket接受所有信息后交由RSocketMessageHandler进行路由
     * RSocketMessageHandler也做编解码的工作
     * 这也是为什么原生的RSocket-java demo中看不到route这个关键词
     * <p>
     * <p>
     * RSocketMessageHandler can be configured via RSocketStrategies
     * which may be useful if you need to share configuration between a client and a server in the same process:
     *
     * @Bean public RSocketMessageHandler rsocketMessageHandler() {
     * RSocketMessageHandler handler = new RSocketMessageHandler();
     * handler.setRSocketStrategies(rsocketStrategies());
     * return handler;
     * }
     * @Bean public RSocketStrategies rsocketStrategies() {
     * return RSocketStrategies.builder()
     * .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
     * .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
     * .routeMatcher(new PathPatternRouteMatcher())
     * .build();
     * }
     */
    @Bean
    public RSocketMessageHandler rsocketMessageHandler() {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        // By default SimpleRouteMatcher is used for matching routes via AntPathMatcher.
        // We recommend plugging in the PathPatternRouteMatcher from spring-web for efficient route matching.
        // RSocket routes can be hierarchical but are not URL paths.
        // Both route matchers are configured to use "." as separator by default and there is no URL decoding as with HTTP URLs.
        // 大致意思是推荐用PathPatternRouteMatcher，然后RSocket的URL paths不像http，他有层次结构且用.做分隔符。
        handler.setRouteMatcher(new PathPatternRouteMatcher());
        return handler;
    }

    // Then start an RSocket server through the Java RSocket API and plug the RSocketMessageHandler for the responder as follows:
    public static void main(String[] args) {
//        ApplicationContext context = ... ;
//        RSocketMessageHandler handler = context.getBean(RSocketMessageHandler.class);
//
//        CloseableChannel server =
//                RSocketFactory.receive()
//                        .acceptor(handler.responder())
//                        .transport(TcpServerTransport.create("localhost", 7000))
//                        .start()
//                        .block();
    }
}

// 由RSocketMessageHandler分发给Controller对应的XxxMapping进行处理
@Controller
class XxxController {

    /**
     * Method Argument
     *
     * @Payload : The payload of the request. This can be a concrete value of asynchronous types like Mono or Flux.
     * RSocketRequester : Requester for making requests to the remote end.
     * @DestinationVariable : Value extracted(提取) from the route based on variables in the mapping pattern, e.g. @MessageMapping("find.radar.{id}").
     * @Header : Metadata value registered for extraction as described in MetadataExtractor.
     * @Headers : Map<String, Object>  All metadata values registered for extraction as described in MetadataExtractor.
     * <p>
     * Return
     * he return value is expected to be one or more Objects to be serialized as response payloads.
     * That can be asynchronous types like Mono or Flux,
     * a concrete value, or either void or a no-value asynchronous type such as Mono<Void>.
     * The RSocket interaction type that an @MessageMapping method supports is determined from
     * the cardinality（数量） of the input (i.e. @Payload argument) and of the output, where cardinality means the following:
     *
     * 1 : single payload or Mono<T>
     * Many : Flux<T>
     * 0 : no payload or Mono<Void>
     *
     * input cardinality 0,1 & out cardinality 0  — fire-and-forget,request/response
     * input cardinality 0,1 & out cardinality 1  — request/response
     * input cardinality 0,1 & out cardinality Many  — request/stream
     * input cardinality Many & out cardinality 0,1,Many  — request-channel
     */
    @MessageMapping("locate.radars.within.{id}")
    public Flux<String> radars(@Payload io.rsocket.Payload payload, @DestinationVariable("id") Integer id) {
        // ...
        return Flux.just();
    }

    /**
     * @ConnectMapping methods support the same arguments as @MessageMapping but based on metadata and data
     * from the SETUP and METADATA_PUSH frames. @ConnectMapping can have a pattern to narrow handling to
     * specific connections that have a route in the metadata, or if no patterns are declared then all connections match.
     *
     * @ConnectMapping methods cannot return data and must be declared with void or Mono<Void> as the return value.
     * If handling returns an error for a new connection then the connection is rejected.
     * Handling must not be held up（被耽搁） to make requests to the RSocketRequester for the connection.
     */
    @ConnectMapping("locate.redars.within.{id}") // default for all
    public Mono<Void> radarsConnect(){
        return Mono.empty();
    }


}
