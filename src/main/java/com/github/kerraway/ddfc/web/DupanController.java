package com.github.kerraway.ddfc.web;

import com.github.kerraway.ddfc.service.DupanService;
import com.github.kerraway.ddfc.vo.dupan.resp.DupanListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kerraway
 * @date 2019/05/19
 */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DupanController {

  @Autowired
  private DupanService dupanService;

  /**
   * List files.
   *
   * @param bduss
   * @param stoken
   * @param bdstoken
   * @param dir
   * @return files
   */
  @GetMapping("/list_files")
  public ResponseEntity<DupanListResp> listFiles(@RequestParam String bduss, @RequestParam String stoken,
                                                 @RequestParam String bdstoken, @RequestParam String dir) {
    DupanListResp dupanListResp = dupanService.listFiles(bduss, stoken, bdstoken, dir);
    return ResponseEntity.ok(dupanListResp);
  }

  /**
   * Save files.
   *
   * @param uid
   * @param bduss
   * @param stoken
   * @param bdstoken
   * @param dir
   * @return result
   */
  @GetMapping("/save_files")
  public ResponseEntity<String> saveFiles(@RequestParam Long uid, @RequestParam String bduss, @RequestParam String stoken,
                                          @RequestParam String bdstoken, @RequestParam String dir) {
    dupanService.saveFiles(uid, bduss, stoken, bdstoken, dir);
    return ResponseEntity.ok("Please checkout logs.");
  }

}
