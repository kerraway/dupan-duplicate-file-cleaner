package com.github.kerraway.ddfc.vo.dupan;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@Data
public class DupanFile {

  private Long fsId;
  private String path;
  private String serverFilename;
  private String md5;
  private Integer isdir;
  @JsonSetter("dir_empty")
  private Integer hasSubdir;
  private Integer empty;
  private Integer category;
  private Long size;
  private Long operId;
  private Long serverCtime;
  private Long serverMtime;
  private Long localCtime;
  private Long localMtime;

  private Integer unlist;
  private Integer share;

}
