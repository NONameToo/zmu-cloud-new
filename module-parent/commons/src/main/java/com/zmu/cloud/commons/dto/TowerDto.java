package com.zmu.cloud.commons.dto;

import com.zmu.cloud.commons.entity.FeedTower;
import com.zmu.cloud.commons.utils.ZmMathUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author YH
 */
@Data
@Builder
@ApiModel("料塔数据")
@AllArgsConstructor
@NoArgsConstructor
public class TowerDto {

    private Long id;
    @NotBlank
    @ApiModelProperty(value = "名称")
    private String name;
    @NotNull
    @ApiModelProperty(value = "警戒值")
    private Integer warning;
    @ApiModelProperty(value = "容量规格（T）")
    private String capacity;
    @ApiModelProperty(value = "饲料品类Id")
    private Long feedTypeId;
    @ApiModelProperty(value = "饲料品类")
    private String feedType;
    @ApiModelProperty(value = "密度Kg/m³")
    private Double density;
    @ApiModelProperty(value = "关联栋舍")
    private String houseIds;


    @ApiModelProperty(value = "设备编号")
    private String deviceNo;
    @ApiModelProperty(value = "版本V1,V2")
    private String version;
    @ApiModelProperty(value = "二维码类型")
    private String type;
    @ApiModelProperty(value = "物联卡iccid")
    private String iccid;

    @ApiModelProperty(value = "进料口直径")
    private int upDiameter;
    @ApiModelProperty(value = "进料口高")
    private int neckHeight;
    @ApiModelProperty(value = "上锥斜面长")
    private int upSlopeLen;
    @ApiModelProperty(value = "中部高")
    private int inHeight;
    @ApiModelProperty(value = "中部直径")
    private int inDiameter;
    @ApiModelProperty(value = "中部到地面的距离")
    private int inFloor;
    @ApiModelProperty(value = "底部到地面的距离")
    private int downFloor;
    @ApiModelProperty(value = "底部周长")
    private int downGirth;
    @ApiModelProperty(value = "默认的定时测量时点")
    private Set<String> defaultTimer;
    @ApiModelProperty(value = "配置是否完成")
    private int dataStatus;
//
//    public FeedTower toSphFeedTower(){
//        FeedTower feedTower = new FeedTower();
//        BeanUtils.copyProperties(this, feedTower);
//        feedTower.setEnable(0);
//        feedTower.setDel(0);
//        feedTower.setCapacity(ZmMathUtil.tTog(this.getCapacity()));
//        feedTower.setDensity(ZmMathUtil.kgTog(this.getDensity()));
//
//        //v2版本不需要这些参数,为了不影响之前的,先将他们设置为0
//        if (ObjectUtils.isEmpty(feedTower.getUpRadius())){
//            feedTower.setUpRadius(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getUpSlopeLen())){
//            feedTower.setUpSlopeLen(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getNeckHeight())){
//            feedTower.setNeckHeight(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getUpSlopeLen())){
//            feedTower.setUpSlopeLen(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getInHeight())){
//            feedTower.setInHeight(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getInstallHeight())){
//            feedTower.setInstallHeight(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getInFloor())){
//            feedTower.setInFloor(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getDownFloor())){
//            feedTower.setDownFloor(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getDownGirth())){
//            feedTower.setDownGirth(0);
//        }
//        if (ObjectUtils.isEmpty(feedTower.getInRadius())){
//            feedTower.setInRadius(0);
//        }
//        return feedTower;
//    }
}
