package com.zhy.resp;

import com.zhy.dto.HotSurveyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotSurveyResp {
    /**
     * 热门问卷list
     */
    List<HotSurveyDTO> hotSurveyList;
}
