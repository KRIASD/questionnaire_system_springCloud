package com.zhy.querstionnairesystem.service;

import com.zhy.resp.EmotionAnalyseResultResp;
import com.zhy.utils.CommonResult;
import org.json.JSONException;

public interface EmotionAnalyseService {
    /**
     * 情感分析
     *
     * @param text 文本
     * @return 情感分析结果泛型
     */
    EmotionAnalyseResultResp analyseEmotion(String text) throws JSONException;

}
