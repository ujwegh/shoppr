package ru.nik.commons.http.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import ru.nik.commons.http.internal.InternalRequestExecutorErrorResponseMapper;
import ru.nik.commons.http.internal.InternalRequestExecutors;
import ru.nik.commons.http.mapper.ObjectJsonMapper;
import ru.nik.commons.http.mapper.ObjectJsonMapperImpl;
import ru.nik.commons.retry.RetryProperties;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.toIntExact;


@Configuration
public class StarterConfiguration {

    @Bean
    @ConfigurationProperties("web-client.load-balanced")
    public WebClientConfigurationProperties loadBalancedConfigurationProperties() {
        return new WebClientConfigurationProperties();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @LoadBalanced
    public WebClient loadBalancedWebClient(@LoadBalanced WebClient.Builder loadBalancedWebClientBuilder,
                                           WebClientConfigurationProperties configurationProperties) {
        HttpClient httpClient = HttpClient.create(ConnectionProvider
                .create("loadBalancedWebClient", configurationProperties.getMaxConnections()))
                .tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, configurationProperties.getConnectTimeout())
                        .doOnConnected(connection -> connection
                                .addHandlerLast(new ReadTimeoutHandler(configurationProperties.getReadTimeout(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(configurationProperties.getWriteTimeout(), TimeUnit.MILLISECONDS))))
                .keepAlive(configurationProperties.isKeepAlive());
        return loadBalancedWebClientBuilder
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(toIntExact(DataSize.parse(configurationProperties.getMaxBufferSize()).toBytes())))
                        .build())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }


    @Bean
    public ObjectJsonMapper objectJsonMapper(ObjectMapper objectMapper) {
        return new ObjectJsonMapperImpl(objectMapper);
    }

    @Bean
    @ConfigurationProperties(prefix = "retry.internal-request")
    public RetryProperties retryProperties() {
        return new RetryProperties();
    }

    @Bean
    public InternalRequestExecutorErrorResponseMapper internalRequestExecutorErrorResponseMapper(ObjectJsonMapper objectJsonMapper) {
        return new InternalRequestExecutorErrorResponseMapper(objectJsonMapper);
    }

    @Bean
    public InternalRequestExecutors internalRequestExecutors(@LoadBalanced WebClient loadBalancedWebClient,
                                                             RetryProperties retryProperties,
                                                             InternalRequestExecutorErrorResponseMapper internalRequestExecutorErrorResponseMapper) {
        return new InternalRequestExecutors(loadBalancedWebClient, retryProperties, internalRequestExecutorErrorResponseMapper);
    }
}
