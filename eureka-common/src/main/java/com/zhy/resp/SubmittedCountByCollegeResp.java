package com.zhy.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedCountByCollegeResp {
    /**
     * 学院
     */
    private String college;
    /**
     * 已提交问卷数
     */
    private Integer count;
}
