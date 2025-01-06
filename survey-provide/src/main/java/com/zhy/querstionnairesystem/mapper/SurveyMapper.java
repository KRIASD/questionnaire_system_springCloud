package com.zhy.querstionnairesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhy.domain.Survey;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface SurveyMapper extends BaseMapper<Survey> {
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Survey survey);
    @Select("SELECT DISTINCT type FROM survey")
    List<String> getSurveyTypes();

    @Select("SELECT type, COUNT(*) AS count FROM survey WHERE `delete_at` IS NULL GROUP BY type")
    List<Map<String, Integer>> getSurveyTypeCount();
}
