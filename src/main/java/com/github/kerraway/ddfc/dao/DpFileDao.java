package com.github.kerraway.ddfc.dao;

import com.github.kerraway.ddfc.model.DpFile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author kerraway
 * @date 2019/05/19
 */
public interface DpFileDao extends JpaRepository<DpFile, Long> {

  DpFile findFirstByPathAndUid(String path, Long uid);

}
