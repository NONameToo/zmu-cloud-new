package com.zmu.cloud.commons.converter;

import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class PigPregnancyResultConvert extends IntegerStringConverter {
    //1.妊娠，2流产，3返情，4阴性
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("妊娠");
        } else if (value == 2) {
            return new WriteCellData<>("流产");
        } else if (value == 3) {
            return new WriteCellData<>("返情");
        } else if (value == 4) {
            return new WriteCellData<>("阴性");
        }
        return null;
    }
}
