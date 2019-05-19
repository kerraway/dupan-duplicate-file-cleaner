package com.github.kerraway.ddfc.client.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@Configuration
public class FeignConfig4Dupan {

  @Bean
  public RequestInterceptor requestInterceptor() {
    return template -> template.header(HttpHeaders.HOST, "pan.baidu.com")
        .header(HttpHeaders.REFERER, "https://pan.baidu.com/disk/home")
        .header(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate,br")
        .header(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,vi;q=0.6,nb;q=0.5,ja;q=0.4,pl;q=0.3")
        .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");
  }

  @Bean
  public Request.Options options() {
    return new Request.Options(10 * 1000, 60 * 1000);
  }

  @Bean
  public Retryer retryer() {
    return new Retryer.Default(100, 1000, 3);
  }

  @Bean
  public Logger.Level level() {
    return Logger.Level.FULL;
  }

  @Bean
  public Logger logger() {
    return new FeignLogger();
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new ErrorDecoder.Default();
  }

  /**
   * Custom {@link ApacheHttpClientFactory}, enable compression.
   *
   * @return ApacheHttpClientFactory
   * @see HttpClientConfiguration.ApacheHttpClientConfiguration#apacheHttpClientFactory(HttpClientBuilder)
   */
  @Bean
  public ApacheHttpClientFactory apacheHttpClientFactory() {
    return () -> HttpClientBuilder.create().disableCookieManagement().useSystemProperties();
  }

}
