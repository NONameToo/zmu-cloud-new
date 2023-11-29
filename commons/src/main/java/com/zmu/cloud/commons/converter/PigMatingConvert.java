package com.zmu.cloud.commons.converter;

import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class PigMatingConvert extends IntegerStringConverter {
    //配种方式1,人工受精，2自然交配
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("人工受精");
        } else if (value == 2) {
            return new WriteCellData<>("自然交配");
        }
        return null;
    }
}
