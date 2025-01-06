package com.zhy.req;

import com.zhy.domain.Question;
import com.zhy.dto.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSurveyReq {
    private int id;
    private String name;
    private String description;
    private Date startTime;
    private Date endTime;
    private String type;
    private String status;
    private List<QuestionDTO> questions;
}
