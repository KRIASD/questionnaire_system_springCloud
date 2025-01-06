package com.zhy.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentStaticsResp {
    /**
     * 总评论量
     */
    private Integer totalCount;
    /**
     * 今日评论量
     */
    private Integer todayCount;
    /**
     * 评论增加百分比
     */
    private Double increasePercent;
}
