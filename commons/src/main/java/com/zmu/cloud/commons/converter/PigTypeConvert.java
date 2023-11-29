package com.zmu.cloud.commons.converter;


import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;


public class PigTypeConvert extends IntegerStringConverter {

    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("公猪");
        } else if (value == 2) {
            return new WriteCellData<>("母猪");
        }
        return null;
    }
}
