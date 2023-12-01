package com.zmu.cloud.commons.converter;

import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class PigLeavingReasonConvert extends IntegerStringConverter {

    //离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，
    // 7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("长期不发育");
        } else if (value == 2) {
            return new WriteCellData<>("生产能力太差");
        }else if (value == 3){
            return new WriteCellData<>("胎龄过大");
        }else if (value == 4){
            return new WriteCellData<>("体况太差");
        }else if (value == 5){
            return new WriteCellData<>("精液质量过差");
        }else if (value == 6){
            return new WriteCellData<>("发烧");
        }else if (value == 7){
            return new WriteCellData<>("难产");
        }else if (value == 8){
            return new WriteCellData<>("肢蹄病");
        }else if (value == 9){
            return new WriteCellData<>("压死");
        }else if (value == 10){
            return new WriteCellData<>("弱仔");
        }else if (value == 11){
            return new WriteCellData<>("病死");
        }else if (value == 12){
            return new WriteCellData<>("打架");
        }else if (value == 13){
            return new WriteCellData<>("其它");
        }
        return null;
    }
}
