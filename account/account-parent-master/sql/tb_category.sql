/*
 Navicat Premium Dump SQL

 Source Server         : jizhang
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : 120.26.193.234:3306
 Source Schema         : jizhang

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 20/12/2024 14:32:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_category
-- ----------------------------
DROP TABLE IF EXISTS `tb_category`;
CREATE TABLE `tb_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint DEFAULT NULL COMMENT '账本id',
  `name` varchar(100) DEFAULT NULL COMMENT '分类名称',
  `sort` tinyint DEFAULT NULL,
  `weight_icon` varchar(100) DEFAULT NULL COMMENT '分类高亮图标url',
  `icon` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分类图标url',
  `type` tinyint DEFAULT NULL COMMENT '分类类型（0-收入 1-支出）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `status` tinyint DEFAULT NULL COMMENT '0-删除，1-正常，2-禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='分类表';

SET FOREIGN_KEY_CHECKS = 1;
