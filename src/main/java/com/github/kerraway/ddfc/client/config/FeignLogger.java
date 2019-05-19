package com.github.kerraway.ddfc.client.config;

import com.github.kerraway.ddfc.client.DupanClient;
import com.google.common.collect.ImmutableMap;
import feign.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;

/**
 * @author kerraway
 * @date 2019/05/19
 */
public class FeignLogger extends Logger {

  /**
   * 配置需要输出日志的方法。
   * key 类名#方法名(参数类型)，注意参数类型直接不能包含空格，详见{@link Feign#configKey(Class, Method)}；
   * value 是否打印返回值。
   *
   * @see DupanClient#listFiles(String, String, String)
   */
  private static Map<String, Boolean> methods = ImmutableMap.<String, Boolean>builder()
      //dupan
      .put("DupanClient#listFiles(String,String,String)", true)
      .build();

  @Override
  protected void logRequest(String configKey, Level logLevel, Request request) {
    if (methods.containsKey(configKey)) {
      if (request.requestBody().length() > 0) {
        String body = request.requestBody().asString();
        log(configKey, "---> %s %s HTTP/1.1 body: %s", request.httpMethod(), request.url(), body);
      } else {
        log(configKey, "---> %s %s HTTP/1.1", request.httpMethod(), request.url());
      }
    }
  }

  @Override
  protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
      throws IOException {
    if (methods.containsKey(configKey)) {
      String reason = response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ? " " + response.reason() : "";
      int status = response.status();
      /**
       * HTTP 204 No Content "...response MUST NOT include a message-body"
       * HTTP 205 Reset Content "...response MUST NOT include an entity"
       */
      if (response.body() != null && !(status == 204 || status == 205)) {
        byte[] bodyData = Util.toByteArray(response.body().asInputStream());
        if (bodyData.length > 0) {
          String data = methods.get(configKey) ? decodeOrDefault(bodyData, UTF_8, "Binary data") : "ignored.";
          log(configKey, "<--- HTTP/1.1 %s%s (%sms) body: %s", status, reason, elapsedTime, data);
        } else {
          log(configKey, "<--- HTTP/1.1 %s%s (%sms)", status, reason, elapsedTime);
        }
        return response.toBuilder().body(bodyData).build();
      }
      log(configKey, "<--- HTTP/1.1 %s%s (%sms)", status, reason, elapsedTime);
    }
    return response;
  }

  @Override
  protected void log(String configKey, String format, Object... args) {
    if (methods.containsKey(configKey)) {
      LoggerFactory.getLogger(this.getClass()).info(String.format(methodTag(configKey) + format, args));
    }
  }
}
