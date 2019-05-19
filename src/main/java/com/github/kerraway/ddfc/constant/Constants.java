package com.github.kerraway.ddfc.constant;

/**
 * @author kerraway
 * @date 2019/05/19
 */
public final class Constants {

  /**
   * 文件状态：0 正常
   */
  public static final int DP_FILE_STATE_NORMAL = 0;
  /**
   * 文件状态：1 删除
   */
  public static final int DP_FILE_STATE_DELETE = 1;

  /**
   * 文件
   */
  public static final int DUPAN_FILE_IS_DIR_NO = 0;
  /**
   * 目录
   */
  public static final int DUPAN_FILE_IS_DIR_YES = 1;

  /**
   * 目录下有子目录
   */
  public static final int DUPAN_DIR_HAS_SUBDIR = 0;
  /**
   * 目录下没有子目录
   */
  public static final int DUPAN_DIR_HAS_NO_SUBDIR = 1;


  private Constants() {
  }
}
