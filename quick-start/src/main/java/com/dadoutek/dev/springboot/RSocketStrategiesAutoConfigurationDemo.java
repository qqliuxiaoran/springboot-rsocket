package com.dadoutek.dev.springboot;

import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.messaging.rsocket.RSocketStrategies;

/**
 * @see org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration
 *
 * Spring Boot auto-configures an RSocketStrategies bean that provides all the required infrastructure
 * for encoding and decoding RSocket payloads.By default, the auto-configuration will try to configure the following (in order):
 * 1.CBOR codecs with Jackson
 *
 * 2.JSON codecs with Jackson
 */
public class RSocketStrategiesAutoConfigurationDemo {
}


/**
 * Developers can customize the RSocketStrategies component by creating beans that implement the RSocketStrategiesCustomizer interface.
 * Note that their @Order is important, as it determines the order of codecs.
 */
class CustomerStrategies implements RSocketStrategiesCustomizer {

    @Override
    public void customize(RSocketStrategies.Builder strategies) {

    }
}