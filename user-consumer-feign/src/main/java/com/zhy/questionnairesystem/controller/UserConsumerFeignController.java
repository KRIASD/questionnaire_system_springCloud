package com.zhy.questionnairesystem.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.zhy.domain.Comment;
import com.zhy.domain.Survey;
import com.zhy.domain.User;
import com.zhy.questionnairesystem.service.SurveyConsumerFeignService;
import com.zhy.questionnairesystem.service.UserConsumerFeignService;
import com.zhy.req.SubmitDetailReq;
import com.zhy.req.SurveyIdWIthPublisherReq;
import com.zhy.utils.CommonResult;
import com.zhy.vo.UserUpdateInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@RestController
@Slf4j
@RequestMapping("/consumer/user")
@CrossOrigin(origins = "*", maxAge = 3600)
@DefaultProperties(defaultFallback = "fallback")
public class UserConsumerFeignController {
//    public static final String USER_PROVIDE_URL = "http://USER-PROVIDE";
//
//    @Autowired
//    private RestTemplate restTemplate;

    @Autowired
    private UserConsumerFeignService userConsumerFeignService;
    @Autowired
    private SurveyConsumerFeignService surveyConsumerFeignService;

    private static final String avatarPath= "/www/wwwroot/questionnaire/dist/src/assets/";


    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return 200:发送成功 500:发送失败
     */
    @GetMapping("/sendVerificationCode/{phone}")
    public CommonResult<?> sendVerificationCode(@PathVariable("phone") String phone) {
        System.out.println("发送手机验证码");
        System.out.println("phone:" + phone);
//        return restTemplate.getForObject(USER_PROVIDE_URL + "/user/sendVerificationCode/" + phone, CommonResult.class);
        return userConsumerFeignService.sendVerificationCode(phone);
    }

    /**
     * 用户登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 200:登录成功 500:登录失败
     */
    @PostMapping("/login/{username}/{password}")
    public CommonResult<?> login(@PathVariable("username") String username, @PathVariable("password") String password) {
        System.out.println("登录");
        System.out.println("username:" + username);
        System.out.println("password:" + password);
//        return restTemplate.getForObject(USER_PROVIDE_URL + "/user/login/" + username + "/" + password, CommonResult.class);
        return userConsumerFeignService.login(username, password);
    }

