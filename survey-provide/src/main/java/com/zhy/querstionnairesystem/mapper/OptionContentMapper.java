package com.zhy.querstionnairesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhy.domain.OptionContent;
import org.apache.ibatis.annotations.Select;

public interface OptionContentMapper extends BaseMapper<OptionContent> {
    /**
     * 根据选项id查询选项内容
     *
     * @param id 选项id
     * @return OptionContent
     */
    @Select("select * from `option_content` where id = #{id}")
    OptionContent selectByOptionContentId(Integer id);
}
