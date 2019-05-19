package com.github.kerraway.ddfc.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dp_files")
@EntityListeners({AuditingEntityListener.class})
public class DpFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  /**
   * 状态：0 正常，1 删除
   * 仅限通过本应用删除的文件的状态，其他方式删除不会记录
   */
  private Integer state;
  /**
   * 所属用户 uid
   *
   * @see DpUser#uid
   */
  private Long uid;
  /**
   * 文件 ID
   */
  private Long fsId;
  /**
   * 文件路径
   */
  private String path;
  /**
   * 文件名称
   */
  private String serverFilename;
  /**
   * 文件 md5
   */
  private String md5;
  /**
   * 是否为目录：0 文件，1 目录
   */
  private Integer isdir;
  /**
   * 是否含有子目录，0 有，1 无
   */
  private Integer hasSubdir;
  /**
   * TODO 暂时不知道是什么 empty
   */
  private Integer empty;
  /**
   * 文件分类
   */
  private Integer category;
  /**
   * 文件大小，单位是字节
   */
  private Long size;
  /**
   * TODO 暂时不知道什么 ID
   */
  private Long operId;
  /**
   * 服务器创建时间
   */
  private Long serverCtime;
  /**
   * 服务器修改时间
   */
  private Long serverMtime;
  /**
   * 本地创建时间
   */
  private Long localCtime;
  /**
   * 本地修改时间
   */
  private Long localMtime;
  /**
   * 创建时间
   */
  @CreatedDate
  private Date createdAt;
  /**
   * 更新时间
   */
  @LastModifiedDate
  private Date updatedAt;

}
