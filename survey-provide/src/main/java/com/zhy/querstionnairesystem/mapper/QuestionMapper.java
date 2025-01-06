package com.zhy.querstionnairesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhy.domain.Question;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {
    /**
     * 根据问卷id查询问题
     *
     * @param surveyId 问卷id
     * @return List<Question>
     */
    @Select("select * from `question` where survey_id = #{surveyId}")
    List<Question> selectBySurveyId(String surveyId);
}
