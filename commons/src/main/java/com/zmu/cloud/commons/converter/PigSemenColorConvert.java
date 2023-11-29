package com.zmu.cloud.commons.converter;

import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class PigSemenColorConvert extends IntegerStringConverter {
    //色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("乳白色");
        } else if (value == 2) {
            return new WriteCellData<>("灰白色");
        } else if (value == 3) {
            return new WriteCellData<>("偏黄色");
        }else if (value == 4) {
            return new WriteCellData<>("红色");
        }else if (value == 5) {
            return new WriteCellData<>("绿色");
        }
        return null;
    }
}
