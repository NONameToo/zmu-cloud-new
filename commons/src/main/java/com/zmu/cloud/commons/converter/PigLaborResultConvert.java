package com.zmu.cloud.commons.converter;

import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class PigLaborResultConvert  extends IntegerStringConverter {
    //分娩结果1，顺产，2，难产，3助产
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("顺产");
        } else if (value == 2) {
            return new WriteCellData<>("难产");
        } else if (value == 3) {
            return new WriteCellData<>("助产");
        }
        return null;
    }
}
