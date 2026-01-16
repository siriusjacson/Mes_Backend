USE mess_db;

-- 1. Modify process template table
DROP TABLE IF EXISTS t_process_template;
CREATE TABLE `t_process_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `model_id` BIGINT NOT NULL COMMENT '所属型号ID',
  `process_name` VARCHAR(100) NOT NULL COMMENT '工序名称',
  `std_time` INT DEFAULT 0 COMMENT '标准工时(分钟)',
  PRIMARY KEY (`id`)
) COMMENT='工序节点表';

-- 2. Create process relation table
CREATE TABLE `t_process_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `model_id` BIGINT NOT NULL COMMENT '为了查询方便冗余字段',
  `pre_process_id` BIGINT NOT NULL COMMENT '上级工序ID (箭头起点)',
  `next_process_id` BIGINT NOT NULL COMMENT '下级工序ID (箭头终点)',
  PRIMARY KEY (`id`)
) COMMENT='工序流程连线表';
