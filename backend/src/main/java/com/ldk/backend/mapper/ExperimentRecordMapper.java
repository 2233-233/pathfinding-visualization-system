package com.ldk.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ldk.backend.commons.R;
import com.ldk.backend.entity.ExperimentRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExperimentRecordMapper extends BaseMapper<ExperimentRecord> {

}
