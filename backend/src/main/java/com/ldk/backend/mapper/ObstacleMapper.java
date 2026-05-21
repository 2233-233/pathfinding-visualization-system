package com.ldk.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldk.backend.entity.Obstacle;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ObstacleMapper extends BaseMapper<Obstacle> {
    
    /**
     * 根据实验记录ID删除所有障碍物
     */
    @Delete("DELETE FROM obstacle WHERE record_id = #{recordId}")
    int deleteByRecordId(@Param("recordId") Integer recordId);
    
    /**
     * 根据实验记录ID查询障碍物数量
     */
    @Select("SELECT COUNT(*) FROM obstacle WHERE record_id = #{recordId}")
    int countByRecordId(@Param("recordId") Integer recordId);
    
    /**
     * 根据实验记录ID查询障碍物列表
     */
    @Select("SELECT * FROM obstacle WHERE record_id = #{recordId} ORDER BY obstacle_id")
    List<Obstacle> selectByRecordId(@Param("recordId") Integer recordId);
    
    /**
     * 批量插入障碍物
     */
    int batchInsert(@Param("list") List<Obstacle> obstacles);
}
