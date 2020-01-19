package com.dadoutek.dev.springboot;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 下面的是为RSocket client考虑的，server pass
 *
 * Once the RSocket channel is established between server and client, any party can send or receive requests to the other.
 *
 * As a server, you can get injected with an RSocketRequester instance on any handler method of an RSocket @Controller. As a client, you need to configure and establish an RSocket connection first. Spring Boot auto-configures an RSocketRequester.Builder for such cases with the expected codecs.
 *
 * The RSocketRequester.Builder instance is a prototype bean, meaning each injection point will provide you with a new instance . This is done on purpose since this builder is stateful and you shouldn’t create requesters with different setups using the same instance.
 *
 * The following code shows a typical example:
 */
@Service
public class CallingRSocketServicesWithRSocketRequesterDemo {
    private final Mono<RSocketRequester> rsocketRequester;

    public CallingRSocketServicesWithRSocketRequesterDemo(RSocketRequester.Builder rsocketRequesterBuilder) {
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("example.org", 9898).cache();
    }

//    public Mono<User> someRSocketCall(String name) {
//        return this.rsocketRequester.flatMap(req ->
//                req.route("user").data(name).retrieveMono(User.class));
//    }
}
