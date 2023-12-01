package com.zmu.cloud.commons.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 表格分页数据对象
 *
 */
@ApiModel
public class TableDataInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数",position=3)
    private long total;

    /**
     * 列表数据
     */
    @ApiModelProperty(value = "列表数据",position=4)
    private T rows;

    /**
     * 消息状态码
     */
    @ApiModelProperty(value = "消息状态码(正常:200)",position=1)
    private int code;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容",position=2)
    private String msg;


    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容",position=2)
    private Object datas;

    /**
     * 表格数据对象
     */
    public TableDataInfo() {
    }

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(T list, int total) {
        this.rows = list;
        this.total = total;
    }

    public Object getDatas() {
        return datas;
    }

    public void setDatas(Object datas) {
        this.datas = datas;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public T getRows() {
        return rows;
    }

    public void setRows(T rows) {
        this.rows = rows;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
