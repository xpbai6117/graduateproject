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

 Date: 20/12/2024 14:33:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(100) DEFAULT NULL COMMENT '用户在当前小程序的唯一标识',
  `nick_name` varchar(100) DEFAULT NULL COMMENT '用户微信昵称',
  `gender` tinyint DEFAULT NULL COMMENT '性别，0-未知、1-男性、2-女性',
  `tel` varchar(20) DEFAULT NULL COMMENT '手机',
  `default_book` varchar(100) DEFAULT NULL COMMENT '默认账本',
  `avatar_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '头像地址',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `user_account` varchar(100) DEFAULT NULL COMMENT '用户名',
  `pwd` varchar(100) DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';

SET FOREIGN_KEY_CHECKS = 1;
