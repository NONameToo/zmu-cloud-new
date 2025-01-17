package com.zmu.cloud.sysadmin.organization.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.sysadmin.organization.entity.po.Group;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface GroupMapper extends BaseMapper<Group> {
}