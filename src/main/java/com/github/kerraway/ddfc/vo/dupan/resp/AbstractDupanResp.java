package com.github.kerraway.ddfc.vo.dupan.resp;

import lombok.Data;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@Data
public abstract class AbstractDupanResp {

  private Integer errno;
  private Long requestId;

  public static boolean isSuccess(AbstractDupanResp dupanResp) {
    return dupanResp != null
        && dupanResp.errno != null && dupanResp.errno == 0;
  }

}
