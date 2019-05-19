package com.github.kerraway.ddfc.client;

import com.github.kerraway.ddfc.client.config.FeignConfig4Dupan;
import com.github.kerraway.ddfc.vo.dupan.resp.DupanListResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@FeignClient(value = "dupan-client", url = "https://pan.baidu.com", configuration = FeignConfig4Dupan.class)
public interface DupanClient {

  /**
   * List files.
   *
   * @param cookies
   * @param bdstoken
   * @param dir
   * @return files resp
   */
  @GetMapping(value = "/api/list?logid=MTU1ODI1MDQyMjk2ODAuNjI0ODk3MDE4NjI5MzQ4MQ==&order=time&desc=1" +
      "&clienttype=0&showempty=0&web=1&page=1&num=2000&channel=chunlei&web=1&app_id=250528")
  DupanListResp listFiles(@RequestHeader(HttpHeaders.COOKIE) String cookies,
                          @RequestParam("bdstoken") String bdstoken, @RequestParam("dir") String dir);

}
