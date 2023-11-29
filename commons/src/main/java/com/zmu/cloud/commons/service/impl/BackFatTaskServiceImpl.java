package com.zmu.cloud.commons.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmu.cloud.commons.config.ZmuCloudProperties;
import com.zmu.cloud.commons.dto.BackFatDto;
import com.zmu.cloud.commons.dto.Pig;
import com.zmu.cloud.commons.dto.commons.RequestInfo;
import com.zmu.cloud.commons.entity.*;
import com.zmu.cloud.commons.enums.BackFatTaskDetailStatus;
import com.zmu.cloud.commons.enums.BackFatTaskStatus;
import com.zmu.cloud.commons.enums.CheckMode;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.mapper.*;
import com.zmu.cloud.commons.redis.CacheKey;
import com.zmu.cloud.commons.service.*;
import com.zmu.cloud.commons.sphservice.SphEmployService;
import com.zmu.cloud.commons.utils.RequestContextUtils;
import com.zmu.cloud.commons.vo.BackFatScanVo;
import com.zmu.cloud.commons.vo.BackFatTaskReportVo;
import com.zmu.cloud.commons.vo.BackFatTaskVo;
import com.zmu.cloud.commons.vo.ColumnVo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.zmu.cloud.commons.constants.Constants.saveBackfat;
import static java.util.stream.Collectors.toList;

