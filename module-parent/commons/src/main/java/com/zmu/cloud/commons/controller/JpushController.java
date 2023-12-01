package com.zmu.cloud.commons.controller;

import com.zmu.cloud.commons.constants.JpushConstants;
import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.dto.PushUserTypeDTO;
import com.zmu.cloud.commons.entity.PushMessageType;
import com.zmu.cloud.commons.entity.admin.SysUser;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.jpush.JPushApi;
import com.zmu.cloud.commons.jpush.JPushMessage;
import com.zmu.cloud.commons.jpush.JpushTagTypeEnum;
import com.zmu.cloud.commons.service.PushMessageTaskService;
import com.zmu.cloud.commons.service.PushMessageTypeService;
import com.zmu.cloud.commons.service.SysUserService;
import com.zmu.cloud.commons.vo.PushMessageTypeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @DESCRIPTION: JpushController
 */
@Api(tags = "app消息推送管理")
@Slf4j
@RestController
@RequestMapping(CommonsConstants.API_PREFIX + "/push")
public class JpushController extends BaseController {

    @Autowired
    private PushMessageTypeService pushMessageTypeService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private JPushApi jPushApi;
    @Autowired
    private PushMessageTaskService pushMessageTaskService;


    @ApiOperation("绑定用户当前设备RID")
    @PutMapping("/rid/{rid}")
    public String rid(@PathVariable String rid) {
        sysUserService.bindRidToUser(getUserId(), rid);
        return rid;
    }

    @ApiOperation("消息推送列表")
    @GetMapping("/my-sub-push")
    public List<PushMessageTypeVO> MyPushMessageType() {
        return pushMessageTypeService.listByUserId(getUserId());
    }

    @ApiOperation("订阅/取消订阅")
    @PutMapping("/change-sub-push")
    public List<PushMessageTypeVO> ChangeMyPushMessageType(@RequestBody @Validated PushUserTypeDTO userPasswordUpdateDTO) {
        pushMessageTypeService.subAndCancelSub(userPasswordUpdateDTO.getTypeId(), userPasswordUpdateDTO.getSubIf(), getUserId());
        return pushMessageTypeService.listByUserId(getUserId());
    }

    @ApiOperation("根据消息推送类型id获取详情")
    @GetMapping("/type/{typeId}")
    public PushMessageType getById(@ApiParam(value = "消息推送类型id") @PathVariable("typeId") Long id) {
        return pushMessageTypeService.getById(id);
    }


    @ApiOperation("发送一条测试消息(广播）")
    @GetMapping("/test")
    public void Test() {
        JPushMessage pushMessage = new JPushMessage();
        pushMessage.setTitle("云慧养");
        pushMessage.setContent(String.format("云慧养提示您,您有新的%d头母猪待产,请及时处理!----发送到所有客户端", RandomUtils.nextInt()));
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("messageType", JpushTagTypeEnum.MATING.getType());
        pushMessage.setExtras(stringStringHashMap);
        jPushApi.pushToAll(pushMessage);
    }

    @ApiOperation("发送一条测试消息(RID）")
    @GetMapping("/test1")
    public void Test1() {
        ArrayList<String> obj = new ArrayList<>();
        SysUser user = sysUserService.getById(getUserId());
        if (ObjectUtils.isEmpty(user.getRid())) {
            throw new BaseException("当前用户尚未绑定RID,请先去绑定RID!");
        }
        obj.add(user.getRid());
        JPushMessage pushMessage = new JPushMessage();
        pushMessage.setBadge(1);
        pushMessage.setPriority(2);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("number", String.valueOf(RandomUtils.nextInt()));
        pushMessage.setExtras(stringStringHashMap);
        pushMessage.setContent(String.format("云慧养提示您,您有新的%d头母猪待产,请及时处理!----根据根据设备ID推送", RandomUtils.nextInt()));
        pushMessage.setTitle("云慧养");
        jPushApi.pushToDevices(obj, pushMessage);
    }


    @ApiOperation("手动触发推送")
    @GetMapping("/test3")
    public void Test3() {
        try {
            pushMessageTaskService.createBasePush(JpushConstants.ZM_CLOUD);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
