package com.dadoutek.dev.rsocketrequester;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

public class RSocketRequesterDemo {
    /**
     * RSocketRequester.Builder provides the following to customize the initial SETUP frame:
     * <p>
     * dataMimeType(MimeType) — set the mime type for data on the connection.
     * <p>
     * metadataMimeType(MimeType) — set the mime type for metadata on the connection.
     * <p>
     * setupData(Object) — data to include in the SETUP.
     * <p>
     * setupRoute(String, Object…​) — route in the metadata to include in the SETUP.
     * <p>
     * setupMetadata(Object, MimeType) — other metadata to include in the SETUP.
     * <p>
     * 5.2.2开始支持的，也可以在rsocketFactory中配置
     */
    public static void test1() {

        Mono<RSocketRequester> rSocketRequesterMono = RSocketRequester.builder().rsocketFactory(clientRSocketFactory -> {
//            clientRSocketFactory. ..

        }).connectTcp("localhost", 7000);

        // metadata,metadataMimeType,dataMimeType can configure in rSocketRequester also
        rSocketRequesterMono.subscribe(rSocketRequester -> {
//            rSocketRequester.rsocket().
        });


//        Mono<RSocketRequester> requesterMono = RSocketRequester.builder()
//                .connectWebSocket(URI.create("https://example.org:8080/rsocket"));
    }


    public static void test2() {
        Mono<RSocketRequester> rSocketRequesterMono = RSocketRequester.builder().rsocketStrategies(configure -> {
            // 配置策略时可以设置codec，default only the basic codecs from spring-core for String, byte[], and ByteBuffer are registered.
//            configure.decoder();
//            configure.encoder();


        }).connectTcp("localhost", 7000);
    }

    public static void test3() {
//        RSocketStrategies strategies = RSocketStrategies.builder()
//                .routeMatcher(new PathPatternRouteMatcher())
//                .build();
//
//        ClientHandler handler = new ClientHandler();
//
//        Mono<RSocketRequester> requesterMono = RSocketRequester.builder()
//                .rsocketFactory(RSocketMessageHandler.clientResponder(strategies, handler))
//                .connectTcp("localhost", 7000);

        /**
         * Note the above is only a shortcut designed for programmatic registration of client responders.
         * For alternative scenarios, where client responders are in Spring configuration,
         * you can still declare RSocketMessageHandler as a Spring bean and then apply as follows:
         *
         *   ApplicationContext context = ... ;
         *         RSocketMessageHandler handler = context.getBean(RSocketMessageHandler.class);
         *
         *         Mono<RSocketRequester> requesterMono = RSocketRequester.builder()
         *                 .rsocketFactory(factory -> factory.acceptor(handler.responder()))
         *                 .connectTcp("localhost", 7000);
         *
         *   Rsocket instance is MessagingRSocket
         * @see RSocketMessageHandler#responder()
         * @see RSocketMessageHandler#createResponder(ConnectionSetupPayload, RSocket)
         *      	return new MessagingRSocket(dataMimeType, metaMimeType, getMetadataExtractor(),
         * 				**requester**, this, obtainRouteMatcher(), this.strategies);
         * 			sendingRSocket wrapped in MessagingRSocket#RSocketRequester
         *
         * @see org.springframework.messaging.rsocket.annotation.support.MessagingRSocket
         *          private final MimeType dataMimeType;
         *
         * 	        private final MimeType metadataMimeType;
         *
         * 	        private final MetadataExtractor metadataExtractor;
         *
         * 	        private final ReactiveMessageHandler messageHandler;
         *
         * 	        private final RouteMatcher routeMatcher;
         *
         * 	        private final RSocketRequester requester;
         *
         * 	        private final RSocketStrategies strategies;
         *
         * 	        MessagingRSocket(MimeType dataMimeType, MimeType metadataMimeType, MetadataExtractor metadataExtractor,
         * 			RSocketRequester requester, ReactiveMessageHandler messageHandler, RouteMatcher routeMatcher,
         * 			RSocketStrategies strategies) {
         */


    }


    // Server Requester by in ConnectMapping and messageMapping
    @ConnectMapping
    Mono<Void> handle(RSocketRequester requester) {
        // make a request and receive
        // request one data and receive stream -> request/stream
//        requester.route("status") // metadata 路由
//                .data("data").retrieveFlux(String.class).subscribe();

//        // channel
//        requester.route("status").data(Flux.just(1, 2, 3)).retrieveFlux(Integer.class).subscribe();

//        // request/response
//        requester.route("status").data("data").retrieveMono(String.class).subscribe();

        // fire and forget
        requester.route("status").data("data").send().subscribe();

        // data can skip
//        requester.route("status").send().subscribe();

        // send data has metadata
//        String securityToken = ... ;
//        ViewBox viewBox = ... ;
//        MimeType mimeType = MimeType.valueOf("message/x.rsocket.authentication.bearer.v0");
//
//        Flux<AirportLocation> locations = requester.route("locate.radars.within")
//                .metadata(securityToken, mimeType)
//                .data(viewBox)
//                .retrieveFlux(AirportLocation.class);

        return Mono.empty();


    }


    public static void main(String[] args) {

    }
}
