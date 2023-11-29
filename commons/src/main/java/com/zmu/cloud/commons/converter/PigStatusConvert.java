package com.zmu.cloud.commons.converter;


import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;


public class PigStatusConvert extends IntegerStringConverter {


    //种猪状态默认：1.后备，配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳，8断奶
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("后备");
        } else if (value == 2) {
            return new WriteCellData<>("配种");
        } else if (value == 3) {
            return new WriteCellData<>("空怀");
        } else if (value == 4) {
            return new WriteCellData<>("返情");
        } else if (value == 5) {
            return new WriteCellData<>("流产");
        } else if (value == 6) {
            return new WriteCellData<>("妊娠");
        } else if (value == 7) {
            return new WriteCellData<>("哺乳");
        } else if (value == 8) {
            return new WriteCellData<>("断奶");
        }
        return null;
    }
}
