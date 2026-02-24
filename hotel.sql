-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `house_rental` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_general_ci;

-- 2. 使用数据库
USE `house_rental`;

-- 1. 用户表（最核心）
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `role_id` BIGINT COMMENT '角色ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 角色表（用于权限控制）
CREATE TABLE `role` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码（ADMIN/LANDLORD/TENANT）',
    `description` VARCHAR(200) COMMENT '角色描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 插入基础角色数据
INSERT INTO `role` (`role_name`, `role_code`, `description`) VALUES 
('管理员', 'ADMIN', '系统管理员，拥有所有权限'),
('房东', 'LANDLORD', '房源发布者，管理自己的房源'),
('租客', 'TENANT', '租房用户，浏览房源、下单');

-- 4. 插入一个测试用户（方便明天登录测试）
-- 密码是 123456 的加密结果
INSERT INTO `user` (`username`, `password`, `phone`, `real_name`, `role_id`, `status`) VALUES 
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13800000001', '管理员', 1, 1),
('landlord1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13800000002', '张房东', 2, 1),
('tenant1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '13900000001', '王租客', 3, 1);

-- 房源表
CREATE TABLE `house` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房源ID',
    `title` VARCHAR(100) NOT NULL COMMENT '房源标题',
    `description` TEXT COMMENT '房源描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '月租金（元）',
    `area` DECIMAL(8,2) COMMENT '面积（平方米）',
    `bedroom_count` INT DEFAULT 1 COMMENT '卧室数量',
    `living_room_count` INT DEFAULT 1 COMMENT '客厅数量',
    `bathroom_count` INT DEFAULT 1 COMMENT '卫生间数量',
    `floor` VARCHAR(20) COMMENT '所在楼层',
    `property_type` VARCHAR(20) COMMENT '房产类型',
    `decoration` VARCHAR(20) COMMENT '装修情况',
    `orientation` VARCHAR(10) COMMENT '朝向',
    `province` VARCHAR(50) COMMENT '省份',
    `city` VARCHAR(50) COMMENT '城市',
    `district` VARCHAR(50) COMMENT '区域',
    `address` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `landlord_id` BIGINT NOT NULL COMMENT '房东ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核，1-已上架，2-已下架，3-已出租',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_landlord_id` (`landlord_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_city_district` (`city`, `district`),
    INDEX `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源表';

-- 房源图片表
CREATE TABLE `house_image` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `house_id` BIGINT NOT NULL COMMENT '房源ID',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `type` TINYINT DEFAULT 1 COMMENT '类型：1-封面图，2-普通图',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源图片表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `tag_code` VARCHAR(50) COMMENT '标签编码（如：near_subway）',
    `category` VARCHAR(50) COMMENT '标签分类（交通/装修/配套）',
    `icon` VARCHAR(200) COMMENT '图标URL',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_tag_code` (`tag_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 房源标签关联表
CREATE TABLE IF NOT EXISTS `house_tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `house_id` BIGINT NOT NULL COMMENT '房源ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_house_id` (`house_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源标签关联表';

-- 用户行为表（用于构建用户画像）
CREATE TABLE IF NOT EXISTS `user_behavior` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `house_id` BIGINT NOT NULL COMMENT '房源ID',
    `behavior_type` TINYINT NOT NULL COMMENT '行为类型：1-浏览，2-收藏，3-咨询，4-分享',
    `weight` FLOAT DEFAULT 1.0 COMMENT '行为权重（收藏比浏览权重大）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_house_id` (`house_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表';

-- 用户画像表（存储用户偏好标签）
CREATE TABLE IF NOT EXISTS `user_profile` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `preferred_tags` JSON COMMENT '偏好标签及权重（如：{"近地铁":0.8, "精装修":0.6}）',
    `price_min` DECIMAL(10,2) COMMENT '偏好价格下限',
    `price_max` DECIMAL(10,2) COMMENT '偏好价格上限',
    `preferred_districts` JSON COMMENT '偏好区域（如：["朝阳区","海淀区"]）',
    `last_update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

-- 插入基础标签
INSERT INTO `tag` (`tag_name`, `tag_code`, `category`, `sort`) VALUES 
('近地铁', 'near_subway', '交通', 1),
('精装修', 'fine_decorate', '装修', 2),
('拎包入住', 'move_in_ready', '配套', 3),
('朝南', 'south_facing', '朝向', 4),
('独立卫浴', 'private_bath', '配套', 5),
('可短租', 'short_term', '租期', 6),
('免中介费', 'no_fee', '费用', 7),
('民水民电', 'residential_utility', '费用', 8),
('有电梯', 'has_elevator', '配套', 9),
('车位', 'parking', '配套', 10),
('首次出租', 'first_rent', '房源', 11),
('随时看房', 'available_anytime', '服务', 12);

-- 为已有的房源添加标签（示例，根据你的实际ID调整）
INSERT INTO `house_tag` (`house_id`, `tag_id`) VALUES 
(4, 1), (4, 2), (4, 3), (4, 4);  -- 房源4：近地铁、精装修、拎包入住、朝南