/**
 * @author YH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackFatTaskServiceImpl extends ServiceImpl<PigBackFatTaskMapper, PigBackFatTask> implements BackFatTaskService {

    final SysUserService userService;
    final SphEmployMapper employMapper;
    final QrcodeService rqCodeService;
    final PigBackFatTaskMapper backFatTaskMapper;
    final PigHouseColumnsService columnsService;
    final PigHouseColumnsMapper columnsMapper;
    final BackFatService backFatService;
    final BackFatTaskDetailService backFatTaskDetailService;
    final PigBackFatTaskDetailMapper backFatTaskDetailMapper;
    final ZmuCloudProperties config;
    final RedissonClient redis;
    final PigBreedingService pigService;

    @Override
    public BackFatScanVo single(String content) {
        PigHouseColumns col = rqCodeService.scan(content);
        Optional<Pig> pigOpt = columnsService.findByCol(col.getId());
        if (pigOpt.isPresent()) {
            PigBackFatTask task = buildTask(col, pigOpt, null, CheckMode.SINGLE);
            PigBackFatTaskDetail detail = backFatTaskDetailService.findListByColId(task.getId(), col, BackFatTaskDetailStatus.Undetected);
            if (ObjectUtil.isNotEmpty(detail)) {
                return wrap(detail);
            }
        }
        throw new BaseException("该栏位未绑定猪只");
    }

    @Override
    public BackFatTaskVo tip() {
        BackFatTaskVo.BackFatTaskVoBuilder builder = BackFatTaskVo.builder();
        Long farmId = RequestContextUtils.getRequestInfo().getPigFarmId();
        List<PigBackFatTask> tasks = findTaskByFarmAndModel(farmId, CheckMode.BATCH, Collections.singletonList(BackFatTaskStatus.RUNNING));
        if (ObjectUtil.isNotEmpty(tasks)) {
            PigBackFatTask task = tasks.get(0);
            builder.tip(backFatTaskDetailService.listByUndetectedField(task.getId()));
        }
        return builder.build();
    }

    @Override
    public List<PigBackFatTask> findTaskByFarmAndModel(Long farmId, CheckMode mode, List<BackFatTaskStatus> statuses) {
        LambdaUpdateWrapper<PigBackFatTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PigBackFatTask::getCheckMode, mode.name());
        if (ObjectUtil.isNotEmpty(statuses)) {
            wrapper.in(PigBackFatTask::getStatus, statuses.stream().map(BackFatTaskStatus::toString).collect(toList()));
        }
        wrapper.orderByDesc(PigBackFatTask::getCreateTime);
        return backFatTaskMapper.selectList(wrapper);
    }

    @Override
    public List<BackFatScanVo> batchTask(Long colId, String endPosition) {
        PigHouseColumns col = columnsMapper.selectById(colId);
        //生成批量测试任务及明细
        PigBackFatTask task = buildTask(col, null, endPosition, CheckMode.BATCH);
        List<PigBackFatTaskDetail> details = backFatTaskDetailService.findList(task.getId(), null,
                BackFatTaskDetailStatus.Undetected, 1, 100000);
        if (ObjectUtil.isNotEmpty(details) && !details.get(0).getPigHouseColumnId().equals(colId)) {
            details = ListUtil.reverse(details);
        }
        return details.stream().map(this::wrap).collect(toList());
    }

    @Override
    public void save(BackFatDto backFatDto, HttpServletRequest request) {
        RequestInfo info = RequestContextUtils.getRequestInfo();
        PigBackFatTaskDetail detail = backFatTaskDetailMapper.selectById(backFatDto.getDetailId());
        detail.setOperator(info.getUserId());
        detail.setCreateTime(DateUtil.date());
        if (Boolean.TRUE.equals(backFatDto.getSkip())) {
            detail.setCreateTime(DateUtil.date());
            detail.setStatus(BackFatTaskDetailStatus.Skip.name());
        } else {
            //更新任务明细
            detail.setBackFat(backFatDto.getBackFat());
            detail.setStatus(BackFatTaskDetailStatus.Detected.name());
            //新增猪只背膘记录
            backFatService.createBackFat(detail.getPigId(), detail.getBackFat(), backFatDto.getBackFatStage(), info.getUserId());
            //同步背膘到巨星
            try {
                syncJxBactFat(backFatDto.getBackFatStage(), detail, info.getUserId(), request);
            } catch (RuntimeException e) {
                redis.getBucket(CacheKey.Web.sph_pig.key + detail.getPigId()).delete();
            }
        }
        backFatTaskDetailMapper.updateById(detail);
        if (CheckMode.BATCH.equals(backFatDto.getMode())) {
            checkTaskStatus(detail.getTaskId());
        }
    }

    private void syncJxBactFat(Integer backFatStage, PigBackFatTaskDetail detail, Long userId, HttpServletRequest request) {
        String token = request.getHeader(config.getConfig().getSphTokenHeaderName());
        RMap<String, Object> object = redis.getMap(CacheKey.Web.jx_user_info.key + token);
        Optional<Pig> optPig  = pigService.findPig(detail.getPigId());
        optPig.ifPresent(pig -> {
            JSONObject param = new JSONObject();
            param.putOpt("z_backfat_stage", backFatStage);
            param.putOpt("z_date", DateUtil.formatDate(new Date()));
            param.putOpt("z_zxr", userId);
            param.putOpt("z_source", detail.getBackFat());
            param.putOpt("z_entering_date", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
            param.putOpt("z_dorm", pig.getHouseId());
            param.putOpt("z_entering_staff", userId);
            param.putOpt("m_org_id", object.get("m_org_id"));
            param.putOpt("z_one_no", pig.getIndividualNumber());
            param.putOpt("z_zzda_id", pig.getId());
            param.putOpt("z_birth_num", pig.getParity());
            param.putOpt("z_org_id", pig.getFarmId());
            param.putOpt("z_rems", "");
            HttpResponse response = HttpUtil.createRequest(Method.POST, config.getConfig().getJxAppBaseUrl().concat(saveBackfat))
                    .header("token", token)
                    .body(param.toString())
                    .contentType("application/json")
                    .execute();
            if (response.body().contains("登录失效")) {
                throw new BaseException("登录失败:" + response.body());
            }
            JSONObject obj = JSONUtil.parseObj(response.body());
            if (obj.get("flag").equals("false") || obj.get("flag").equals(false)) {
                throw new BaseException("保存失败！");
            }
            log.info("背膘同步成功：EarNumber={}，BackFat={}", pig.getEarNumber(), detail.getBackFat());
        });
    }

    @Override
    public BackFatTaskReportVo report(Long taskId) {
        List<PigBackFatTaskDetail> details = backFatTaskDetailService.findList(taskId, null, null, 0, 10000);
        return BackFatTaskReportVo.builder().operator(details.stream()
                .map(PigBackFatTaskDetail::getOperator)
                .collect(Collectors.toSet())
                .stream()
                .map(operatorId -> {
                    SphEmploy employ = employMapper.selectById(operatorId);
                    if (ObjectUtil.isNotEmpty(employ)) {
                        return employ.getName();
                    }
                    return userService.getById(operatorId).getRealName();
                })
                .collect(Collectors.joining(",")))
        .date(DateUtil.formatDate(new Date())).details(details).build();
    }

    /**
     * 完成后返回测膘报告
     * @param taskId
     * @return
     */
    private void checkTaskStatus(Long taskId) {
        List<PigBackFatTaskDetail> details = backFatTaskDetailService.findList(taskId, null,
                BackFatTaskDetailStatus.Undetected, 1, 100000);
        if (CollectionUtils.isEmpty(details)) {
            //修改任务状态
            backFatTaskMapper.updateById(PigBackFatTask.builder().id(taskId).status(BackFatTaskStatus.COMPLETED.name()).build());
        }
    }

    @Transactional
    public PigBackFatTask buildTask(PigHouseColumns columns, Optional<Pig> optPig, String endPosition, CheckMode mode) {
        PigBackFatTask task;
        String name = String.format("%s 背膘检测", DateUtil.format(new Date(), "YYYY-MM-dd"));
        log.debug("生成背膘检测任务");
        //生成任务
        Long userId = RequestContextUtils.getUserId();
        List<PigHouseColumns> cols;
        if (CheckMode.BATCH.equals(mode)) {
            //生成批量任务明细
            if (columns.getPosition().compareTo(endPosition) < 0) {
                cols = columnsService.findByPositionAndHouseId(columns.getPigHouseId(), columns.getPosition(), endPosition);
            } else {
                cols = columnsService.findByPositionAndHouseId(columns.getPigHouseId(), endPosition, columns.getPosition());
            }
            PigHouseColumns end;
            if (cols.get(0).getPosition().equals(columns.getPosition())) {
                end = cols.get(cols.size() - 1);
            } else {
                end = cols.get(0);
            }
            task = build(columns.getPigHouseId(), name, columns.getId(), columns.getPosition(),
                    end.getId(), endPosition, mode, userId);
        } else {
            cols = Collections.singletonList(columns);
            task = build(columns.getPigHouseId(), name, columns.getId(), columns.getPosition(),
                    columns.getId(), columns.getPosition(), mode, userId);
        }
        generateBackFatTask(task, cols, optPig, userId);
        return task;
    }

    /**
     * 生成任务明细
     * @param task
     * @param columns
     * @param optPig
     */
    private void generateBackFatTask(PigBackFatTask task, List<PigHouseColumns> columns, Optional<Pig> optPig, Long userId) {
        List<PigBackFatTaskDetail> details = columns.stream()
                .map(col -> {
                    if (CheckMode.BATCH.name().equals(task.getCheckMode())) {
                        return createDetail(task, col, columnsService.findByCol(col.getId()), userId);
                    } else {
                        return createDetail(task, col, optPig, userId);
                    }
                })
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(details)) {
            backFatTaskDetailService.saveBatch(details);
        }
    }

    /**
     * 新建明细
     * @param task
     * @param columns
     * @param pigOpt
     * @return
     */
    private PigBackFatTaskDetail createDetail(PigBackFatTask task, PigHouseColumns columns, Optional<Pig> pigOpt, Long userId) {
        PigBackFatTaskDetail detail = new PigBackFatTaskDetail();
        detail.setCompanyId(columns.getCompanyId());
        detail.setPigFarmId(columns.getPigFarmId());
        detail.setPigHouseId(columns.getPigHouseId());
        detail.setPigHouseColumnId(columns.getId());
        detail.setColumnCode(columns.getCode());
        detail.setColumnPosition(columns.getPosition());
        detail.setUpdateBy(userId);
        detail.setTaskId(task.getId());
        pigOpt.ifPresent(pig -> {
            detail.setPigId(pig.getId());
            detail.setEarNumber(pig.getEarNumber());
            detail.setBackFat(pig.getBackFat());
        });
        detail.setStatus(BackFatTaskDetailStatus.Undetected.name());
        return detail;
    }

    /**
     * 创建任务
     * @param houseId
     * @param name
     * @param mode
     * @return
     */
    private PigBackFatTask build(Long houseId, String name, Long beginColId, String beginPosition,
                                 Long endColId, String endPosition, CheckMode mode, Long userId) {
        PigBackFatTask task = new PigBackFatTask();
        task.setPigHouseId(houseId);
        task.setName(name);
        task.setStatus(BackFatTaskStatus.RUNNING.name());
        task.setCheckMode(mode.name());
        task.setBeginTime(LocalDateTime.now());
        task.setBeginColumnId(beginColId);
        task.setBeginPosition(beginPosition);
        task.setEndColumnId(endColId);
        task.setEndPosition(endPosition);
        task.setCreateTime(DateUtil.date());
        task.setCreateBy(userId);
        backFatTaskMapper.insert(task);
        return task;
    }

    private BackFatScanVo wrap(PigBackFatTaskDetail detail) {
        BackFatScanVo.BackFatScanVoBuilder builder = BackFatScanVo.builder();
        builder.taskId(detail.getTaskId())
                .detailId(detail.getId())
                .columnId(detail.getPigHouseColumnId())
                .columnCode(detail.getColumnCode())
                .position(detail.getColumnPosition())
                .status(detail.getStatus())
                .pigId(detail.getPigId())
                .earNumber(detail.getEarNumber())
                .backFat(detail.getBackFat())
                .skip(ObjectUtil.isEmpty(detail.getPigId()));
        return builder.build();
    }
}
