package com.github.kerraway.ddfc.service;

import com.github.kerraway.ddfc.client.DupanClient;
import com.github.kerraway.ddfc.dao.DpFileDao;
import com.github.kerraway.ddfc.dao.DpUserDao;
import com.github.kerraway.ddfc.model.DpFile;
import com.github.kerraway.ddfc.model.DpUser;
import com.github.kerraway.ddfc.util.BeanUtil;
import com.github.kerraway.ddfc.vo.dupan.DupanFile;
import com.github.kerraway.ddfc.vo.dupan.resp.DupanListResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.kerraway.ddfc.constant.Constants.DP_FILE_STATE_NORMAL;
import static com.github.kerraway.ddfc.constant.Constants.DUPAN_FILE_IS_DIR_YES;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@Slf4j
@Service
public class DupanService {

  @Autowired
  private DpUserDao dpUserDao;
  @Autowired
  private DpFileDao dpFileDao;
  @Autowired
  private DupanClient dupanClient;

  /**
   * Random instance
   */
  private static final Random RANDOM = new Random();

  /**
   * List files.
   *
   * @param bduss
   * @param stoken
   * @param bdstoken
   * @param dir
   * @return files
   */
  public DupanListResp listFiles(String bduss, String stoken, String bdstoken, String dir) {
    String cookies = buildCookies(bduss, stoken);
    return dupanClient.listFiles(cookies, bdstoken, encode(dir));
  }

  /**
   * Save files into DB.
   *
   * @param uid
   * @param bduss
   * @param stoken
   * @param bdstoken
   * @param dir
   */
  @Async
  public void saveFiles(Long uid, String bduss, String stoken, String bdstoken, String dir) {
    DpUser dpUser = dpUserDao.findFirstByUid(uid);
    if (dpUser == null) {
      logger.error("Can't find matching dp user, uid {} may be wrong.", uid);
      return;
    }

    Instant start = Instant.now();
    logger.info("Save files into DB start, user uid: {}, name: {}, dir: {}.", uid, dpUser.getName(), dir);
    try {
      String cookies = buildCookies(bduss, stoken);
      saveFiles(uid, cookies, bdstoken, dir);
    } finally {
      logger.info("Save files into DB end, user uid: {}, name: {}, dir: {}, use {} s.",
          uid, dpUser.getName(), dir, Duration.between(start, Instant.now()).getSeconds());
    }
  }

  /**
   * Save files into DB recursively.
   *
   * @param uid
   * @param cookies
   * @param bdstoken
   * @param dir
   */
  private void saveFiles(Long uid, String cookies, String bdstoken, String dir) {
    List<DupanFile> dupanFiles = listFiles(cookies, bdstoken, dir);
    if (dupanFiles.isEmpty()) {
      return;
    }

    //sleep a while
    try {
      TimeUnit.MILLISECONDS.sleep(RANDOM.nextInt(500));
    } catch (InterruptedException e) {
      //ignore
    }

    //traverse files
    for (DupanFile dupanFile : dupanFiles) {
      //save into DB
      saveFile(uid, dupanFile);
      //dig into dir
      if (Objects.equals(DUPAN_FILE_IS_DIR_YES, dupanFile.getIsdir())) {
        saveFiles(uid, cookies, bdstoken, dupanFile.getPath());
      }
    }
  }

  /**
   * Save single file into DB.
   *
   * @param uid
   * @param dupanFile
   */
  private void saveFile(Long uid, DupanFile dupanFile) {
    DpFile dpFile = dpFileDao.findFirstByPathAndUid(dupanFile.getPath(), uid);
    if (dpFile == null) {
      dpFile = new DpFile();
      dpFile.setState(DP_FILE_STATE_NORMAL);
      dpFile.setUid(uid);
    }
    BeanUtil.copyPropertiesIgnoreNull(dupanFile, dpFile);
    try {
      dpFileDao.save(dpFile);
    } catch (Exception e) {
      logger.error("Save dp file {} into DB error.", dpFile, e);
    }
  }

  /**
   * List files.
   *
   * @param cookies
   * @param bdstoken
   * @param dir
   * @return files
   */
  private List<DupanFile> listFiles(String cookies, String bdstoken, String dir) {
    DupanListResp dupanListResp = dupanClient.listFiles(cookies, bdstoken, encode(dir));
    if (DupanListResp.isSuccess(dupanListResp)) {
      return dupanListResp.getList();
    }
    logger.warn("Dupan list resp isn't success, dir: {}, resp: {}.", dir, dupanListResp);
    return Collections.emptyList();
  }

  /**
   * Cookie:
   * PANWEB=1;
   * BAIDUID=99100902B0CF6C5E3A37EA77E367496C:FG=1;
   * ZD_ENTRY=google;
   * locale=zh;
   * pgv_pvi=7524517888;
   * pgv_si=s4076949504;
   * BIDUPSID=99100902B0CF6C5E3A37EA77E367496C;
   * PSTM=1547825897;
   * pan_login_way=1;
   * Hm_lvt_7a3960b6f067eb0085b7f96ff5e660b0=1548988159;
   * Hm_lpvt_7a3960b6f067eb0085b7f96ff5e660b0=1550815686;
   * BDUSS=NsV09ORk91Q291Qk03aVBCMGQ2UzQ0VjQ1QU03aTliRFJEUkFubE96Vlk5ZHRjRVFBQUFBJCQAAAAAAAAAAAEAAABEunLOZ2VlZWVneQAAAA
   * AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFhotFxYaLRcTE;
   * H_PS_PSSID=1457_28939_21085_28775_28723_28833_28585_22160;
   * SCRC=67fd2a2c8ca52268066d818aeef49963;
   * STOKEN=e14fa3563d71959738168962688e13e342e16444f000824ada70b96dc1707d3b;
   * recommendTime=guanjia2019-05-16%2016%3A00%3A00;
   * cflag=13%3A3;
   * BDCLND=hSwTLuI53F1viM1RPgPFktOFImCzgC5i;
   * PANPSC=12145588460388199737%3AXSPawFGHec4mmB5TZp%2BpQKeLxxr5GVy0wnxiHpMJXrkI5%2BB0eenXjzWx8ty9ofcmhKwza%2FXPOrSDSY
   * hl0sel1F%2BC9%2BTZul25O8kmguT8x%2BZfZeVSwpGLRrBu2GS532flr%2Fo2LS0zEd5CNDWYUdN3xbnkjkK9qX6SfW3xVeVF9lU%3D
   *
   * @param bduss
   * @param stoken
   * @return cookies
   */
  private String buildCookies(String bduss, String stoken) {
    Map<String, String> cookies = new HashMap<>(10);
    cookies.put("PANWEB", "1");
    cookies.put("ZD_ENTRY", "google");
    cookies.put("locale", "zh");
    cookies.put("pan_login_way", "1");
    cookies.put("BDUSS", bduss);
    cookies.put("STOKEN", stoken);

    return cookies.entrySet().stream()
        .map(entry -> String.format("%s=%s;", entry.getKey(), entry.getValue()))
        .collect(Collectors.joining("; "));
  }

  /**
   * Encode dir.
   *
   * @param dir
   * @return encoded dir
   */
  private String encode(String dir) {
    try {
      return URLEncoder.encode(dir, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      logger.warn("UnsupportedEncodingException occurred.", e);
    }
    return dir;
  }

}
