package com.zhy.questionnairesystem.service;

import com.zhy.domain.Comment;
import com.zhy.domain.Survey;
import com.zhy.questionnairesystem.hystrix.SurveyConsumerFeignServiceHystrix;
import com.zhy.req.*;
import com.zhy.utils.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(value = "SURVEY-PROVIDE", fallback = SurveyConsumerFeignServiceHystrix.class)
public interface SurveyConsumerFeignService {
    /**
     * 查看问卷详情
     *
     * @param surveyId 问卷id
     * @return Survey
     */
    @GetMapping("/survey/detail/{surveyId}")
    CommonResult<?> getSurveyDetail(@PathVariable("surveyId") Integer surveyId);

    /**
     * 分页获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return surveys 问卷列表
     */
    @GetMapping("/survey/list/{currentPage}/{pageSize}")
    CommonResult<?> getSurveyList(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 新建问卷
     *
     * @param survey 问卷
     * @return 200:新建成功 500:新建失败
     */
    @PutMapping("/survey/create")
    CommonResult<?> createSurvey(@RequestBody CreateSurveyReq survey);

    /**
     * 更新问卷详情
     *
     * @param survey 问卷
     * @return 200:更新成功 500:更新失败
     */
    @PutMapping("/survey/updateDetail")
    CommonResult<?> updateSurveyDetail(@RequestBody Survey survey);


    /**
     * 删除问卷
     *
     * @param surveyId 问卷id
     * @return 200:删除成功 500:删除失败
     */
    @PutMapping("/survey/deleteSurvey/{surveyId}")
    CommonResult<?> getSurveyBrief(@PathVariable("surveyId") Integer surveyId);

    /**
     * 修改问卷的简要信息
     *
     * @param survey 问卷详情
     * @return 200:修改成功 500:修改失败
     */
    @PutMapping("/survey/updateWill")
    CommonResult<?> updateSurveyWill(@RequestBody Survey survey);

    /**
     * 发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:发布成功 500:发布失败
     */
    @PutMapping("/survey/publish/{surveyId}")
    CommonResult<?> publishSurvey(@PathVariable("surveyId") Integer surveyId);

    /**
     * 取消发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:取消发布成功 500:取消发布失败
     */
    @PutMapping("/survey/cancelPublish/{surveyId}")
    CommonResult<?> cancelPublishSurvey(@PathVariable("surveyId") Integer surveyId);

    /**
     * 删除问卷的问题
     *
     * @param questionId 问题id
     * @return 200:删除成功 500:删除失败
     */
    @PutMapping("/survey/deleteQuestion/{questionId}")
    CommonResult<?> deleteQuestion(@PathVariable("questionId") Integer questionId);

    /**
     * 删除问题的选项
     *
     * @param questionId 问题id
     * @param optionId   选项id
     * @return 200:删除成功 500:删除失败
     */
    @PutMapping("/survey/deleteOption/{questionId}/{optionId}")
    CommonResult<?> deleteOption(@PathVariable("questionId") Integer questionId, @PathVariable("optionId") Integer optionId);

    /**
     * 删除问卷
     *
     * @param surveyId 问卷id
     * @return 200:删除成功 500:删除失败
     */
    @PutMapping("/survey/deleteSurvey/{surveyId}")
    CommonResult<?> deleteSurvey(@PathVariable("surveyId") Integer surveyId);

    /**
     * 获取热门问卷列表
     *
     * @return 热门问卷列表
     */
    @GetMapping("/survey/hot")
    CommonResult<?> getHotSurveyList();

    /**
     * 多条件模糊查询
     *
     * @param survey      问卷
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 问卷列表
     */
    @GetMapping("/survey/search/{currentPage}/{pageSize}")
    CommonResult<?> searchSurvey(@RequestPart("survey") Survey survey, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 对用户个性化推荐问卷
     *
     * @param userId 用户id
     * @return 个性化推荐问卷列表
     */
    @GetMapping("/survey/recommend/{userId}")
    CommonResult<?> getRecommendSurveyList(@PathVariable("userId") Integer userId);

    /**
     * 下载问卷
     *
     * @param surveyId 问卷id
     * @return 200:下载成功 500:下载失败
     */
    @GetMapping("/survey/download/{username}/{surveyId}")
    CommonResult<?> downloadSurvey(@PathVariable("username") String username, @PathVariable("surveyId") Integer surveyId);

    /**
     * 根据所有填写过当前的问卷的用户的答案，生成问卷的统计信息
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    @GetMapping("/survey/surveySubmittedStatistics/{surveyId}")
    CommonResult<?> surveySubmittedStatistics(@PathVariable("surveyId") Integer surveyId);

    /**
     * 针对每个问题的每个选项，统计选择该选项的人数
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    @GetMapping("/survey/surveySubmittedStatisticsByOption/{surveyId}")
    CommonResult<?> surveySubmittedStatisticsByOption(@PathVariable("surveyId") Integer surveyId);

    /**
     * 获取所有的提交问卷记录
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedRecords/{currentPage}/{pageSize}")
    CommonResult<?> getAllSubmittedRecords(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 查询一条提交记录的详情
     *
     * @param submittedId 提交记录id
     * @param username    用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedRecordDetail/{submittedId}/{username}")
    CommonResult<?> getSubmittedRecordDetail(@PathVariable("submittedId") Integer submittedId, @PathVariable("username") String username);

    /**
     * 根据用户名查询该用户的提交记录
     *
     * @param userId      用户id
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedSurvey/{username}/{currentPage}/{pageSize}")
    CommonResult<?> getSubmittedByUsername(@PathVariable("username") String userId, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 根据问卷id获取该问卷的提交总数
     *
     * @param surveyId 问卷id
     * @return 当前问卷的提交总数
     */
    @GetMapping("/survey/submittedCount/{surveyId}")
    CommonResult<?> getSubmittedCount(@PathVariable("surveyId") Integer surveyId);

    /**
     * 用户填写提交问卷
     *
     * @param req 提交问卷详情
     * @return 200:提交成功 500:提交失败
     */
    @PutMapping("/survey/submit")
    CommonResult<?> submitSurvey(@RequestBody SubmitDetailReq req);

    /**
     * 根据问卷id和用户名查询该用户对该问卷的评论
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/comments/{surveyId}/{username}")
    CommonResult<?> getComment(@PathVariable("surveyId") Integer surveyId, @PathVariable("username") String username);

    /**
     * 根据问卷id查询该问卷的所有评论
     *
     * @param surveyId    问卷id
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return comments 评论列表
     */
    @GetMapping("/survey/comments/{surveyId}/{currentPage}/{pageSize}")
    CommonResult<?> getComments(@PathVariable("surveyId") Integer surveyId, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 用户评论问卷并打分
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @param comment  评论
     * @return 200:评论成功 500:评论失败
     */
    @PutMapping("/survey/doComment/{surveyId}/{username}")
    CommonResult<?> commentSurvey(@PathVariable("surveyId") Integer surveyId, @PathVariable("username") String username, @RequestPart("comment") Comment comment);


    /**
     * 获取所有问卷的类型（去重做类型选择器）
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/types")
    CommonResult<?> getSurveyTypes();

    /**
     * 根据问卷类型获取该类型的所有问卷
     *
     * @param surveyType  问卷类型
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/type/{surveyType}/{currentPage}/{pageSize}")
    CommonResult<?> getSurveyByType(@PathVariable("surveyType") String surveyType, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 统计一天内的提交问卷数
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedCountByDay")
    CommonResult<?> getSubmittedCountByDay();

    /**
     * 根据问卷id统计该问卷问题的类型
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/questionTypeStatistics/{surveyId}")
    CommonResult<?> getQuestionType(@PathVariable("surveyId") Integer surveyId);

    /**
     * 按照问卷的提交量，按照时间段统计每天的问卷提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedCountByTime")
    CommonResult<?> getSubmittedCountByTime();

    /**
     * 按照提交量正序排序，获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/listBySubmittedCountDesc/{currentPage}/{pageSize}")
    CommonResult<?> getSurveyListBySubmittedCount(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 按照提交量倒序排序，获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/listBySubmittedCount/{currentPage}/{pageSize}")
    CommonResult<?> getSurveyListBySubmittedCountDesc(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 根据问卷提交量按照星期统计所有问卷的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/activeSurveyCountByWeek")
    CommonResult<?> getActiveSurveyCountByWeek();

    /**
     * 为指定条件的学生发布问卷
     *
     * @param publishConditionListReq 发布条件
     * @return 200:发布成功 500:发布失败
     */
    @PutMapping("/survey/publishSurveyForCondition")
    CommonResult<?> publishSurveyForCondition(@RequestBody PublishConditionListReq publishConditionListReq);

    /**
     * 为指定条件的学生取消发布问卷
     *
     * @param publishConditionListReq 取消发布条件
     * @return 200:发布成功 500:发布失败
     */
    @PutMapping("/survey/cancelPublishSurveyForCondition")
    CommonResult<?> cancelPublishSurveyForCondition(@RequestBody PublishConditionListReq publishConditionListReq);

    /**
     * 获取用户发布的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/publishedSurvey/{username}/{currentPage}/{pageSize}")
    CommonResult<?> getPublishedSurvey(@PathVariable("username") String username, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 获取该用户可以填写的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/canSubmitSurvey/{username}/{currentPage}/{pageSize}")
    CommonResult<?> getCanSubmitSurvey(@PathVariable("username") String username, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 获取所有的学院
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getCollege")
    CommonResult<?> getCollege();

    /**
     * 根据学院code获取该学院的所有专业
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getMajor")
    CommonResult<?> getMajor();

    /**
     * 获取当前问卷的响应率，填写人数/发布人数
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/responseRate/{username}/{surveyId}")
    CommonResult<?> getResponseRate(@PathVariable("username") String username, @PathVariable("surveyId") Integer surveyId);

    /**
     * 根據用戶名查询该用户发布的所有的问卷的提交列表
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getPublishedSurveySubmit/{username}/{currentPage}/{pageSize}")
    CommonResult<?> getPublishedSurveySubmit(@PathVariable("username") String username, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 根据问卷id统计该问卷的提交时间和提交数量
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedTotalByTime/{surveyId}")
    CommonResult<?> getSubmittedTotalByTime(@PathVariable("surveyId") Integer surveyId);

    /**
     * 添加问卷到我的问卷
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:添加成功 500:添加失败
     */
    @PutMapping("/survey/addMySurvey/{surveyId}/{username}")
    CommonResult<?> addMySurvey(@PathVariable("surveyId") Integer surveyId, @PathVariable("username") String username);

    /**
     * 管理员：根据用户名查询该用户的我的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getMySurveyAdmin/{username}/{currentPage}/{pageSize}")
    CommonResult<?> getMySurveyAdmin(@PathVariable("username") String username, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);


    /**
     * 根据问卷id删除我的问卷中的问卷
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:删除成功 500:删除失败
     */
    @DeleteMapping("/survey/deleteMySurveyById/{surveyId}/{username}")
    CommonResult<?> deleteMySurveyById(@PathVariable("surveyId") Integer surveyId, @PathVariable("username") String username);

    /**
     * 根据问卷id和用户名修改问卷信息
     *
     * @param editMySurveyWillReq 问卷
     * @return 200:修改成功 500:修改失败
     */
    @PutMapping("/survey/editMySurveyWill")
    CommonResult<?> editMySurveyWill(@RequestBody EditMySurveyWillReq editMySurveyWillReq);

    /**
     * 根据用户名和问卷id修改问卷详情
     *
     * @param editMySurveyReq 修改问卷详情请求体
     * @return 200:修改成功 500:修改失败
     */
    @PutMapping("/survey/editMySurveyDetail")
    CommonResult<?> editMySurveyDetail(@RequestBody EditMySurveyReq editMySurveyReq);

    /**
     * 管理员：根据用户名和问卷id，查询我的问卷中的问卷的详情（Redis）
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/mySurvey/detail/{username}/{surveyId}")
    CommonResult<?> getMySurveyDetail(@PathVariable("username") String username, @PathVariable("surveyId") Integer surveyId);


    /**
     * 按照用户登录的时间段按星期统计所有用户的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getActiveUserByWeek")
    CommonResult<?> getActiveUserByWeek();

    /**
     * 获取总提交量和今日提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedStatics")
    CommonResult<?> getSubmittedStatics();


    /**
     * 获取问卷总数和本月新增问卷数
     */
    @GetMapping("/survey/surveyStatics")
    CommonResult<?> getSurveyStatics();

    /**
     * 获取系统总访问量、今日访问量和增加百分比
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/userStatics")
    CommonResult<?> getUserStatics();

    /**
     * 获取评论总数和今日新增评论数
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/commentStatics")
    CommonResult<?> getCommentStatics();

    /**
     * 近十天的系统访问量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/userStaticsByTenDay")
    CommonResult<?> getUserStaticsByTenDay();


    /**
     * 根据评论量来统计最热门的问卷
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/hotSurveyByComment")
    CommonResult<?> getHotSurveyByComment();

    /**
     * 根据类型统计提交问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedCountByType")
    CommonResult<?> getSubmittedCountByType();

    /**
     * 根据学院统计提交问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedCountByCollege")
    CommonResult<?> getSubmittedCountByCollege();

    /**
     * 统计所有反馈分数每个分段的人数
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/feedbackScoreStatistics")
    CommonResult<?> getFeedbackScoreStatistics();

    /**
     * 获取系统日志
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/systemLog/{username}/{currentPage}/{pageSize}")
    CommonResult<?> getSystemLog(@PathVariable("username") String username, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);


    /**
     * 接入AI分析问卷回答情况
     *
     * @param id 提交记录id
     * @return 200:接入成功 500:接入失败
     */
    @GetMapping("/survey/analyseEmotion/{id}")
    CommonResult<?> analyseEmotion(@PathVariable("id") Integer id);

    /**
     * 文件下载
     *
     * @param fileName 文件名
     * @return 200:下载成功 500:下载失败
     */
    @GetMapping("/survey/downloadSurveyTemplate")
    CommonResult<?> downloadSurveyTemplate(String fileName);

    /**
     * 解析文件成问卷
     *
     * @param filePath 文件路径
     * @return 200:解析成功 500:解析失败
     */
    @GetMapping("/survey/parseSurvey")
    CommonResult<?> parseSurvey(@RequestParam("filePath") String filePath);

    /**
     * 根据解析结果创建问卷
     *
     * @param survey 问卷
     * @return 200:创建成功 500:创建失败
     */
    @PutMapping("/survey/createByParse")
    CommonResult<?> createByParse(Survey survey);

    /**
     * 通过redis查询发布到当前用户的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getSurveyBySelf/{username}/{currentPage}/{pageSize}")
    CommonResult<?> getSurveyBySelf(@PathVariable("username") String username, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize);

    /**
     * 通过根据发布人和问卷id查询redis中的问卷
     *
     * @param surveyIdWithPublisherReq 请求体
     * @return 200:获取成功 500:获取失败
     */
    @PostMapping("/survey/detailByPublisher")
    CommonResult<?> getSurveyDetailByPublisher(SurveyIdWIthPublisherReq surveyIdWithPublisherReq);

    /**
     * 根据用户名和问卷id获取当前用户对当前问卷的评论
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/myComment/{username}/{surveyId}")
    CommonResult<?> getMyComment(@PathVariable("username") String username, @PathVariable("surveyId") Integer surveyId);

    /**
     * 获取ai建议
     *
     * @param id 提交记录id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/aiSuggest/{id}")
    CommonResult<?> getAiSuggestions(@PathVariable("id") Integer id);

    /**
     * 根据用户名获取当前用户所有评论信息
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/myComments/{username}")
    CommonResult<?> getMyComments(@PathVariable("username") String username);
}


