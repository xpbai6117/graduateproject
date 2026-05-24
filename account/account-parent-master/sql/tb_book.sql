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

 Date: 20/12/2024 14:32:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_book
-- ----------------------------
DROP TABLE IF EXISTS `tb_book`;
CREATE TABLE `tb_book` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_name` varchar(100) DEFAULT NULL COMMENT '账本名称',
  `book_avatar` varchar(100) DEFAULT NULL COMMENT '账本图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `user_audit` tinyint DEFAULT NULL COMMENT '成员加入账本是否需要审核（0-不需要 1-需要管理员审核）',
  `show_search` tinyint DEFAULT NULL COMMENT '是否允许对外搜索私密账本（0-私密不允许，1-公开允许）',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `status` tinyint DEFAULT NULL COMMENT '0-删除，1-正常，2-禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Book对象';

SET FOREIGN_KEY_CHECKS = 1;
