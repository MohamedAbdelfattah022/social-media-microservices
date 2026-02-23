package com.socialmedia.userservice.config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

public class FeignMultipartConfig {

    @Bean
    public Encoder feignEncoder(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }

    @Bean
    public Decoder feignDecoder() {
        List<HttpMessageConverter<?>> converterList = List.of(
                new JacksonJsonHttpMessageConverter(),
                new StringHttpMessageConverter(StandardCharsets.UTF_8));
        ObjectProvider<HttpMessageConverter<?>> provider = new ObjectProvider<>() {
            @Override
            public HttpMessageConverter<?> getObject() {
                return converterList.get(0);
            }

            @Override
            public Iterator<HttpMessageConverter<?>> iterator() {
                return converterList.iterator();
            }

            @Override
            public java.util.stream.Stream<HttpMessageConverter<?>> stream() {
                return converterList.stream();
            }
        };
        return new SpringDecoder(provider);
    }
}
