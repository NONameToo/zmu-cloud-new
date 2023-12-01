package com.zmu.cloud.commons.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lqp0817@gmail.com
 * @date 2022/5/3 08:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ColumnWidth(17)
@HeadRowHeight(40)
@HeadStyle(fillForegroundColor = 1)
public class FinanceIncomeAndExpenditureExcel {

    @ExcelIgnore
    private Long id;
    @ExcelIgnore
    private Integer status;

    @ExcelProperty(value = "时间")
    private String date;
    @ExcelProperty(value = "摘要")
    private String remark;
    @ExcelProperty(value = "数量/头")
    private String number;
    @ExcelProperty(value = "单价/元")
    private String price;
    @ExcelProperty(value = "总价/元")
    private String totalPrice;
    @ExcelProperty(value = "数据类型")
    private String dataTypeName;
    @ExcelProperty("收支类型")
    private String income;
    @ExcelProperty("状态")
    private String statusStr;

}
