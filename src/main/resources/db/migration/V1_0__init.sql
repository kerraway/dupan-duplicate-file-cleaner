# dp_users 表
# author kerraway
# date 2019/05/19
CREATE TABLE `dp_users` (
    `id`         INT         NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `uid`        BIGINT      NOT NULL COMMENT 'uid',
    `name`       VARCHAR(50) NOT NULL COMMENT '用户名',
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ix_uid` (`uid`),
    UNIQUE KEY `ix_name` (`name`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '度盘用户表';


# dp_files 表
# author kerraway
# date 2019/05/19
CREATE TABLE `dp_files` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `state`           TINYINT      NOT NULL COMMENT '状态：0 正常，1 删除',
    `uid`             BIGINT       NOT NULL COMMENT '所属用户 uid',
    `fs_id`           BIGINT       NOT NULL COMMENT '文件 ID',
    `path`            VARCHAR(750) NOT NULL COMMENT '文件路径',
    `server_filename` VARCHAR(250) NOT NULL COMMENT '文件名称',
    `md5`             VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '文件 md5',
    `isdir`           TINYINT      NOT NULL COMMENT '是否为目录：0 文件，1 目录',
    `has_subdir`      TINYINT      NOT NULL DEFAULT '-1' COMMENT '是否含有子目录，0 有，1 无',
    `empty`           TINYINT      NOT NULL DEFAULT '-1' COMMENT '未知 empty',
    `category`        TINYINT      NOT NULL COMMENT '文件分类',
    `size`            BIGINT       NOT NULL COMMENT '文件大小，单位是字节',
    `oper_id`         BIGINT       NOT NULL COMMENT '未知 ID',
    `server_ctime`    BIGINT       NOT NULL COMMENT '服务器创建时间',
    `server_mtime`    BIGINT       NOT NULL COMMENT '服务器修改时间',
    `local_ctime`     BIGINT       NOT NULL COMMENT '本地创建时间',
    `local_mtime`     BIGINT       NOT NULL COMMENT '本地修改时间',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ix_path_uid` (`path`, `uid`),
    KEY `ix_state_uid` (`state`, `uid`),
    KEY `ix_md5_size_uid` (`md5`, `size`, `uid`)
)ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '度盘文件表';

