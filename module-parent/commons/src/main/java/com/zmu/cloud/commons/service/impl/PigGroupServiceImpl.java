package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.entity.PigGroup;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.mapper.PigGroupMapper;
import com.zmu.cloud.commons.mapper.PigHouseColumnsMapper;
import com.zmu.cloud.commons.service.PigGroupService;
import com.zmu.cloud.commons.service.PigHouseColumnsService;
import com.zmu.cloud.commons.vo.PigGroupListVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PigGroupServiceImpl extends ServiceImpl<PigGroupMapper, PigGroup>
        implements PigGroupService {

    public List<PigGroupListVO> list(Long pigHouseColumnsId) {

        return baseMapper.list(pigHouseColumnsId);
    }

    @Override
    public List<PigGroupListVO> listNew(Long pigHouseId) {
        return baseMapper.listNew(pigHouseId);
    }
}
