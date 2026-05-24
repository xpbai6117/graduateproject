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

 Date: 20/12/2024 14:33:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book_budget_info
-- ----------------------------
DROP TABLE IF EXISTS `book_budget_info`;
CREATE TABLE `book_budget_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `book_id` int DEFAULT NULL COMMENT '账本id',
  `year` int DEFAULT NULL COMMENT '年份',
  `month` int DEFAULT NULL COMMENT '月份',
  `budget` bigint DEFAULT NULL COMMENT '预算',
  `status` tinyint DEFAULT NULL COMMENT '状态，0禁用1启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1880043532 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账本的预算信息';

SET FOREIGN_KEY_CHECKS = 1;
