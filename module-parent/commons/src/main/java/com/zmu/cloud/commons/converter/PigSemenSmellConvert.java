package com.zmu.cloud.commons.converter;

import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class PigSemenSmellConvert extends IntegerStringConverter {
    //色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("正常");
        } else if (value == 2) {
            return new WriteCellData<>("异常");
        }
        return null;
    }
}
