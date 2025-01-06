package com.zhy.resp;

import com.zhy.domain.Answer;
import lombok.Data;

@Data
public class SubmitInfoForCategoryResp {
    private Integer submitId;
    private String username;
    private String submitTime;
}
