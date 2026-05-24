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

 Date: 20/12/2024 14:32:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_book_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_book_user`;
CREATE TABLE `tb_book_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint DEFAULT NULL COMMENT '账本外键',
  `user_id` bigint DEFAULT NULL COMMENT '用户外键',
  `user_name` varchar(100) DEFAULT NULL COMMENT '真实姓名',
  `really_name` varchar(100) DEFAULT NULL COMMENT '用户别名',
  `avatar_url` longtext COMMENT '头像地址',
  `default_book` tinyint DEFAULT NULL COMMENT '是否管理员权限（0-否，1是）',
  `auth` tinyint DEFAULT NULL COMMENT '是否管理员权限（0-成员，1-管理员,2群主）',
  `audit_status` tinyint DEFAULT NULL COMMENT '0-无，1-同意，2-拒绝',
  `remark` varchar(100) DEFAULT NULL COMMENT '加入账本描述备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `status` tinyint DEFAULT NULL COMMENT '0-删除，1-正常，2-禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账本用户表';

SET FOREIGN_KEY_CHECKS = 1;
