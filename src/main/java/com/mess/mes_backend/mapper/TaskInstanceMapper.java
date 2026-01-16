package com.mess.mes_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mess.mes_backend.entity.TaskInstance;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskInstanceMapper extends BaseMapper<TaskInstance> {
    @Select("select * from mes_task_instance where id = #{id} for update")
    TaskInstance selectByIdForUpdate(Long id);
}
