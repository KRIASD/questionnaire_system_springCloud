package com.zhy.querstionnairesystem.service;

import com.zhy.domain.Comment;
import com.zhy.domain.Survey;
import com.zhy.req.*;
import com.zhy.utils.CommonResult;
import org.json.JSONException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public interface SurveyService {
    /**
     * 查看问卷详情
     *
     * @param surveyId 问卷id
     * @return Survey
     */
    CommonResult<?> getSurveyDetail(Integer surveyId);

    /**
     * 分页获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return surveys 问卷列表
     */
    CommonResult<?> getSurveyList(Integer currentPage, Integer pageSize);

    /**
     * 新建问卷
     *
     * @param survey 问卷
     * @return surveys 问卷列表
     */
    CommonResult<?> createSurvey(CreateSurveyReq survey);

    /**
     * 修改问卷信息
     *
     * @param survey 问卷详情
     * @return 200:修改成功 500:修改失败
     */
    CommonResult<?> updateSurveyDetail(Survey survey);

    /**
     * 删除问卷
     *
     * @param surveyId 问卷id
     * @return 200:删除成功 500:删除失败
     */
    CommonResult<?> getSurveyBrief(Integer surveyId);

    /**
     * 修改问卷的简要信息
     *
     * @param survey 问卷详情
     * @return 200:修改成功 500:修改失败
     */
    CommonResult<?> updateSurveyWill(Survey survey);

    /**
     * 发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:发布成功 500:发布失败
     */
    CommonResult<?> publishSurvey(Integer surveyId);

    /**
     * 取消发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:取消发布成功 500:取消发布失败
     */
    CommonResult<?> cancelPublishSurvey(Integer surveyId);

    /**
     * 删除问卷的问题
     *
     * @param questionId 问题id
     * @return 200:删除成功 500:删除失败
     */
    CommonResult<?> deleteQuestion(Integer questionId);

    /**
     * 删除问题的选项
     *
     * @param questionId 问题id
     * @param optionId   选项id
     * @return 200:删除成功 500:删除失败
     */
    CommonResult<?> deleteOption(Integer questionId, Integer optionId);

    /**
     * 删除问卷
     *
     * @param surveyId 问卷id
     * @return 200:删除成功 500:删除失败
     */
    CommonResult<?> deleteSurvey(Integer surveyId);

    /**
     * 获取热门问卷列表
     *
     * @return 热门问卷列表
     */
    CommonResult<?> getHotSurveyList();

    /**
     * 多条件模糊查询
     *
     * @param survey 问卷
     * @return 问卷列表
     */
    CommonResult<?> searchSurvey(Survey survey, Integer currentPage, Integer pageSize);

    /**
     * 对用户个性化推荐问卷
     *
     * @param userId 用户id
     * @return 个性化推荐问卷列表
     */
    CommonResult<?> getRecommendSurveyList(Integer userId);

    /**
     * 下载问卷
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:下载成功 500:下载失败
     */
    CommonResult<?> downloadSurvey(String username, Integer surveyId);

    /**
     * 根据所有填写过当前的问卷的用户的答案，生成问卷的统计信息
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    CommonResult<?> surveySubmittedStatistics(Integer surveyId);

    /**
     * 针对每个问题的每个选项，统计选择该选项的人数
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    CommonResult<?> surveySubmittedStatisticsByOption(Integer surveyId);

    /**
     * 获取所有的提交问卷记录
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getAllSubmittedRecords(Integer currentPage, Integer pageSize);

    /**
     * 查询一条提交记录的详情
     *
     * @param submittedId 提交记录
     * @param username    用户名
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmittedRecordDetail(Integer submittedId, String username);

    /**
     * 根据用户名查询该用户的提交记录
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmittedByUsername(String username, Integer currentPage, Integer pageSize);

    /**
     * 根据问卷id查询该问卷的提交总数
     *
     * @param surveyId 问卷id
     * @return 当前问卷的提交总数
     */
    CommonResult<?> getSubmittedCount(Integer surveyId);

    /**
     * 用户填写提交问卷
     *
     * @param req 问卷
     * @return 200:提交成功 500:提交失败
     */
    CommonResult<?> submitSurvey(SubmitDetailReq req) throws JSONException;

    /**
     * 根据问卷id和用户名查询该用户对该问卷的评论
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getComment(Integer surveyId, String username);

    /**
     * 根据问卷id查询该问卷的所有评论
     *
     * @param surveyId    问卷id
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getComments(Integer surveyId, Integer currentPage, Integer pageSize);

    /**
     * 用户评论问卷并打分
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @param comment  评论
     * @return 200:评论成功 500:评论失败
     */
    CommonResult<?> commentSurvey(Integer surveyId, String username, Comment comment);


    /**
     * 获取所有问卷的类型（去重做类型选择器）
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSurveyTypes();


    /**
     * 根据问卷类型获取该类型的所有问卷
     *
     * @param surveyType  问卷类型
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSurveysByType(String surveyType, Integer currentPage, Integer pageSize);

    /**
     * 统计一天内的提交问卷数量
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmittedCountByDay();

    /**
     * 根据问卷id统计该问卷问题的类型
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getQuestionType(Integer surveyId);

    /**
     * 按照问卷的提交量，按照时间段统计每天的问卷提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmittedCountByTime();

    /**
     * 按照提交量正序排序，获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSurveyListBySubmittedCount(Integer currentPage, Integer pageSize);

    /**
     * 按照提交量倒序排序，获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSurveyListBySubmittedCountDesc(Integer currentPage, Integer pageSize);

    /**
     * 根据问卷提交量按照星期统计所有问卷的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getActiveSurveyCountByWeek();


    /**
     * 为指定条件的学生发布问卷
     *
     * @param publishConditionListReq 发布条件
     * @return 200:发布成功 500:发布失败
     */
    CommonResult<?> publishSurveyForCondition(PublishConditionListReq publishConditionListReq);

    /**
     * 为指定条件的学生取消发布问卷
     *
     * @param publishConditionListReq 取消发布条件
     * @return 200:发布成功 500:发布失败
     */
    CommonResult<?> cancelPublishSurveyForCondition(PublishConditionListReq publishConditionListReq);

    /**
     * 获取用户发布的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getPublishedSurvey(String username, Integer currentPage, Integer pageSize);

    /**
     * 获取该用户可以填写的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getCanSubmitSurvey(String username, Integer currentPage, Integer pageSize);

    /**
     * 获取所有的学院和专业
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getCollege();

    /**
     * 根据学院获取专业
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getMajor();

    /**
     * 根据问卷的类型统计每种类型下的问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSurveyTypeCount();


    /**
     * 根据类型统计提交问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmittedCountByType();

    /**
     * 获取问卷的响应率
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getResponseRate(String username, Integer surveyId);

    /**
     * 根据传入的被发布的学院或者专业或者年级或者班级，获取当前参数匹配的人群的提交信息
     *
     * @param publishConditionReq 查询条件
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmitInfoByCondition(PublishConditionReq publishConditionReq);

    /**
     * 根據用戶名查询该用户发布的所有的问卷的提交列表
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getPublishedSurveySubmit(String username, Integer currentPage, Integer pageSize);

    /**
     * 根据问卷id统计该问卷的提交时间和提交数量
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmittedTotalByTime(Integer surveyId);

    /**
     * 添加问卷到我的问卷
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:添加成功 500:添加失败
     */
    CommonResult<?> addMySurvey(Integer surveyId, String username);

    /**
     * 管理员：根据用户名查询该用户的我的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getMySurveyAdmin(String username, Integer currentPage, Integer pageSize);

    /**
     * 根据问卷id删除我的问卷中的问卷
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:删除成功 500:删除失败
     */
    CommonResult<?> deleteMySurveyById(Integer surveyId, String username);

    /**
     * 根据问卷id和用户名修改问卷信息
     *
     * @param editMySurveyWillReq 问卷
     * @return 200:修改成功 500:修改失败
     */
    CommonResult<?> editMySurveyWill(EditMySurveyWillReq editMySurveyWillReq);

    /**
     * 根据用户名和问卷id修改问卷详情
     *
     * @param editMySurveyReq 修改问卷详情请求体
     * @return 200:修改成功 500:修改失败
     */
    CommonResult<?> editMySurveyDetail(EditMySurveyReq editMySurveyReq);

    /**
     * 管理员：根据用户名和问卷id，查询我的问卷中的问卷的详情（Redis）
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getMySurveyDetail(String username, Integer surveyId);

    /**
     * 按照用户登录的时间段按星期统计所有用户的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getActiveUserByWeek();

    /**
     * 获取总提交量和今日提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmittedStatics();


    /**
     * 获取问卷总数和本月新增问卷数
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSurveyStatics();


    /**
     * 获取系统总访问量、今日访问量和增加百分比
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getUserStatics();

    /**
     * 获取评论总数和今日新增评论数
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getCommentStatics();

    /**
     * 近十天的系统访问量
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getUserStaticsByTenDay();

    /**
     * 根据评论量来统计最热门的问卷
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getHotSurveyByComment();

    /**
     * 统计每个学院的提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSubmittedCountByCollege();

    /**
     * 统计所有反馈分数每个分段的人数
     *
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getFeedbackScoreStatistics();

    /**
     * 获取系统日志
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSystemLog(String username, Integer currentPage, Integer pageSize);

    /**
     * 获取问卷的AI分析信息
     *
     * @param id 问卷id
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> analyseEmotion(Integer id) throws JSONException;

    /**
     * 文件下载
     *
     * @param fileName 文件名
     * @param os       输出流
     */
    void downloadSurveyTemplate(String fileName, OutputStream os) throws FileNotFoundException, IOException;

    /**
     * 上传问卷
     *
     * @param file 文件
     * @return 200:上传成功 500:上传失败
     */
    CommonResult<?> uploadSurvey(MultipartFile file) throws IOException;

    /**
     * 解析问卷
     *
     * @param filePath 文件路径
     * @return 200:解析成功 500:解析失败
     */
    CommonResult<?> parseSurvey(String filePath);

    /**
     * 根据解析结果创建问卷
     *
     * @param survey 问卷
     * @return 200:创建成功 500:创建失败
     */
    CommonResult<?> createSurveyByParse(Survey survey);

    /**
     * 通过redis查询发布到当前用户的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSurveyBySelf(String username, Integer currentPage, Integer pageSize);

    /**
     * 通过根据发布人和问卷id查询redis中的问卷
     *
     * @param surveyIdWithPublisherReq 请求体
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getSurveyDetailByPublisher(SurveyIdWIthPublisherReq surveyIdWithPublisherReq);

    /**
     * 根据用户名和问卷id获取当前用户对当前问卷的评论
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getMyComment(String username, Integer surveyId);

    /**
     * 获取ai建议
     *
     * @param id 提交记录id
     * @return 200:获取成功 500:获取失败
     */
      CommonResult<?> getAiSuggest(Integer id);

    /**
     * 根据用户名获取当前用户所有评论信息
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    CommonResult<?> getMyComments(String username);
}
