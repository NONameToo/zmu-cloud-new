package com.zmu.cloud.commons.converter;


import com.alibaba.excel.converters.integer.IntegerStringConverter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;


public class PigVarietyConvert extends IntegerStringConverter {


    //品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == 1) {
            return new WriteCellData<>("长白");
        } else if (value == 2) {
            return new WriteCellData<>("大白");
        } else if (value == 3) {
            return new WriteCellData<>("二元");
        } else if (value == 4) {
            return new WriteCellData<>("三元");
        } else if (value == 5) {
            return new WriteCellData<>("土猪");
        } else if (value == 6) {
            return new WriteCellData<>("大长");
        } else if (value == 7) {
            return new WriteCellData<>("杜洛克");
        } else if (value == 8) {
            return new WriteCellData<>("皮杜");
        }
        return null;
    }
}
