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

 Date: 20/12/2024 14:32:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_book_money
-- ----------------------------
DROP TABLE IF EXISTS `tb_book_money`;
CREATE TABLE `tb_book_money` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_time` date DEFAULT NULL COMMENT '记账时间',
  `book_id` bigint DEFAULT NULL COMMENT '账本id',
  `category_id` bigint DEFAULT NULL COMMENT '分类外键id',
  `category_name` varchar(100) DEFAULT NULL COMMENT '分类名',
  `user_name` varchar(100) DEFAULT NULL COMMENT '名称',
  `user_id` bigint DEFAULT NULL COMMENT '捐款人book_money表id',
  `avatar_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分类图标url',
  `money` bigint DEFAULT NULL COMMENT '金额',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `type` tinyint DEFAULT NULL COMMENT '0-支出，1-收入',
  `detail_desc` varchar(100) DEFAULT NULL COMMENT '富文本详情',
  `detail_desc_flag` varchar(100) DEFAULT NULL COMMENT '是否显示详情 0-否 1-是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `status` tinyint DEFAULT NULL COMMENT '0-删除，1-正常，2-禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账本记账金额表';

SET FOREIGN_KEY_CHECKS = 1;
