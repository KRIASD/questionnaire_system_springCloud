package com.zhy.vo;

import com.zhy.domain.Survey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionStatisticsPercentCount {
    private Integer submitCount;
    private Map<String, Map<String, Integer>> optionStatistics;
}
