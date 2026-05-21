/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : pathfinding

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 14/04/2026 17:05:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for algorithm
-- ----------------------------
DROP TABLE IF EXISTS `algorithm`;
CREATE TABLE `algorithm`  (
  `algorithm_id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `complexity` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`algorithm_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of algorithm
-- ----------------------------
INSERT INTO `algorithm` VALUES (1, 'A*', 'A*', 'n');
INSERT INTO `algorithm` VALUES (2, 'Dijkstra', '最短路径算法（更新）', 'O(E+V log V)');
INSERT INTO `algorithm` VALUES (4, 'DFS', '深度优先算法', NULL);
INSERT INTO `algorithm` VALUES (5, 'BFS', '广度优先搜索算法', 'O(V + E)');
INSERT INTO `algorithm` VALUES (6, 'aaa', 'hl', '【【【');

-- ----------------------------
-- Table structure for experimentrecord
-- ----------------------------
DROP TABLE IF EXISTS `experimentrecord`;
CREATE TABLE `experimentrecord`  (
  `record_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `problem_id` int(0) NOT NULL,
  `algorithm_id` int(0) NOT NULL,
  `start_time` datetime(0) NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  `path_length` int(0) NULL DEFAULT NULL,
  `visited_count` int(0) NULL DEFAULT NULL,
  `status` enum('completed','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `problem_id`(`problem_id`) USING BTREE,
  INDEX `algorithm_id`(`algorithm_id`) USING BTREE,
  CONSTRAINT `experimentrecord_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `experimentrecord_ibfk_2` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `experimentrecord_ibfk_3` FOREIGN KEY (`algorithm_id`) REFERENCES `algorithm` (`algorithm_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for experimentstep
-- ----------------------------
DROP TABLE IF EXISTS `experimentstep`;
CREATE TABLE `experimentstep`  (
  `step_id` int(0) NOT NULL AUTO_INCREMENT,
  `record_id` int(0) NOT NULL,
  `step_index` int(0) NOT NULL,
  `node_x` int(0) NULL DEFAULT NULL,
  `node_y` int(0) NULL DEFAULT NULL,
  `node_state` enum('visited','path','unvisited') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `grid_state` json NULL,
  PRIMARY KEY (`step_id`) USING BTREE,
  INDEX `record_id`(`record_id`) USING BTREE,
  CONSTRAINT `experimentstep_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `experimentrecord` (`record_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for obstacle
-- ----------------------------
DROP TABLE IF EXISTS `obstacle`;
CREATE TABLE `obstacle`  (
  `obstacle_id` int(0) NOT NULL AUTO_INCREMENT,
  `record_id` int(0) NOT NULL,
  `x` int(0) NOT NULL COMMENT '障碍物X坐标',
  `y` int(0) NOT NULL COMMENT '障碍物Y坐标',
  `obstacle_type` enum('wall') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'wall' COMMENT '障碍物类型（可扩展）',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`obstacle_id`) USING BTREE,
  INDEX `idx_record_id`(`record_id`) USING BTREE,
  INDEX `idx_coordinates`(`x`, `y`) USING BTREE,
  CONSTRAINT `obstacle_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `experimentrecord` (`record_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '实验障碍物坐标表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for problem
-- ----------------------------
DROP TABLE IF EXISTS `problem`;
CREATE TABLE `problem`  (
  `problem_id` int(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `difficulty` enum('easy','medium','hard') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `algorithm_id` int(0) NOT NULL,
  `leetcode_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '力扣链接',
  PRIMARY KEY (`problem_id`) USING BTREE,
  INDEX `algorithm_id`(`algorithm_id`) USING BTREE,
  CONSTRAINT `problem_ibfk_1` FOREIGN KEY (`algorithm_id`) REFERENCES `algorithm` (`algorithm_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for problemcompletion
-- ----------------------------
DROP TABLE IF EXISTS `problemcompletion`;
CREATE TABLE `problemcompletion`  (
  `completion_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `problem_id` int(0) NOT NULL,
  `completion_status` enum('not_started','in_progress','completed') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'not_started',
  `attempt_count` int(0) NOT NULL DEFAULT 0,
  `is_solved` tinyint(1) NULL DEFAULT 0,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `last_review_date` date NULL DEFAULT NULL,
  `difficulty_rating` tinyint(0) NULL DEFAULT NULL,
  `time_spent_minutes` int(0) NULL DEFAULT 0,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`completion_id`) USING BTREE,
  UNIQUE INDEX `user_problem_unique`(`user_id`, `problem_id`) USING BTREE,
  INDEX `problem_id`(`problem_id`) USING BTREE,
  CONSTRAINT `problemcompletion_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `problemcompletion_ibfk_2` FOREIGN KEY (`problem_id`) REFERENCES `problem` (`problem_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('student','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