    /**
     * 用户登录方法（使用手机验证码）
     *
     * @param phone            手机号码
     * @param verificationCode 验证码
     * @return 200:登录成功 500:登录失败
     */
    @GetMapping("/loginByCode/{phone}/{verificationCode}")
    public CommonResult<?>
    loginByVerificationCode(@PathVariable("phone") String phone, @PathVariable("verificationCode") String verificationCode) {
        System.out.println("使用手机验证码登录");
        System.out.println("phone:" + phone);
        System.out.println("verificationCode:" + verificationCode);
//        return restTemplate.getForObject(USER_PROVIDE_URL + "/user/login/" + phone + "/" + verificationCode, CommonResult.class);
        return userConsumerFeignService.loginByVerificationCode(phone, verificationCode);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/userInfo/username/{username}")
    public CommonResult<?> getUserInfoByUsername(@PathVariable("username") String username) {
        System.out.println("根据用户名获取用户信息");
        System.out.println("username:" + username);
//        return restTemplate.getForObject(USER_PROVIDE_URL + "/user/userInfo/username/" + username, CommonResult.class);
        return userConsumerFeignService.getUserInfoByUsername(username);
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/userInfo/id/{id}")
    public CommonResult<?> getUseInfoById(@PathVariable("id") Integer id) {
        System.out.println("根据用户id获取用户信息");
        System.out.println("id:" + id);
//        return restTemplate.getForObject(USER_PROVIDE_URL + "/user/userInfo/id/" + id, CommonResult.class);\
        return userConsumerFeignService.getUseInfoById(id);
    }

    /**
     * 用户注册方法
     *
     * @param user 用户信息
     * @return 200:注册成功 500:注册失败
     */
    @PutMapping("/register")
    public CommonResult<?> register(@RequestBody User user) {
        System.out.println("用户注册方法");
        System.out.println("user:" + user);
//        return restTemplate.postForObject(USER_PROVIDE_URL + "/user/register?verificationCode=" + verificationCode, user, CommonResult.class);
        return userConsumerFeignService.register(user);
    }

    /**
     * 用户更新头像
     *
     * @param username 用户名
     * @return 200:更新成功 500:更新失败
     */
    @PutMapping("/updateAvatar/{username}")
    public CommonResult<?> updateAvatar(@PathVariable("username") String username, @RequestPart("avatar") MultipartFile avatar) {
        System.out.println("用户更新头像");
        System.out.println("username:" + username);
        System.out.println("avatar:" + avatar);
//        return restTemplate.postForObject(USER_PROVIDE_URL + "/user/updateAvatar/" + username, avatar, CommonResult.class);
        return userConsumerFeignService.updateAvatar(username, avatar);
    }

    /**
     * 用户更新信息
     *
     * @param user   用户信息
     * @param avatar 头像
     * @return 200:更新成功 500:更新失败
     */
    @PutMapping(value = "/updateUserInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<?> updateUserInfo(@RequestPart("user") User user, @RequestPart("avatar") MultipartFile avatar) throws JsonProcessingException {
        System.out.println("用户更新信息");
        System.out.println("user:" + user);
        System.out.println("avatar:" + avatar);
//        return restTemplate.postForObject(USER_PROVIDE_URL + "/user/updateUserInfo", user, CommonResult.class);
        UserUpdateInfo userUpdateInfo = new UserUpdateInfo();
        userUpdateInfo.setUser(user);
        userUpdateInfo.setAvatar(avatar);
        ObjectMapper objectMapper = new ObjectMapper();
        String userUpdateInfoJson = objectMapper.writeValueAsString(userUpdateInfo);
        MultipartFile userUpdateInfoFile = new MockMultipartFile("userUpdateInfo", userUpdateInfoJson.getBytes());
        return userConsumerFeignService.updateUserInfo(userUpdateInfoFile);
    }

    /**
     * 修改密码（发送手机验证码）
     *
     * @param user 用户信息
     * @return 200:发送成功且修改成功 500:发送失败或修改失败
     */
    @PutMapping("/updatePassword/{verificationCode}")
    public CommonResult<?> updatePassword(@RequestBody User user, @PathVariable String verificationCode) {
        System.out.println("修改密码（发送手机验证码）");
        System.out.println("user:" + user);
        System.out.println("verificationCode:" + verificationCode);
//        return restTemplate.postForObject(USER_PROVIDE_URL + "/user/updatePassword?verificationCode=" + verificationCode, user, CommonResult.class);
        return userConsumerFeignService.updatePassword(user, verificationCode);
    }

    /**
     * 退出登录
     *
     * @param username 用户名
     * @return 200:退出成功 500:退出失败
     */
    @PutMapping("/logout/{username}")
    public CommonResult<?> logout(@PathVariable("username") String username) {
        System.out.println("退出登录");
        System.out.println("username:" + username);
//        return restTemplate.postForObject(USER_PROVIDE_URL + "/user/logout/" + username, null, CommonResult.class);
        return userConsumerFeignService.logout(username);
    }

    /**
     * 账户彻底注销删除
     *
     * @param username         用户名
     * @param verificationCode 验证码
     * @return 200:注销成功 500:注销失败
     */
    @DeleteMapping("/deleteUser/{username}/{verificationCode}")
    public CommonResult<?> deleteUser(@PathVariable("username") String username, @PathVariable("verificationCode") String verificationCode) {
        System.out.println("账户彻底注销删除");
        System.out.println("username:" + username);
        System.out.println("verificationCode:" + verificationCode);
//        return restTemplate.postForObject(USER_PROVIDE_URL + "/user/deleteUser/" + username + "/" + verificationCode, null, CommonResult.class);
        return userConsumerFeignService.deleteUser(username, verificationCode);
    }

    /**
     * 获取用户列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/userList/{currentPage}/{pageSize}")
    public CommonResult<?> getUserList(@PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize) {
        System.out.println("获取用户列表");
        System.out.println("currentPage:" + currentPage);
        System.out.println("pageSize:" + pageSize);
//        return restTemplate.getForObject(USER_PROVIDE_URL + "/user/userList/" + currentPage + "/" + pageSize, CommonResult.class);
        return userConsumerFeignService.getUserList(currentPage, pageSize);
    }

    /**
     * 测试断路器
     *
     * @param id id
     * @return 200:调用成功 500:调用失败
     */
    @HystrixCommand(commandProperties = {
            // 设置断路器生效
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
            // 请求次数
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),
            // 失败率达到多少后跳闸
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")
    })
    @GetMapping("/testCircuitBreaker/{id}")
    public CommonResult<?> testCircuitBreaker(@PathVariable("id") Integer id) {
        return userConsumerFeignService.testCircuitBreaker(id);
    }

    /**
     * 分页获取问卷列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
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
     * @return survey 问卷信息
     */
    @GetMapping("/survey/detail/{surveyId}")
    public CommonResult<?> getSurveyDetail(@PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getSurveyDetail(surveyId);
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
     * 获取热门问卷列表
     *
     * @return 热门问卷列表
     */
    @GetMapping("/survey/hot")
    public CommonResult<?> getHotSurveyList() {
        return surveyConsumerFeignService.getHotSurveyList();
    }

    /**
     * 多条件模糊查询
     *
     * @param survey      问卷信息
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 问卷列表
     */
    @GetMapping("survey/search/{currentPage}/{pageSize}")
    public CommonResult<?> searchSurvey(@RequestPart("survey") Survey survey, @PathVariable("currentPage") Integer currentPage, @PathVariable("pageSize") Integer pageSize) {
        return surveyConsumerFeignService.searchSurvey(survey, currentPage, pageSize);
    }

    /**
     * 对用户个性化推荐问卷
     *
     * @param userId 用户id
     * @return 问卷列表
     */
    @GetMapping("/survey/recommend/{userId}")
    public CommonResult<?> recommendSurvey(@PathVariable Integer userId) {
        return surveyConsumerFeignService.getRecommendSurveyList(userId);
    }

    /**
     * 下载问卷
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:下载成功 500:下载失败
     */
    @GetMapping("/survey/download/{username}/{surveyId}")
    public CommonResult<?> downloadSurvey(@PathVariable String username, @PathVariable Integer surveyId) {
        return surveyConsumerFeignService.downloadSurvey(username, surveyId);
    }

    /**
     * 根据所有填写过当前问卷的用户的答案，生成问卷的统计信息
     *
     * @param surveyId 问卷id
     * @return 200:生产成功 500:生成失败
     */
    @GetMapping("/survey/surveySubmittedStatistics/{surveyId}")
    public CommonResult<?> getSurveySubmittedStatistics(@PathVariable Integer surveyId) {
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
     * 根据用户名查询该用户的提交记录
     *
     * @param username 用户名
     * @return 200:查询成功 500:查询失败
     */
    @GetMapping("/survey/submittedSurvey/{username}/{currentPage}/{pageSize}")
    public CommonResult<?> getSubmittedByUsername(@PathVariable String username, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getSubmittedByUsername(username, currentPage, pageSize);
    }

    /**
     * 根据提交记录id查询提交记录详情
     *
     * @param submittedId 提交记录id
     * @param username    用户名
     * @return 200:查询成功 500:查询失败
     */
    @GetMapping("/survey/submittedRecordDetail/{submittedId}/{username}")
    public CommonResult<?> getSubmittedRecordDetail(@PathVariable Integer submittedId, @PathVariable String username) {
        return surveyConsumerFeignService.getSubmittedRecordDetail(submittedId, username);
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
     * 用户填写提交问卷
     *
     * @param req 提交问卷详情
     * @return 200:提交成功 500:提交失败
     */
    @PutMapping("/survey/submit")
    public CommonResult<?> submitSurvey(@RequestBody SubmitDetailReq req) {
        return surveyConsumerFeignService.submitSurvey(req);
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
     * 用户评论问卷并打分
     *
     * @param surveyId 问卷id
     * @param username 用户名
     * @param comment  评论
     * @return 200:评论成功 500:评论失败
     */
    @PutMapping("/survey/doComment/{surveyId}/{username}")
    public CommonResult<?> doComment(@PathVariable Integer surveyId, @PathVariable String username, @RequestPart Comment comment) {
        return surveyConsumerFeignService.commentSurvey(surveyId, username, comment);
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
     * 获取该用户可以填写的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/canSubmitSurvey/{username}/{currentPage}/{pageSize}")
    public CommonResult<?> getCanSubmitSurvey(@PathVariable String username, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getCanSubmitSurvey(username, currentPage, pageSize);
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
     * 通过redis查询发布到当前用户的问卷
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/getSurveyBySelf/{username}/{currentPage}/{pageSize}")
    public CommonResult<?> getSurveyBySelf(@PathVariable String username, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        return surveyConsumerFeignService.getSurveyBySelf(username, currentPage, pageSize);
    }

    /**
     * 通过根据发布人和问卷id查询redis中的问卷
     *
     * @param surveyIdWithPublisherReq 请求体
     * @return 200:获取成功 500:获取失败
     */
    @PostMapping("/survey/detailByPublisher")
    public CommonResult<?> getSurveyDetailByPublisher(@RequestBody SurveyIdWIthPublisherReq surveyIdWithPublisherReq) {
        return surveyConsumerFeignService.getSurveyDetailByPublisher(surveyIdWithPublisherReq);
    }

    /**
     * 根据用户名和问卷id获取当前用户对当前问卷的评论
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/myComment/{username}/{surveyId}")
    public CommonResult<?> getMyComment(@PathVariable String username, @PathVariable Integer surveyId) {
        return surveyConsumerFeignService.getMyComment(username, surveyId);
    }

    /**
     * 获取ai建议
     *
     * @param id 提交记录id
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/aiSuggest/{id}")
    public CommonResult<?> getAiSuggest(@PathVariable Integer id) {
        return surveyConsumerFeignService.getAiSuggestions(id);
    }

    /**
     * 注册时上传头像
     */
    @PostMapping("/uploadAvatarRegister")
    public CommonResult<?> uploadAvatarRegister(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String destinationPath = avatarPath + originalFilename;

        // Save the file
        file.transferTo(new File(destinationPath));

        // Check if the file exists
        File newFile = new File(destinationPath);
        if (!newFile.exists()) {
            return CommonResult.failed("Failed to save the file.");
        }

        // Copy the file
        String copiedFilePath = avatarPath + originalFilename;
        Files.copy(newFile.toPath(), new File(copiedFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("path", "src/assets/Avatar" + originalFilename);
        return CommonResult.success(jsonObject);
    }

    /**
     * 根据用户名获取当前用户所有评论信息
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @GetMapping("/survey/myComments/{username}")
    public CommonResult<?> getMyComments(@PathVariable String username) {
        return surveyConsumerFeignService.getMyComments(username);
    }
    /**
     * 降级方法
     *
     * @return 500:Global异常处理信息，请稍后重试。
     */
    public CommonResult<?> fallback() {
        return CommonResult.failed("Global异常处理信息，请稍后重试。");
    }
}
