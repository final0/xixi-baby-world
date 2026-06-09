-- ================================================
-- 汐汐的小窝 - 数据库初始化 SQL
-- ================================================

CREATE DATABASE IF NOT EXISTS `xixi_world`
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `xixi_world`;

CREATE TABLE IF NOT EXISTS `t_user` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `username`     VARCHAR(50)  NOT NULL,
    `password`     VARCHAR(255) NOT NULL,
    `email`        VARCHAR(100) NOT NULL,
    `nickname`     VARCHAR(50)  DEFAULT NULL,
    `role`         VARCHAR(20)  NOT NULL DEFAULT 'other',
    `role_motto`   VARCHAR(100) DEFAULT NULL,
    `avatar_url`   VARCHAR(500) DEFAULT NULL,
    `phone`        VARCHAR(20)  DEFAULT NULL,
    `is_admin`     TINYINT(1)   NOT NULL DEFAULT 0,
    `first_login`  TINYINT(1)   NOT NULL DEFAULT 1,
    `status`       TINYINT(1)   NOT NULL DEFAULT 1,
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `t_baby_info` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`          VARCHAR(50)  NOT NULL DEFAULT '汐汐',
    `birthday`      DATE         NOT NULL,
    `birth_weight`  DECIMAL(5,2) DEFAULT NULL,
    `birth_height`  DECIMAL(5,2) DEFAULT NULL,
    `avatar_url`    VARCHAR(500) DEFAULT NULL,
    `intro`         VARCHAR(500) DEFAULT NULL,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宝宝基本信息';

CREATE TABLE IF NOT EXISTS `t_photo` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `uploader_id` BIGINT       NOT NULL,
    `title`       VARCHAR(100) DEFAULT NULL,
    `description` VARCHAR(500) DEFAULT NULL,
    `url`         VARCHAR(500) NOT NULL,
    `thumbnail`   VARCHAR(500) DEFAULT NULL,
    `taken_at`    DATE         DEFAULT NULL,
    `is_featured` TINYINT(1)   NOT NULL DEFAULT 0,
    `sort_order`  INT          NOT NULL DEFAULT 0,
    `status`      TINYINT(1)   NOT NULL DEFAULT 1,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_uploader` (`uploader_id`),
    INDEX `idx_featured` (`is_featured`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='照片表';

CREATE TABLE IF NOT EXISTS `t_video` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `uploader_id` BIGINT       NOT NULL,
    `title`       VARCHAR(100) NOT NULL,
    `description` VARCHAR(500) DEFAULT NULL,
    `url`         VARCHAR(500) NOT NULL,
    `cover_url`   VARCHAR(500) DEFAULT NULL,
    `duration`    INT          DEFAULT NULL,
    `taken_at`    DATE         DEFAULT NULL,
    `status`      TINYINT(1)   NOT NULL DEFAULT 1,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_uploader` (`uploader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频表';

CREATE TABLE IF NOT EXISTS `t_diary` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `author_id`   BIGINT       NOT NULL,
    `title`       VARCHAR(100) NOT NULL,
    `content`     TEXT         NOT NULL,
    `diary_date`  DATE         NOT NULL,
    `mood`        VARCHAR(20)  DEFAULT NULL,
    `status`      TINYINT(1)   NOT NULL DEFAULT 1,
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_author` (`author_id`),
    INDEX `idx_date` (`diary_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成长日记表';

CREATE TABLE IF NOT EXISTS `t_motto` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `content`    VARCHAR(200) NOT NULL,
    `author`     VARCHAR(50)  DEFAULT NULL,
    `is_active`  TINYINT(1)   NOT NULL DEFAULT 1,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专属小短句表';

CREATE TABLE IF NOT EXISTS `t_system_config` (
    `id`           INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `config_key`   VARCHAR(100) NOT NULL,
    `config_value` TEXT         DEFAULT NULL,
    `description`  VARCHAR(200) DEFAULT NULL,
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

CREATE TABLE IF NOT EXISTS `t_ai_chat` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`    BIGINT       NOT NULL,
    `session_id` VARCHAR(64)  NOT NULL,
    `role`       VARCHAR(10)  NOT NULL,
    `content`    TEXT         NOT NULL,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_session` (`session_id`),
    INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录表';

-- 初始化数据
-- 管理员账号（密码: Admin@123）
INSERT IGNORE INTO `t_user` (`username`,`password`,`email`,`nickname`,`role`,`role_motto`,`is_admin`,`first_login`,`status`)
VALUES ('admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTpyM.UYXNW','admin@xixi.com','管理员','other','我们都爱汐汐 🌸',1,0,1);

-- 宝宝初始信息（请修改为实际日期）
INSERT IGNORE INTO `t_baby_info` (`id`,`name`,`birthday`,`birth_weight`,`birth_height`,`intro`)
VALUES (1,'汐汐','2024-01-01',3.25,50.0,'我们家最可爱的小宝贝 🌸');

-- 小短句初始数据
INSERT IGNORE INTO `t_motto` (`content`, `author`) VALUES
('每一天都是新的奇迹，汐汐加油 🌸', '妈妈'),
('你的笑容是我们最大的幸福 💕', '爸爸'),
('小小的你，大大的世界等着你探索 ✨', '爷爷'),
('汐汐每天都在进步，我们都看在眼里 🎉', '奶奶'),
('愿你永远健康快乐，平安喜乐 🍀', '外公'),
('最美好的事，就是看着你长大 🌙', '外婆'),
('好奇心是最棒的礼物，保持它哦 🔭', NULL),
('今天的你，是明天最好的起点 🌅', NULL),
('汐汐的每一步都走进了我们心里 👣', NULL),
('家人永远在你身边 💞', NULL);

-- 系统配置初始键
INSERT IGNORE INTO `t_system_config` (`config_key`, `description`) VALUES
('smtp_host','SMTP 服务器地址'),('smtp_port','SMTP 端口'),
('smtp_username','发件邮箱账号'),('smtp_password','SMTP 授权码'),
('smtp_ssl','是否启用 SSL'),('ai_api_key','Deepseek API Key'),
('ai_base_url','AI API 基础地址'),('ai_model','AI 模型名称');
