package com.zhy.vo;

import com.zhy.domain.Survey;
import com.zhy.dto.PublishInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyWithPublishInfo {
    private Survey survey;
    private PublishInfoDTO publishInfo;
}
