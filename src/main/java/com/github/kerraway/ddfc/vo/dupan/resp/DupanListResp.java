package com.github.kerraway.ddfc.vo.dupan.resp;

import com.github.kerraway.ddfc.util.JacksonUtil;
import com.github.kerraway.ddfc.vo.dupan.DupanFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DupanListResp extends AbstractDupanResp {

  private List<DupanFile> list;
  private Integer guid;
  private String guidInfo;

  @Override
  public String toString() {
    return JacksonUtil.writeValue(this);
  }

  public static boolean isSuccess(DupanListResp dupanListResp) {
    return isSuccess((AbstractDupanResp) dupanListResp)
        && dupanListResp.getList() != null;
  }
}
