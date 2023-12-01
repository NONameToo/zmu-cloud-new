package com.zmu.cloud.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.commons.Page;
import com.zmu.cloud.commons.entity.Banner;
import com.zmu.cloud.commons.mapper.BannerMapper;
import com.zmu.cloud.commons.service.BannerService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.BannerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public Long add(Banner banner) {
        banner.setCreateBy(RequestContextUtils.getUserId());
        bannerMapper.insert(banner);
        return banner.getId();
    }

    @Override
    public boolean update(Banner banner) {
        banner.setUpdateBy(RequestContextUtils.getUserId());
        return bannerMapper.updateById(banner) > 0;
    }

    @Override
    public boolean delete(Long id) {
        LambdaUpdateWrapper<Banner> wrapper = new LambdaUpdateWrapper<Banner>()
                .set(Banner::getUpdateBy, RequestContextUtils.getUserId())
                .set(Banner::isDel, true)
                .eq(Banner::getId, id);
        return update(wrapper);
    }

    @Override
    public PageInfo<Banner> adminList(Page page, Integer position, Integer status) {
        PageHelper.startPage(page.getPage(), page.getSize());
        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();
        if (position != null) {
            queryWrapper.eq(Banner::getPosition, position);
        }
        if (status != null) {
            queryWrapper.eq(Banner::getStatus, status);
        }
        queryWrapper.orderByDesc(Banner::getSort).orderByDesc(Banner::getId);
        return PageInfo.of(bannerMapper.selectList(queryWrapper));
    }

    @Override
    public List<BannerVO> list(Integer position) {
        return bannerMapper.list(position);
    }
}
