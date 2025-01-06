package com.zhy.resp;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedStaticsResp {
    /**
     * 总提交量
     */
    private Integer totalCount;
    /**
     * 今日提交量
     */
    private Integer todayCount;
    /**
     * 提交增加百分比
     */
    private Double increasePercent;
}
