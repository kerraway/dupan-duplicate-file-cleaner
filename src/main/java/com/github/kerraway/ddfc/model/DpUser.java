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
@Table(name = "dp_users")
@EntityListeners({AuditingEntityListener.class})
public class DpUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  /**
   * uid
   */
  private Long uid;
  /**
   * 用户名
   */
  private String name;
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
