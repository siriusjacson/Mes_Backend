CREATE DATABASE IF NOT EXISTS mess_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE mess_db;

-- 1. 设备型号表 (比如：iPhone 15, 特斯拉Model 3)
CREATE TABLE IF NOT EXISTS `t_device_model` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_name` VARCHAR(100) NOT NULL COMMENT '型号名称',
  `model_code` VARCHAR(50) NOT NULL COMMENT '型号编码',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) COMMENT='设备型号表';

-- 2. 工序模版表 (比如：第一步安装底座，第二步安装屏幕)
CREATE TABLE IF NOT EXISTS `t_process_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `model_id` BIGINT NOT NULL COMMENT '所属型号ID',
  `process_name` VARCHAR(100) NOT NULL COMMENT '工序名称',
  `process_seq` INT NOT NULL COMMENT '顺序号，1,2,3...',
  `std_time` INT DEFAULT 0 COMMENT '标准工时(分钟)',
  PRIMARY KEY (`id`)
) COMMENT='工序模版表';

-- 3. 项目生产实例表 (实际要生产的一台设备)
CREATE TABLE IF NOT EXISTS `t_project_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_code` VARCHAR(50) NOT NULL COMMENT '项目编号',
  `model_id` BIGINT NOT NULL COMMENT '生产什么型号',
  `device_sn` VARCHAR(100) COMMENT '设备唯一序列号(二维码内容)',
  `status` INT DEFAULT 0 COMMENT '0:未开始 1:进行中 2:已完成',
  PRIMARY KEY (`id`)
) COMMENT='项目实例表';

-- 4. 工序任务实例表 (Runtime Task)
CREATE TABLE IF NOT EXISTS `mes_task_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `node_tpl_id` BIGINT NOT NULL COMMENT '关联的模版ID',
  `node_name` VARCHAR(64) NOT NULL COMMENT '冗余名称防模版修改',
  `status` INT DEFAULT 0 COMMENT '0:锁定(不可做) 1:待执行(可抢) 2:进行中 3:已完成',
  `start_time` DATETIME,
  `end_time` DATETIME,
  PRIMARY KEY (`id`)
);

-- 5. 作业流水记录表 (解决多人协作)
CREATE TABLE IF NOT EXISTS `mes_work_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `task_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `user_name` VARCHAR(32),
  `action_type` TINYINT COMMENT '1:开始 2:暂停 3:完成',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- 6. 工序模版表 (定义节点)
CREATE TABLE IF NOT EXISTS `mes_process_node_tpl` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `model_id` BIGINT NOT NULL COMMENT '所属设备型号ID',
  `node_name` VARCHAR(64) NOT NULL COMMENT '工序名称，如：底座安装',
  `std_time` INT COMMENT '标准工时(分钟)',
  `allow_parallel` TINYINT DEFAULT 1 COMMENT '是否允许多人同时作业',
  PRIMARY KEY (`id`)
);

-- 7. 工序关系模版表 (定义连线，核心DAG结构)
CREATE TABLE IF NOT EXISTS `mes_process_link_tpl` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `model_id` BIGINT NOT NULL,
  `prev_node_id` BIGINT NOT NULL COMMENT '上级工序ID',
  `next_node_id` BIGINT NOT NULL COMMENT '下级工序ID',
  PRIMARY KEY (`id`)
);

-- 8. 生产项目实例表 (对应二维码 - Duplicate of t_project_instance but kept for completeness based on Gemini)
CREATE TABLE IF NOT EXISTS `mes_project_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_no` VARCHAR(64) NOT NULL COMMENT '项目编号',
  `device_sn` VARCHAR(64) NOT NULL COMMENT '设备序列号，也是二维码内容',
  `model_id` BIGINT NOT NULL COMMENT '对应的设备型号',
  `status` INT DEFAULT 0 COMMENT '0:未开始 1:进行中 2:已完成',
  PRIMARY KEY (`id`)
);
