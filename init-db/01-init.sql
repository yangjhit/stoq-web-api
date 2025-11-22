-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS stoqdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE stoqdb;

-- 设置时区
SET time_zone = '+08:00';

-- 表会由Hibernate自动创建，这里只是示例
-- 如果需要手动创建表或插入初始数据，可以在这里添加

-- 示例：插入初始国家数据
-- CREATE TABLE IF NOT EXISTS countries (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(100) NOT NULL,
--     code VARCHAR(10) NOT NULL UNIQUE,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- INSERT INTO countries (name, code) VALUES 
-- ('中国', 'CN'),
-- ('美国', 'US'),
-- ('日本', 'JP')
-- ON DUPLICATE KEY UPDATE name=name;
