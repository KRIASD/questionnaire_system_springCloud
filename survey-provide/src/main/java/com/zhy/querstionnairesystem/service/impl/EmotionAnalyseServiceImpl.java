package com.zhy.querstionnairesystem.service.impl;

import com.baidu.aip.nlp.AipNlp;
import com.zhy.querstionnairesystem.service.EmotionAnalyseService;
import com.zhy.resp.EmotionAnalyseResultResp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class EmotionAnalyseServiceImpl implements EmotionAnalyseService {
    private static final String API_ID = "60543440";
    private static final String API_KEY = "JTn2bn1gEEY31cAuXDeUXbOH";
    private static final String SECRET_KEY = "IQgvAAW7HmGm3YR3eE2kuub0Sf4xPSZd";

    @Override
    public EmotionAnalyseResultResp analyseEmotion(String text) throws JSONException {
        AipNlp client = new AipNlp(API_ID, API_KEY, SECRET_KEY);
        JSONObject res = client.sentimentClassify(text, null);
        EmotionAnalyseResultResp emotionAnalyseResultResp = new EmotionAnalyseResultResp();
        JSONArray items = res.getJSONArray("items");
        JSONObject firstItem = items.getJSONObject(0);
        emotionAnalyseResultResp.setPositiveProb(firstItem.getString("positive_prob"));
        emotionAnalyseResultResp.setNegativeProb(firstItem.getString("negative_prob"));
        emotionAnalyseResultResp.setSentiment(firstItem.getString("sentiment"));
        return emotionAnalyseResultResp;
    }
}
