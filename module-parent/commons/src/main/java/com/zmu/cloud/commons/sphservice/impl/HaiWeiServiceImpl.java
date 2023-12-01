package com.zmu.cloud.commons.sphservice.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmu.cloud.commons.entity.HaiweiConfig;
import com.zmu.cloud.commons.enums.HaiWeiDevice;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.HaiweiConfigMapper;
import com.zmu.cloud.commons.sphservice.HaiWeiService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YH
 */
@Service
@RequiredArgsConstructor
public class HaiWeiServiceImpl implements HaiWeiService {

    final HaiweiConfigMapper haiweiConfigMapper;

    @Override
    public String find(Long houseId, HaiWeiDevice device) {
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        LambdaQueryWrapper<HaiweiConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HaiweiConfig::getFarmId, farmId).eq(HaiweiConfig::getDevice, device.name());
        if (HaiWeiDevice.Material_Line.equals(device)) {
            wrapper.eq(HaiweiConfig::getHouseId, houseId);
        }
        HaiweiConfig config = haiweiConfigMapper.selectOne(wrapper);
        if (ObjectUtil.isNotEmpty(config)) {
            Map<String, Object> param = new HashMap<>();
            param.put("account", config.getAccount());
            param.put("privateKey", config.getPrivatekey());
            param.put("machineCode", config.getMachinecode());
            param.put("platform", config.getPlatform());
            String response = HttpUtil.post(config.getUrl(), HttpUtil.toParams(param));
            JSONObject obj = JSONUtil.parseObj(response);
            if (obj.containsKey("error")) {
                throw new BaseException("海为云接口调用异常！");
            }
            return obj.getJSONObject("result").getStr("url");
        }
        return null;
    }
}
