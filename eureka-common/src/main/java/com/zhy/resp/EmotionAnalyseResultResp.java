package com.zhy.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmotionAnalyseResultResp {
    private String positiveProb;
    private String negativeProb;
    private String sentiment;
}
