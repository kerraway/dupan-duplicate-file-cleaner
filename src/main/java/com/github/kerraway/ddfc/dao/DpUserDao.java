package com.github.kerraway.ddfc.dao;

import com.github.kerraway.ddfc.model.DpUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author kerraway
 * @date 2019/05/19
 */
public interface DpUserDao extends JpaRepository<DpUser, Integer> {

  DpUser findFirstByUid(Long uid);

}
