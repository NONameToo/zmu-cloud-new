package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zmu.cloud.commons.entity.Banner;
import com.zmu.cloud.commons.entity.PigBackFatTaskDetail;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.entity.PigHouseRows;
import com.zmu.cloud.commons.enums.BackFatTaskDetailStatus;
import com.zmu.cloud.commons.mapper.BannerMapper;
import com.zmu.cloud.commons.mapper.PigBackFatTaskDetailMapper;
import com.zmu.cloud.commons.mapper.PigHouseRowsMapper;
import com.zmu.cloud.commons.service.BackFatTaskDetailService;
import com.zmu.cloud.commons.service.BannerService;
import com.zmu.cloud.commons.service.PigHouseRowsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackFatTaskDetailServiceImpl extends ServiceImpl<PigBackFatTaskDetailMapper, PigBackFatTaskDetail>
        implements BackFatTaskDetailService {

    final PigBackFatTaskDetailMapper backFatTaskDetailMapper;

    @Override
    public void saveBatch(List<PigBackFatTaskDetail> details) {
        super.saveBatch(details);
    }

    @Override
    public PigBackFatTaskDetail findListByColId(Long taskId, PigHouseColumns col, BackFatTaskDetailStatus status) {
        LambdaQueryWrapper<PigBackFatTaskDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PigBackFatTaskDetail::getTaskId, taskId).eq(PigBackFatTaskDetail::getPigHouseColumnId, col.getId());
        if (ObjectUtil.isNotEmpty(status)) {
            wrapper.eq(PigBackFatTaskDetail::getStatus, status);
        }
        return backFatTaskDetailMapper.selectOne(wrapper);
    }

    @Override
    public String listByUndetectedField(Long taskId) {
        LambdaQueryWrapper<PigBackFatTaskDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PigBackFatTaskDetail::getTaskId, taskId);
        wrapper.orderByAsc(PigBackFatTaskDetail::getColumnPosition);
        List<PigBackFatTaskDetail> details = backFatTaskDetailMapper.selectList(wrapper);
        details.replaceAll(detail -> detail.getStatus().equals(BackFatTaskDetailStatus.Undetected.name())?detail:null);
        StrBuilder builder = StrBuilder.create();
        getUndetectedFiledCodeStr(details, builder);
        return builder.toString();
    }

    @Override
    public List<PigBackFatTaskDetail> findList(Long taskId, Long colId, BackFatTaskDetailStatus status,
                                                        Integer page, Integer size) {
        LambdaQueryWrapper<PigBackFatTaskDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PigBackFatTaskDetail::getTaskId, taskId);
        if (ObjectUtil.isNotEmpty(colId)) {
            wrapper.eq(PigBackFatTaskDetail::getPigHouseColumnId, colId);
        }
        if (ObjectUtil.isNotEmpty(status)) {
            wrapper.eq(PigBackFatTaskDetail::getStatus, status);
        }
        wrapper.orderByAsc(PigBackFatTaskDetail::getColumnPosition);
        return backFatTaskDetailMapper.selectList(wrapper);
    }

    private void getUndetectedFiledCodeStr(List<PigBackFatTaskDetail> details, StrBuilder builder) {
        //去掉头部空值
        removeHeadToNull(details);
        int index = details.indexOf(null);
        if (index >= 0) {
            List<PigBackFatTaskDetail> unprocesseds = details.subList(0, index);
            if (!CollectionUtils.isEmpty(unprocesseds)) {
                builder.append(unprocesseds.get(0).getColumnPosition())
                        .append(" ~ ")
                        .append(unprocesseds.get(unprocesseds.size()-1).getColumnPosition())
                        .append("、");
            }
            getUndetectedFiledCodeStr(details.subList(index+1, details.size()), builder);
        } else {
            if (!CollectionUtils.isEmpty(details)) {
                builder.append(details.get(0).getColumnPosition())
                        .append(" ~ ")
                        .append(details.get(details.size()-1).getColumnPosition());
            }
        }
    }

    private static void removeHeadToNull(List list) {
        if (list.size() > 0 && list.get(0) == null) {
            list.remove(0);
            removeHeadToNull(list);
        }
    }
}
