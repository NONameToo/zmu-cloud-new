package com.zmu.cloud.commons.converter;

import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class PigLeaveTypeConvert extends IntegerStringConverter {

    //离场类型1.死淘，2转出,3其他，4出栏
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("死亡");
        } else if (value == 2) {
            return new WriteCellData<>("淘汰");
        }else if (value == 3){
            return new WriteCellData<>("其他");
        }else if (value == 4){
            return new WriteCellData<>("出栏");
        }
        return null;
    }
}
