package com.zmu.cloud.commons.dto.commons;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: Page
 * @Author
 * @Date 2019-01-08 17:31
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class Pagination<T> implements Serializable {
    private static final long serialVersionUID = 5047735679070084680L;

    @ApiModelProperty("当前页数")
    private int page;
    @ApiModelProperty("分页大小")
    private int size;
    @ApiModelProperty("总数")
    private long total;
    @ApiModelProperty("数据")
    private List<T> list;
    @ApiModelProperty("总共多少页")
    private long totalPage;
    @ApiModelProperty("是否有下一页")
    private boolean hasNextPage;

    public Pagination(List<T> list, long total, int page, int size) {
        if (size == 0)
            size = 10;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPage = total % size == 0 ? total / size : (total / size) + 1;
        this.list = list;
        this.hasNextPage = this.totalPage > this.page;
    }

    public static <T> Pagination<T> of(List<T> list, int total, int page, int size) {
        return new Pagination<>(list, total, page, size);
    }

    public static <T> Pagination<T> of(PageInfo<T> pageInfo) {
        return new Pagination<>(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
}
