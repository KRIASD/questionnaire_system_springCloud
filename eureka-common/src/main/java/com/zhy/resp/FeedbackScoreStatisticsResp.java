package com.zhy.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackScoreStatisticsResp {
    private Double score;
    private Integer count;
}
