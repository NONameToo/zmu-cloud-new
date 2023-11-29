package com.zmu.cloud.commons.enums;

import lombok.Getter;

/**
 * @author YH
 */
@Getter
public enum QrcodeType {

    PrecisionFeeder,//二维码替换完后，该类型将被废弃
    BreedingPigFeeder,//种猪饲喂器
    FatPigFeeder, //育肥饲喂器
    Column //栏位

}
