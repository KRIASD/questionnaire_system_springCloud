package com.zhy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotSurveyDTO {
    /**
     * 问卷id
     */
    private Integer id;
    /**
     * 问卷名称
     */
    private String title;
    /**
     * 问卷描述
     */
    private String description;
    /**
     * 问卷评论总数
     */
    private Integer commentCount;
}
