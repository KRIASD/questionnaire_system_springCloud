package com.zhy.querstionnairesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhy.domain.Option;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface OptionBriefMapper extends BaseMapper<Option> {
    /**
     * 根据问题id查询选项
     *
     * @param id 问题id
     * @return List<Option>
     */
    @Select("select * from `option_brief` where question_id = #{id} AND delete_at is null")
    List<Option> selectByQuestionId(Integer id);

    /**
     * 根据问题id删除选项
     *
     * @param id 问题id
     */
    @Update("update `option_brief` set delete_at = now() where question_id = #{id}")
    void deleteByQuestionId(Integer id);
}
