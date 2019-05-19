package com.github.kerraway.ddfc.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * MDC Filter
 *
 * @author kerraway
 * @date 2019/05/19
 */
@Component
public class MdcFilter extends GenericFilterBean {

  public static final String REQUEST_UUID_KEY = "request_uuid";
  private static final String REQUEST_UUID_REGEX = "(\\w{8}(-\\w{4}){3}-\\w{12})";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String requestUuid = Optional.of(request)
        .filter(HttpServletRequest.class::isInstance)
        .map(HttpServletRequest.class::cast)
        .map(req -> req.getHeader(REQUEST_UUID_KEY))
        .filter(reqUuid -> reqUuid.matches(REQUEST_UUID_REGEX))
        .orElse(UUID.randomUUID().toString());
    MDC.put(REQUEST_UUID_KEY, requestUuid);

    try {
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

}
