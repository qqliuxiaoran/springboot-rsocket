package com.dadoutek.dev.springboot;

import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;

/**
 * 4.8.3. Spring Messaging RSocket support
 * Spring Boot will auto-configure the Spring Messaging infrastructure for RSocket.
 *
 * This means that Spring Boot will create a RSocketMessageHandler bean that will handle RSocket requests to your application.
 *
 * @see RSocketStrategiesCustomizer
 * RSocketMessageHandler在前面的demo中已经讲过，它相当于web应用中的dispatcherServer
 * 策略中配置实际上都是配进了RSocketMessageHandler
 * spring会自动装配一个RSocketMessageHandler，要对RSocketMessageHandler进行配置就对
 * 装配RSocketStrategiesCustomizer的实现类
 */
public class SpringMessageRSocketSupportDemo {
}
