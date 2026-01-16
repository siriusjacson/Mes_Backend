USE mess_db;

-- 1. Process Node Template (Renamed from t_process_template)
DROP TABLE IF EXISTS t_process_template;
DROP TABLE IF EXISTS mes_process_node_tpl;
CREATE TABLE `mes_process_node_tpl` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `model_id` BIGINT NOT NULL COMMENT '所属型号ID',
  `process_name` VARCHAR(100) NOT NULL COMMENT '工序名称',
  `std_time` INT DEFAULT 0 COMMENT '标准工时(分钟)',
  `allow_parallel` INT DEFAULT 0 COMMENT '是否允许并行',
  PRIMARY KEY (`id`)
) COMMENT='工序节点模版表';

-- 2. Process Link Template (Renamed from t_process_relation)
DROP TABLE IF EXISTS t_process_relation;
DROP TABLE IF EXISTS mes_process_link_tpl;
CREATE TABLE `mes_process_link_tpl` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `model_id` BIGINT NOT NULL COMMENT '为了查询方便冗余字段',
  `pre_process_id` BIGINT NOT NULL COMMENT '上级工序ID',
  `next_process_id` BIGINT NOT NULL COMMENT '下级工序ID',
  PRIMARY KEY (`id`)
) COMMENT='工序连线模版表';

-- 3. Project Instance (Runtime Project)
DROP TABLE IF EXISTS mes_project_instance;
CREATE TABLE `mes_project_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_no` VARCHAR(50) NOT NULL COMMENT '项目编号',
  `device_sn` VARCHAR(50) NOT NULL COMMENT '设备序列号',
  `model_id` BIGINT NOT NULL COMMENT '所属型号ID',
  `status` INT DEFAULT 0 COMMENT '0:未开始 1:进行中 2:已完成',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='生产项目实例表';

-- 4. Task Instance (Runtime Task)
DROP TABLE IF EXISTS mes_task_instance;
CREATE TABLE `mes_task_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL COMMENT '项目ID',
  `node_tpl_id` BIGINT NOT NULL COMMENT '模版节点ID',
  `task_name` VARCHAR(100) NOT NULL COMMENT '工序名称快照',
  `status` INT DEFAULT 0 COMMENT '0:锁定 1:待办 2:进行中 3:完成',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员ID',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`)
) COMMENT='运行时任务实例表';

-- 5. Work Record (Operation Log)
DROP TABLE IF EXISTS mes_work_record;
CREATE TABLE `mes_work_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `task_id` BIGINT NOT NULL COMMENT '任务ID',
  `operator_name` VARCHAR(50) COMMENT '操作员姓名',
  `action_type` VARCHAR(20) COMMENT '操作类型 (START/FINISH)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) COMMENT='操作日志表';
