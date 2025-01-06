package com.zhy.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStaticsResp {
    /**
     * 系统总访问量
     */
    private Integer totalCount;
    /**
     * 今日访问量
     */
    private Integer todayCount;
    /**
     * 增加百分比
     */
    private Double increasePercent;
}
