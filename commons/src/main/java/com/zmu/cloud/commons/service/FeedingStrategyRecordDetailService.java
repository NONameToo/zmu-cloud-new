package com.zmu.cloud.commons.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zmu.cloud.commons.entity.FarmFeedingStrategyRecordDetail;
import com.zmu.cloud.commons.entity.PigBreeding;
import com.zmu.cloud.commons.entity.PigFarm;
import com.zmu.cloud.commons.entity.PigHouseColumns;
import com.zmu.cloud.commons.vo.FeedingStrategyVo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FeedingStrategyRecordDetailService extends IService<FarmFeedingStrategyRecordDetail> {

}
