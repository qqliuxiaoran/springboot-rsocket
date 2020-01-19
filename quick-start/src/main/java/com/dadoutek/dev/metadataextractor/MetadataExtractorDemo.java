package com.dadoutek.dev.metadataextractor;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.DefaultMetadataExtractor;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Responders must interpret metadata. Composite metadata allows independently formatted metadata values
 * (e.g. for routing, security, tracing) each with its own mime type.
 * Applications need a way to configure metadata mime types to support, and a way to access extracted values.
 * metadata需要被处理，Composite metadata(符合metadata)可以独立处理metadata中各段值(如routing,security,tracing)，没段值都有
 * 自己的MimeType。应用程序需要一个能简化向metadata设值和取值的方法
 * <p>
 * MetadataExtractor is a contract to take serialized metadata and return decoded name-value pairs that can then be accessed
 * like headers by name, for example via @Header in annotated handler methods.
 * MetadataExtractor(metadata抽取器)因运而生，操作metadata和操作Map一样
 * <p>
 * DefaultMetadataExtractor can be given Decoder instances to decode metadata.
 * Out of the box it has built-in support for "message/x.rsocket.routing.v0" which
 * it decodes to String and saves under the "route" key.
 * For any other mime type you’ll need to provide a Decoder and register the mime type as follows:
 * 可以为DefaultMetadataExtractor提供解码器实例来解码元数据。它内置了对“message/x.rsocket.routing.v0”的支持，
 * 并将其解码为字符串并保存在“route”键下。对于任何其他mime类型，您需要提供解码器并按如下方式注册mime类型：
 */
@Configuration
public class MetadataExtractorDemo {

    @Bean
    public RSocketMessageHandler rsocketMessageHandler() {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(rsocketStrategies());
        return handler;
    }

    @Bean
    public RSocketStrategies rsocketStrategies() {
        /**
         * 不设置任何decode，看DefaultMetadataExtractor源码
         * Step 1.
         * @see DefaultMetadataExtractor#extract(Payload, MimeType) return Map<String,Object> 由框架传入这个两个参数
         * 如果原生payload的MimeType是message/x.rsocket.composite-metadata.v0，即支持复合metadata则将其抽取拆分成CompositeMetadata.Entry
         * 等待抽取（调用extractEntry() {@link DefaultMetadataExtractor#extractEntry(ByteBuf, String, Map)}）
         *
         * 如果原生Payload的MimeType不是message/x.rsocket.composite-metadata.v0,则将原生的payload.metadata.slice()和原生的metadataMineType.toString()
         * 传入{@link DefaultMetadataExtractor#extractEntry(ByteBuf, String, Map)}
         *
         * Step 2.
         * @see DefaultMetadataExtractor#extractEntry(ByteBuf, String, Map)
         * 如果mimeType(String)是message/x.rsocket.routing.v0的话就由RoutingMetadata进行路由。
         *
         * @see DefaultMetadataExtractor#registrations
         * 如果不是，则遍历registrations（Map<String, EntryExtractor<?>>），查找已经注册（需要进行处理的mimeType）的EntryExtractor，
         * 如果的EntryExtractor的key = mimeType则调用的EntryExtractor实例的extract方法
         *
         */
        DefaultMetadataExtractor defaultMetadataExtractor = new DefaultMetadataExtractor();

        /**
         * Step 3.
         * @see DefaultMetadataExtractor#metadataToExtract(MimeType, Class, String)
         * 注册需要decode的mimeType,并解析成对应的key = name，value type = Class，上述的metadataToExtract方法是
         * MetadataExtractorRegistry（iterface）中的default方法，他调用的是
         * @see DefaultMetadataExtractor#metadataToExtract(MimeType, Class, BiConsumer)
         * 最后再调用
         * @see DefaultMetadataExtractor#registerMetadata(MimeType, ResolvableType, BiConsumer)
         * 在registerMetadata方法中会遍历已经注册过的decodes，找出一个能decode这个mimeType并返回对应ResolvableType的。
         * 如果这个decode存在，则生成EntryExtractor实例(wrap MimeType，ResolvableType，BiConsumer和decoder)放入registrations中，
         * key为mimeType.toString()
         *
         * 至此如果把原生的Payload中metadata转成Map就结束了，他需要有能解析对用mimeType和ResolvableType的decoder（条件一），并且
         * 需要注册需要抽取的mimeType（条件二）
         */
        defaultMetadataExtractor.metadataToExtract(MimeType.valueOf("aString"), String.class, "string");


        /**
         * Composite metadata works well to combine independent metadata values.
         * However the requester might not support composite metadata,
         * or may choose not to use it. For this,
         * DefaultMetadataExtractor may needs custom logic to map the decoded value to the output map.
         * Here is an example where JSON is used for metadata:
         *
         */
        defaultMetadataExtractor.metadataToExtract(
                MimeType.valueOf("application/vnd.myapp.metadata+json"),
                new ParameterizedTypeReference<Map<String, String>>() {
                },
                (jsonMap, outputMap) -> {
                    outputMap.putAll(jsonMap);
                });


        return RSocketStrategies.builder()
                .metadataExtractor(defaultMetadataExtractor)

                /**
                 * Append to the list of decoders to use for de-serializing Objects from
                 * the data or metadata of a {@link Payload}.
                 * <p>By default this is initialized with decoders for {@code String},
                 * {@code byte[]}, {@code ByteBuffer}, and {@code DataBuffer}.
                 *
                 * 这里配置decode可以解码payload的metadata,且默认有String,byte[],ByteBuffer,DataBuffer的解码器
                 */
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder())) // CBOR means binary object
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
                .routeMatcher(new PathPatternRouteMatcher())
                .build();
    }
}
