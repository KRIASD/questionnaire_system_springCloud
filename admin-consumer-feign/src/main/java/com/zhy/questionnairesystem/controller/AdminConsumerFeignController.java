package com.zhy.questionnairesystem.controller;

import com.zhy.domain.Survey;
import com.zhy.questionnairesystem.service.AdminConsumerFeignService;
import com.zhy.questionnairesystem.service.SurveyConsumerFeignService;
import com.zhy.questionnairesystem.service.UserConsumerFeignService;
import com.zhy.req.CreateSurveyReq;
import com.zhy.req.EditMySurveyReq;
import com.zhy.req.EditMySurveyWillReq;
import com.zhy.req.PublishConditionListReq;
import com.zhy.utils.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/consumer/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
//@DefaultProperties(defaultFallback = "fallback")
public class AdminConsumerFeignController {
    @Autowired
    private AdminConsumerFeignService adminConsumerFeignService;
    @Autowired
    private SurveyConsumerFeignService surveyConsumerFeignService;
    @Autowired
    private UserConsumerFeignService userConsumerFeignService;

    /**
     * 管理员登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    @GetMapping("/login/{username}/{password}")
    public CommonResult<?> login(@PathVariable String username, @PathVariable String password) {
        return adminConsumerFeignService.login(username, password);
    }

    /**
     * 获取管理员信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/info/{username}")
    public CommonResult<?> getAdminInfo(@PathVariable String username) {
        return adminConsumerFeignService.getAdminInfo(username);
    }

    /**
     * 获取用户列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/user/userList/{currentPage}/{pageSize}")
    public CommonResult<?> getUserList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return userConsumerFeignService.getUserList(currentPage, pageSize);
    }


    /**
     * 分页获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return surveys 问卷列表
     */
    @GetMapping("/survey/list/{currentPage}/{pageSize}")
    public CommonResult<?> getSurveyList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getSurveyList(currentPage, pageSize);
    }

    /**
     * 查看问卷详情
     *
     * @param surveyId 问卷id
     * @return Survey  问卷详情
     */
    @GetMapping("/survey/detail/{surveyId}")
    public CommonResult<?> getSurveyDetail(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getSurveyDetail(surveyId);
    }

    /**
     * 新建问卷
     *
     * @param survey 问卷
     * @return surveys 问卷列表
     */
    @PutMapping("/survey/create")
    public CommonResult<?> createSurvey(@RequestBody CreateSurveyReq survey) {
        return surveyConsumerFeignService.createSurvey(survey);
    }

    /**
     * 更新问卷详情
     *
     * @param survey 问卷详情
     * @return 200:修改成功 500:修改失败
     */
    @PutMapping("/survey/updateDetail")
    public CommonResult<?> updateSurveyDetail(@RequestBody Survey survey) {
        return surveyConsumerFeignService.updateSurveyDetail(survey);
    }

    /**
     * 查看问卷简要信息
     *
     * @param surveyId 问卷id
     * @return Survey 问卷简要信息
     */
    @GetMapping("/survey/brief/{surveyId}")
    public CommonResult<?> getSurveyBrief(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getSurveyBrief(surveyId);
    }

    /**
     * 修改问卷的简要信息
     *
     * @param survey 问卷
     * @return 200:修改成功 500:修改失败
     */
    @PutMapping("/survey/updateWill")
    public CommonResult<?> updateSurveyWill(@RequestBody Survey survey) {
        return surveyConsumerFeignService.updateSurveyWill(survey);
    }

    /**
     * 发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:发布成功 500:发布失败
     */
    @PutMapping("/survey/publish/{surveyId}")
    public CommonResult<?> publishSurvey(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.publishSurvey(surveyId);
    }

    /**
     * 取消发布问卷
     *
     * @param surveyId 问卷id
     */
    @PutMapping("/survey/cancelPublish/{surveyId}")
    public CommonResult<?> cancelPublishSurvey(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.cancelPublishSurvey(surveyId);
    }

    /**
     * 删除问卷的问题
     *
     * @param questionId 问题id
     * @return 200:删除成功 500:删除失败
     */
    @PutMapping("/survey/deleteQuestion/{questionId}")
    public CommonResult<?> deleteQuestion(@PathVariable Integer questionId) {
        return surveyConsumerFeignService.deleteQuestion(questionId);
    }

    /**
     * 删除问卷的选项
     *
     * @param questionId 问题id
     * @param optionId   选项id
     * @return 200:删除成功 500:删除失败
     */
    @PutMapping("/survey/deleteOption/{questionId}/{optionId}")
    public CommonResult<?> deleteOption(@PathVariable Integer questionId, @PathVariable Integer optionId) {
        return surveyConsumerFeignService.deleteOption(questionId, optionId);
    }

    /**
     * 删除问卷
     *
     * @param surveyId 问卷id
     * @return 200:删除成功 500:删除失败
     */
    @PutMapping("/survey/deleteSurvey/{surveyId}")
    public CommonResult<?> deleteSurvey(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.deleteSurvey(surveyId);
    }

    /**
     * 获取热门问卷列表
     *
     * @return 热门问卷列表
     */
    @GetMapping("/survey/survey/hot")
    public CommonResult<?> getHotSurveyList() {
        return surveyConsumerFeignService.getHotSurveyList();
    }

    /**
     * 多条件模糊查询
     *
     * @param survey      问卷
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 问卷列表
     */
    @GetMapping("/survey/search/{currentPage}/{pageSize}")
    public CommonResult<?> searchSurvey(@RequestPart("survey") Survey survey, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize) {
        return surveyConsumerFeignService.searchSurvey(survey, currentPage, pageSize);
    }


    /**
     * 下载问卷
     *
     * @param surveyId 问卷id
     * @return 200:下载成功 500:下载失败
     */
    @GetMapping("/survey/download/{username}/{surveyId}")
    public CommonResult<?> downloadSurvey(@PathVariable String username, @PathVariable Integer surveyId) {
        return surveyConsumerFeignService.downloadSurvey(username, surveyId);
    }

    /**
     * 根据所有填写过当前的问卷的用户的答案，生成问卷的统计信息
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    @GetMapping("/survey/surveySubmittedStatistics/{surveyId}")
    public CommonResult<?> surveySubmittedStatistics(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.surveySubmittedStatistics(surveyId);
    }

    /**
     * 针对每个问题的每个选项，统计选择该选项的人数
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    @GetMapping("/survey/surveySubmittedStatisticsByOption/{surveyId}")
    public CommonResult<?> surveySubmittedStatisticsByOption(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.surveySubmittedStatisticsByOption(surveyId);
    }

    /**
     * 获取所有的提交问卷记录
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedRecords/{currentPage}/{pageSize}")
    public CommonResult<?> getSubmittedRecords(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getAllSubmittedRecords(currentPage, pageSize);
    }

    /**
     * 查询一条提交记录的详情
     *
     * @param submittedId 提交记录id
     * @param username    用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedRecordDetail/{submittedId}/{username}")
    public CommonResult<?> getSubmittedRecordDetail(@PathVariable Integer submittedId, @PathVariable String username) {
        return surveyConsumerFeignService.getSubmittedRecordDetail(submittedId, username);
    }

    /**
     * 根据用户名查询该用户的提交记录
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedRecordByUserId/{username}/{currentPage}/{pageSize}")
    public CommonResult<?> getSubmittedRecordByUserId(@PathVariable String username, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getSubmittedByUsername(username, currentPage, pageSize);
    }


    /**
     * 根据问卷id获取该问卷的提交总数
     *
     * @param surveyId 问卷id
     * @return 当前问卷的提交总数
     */
    @GetMapping("/survey/submittedCount/{surveyId}")
    public CommonResult<?> getSubmittedCount(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getSubmittedCount(surveyId);
    }

    /**
     * 根据问卷id查询该问卷的所有评论
     *
     * @param surveyId    问卷id
     * @param currentPage 当前页
     * @param pageSize    页大小
     */
    @GetMapping("/survey/comments/{surveyId}/{currentPage}/{pageSize}")
    public CommonResult<?> getComments(@PathVariable Integer surveyId, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getComments(surveyId, currentPage, pageSize);
    }

    /**
     * 获取所有问卷的类型（去重做类型选择器）
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/types")
    public CommonResult<?> getSurveyTypes() {
        return surveyConsumerFeignService.getSurveyTypes();
    }


    /**
     * 根据问卷类型获取该类型的所有问卷
     *
     * @param surveyType  问卷类型
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/type/{surveyType}/{currentPage}/{pageSize}")
    public CommonResult<?> getSurveyByType(@PathVariable String surveyType, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getSurveyByType(surveyType, currentPage, pageSize);
    }

    /**
     * 统计一天内的提交问卷数(Map<"surveyId", "submittedCount">)
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedCountByDay")
    public CommonResult<?> getSubmittedCountByDay() {
        return surveyConsumerFeignService.getSubmittedCountByDay();
    }

    /**
     * 根据问卷id统计该问卷的问题的类型
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/questionTypeStatistics/{surveyId}")
    public CommonResult<?> getQuestionType(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getQuestionType(surveyId);
    }

    /**
     * 按照问卷的提交量，按照时间段统计每天的问卷提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedCountByTime")
    public CommonResult<?> getSubmittedCountByTime() {
        return surveyConsumerFeignService.getSubmittedCountByTime();
    }

    /**
     * 按照提交量正序排序，获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/listBySubmittedCount/{currentPage}/{pageSize}")
    public CommonResult<?> getSurveyListBySubmittedCount(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getSurveyListBySubmittedCount(currentPage, pageSize);
    }

    /**
     * 按照提交量倒序排序，获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/listBySubmittedCountDesc/{currentPage}/{pageSize}")
    public CommonResult<?> getSurveyListBySubmittedCountDesc(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getSurveyListBySubmittedCountDesc(currentPage, pageSize);
    }

    /**
     * 根据问卷提交量按照星期统计所有问卷的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/activeSurveyCountByWeek")
    public CommonResult<?> getActiveSurveyCountByWeek() {
        return surveyConsumerFeignService.getActiveSurveyCountByWeek();
    }


    /**
     * 为指定条件的学生发布问卷
     *
     * @param publishConditionListReq 发布条件
     * @return 200:发布成功 500:发布失败
     */
    @PutMapping(value = "/survey/publishSurveyForCondition", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommonResult<?> publishSurveyForCondition(@RequestBody PublishConditionListReq publishConditionListReq) {
        System.out.println(publishConditionListReq);
        return surveyConsumerFeignService.publishSurveyForCondition(publishConditionListReq);
    }

    /**
     * 为指定条件的学生取消发布问卷
     *
     * @param publishConditionListReq 取消发布条件
     * @return 200:发布成功 500:发布失败
     */
    @PutMapping("/survey/cancelPublishSurveyForCondition")
    public CommonResult<?> cancelPublishSurveyForCondition(@RequestBody PublishConditionListReq publishConditionListReq) {
        return surveyConsumerFeignService.cancelPublishSurveyForCondition(publishConditionListReq);
    }

    /**
     * 获取用户发布的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/publishedSurvey/{username}/{currentPage}/{pageSize}")
    public CommonResult<?> getPublishedSurvey(@PathVariable String username, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getPublishedSurvey(username, currentPage, pageSize);
    }

    /**
     * 获取所有的学院
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getCollege")
    public CommonResult<?> getCollege() {
        return surveyConsumerFeignService.getCollege();
    }

    /**
     * 根据学院code获取该学院的所有专业
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getMajor")
    public CommonResult<?> getMajor() {
        return surveyConsumerFeignService.getMajor();
    }

    /**
     * 获取当前问卷的响应率，填写人数/发布人数
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/responseRate/{username}/{surveyId}")
    public CommonResult<?> getResponseRate(@PathVariable String username, @PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getResponseRate(username, surveyId);
    }

    /**
     * 根據用戶名查询该用户发布的所有的问卷的提交列表
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getPublishedSurveySubmit/{username}/{currentPage}/{pageSize}")
    public CommonResult<?> getPublishedSurveySubmit(@PathVariable String username, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getPublishedSurveySubmit(username, currentPage, pageSize);
    }

    /**
     * 根据问卷id和用户名查询该用户对该问卷的评论
     *
     * @param surveyId 问卷id
     * @param username 用户id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/comments/{surveyId}/{username}")
    public CommonResult<?> getComment(@PathVariable Integer surveyId, @PathVariable String username) {
        return surveyConsumerFeignService.getComment(surveyId, username);
    }

    /**
     * 根据问卷id统计该问卷的提交时间和提交数量
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedTotalByTime/{surveyId}")
    public CommonResult<?> getSubmittedTotalByTime(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getSubmittedTotalByTime(surveyId);
    }

    /**
     * 添加问卷到我的问卷
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:添加成功 500:添加失败
     */
    @PutMapping("/survey/addMySurvey/{surveyId}/{username}")
    public CommonResult<?> addMySurvey(@PathVariable Integer surveyId, @PathVariable String username) {
        return surveyConsumerFeignService.addMySurvey(surveyId, username);
    }

    /**
     * 管理员：根据用户名查询该用户的我的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getMySurveyAdmin/{username}/{currentPage}/{pageSize}")
    public CommonResult<?> getMySurveyAdmin(@PathVariable String username, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getMySurveyAdmin(username, currentPage, pageSize);
    }

    /**
     * 根据问卷id删除我的问卷中的问卷
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @return 200:删除成功 500:删除失败
     */
    @DeleteMapping("/survey/deleteMySurveyById/{surveyId}/{username}")
    public CommonResult<?> deleteMySurveyById(@PathVariable Integer surveyId, @PathVariable String username) {
        return surveyConsumerFeignService.deleteMySurveyById(surveyId, username);
    }

    /**
     * 根据问卷id和用户名修改问卷信息
     *
     * @param editMySurveyWillReq 问卷
     * @return 200:修改成功 500:修改失败
     */
    @PutMapping("/survey/editMySurveyWill")
    public CommonResult<?> editMySurveyWill(@RequestBody EditMySurveyWillReq editMySurveyWillReq) {
        return surveyConsumerFeignService.editMySurveyWill(editMySurveyWillReq);
    }

    /**
     * 根据用户名和问卷id修改问卷详情
     *
     * @param editMySurveyReq 修改问卷详情请求体
     * @return 200:修改成功 500:修改失败
     */
    @PutMapping("/survey/editMySurveyDetail")
    public CommonResult<?> editMySurvey(@RequestBody EditMySurveyReq editMySurveyReq) {
        return surveyConsumerFeignService.editMySurveyDetail(editMySurveyReq);
    }

    /**
     * 管理员：根据用户名和问卷id，查询我的问卷中的问卷的详情（Redis）
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/mySurvey/detail/{username}/{surveyId}")
    public CommonResult<?> getMySurveyDetail(@PathVariable String username, @PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getMySurveyDetail(username, surveyId);
    }

    /**
     * 按照用户登录的时间段按星期统计所有用户的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getActiveUserByWeek")
    public CommonResult<?> getActiveUserByWeek() {
        return surveyConsumerFeignService.getActiveUserByWeek();
    }

    /**
     * 获取总提交量和今日提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedStatics")
    public CommonResult<?> getSubmittedStatics() {
        return surveyConsumerFeignService.getSubmittedStatics();
    }

    /**
     * 获取问卷总数和本月新增问卷数
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/surveyStatics")
    public CommonResult<?> getSurveyStatics() {
        return surveyConsumerFeignService.getSurveyStatics();
    }

    /**
     * 获取系统总访问量、今日访问量和增加百分比
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/userStatics")
    public CommonResult<?> getUserStatics() {
        return surveyConsumerFeignService.getUserStatics();
    }

    /**
     * 获取评论总数和今日新增评论数
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/commentStatics")
    public CommonResult<?> getCommentStatics() {
        return surveyConsumerFeignService.getCommentStatics();
    }

    /**
     * 近十天的系统访问量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/userStaticsByTenDay")
    public CommonResult<?> getUserStaticsByTenDay() {
        return surveyConsumerFeignService.getUserStaticsByTenDay();
    }

    /**
     * 根据评论量来统计最热门的问卷
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/hotSurveyByComment")
    public CommonResult<?> getHotSurveyByComment() {
        return surveyConsumerFeignService.getHotSurveyByComment();
    }

    /**
     * 根据类型统计提交问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedCountByType")
    public CommonResult<?> getSubmittedCountByType() {
        return surveyConsumerFeignService.getSubmittedCountByType();
    }

    /**
     * 统计每个学院的提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/submittedCountByCollege")
    public CommonResult<?> getSubmittedCountByCollege() {
        return surveyConsumerFeignService.getSubmittedCountByCollege();
    }

    /**
     * 统计所有反馈分数每个分段的人数
     *
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/feedbackScoreStatistics")
    public CommonResult<?> getFeedbackScoreStatistics() {
        return surveyConsumerFeignService.getFeedbackScoreStatistics();
    }

    /**
     * 查看系统日志
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/systemLog/{username}/{currentPage}/{pageSize}")
    public CommonResult<?> getSystemLog(@PathVariable String username, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getSystemLog(username, currentPage, pageSize);
    }


    /**
     * 接入AI分析问卷回答情况
     *
     * @param id 提交记录id
     * @return 200:接入成功 500:接入失败
     */
    @GetMapping("/survey/analyseEmotion/{id}")
    public CommonResult<?> analyseEmotion(@PathVariable Integer id) throws JSONException {
        return surveyConsumerFeignService.analyseEmotion(id);
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名
     * @return 200:下载成功 500:下载失败
     */
    @GetMapping("/survey/downloadSurveyTemplate")
    public CommonResult<?> downloadSurveyTemplate(String fileName) {
        return surveyConsumerFeignService.downloadSurveyTemplate(fileName);
    }

    /**
     * 解析文件成问卷
     *
     * @param filePath 文件路径
     * @return 200:解析成功 500:解析失败
     */
    @GetMapping("/survey/parseSurvey")
    public CommonResult<?> parseSurvey(@RequestParam String filePath) {
        return surveyConsumerFeignService.parseSurvey(filePath);
    }

    /**
     * 根据解析结果创建问卷
     *
     * @param survey 问卷
     * @return 200:创建成功 500:创建失败
     */
    @PutMapping("/survey/createByParse")
    public CommonResult<?> createByParse(@RequestBody Survey survey) {
        return surveyConsumerFeignService.createByParse(survey);
    }
}