package com.zhy.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyStaticsResp {
    /**
     * 问卷总数
     */
    private Integer totalCount;
    /**
     * 本月新增问卷数
     */
    private Integer monthCount;
    /**
     * 问卷增加百分比
     */
    private Double increasePercent;
}
