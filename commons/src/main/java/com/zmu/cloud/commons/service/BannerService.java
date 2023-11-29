package com.zmu.cloud.commons.service;

import com.github.pagehelper.PageInfo;
import com.zmu.cloud.commons.dto.commons.Page;
import com.zmu.cloud.commons.entity.Banner;
import com.zmu.cloud.commons.vo.BannerVO;
import java.util.List;

public interface BannerService {

    Long add(Banner banner);

    boolean update(Banner banner);

    boolean delete(Long id);

    PageInfo<Banner> adminList(Page page, Integer position, Integer status);

    List<BannerVO> list(Integer position);
}
