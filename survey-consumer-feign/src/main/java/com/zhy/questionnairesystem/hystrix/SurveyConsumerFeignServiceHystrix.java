package com.zhy.questionnairesystem.hystrix;

import com.zhy.domain.Comment;
import com.zhy.domain.Survey;
import com.zhy.questionnairesystem.service.SurveyConsumerFeignService;
import com.zhy.req.*;
import com.zhy.utils.CommonResult;
import org.springframework.stereotype.Component;

@Component
public class SurveyConsumerFeignServiceHystrix implements SurveyConsumerFeignService {
    /**
     * 查看问卷详情
     *
     * @param surveyId 问卷id
     * @return Survey
     */
    @Override
    public CommonResult<?> getSurveyDetail(Integer surveyId) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 分页获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return surveys 问卷列表
     */
    @Override
    public CommonResult<?> getSurveyList(Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 新建问卷
     *
     * @param survey 问卷
     * @return 200:新建成功 500:新建失败
     */
    @Override
    public CommonResult<?> createSurvey(CreateSurveyReq survey) {
        return CommonResult.failed("创建失败");
    }

    /**
     * 更新问卷详情
     *
     * @param survey 问卷
     * @return 200:更新成功 500:更新失败
     */
    @Override
    public CommonResult<?> updateSurveyDetail(Survey survey) {
        return CommonResult.failed("更新失败");
    }

    /**
     * 删除问卷
     *
     * @param surveyId 问卷id
     * @return 200:删除成功 500:删除失败
     */
    @Override
    public CommonResult<?> getSurveyBrief(Integer surveyId) {
        return CommonResult.failed("删除失败");
    }

    /**
     * 修改问卷的简要信息
     *
     * @param survey 问卷详情
     * @return 200:修改成功 500:修改失败
     */
    @Override
    public CommonResult<?> updateSurveyWill(Survey survey) {
        return CommonResult.failed("修改失败");
    }

    /**
     * 发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:发布成功 500:发布失败
     */
    @Override
    public CommonResult<?> publishSurvey(Integer surveyId) {
        return CommonResult.failed("发布失败");
    }

    /**
     * 取消发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:取消发布成功 500:取消发布失败
     */
    @Override
    public CommonResult<?> cancelPublishSurvey(Integer surveyId) {
        return CommonResult.failed("取消发布失败");
    }

    /**
     * 删除问卷的问题
     *
     * @param questionId 问题id
     * @return 200:删除成功 500:删除失败
     */
    @Override
    public CommonResult<?> deleteQuestion(Integer questionId) {
        return CommonResult.failed("删除失败");
    }

    /**
     * 删除问题的选项
     *
     * @param questionId 问题id
     * @param optionId   选项id
     * @return 200:删除成功 500:删除失败
     */
    @Override
    public CommonResult<?> deleteOption(Integer questionId, Integer optionId) {
        return CommonResult.failed("删除失败");
    }

    /**
     * 删除问卷
     *
     * @param surveyId 问卷id
     * @return 200:删除成功 500:删除失败
     */
    @Override
    public CommonResult<?> deleteSurvey(Integer surveyId) {
        return CommonResult.failed("删除失败");
    }

    /**
     * 获取热门问卷列表
     *
     * @return 热门问卷列表
     */
    @Override
    public CommonResult<?> getHotSurveyList() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 多条件模糊查询
     *
     * @param survey      问卷
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 问卷列表
     */
    @Override
    public CommonResult<?> searchSurvey(Survey survey, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 对用户个性化推荐问卷
     *
     * @param userId 用户id
     * @return 个性化推荐问卷列表
     */
    @Override
    public CommonResult<?> getRecommendSurveyList(Integer userId) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 下载问卷
     *
     * @param surveyId 问卷id
     * @return 200:下载成功 500:下载失败
     */
    @Override
    public CommonResult<?> downloadSurvey(String username, Integer surveyId) {
        return CommonResult.failed("下载失败");
    }

    /**
     * 根据所有填写过当前的问卷的用户的答案，生成问卷的统计信息
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    @Override
    public CommonResult<?> surveySubmittedStatistics(Integer surveyId) {
        return CommonResult.failed("生成失败");
    }

    /**
     * 针对每个问题的每个选项，统计选择该选项的人数
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    @Override
    public CommonResult<?> surveySubmittedStatisticsByOption(Integer surveyId) {
        return CommonResult.failed("生成失败");
    }

    /**
     * 获取所有的提交问卷记录
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getAllSubmittedRecords(Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 查询一条提交记录的详情
     *
     * @param submittedId 问卷id
     * @param username    用户名
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedRecordDetail(Integer submittedId, String username) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据用户名查询该用户的提交记录
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedByUsername(String username, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据问卷id获取该问卷的提交总数
     *
     * @param surveyId 问卷id
     * @return 当前问卷的提交总数
     */
    @Override
    public CommonResult<?> getSubmittedCount(Integer surveyId) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 用户填写提交问卷
     *
     * @param req 提交问卷详情
     * @return 200:提交成功 500:提交失败
     */
    @Override
    public CommonResult<?> submitSurvey(SubmitDetailReq req) {
        return CommonResult.failed("提交失败");
    }

    /**
     * 根据问卷id和用户名查询该用户对该问卷的评论
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getComment(Integer surveyId, String username) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据问卷id查询该问卷的所有评论
     *
     * @param surveyId    问卷id
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return comments 评论列表
     */
    @Override
    public CommonResult<?> getComments(Integer surveyId, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 用户评论问卷并打分
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @param comment  评论
     * @return 200:评论成功 500:评论失败
     */
    @Override
    public CommonResult<?> commentSurvey(Integer surveyId, String username, Comment comment) {
        return CommonResult.failed("评论失败");
    }


    /**
     * 获取所有问卷的类型（去重做类型选择器）
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyTypes() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据问卷类型获取该类型的所有问卷
     *
     * @param surveyType  问卷类型
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyByType(String surveyType, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 统计一天内的提交问卷数
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedCountByDay() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据问卷id统计该问卷问题的类型
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getQuestionType(Integer surveyId) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 按照问卷的提交量，按照时间段统计每天的问卷提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedCountByTime() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 按照提交量正序排序，获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyListBySubmittedCount(Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 按照提交量倒序排序，获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyListBySubmittedCountDesc(Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据问卷提交量按照星期统计所有问卷的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getActiveSurveyCountByWeek() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 为指定条件的学生发布问卷
     *
     * @param publishConditionListReq 发布条件
     * @return 200:发布成功 500:发布失败
     */
    @Override
    public CommonResult<?> publishSurveyForCondition(PublishConditionListReq publishConditionListReq) {
        return CommonResult.failed("发布失败");
    }

    /**
     * 为指定条件的学生取消发布问卷
     *
     * @param publishConditionListReq 取消发布条件
     * @return 200:发布成功 500:发布失败
     */
    @Override
    public CommonResult<?> cancelPublishSurveyForCondition(PublishConditionListReq publishConditionListReq) {
        return CommonResult.failed("取消发布失败");
    }

    /**
     * 获取用户发布的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getPublishedSurvey(String username, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 获取该用户可以填写的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getCanSubmitSurvey(String username, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 获取所有的学院
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getCollege() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据学院code获取该学院的所有专业
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getMajor() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 获取当前问卷的响应率，填写人数/发布人数
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getResponseRate(String username, Integer surveyId) {
        return CommonResult.failed("获取失败");
    }


    /**
     * 根據用戶名查询该用户发布的所有的问卷的提交列表
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getPublishedSurveySubmit(String username, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据问卷id统计该问卷的提交时间和提交数量
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedTotalByTime(Integer surveyId) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 添加问卷到我的问卷
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:添加成功 500:添加失败
     */
    @Override
    public CommonResult<?> addMySurvey(Integer surveyId, String username) {
        return CommonResult.failed("添加失败");
    }

    /**
     * 管理员：根据用户名查询该用户的我的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getMySurveyAdmin(String username, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据问卷id删除我的问卷中的问卷
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:删除成功 500:删除失败
     */
    @Override
    public CommonResult<?> deleteMySurveyById(Integer surveyId, String username) {
        return CommonResult.failed("删除失败");
    }

    /**
     * 根据问卷id和用户名修改问卷信息
     *
     * @param editMySurveyWillReq 问卷
     * @return 200:修改成功 500:修改失败
     */
    @Override
    public CommonResult<?> editMySurveyWill(EditMySurveyWillReq editMySurveyWillReq) {
        return CommonResult.failed("修改失败");
    }

    /**
     * 根据用户名和问卷id修改问卷详情
     *
     * @param editMySurveyReq 修改问卷详情请求体
     * @return 200:修改成功 500:修改失败
     */
    @Override
    public CommonResult<?> editMySurveyDetail(EditMySurveyReq editMySurveyReq) {
        return CommonResult.failed("修改失败");
    }

    /**
     * 管理员：根据用户名和问卷id，查询我的问卷中的问卷的详情（Redis）
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getMySurveyDetail(String username, Integer surveyId) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 按照用户登录的时间段按星期统计所有用户的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getActiveUserByWeek() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 获取总提交量和今日提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedStatics() {
        return CommonResult.failed("获取失败");
    }


    /**
     * 获取问卷总数和本月新增问卷数
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyStatics() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 获取系统总访问量、今日访问量和增加百分比
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUserStatics() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 获取评论总数和今日新增评论数
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getCommentStatics() {
        return CommonResult.failed("获取失败");
    }


    /**
     * 近十天的系统访问量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUserStaticsByTenDay() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据评论量来统计最热门的问卷
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getHotSurveyByComment() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据类型统计提交问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedCountByType() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据学院统计提交问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedCountByCollege() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 统计所有反馈分数每个分段的人数
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getFeedbackScoreStatistics() {
        return CommonResult.failed("获取失败");
    }

    /**
     * 获取系统日志
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSystemLog(String username, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }


    /**
     * 接入AI分析问卷回答情况
     *
     * @param id 提交记录id
     * @return 200:接入成功 500:接入失败
     */
    @Override
    public CommonResult<?> analyseEmotion(Integer id) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名
     * @return 200:下载成功 500:下载失败
     */
    @Override
    public CommonResult<?> downloadSurveyTemplate(String fileName) {
        return CommonResult.failed("下载失败");
    }

    /**
     * 解析文件成问卷
     *
     * @param filePath 文件路径
     * @return 200:解析成功 500:解析失败
     */
    @Override
    public CommonResult<?> parseSurvey(String filePath) {
        return CommonResult.failed("解析失败");
    }

    /**
     * 根据解析结果创建问卷
     *
     * @param survey 问卷
     * @return 200:创建成功 500:创建失败
     */
    @Override
    public CommonResult<?> createByParse(Survey survey) {
        return CommonResult.failed("创建失败");
    }

    /**
     * 通过redis查询发布到当前用户的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyBySelf(String username, Integer currentPage, Integer pageSize) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 通过根据发布人和问卷id查询redis中的问卷
     *
     * @param surveyIdWithPublisherReq 请求体
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyDetailByPublisher(SurveyIdWIthPublisherReq surveyIdWithPublisherReq) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据用户名和问卷id获取当前用户对当前问卷的评论
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getMyComment(String username, Integer surveyId) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 获取ai建议
     *
     * @param id 提交记录id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getAiSuggestions(Integer id) {
        return CommonResult.failed("获取失败");
    }

    /**
     * 根据用户名获取当前用户所有评论信息
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getMyComments(String username) {
        return CommonResult.failed("获取失败");
    }
}

