package com.github.kerraway.ddfc.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Collections;
import java.util.TimeZone;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@Configuration
public class MessageConverterConfig {

  /**
   * Config http message converter.
   *
   * @return HttpMessageConverter
   * @see org.springframework.http.converter.HttpMessageConverter
   */
  @Bean
  public HttpMessageConverter httpMessageConverter() {
    MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
    httpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    httpMessageConverter.setObjectMapper(buildObjectMapper());
    return httpMessageConverter;
  }

  private ObjectMapper buildObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

}
