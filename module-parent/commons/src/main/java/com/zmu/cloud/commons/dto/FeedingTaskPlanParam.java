package com.zmu.cloud.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedingTaskPlanParam {

    private int taskPlanId;
    private Time time;
    private List<BaseFeedingDTO> detailParams;

}
