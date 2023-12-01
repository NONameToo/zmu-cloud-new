package com.zmu.cloud.commons.converter;


import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;


public class PigApproachTypeConvert extends IntegerStringConverter {


    //品种进场类型1.自繁，2购买，3转入
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("自繁");
        } else if (value == 2) {
            return new WriteCellData<>("购买");
        } else if (value == 3) {
            return new WriteCellData<>("转入");
        }
        return null;
    }
}
