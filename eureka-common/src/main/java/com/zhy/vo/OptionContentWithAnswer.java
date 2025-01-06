package com.zhy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionContentWithAnswer {
    private String optionContent;
    private Boolean isAnswer;
}
