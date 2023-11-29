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
public class    FinanceDataExcel {

    @ExcelIgnore
    private Long id;
    @ExcelIgnore
    private Integer status;

    @ExcelProperty(value = "凭证日期")
    private String date;
    @ExcelProperty(value = "凭证摘要")
    private String remark;
    @ExcelProperty(value = "科目代码")
    private String subjectCode;
    @ExcelProperty(value = "科目名称")
    private String subjectName;
    @ExcelProperty(value = "数量")
    private String number;
    @ExcelProperty(value = "单价")
    private String price;
    @ExcelProperty(value = "借方")
    private String borrow;
    @ExcelProperty(value = "贷方")
    private String loans;
}
