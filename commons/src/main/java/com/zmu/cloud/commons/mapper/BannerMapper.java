package com.zmu.cloud.commons.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmu.cloud.commons.entity.Banner;
import com.zmu.cloud.commons.vo.BannerVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BannerMapper extends BaseMapper<Banner> {

    List<Banner> adminList(@Param("position") Integer position, @Param("status") Integer status);

    List<BannerVO> list(@Param("position") Integer position);
}