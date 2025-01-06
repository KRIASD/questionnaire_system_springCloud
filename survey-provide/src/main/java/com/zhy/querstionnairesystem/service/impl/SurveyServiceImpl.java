package com.zhy.querstionnairesystem.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhy.domain.*;
import com.zhy.dto.*;
import com.zhy.enums.CollegeEnum;
import com.zhy.enums.MajorEnum;
import com.zhy.querstionnairesystem.mapper.*;
import com.zhy.querstionnairesystem.service.EmotionAnalyseService;
import com.zhy.querstionnairesystem.service.SurveyService;
import com.zhy.req.*;
import com.zhy.resp.*;
import com.zhy.utils.CommonResult;
import com.zhy.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@PropertySource("classpath:/properties/image.properties")
public class SurveyServiceImpl implements SurveyService {

    private static final String API_KEY = "yfBbQJipNf49LckqBwRtlWzq";
    private static final String SECRET_KEY = "aKrg7TZ6xy7YaKe9vKgTky4bHTrYygAh";
    private String contentText;
    @Autowired
    private SurveyMapper surveyMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private OptionBriefMapper optionBriefMapper;
    @Autowired
    private OptionContentMapper optionContentMapper;
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    @Autowired
    private SubmitSurveyRecordMapper submitSurveyRecordMapper;
    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private AccessLogMapper accessLogMapper;
    @Autowired
    private SystemLogMapper systemLogMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private EmotionAnalyseService emotionAnalyseService;
    @Value("${image.localPathDir}")
    private String localPathDir;
    @Value("${image.localUrlPath}")
    private String localUrlPath;

    @Autowired

    public static boolean notEmpty(Object val) {
        if (val instanceof String) {
            return StringUtils.isNotBlank((String) val);
        }
        if (val instanceof Collection) {
            return !com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty((Collection) val);
        }
        if (val instanceof Map) {
            return !MapUtil.isEmpty((Map) val);
        }
        return val != null;
    }

    public static String getAccessToken() {
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY;
        Map map = JSONObject.parseObject(HttpUtil.get(url), Map.class);
        return map.get("access_token").toString();
    }

    public static void main(String[] args) {
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token=" + getAccessToken();
        HashMap<String, String> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", "下面是一份问卷调查的回答情况，请帮我按照回答内容给出适当的建议。(全面分析，列出至少五条建议，每条在30到40字左右)" + "问：您目前是哪个年级的学生？答：大一，问：您对学校的食堂环境与服务非常满意度如何？答：非常满意，问：您对学校的宿舍环境与管理非常满意度如何？答：非常满意，问：您对学校的图书馆服务非常满意度如何？答：非常满意，问：您对学校的体育设施和活动非常满意度如何？答：非常满意，问：您对学校的校园安全非常满意度如何？答：非常满意，问：您对学校的教学质量非常满意度如何？答：非常满意，问：您平时经常去学校的哪里？答：教室");
        ArrayList<HashMap> messagesList = new ArrayList<>();
        messagesList.add(msg);
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messagesList);
        String parame = JSON.toJSONString(requestBody);
        Map map = JSONObject.parseObject(HttpUtil.post(url, parame), Map.class);
        System.out.println(map);
    }

    /**
     * 查看问卷详情
     *
     * @param surveyId 问卷id
     * @return Survey
     */
    @Override
    public CommonResult<?> getSurveyDetail(Integer surveyId) {
        // 从redis中获取数据
        String key = "survey:" + surveyId;
        String surveyJson = redisTemplate.opsForValue().get(key);
        Survey survey = null;
        if (surveyJson != null) {
            System.out.println("从redis中获取数据");
            survey = JSON.parseObject(surveyJson, new TypeReference<Survey>() {
            });
        }
        if (surveyJson == null) {
            System.out.println("从数据库中获取数据");
            survey = surveyMapper.selectById(surveyId);
            LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
            //过滤deleteAt不为空的数据
            survey.setQuestions(questionMapper.selectList(wrapper.eq(Question::getSurveyID, surveyId).isNull(Question::getDeleteAt)));
            survey.getQuestions().forEach(question -> {
                //根据问题id查询选项，过滤掉deleteAt不为空的数据
                question.setOptions(optionBriefMapper.selectByQuestionId(question.getId()));
                question.getOptions().forEach(option -> {
                    option.setOptionContent(optionContentMapper.selectByOptionContentId(option.getOptionContentID()));
                });
            });
            String json = JSON.toJSONString(survey);
            redisTemplate.opsForValue().set(key, json);
        }
        return CommonResult.success(survey);
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
        String key = "surveyPage:" + currentPage + ":" + pageSize;
        String surveyPageJson = redisTemplate.opsForValue().get(key);
        Page<Survey> surveyPage = null;
        if (surveyPageJson != null) {
            System.out.println("从redis中获取数据");
            surveyPage = JSON.parseObject(surveyPageJson, new TypeReference<Page<Survey>>() {
            });
        }
        if (surveyPageJson == null) {
            System.out.println("从数据库中获取数据");
            Page<Survey> page = new Page<>(currentPage, pageSize);
            LambdaQueryWrapper<Survey> wrapper = Wrappers.<Survey>lambdaQuery()
                    .isNull(Survey::getDeleteAt);
            surveyPage = surveyMapper.selectPage(page, wrapper);
            //循环给每个问卷的submitCount赋值
            surveyPage.getRecords().forEach(survey -> {
                Integer submitCount = Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, survey.getId())));
                survey.setSubmitCount(submitCount);
            });
            String json = JSON.toJSONString(surveyPage);
            redisTemplate.opsForValue().set(key, json);
        }
        return CommonResult.success(surveyPage);
    }

    /**
     * 新建问卷
     *
     * @param survey 问卷
     * @return 200:新建成功 500:新建失败
     */
    @Override
    public CommonResult<?> createSurvey(CreateSurveyReq survey) {
        System.out.println(survey);
        Survey surveyToInsert = new Survey();
        surveyToInsert.setCreateTime(LocalDateTime.now());
        surveyToInsert.setName(survey.getName());
        surveyToInsert.setDescription(survey.getDescription());
        Date startTime = survey.getStartTime();
        Date endTime = survey.getEndTime();
        surveyToInsert.setStartTime(startTime.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime());
        surveyToInsert.setEndTime(endTime.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime());
        surveyToInsert.setType(survey.getType());
        surveyMapper.insert(surveyToInsert);
        log.info("surveyToInsert :{} ", surveyToInsert);
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        survey.getQuestions().forEach(question -> {
            Question questionToInsert = new Question();
            questionToInsert.setSurveyID(surveyToInsert.getId());
            questionToInsert.setContent(question.getContent());
            questionToInsert.setType(question.getType());
            questionMapper.insert(questionToInsert);
            if (!Objects.equals(question.getType(), "填空")) {
                question.getOptionList().forEach(option -> {
                    option.getOptionContentList().forEach(optionContent -> {
                        OptionContent optionContentToInsert = new OptionContent();
                        optionContentToInsert.setContent(optionContent.getContent());
                        optionContentMapper.insert(optionContentToInsert);
                        Option optionToInsert = new Option();
                        optionToInsert.setQuestionID(questionToInsert.getId());
                        optionToInsert.setOptionContentID(optionContentToInsert.getId());
                        optionBriefMapper.insert(optionToInsert);
                    });
                });
            }
        });
        SystemLog systemLog = new SystemLog();
        systemLog.setActionPeople("admin");
        systemLog.setActionDetail("admin新建了" + survey.getName() + "问卷。");
        systemLog.setActionTime(LocalDateTime.now());
        systemLog.setActionName("新建问卷");
        systemLogMapper.insert(systemLog);
        return CommonResult.success();
    }

    /**
     * 修改问卷信息
     *
     * @param survey 问卷详情
     * @return 200:修改成功 500:修改失败
     */
    @Override
    public CommonResult<?> updateSurveyDetail(Survey survey) {
        //清空key为survey:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("survey:" + survey.getId())));
        //清空key为surveyPge的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        int surveyToUpdate = surveyMapper.updateById(survey);
        if (surveyToUpdate == 0) {
            return CommonResult.failed("修改失败");
        } else {
            List<Question> questionsNeedToUpdate = survey.getQuestions();
            questionsNeedToUpdate.forEach(question -> {
                if (question.getId() == null) {
                    question.setSurveyID(survey.getId());
                    questionMapper.insert(question);
                    if (!Objects.equals(question.getType(), "填空")) {
                        question.getOptions().forEach(option -> {
                            optionContentMapper.insert(option.getOptionContent());
                            option.setOptionContentID(option.getOptionContent().getId());
                            option.setQuestionID(question.getId());
                            optionBriefMapper.insert(option);
                        });
                    }
                } else {
                    questionMapper.updateById(question);
                    if (!Objects.equals(question.getType(), "填空")) {
                        List<Option> optionsNeedToUpdate = question.getOptions();
                        List<Option> optionsInDB = optionBriefMapper.selectByQuestionId(question.getId());
                        optionsNeedToUpdate.forEach(option -> {
                            if (option.getId() == null) {
                                optionContentMapper.insert(option.getOptionContent());
                                option.setOptionContentID(option.getOptionContent().getId());
                                option.setQuestionID(question.getId());
                                optionBriefMapper.insert(option);
                            } else {
                                optionBriefMapper.updateById(option);
                            }
                        });
                        optionsInDB.forEach(option -> {
                            boolean flag = false;
                            for (Option option1 : optionsNeedToUpdate) {
                                if (option.getId().equals(option1.getId())) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                optionBriefMapper.deleteById(option.getId());
                            }
                        });
                    }
                }
            });
            SystemLog systemLog = new SystemLog();
            systemLog.setActionPeople("admin");
            systemLog.setActionDetail("admin修改了" + survey.getName() + "问卷。");
            systemLog.setActionTime(LocalDateTime.now());
            systemLog.setActionName("修改问卷");
            systemLogMapper.insert(systemLog);
            return CommonResult.success();
        }
    }

    /**
     * 查看问卷简要信息
     *
     * @param surveyId 问卷id
     * @return Survey 问卷简要信息
     */
    @Override
    public CommonResult<?> getSurveyBrief(Integer surveyId) {
        String key = "surveyBrief:" + surveyId;
        String surveyBriefJson = redisTemplate.opsForValue().get(key);
        Survey survey;
        if (surveyBriefJson != null) {
            System.out.println("从redis中获取数据");
            survey = JSON.parseObject(surveyBriefJson, new TypeReference<Survey>() {
            });
        } else {
            System.out.println("从数据库中获取数据");
            survey = surveyMapper.selectById(surveyId);
            String json = JSON.toJSONString(survey);
            redisTemplate.opsForValue().set(key, json);
        }
        return CommonResult.success(survey);
    }

    /**
     * 修改问卷的简要信息
     *
     * @param survey 问卷详情
     * @return 200:修改成功 500:修改失败
     */
    @Override
    public CommonResult<?> updateSurveyWill(Survey survey) {
        //清空key为survey:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("survey:" + survey.getId())));
        //清空key为surveyPge的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        //清空key为surveyBric ef:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyBrief:" + survey.getId())));
        int surveyToUpdate = surveyMapper.updateById(survey);
        if (surveyToUpdate == 0) {
            return CommonResult.failed("修改失败");
        } else {
            SystemLog systemLog = new SystemLog();
            systemLog.setActionPeople("admin");
            systemLog.setActionDetail("admin修改了" + survey.getName() + "问卷的简要信息。");
            systemLog.setActionTime(LocalDateTime.now());
            systemLog.setActionName("修改问卷简要信息");
            systemLogMapper.insert(systemLog);
            return CommonResult.success();
        }
    }

    /**
     * 发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:发布成功 500:发布失败
     */
    @Override
    public CommonResult<?> publishSurvey(Integer surveyId) {
        Survey survey = surveyMapper.selectById(surveyId);
        if (survey == null) {
            return CommonResult.failed("问卷不存在");
        }
        //清空key为survey:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("survey:" + survey.getId())));
        //清空key为surveyPge的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        //清空key为surveyBrief:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyBrief:" + survey.getId())));
        survey.setStatus(1);
        int surveyToUpdate = surveyMapper.updateById(survey);
        if (surveyToUpdate == 0) {
            return CommonResult.failed("发布失败");
        } else {
            return CommonResult.success();
        }
    }

    /**
     * 取消发布问卷
     *
     * @param surveyId 问卷id
     * @return 200:取消发布成功 500:取消发布失败
     */
    @Override
    public CommonResult<?> cancelPublishSurvey(Integer surveyId) {
        Survey survey = surveyMapper.selectById(surveyId);
        if (survey == null) {
            return CommonResult.failed("问卷不存在");
        }
        //清空key为survey:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("survey:" + survey.getId())));
        //清空key为surveyPge的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        //清空key为surveyBrief:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyBrief:" + survey.getId())));
        survey.setStatus(0);
        int surveyToUpdate = surveyMapper.updateById(survey);
        if (surveyToUpdate == 0) {
            return CommonResult.failed("取消发布失败");
        } else {
            return CommonResult.success();
        }
    }

    /**
     * 删除问卷中的问题
     *
     * @param questionId 问题id
     * @return 200:删除成功 500:删除失败
     */
    @Override
    public CommonResult<?> deleteQuestion(Integer questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return CommonResult.failed("问题不存在");
        }
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("survey:" + question.getSurveyID())));
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        question.setDeleteAt(LocalDateTime.now());
        questionMapper.updateById(question);
        return CommonResult.success();
    }

    /**
     * 删除问题中的选项
     *
     * @param questionId 问题id
     * @param optionId   选项id
     * @return 200:删除成功 500:删除失败
     */
    @Override
    public CommonResult<?> deleteOption(Integer questionId, Integer optionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return CommonResult.failed("问题不存在");
        }
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("survey:" + question.getSurveyID())));
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        optionBriefMapper.deleteById(optionId);

        return CommonResult.success();
    }

    /**
     * 删除问卷
     *
     * @param surveyId 问卷id
     * @return 200:删除成功 500:删除失败
     */
    @Override
    public CommonResult<?> deleteSurvey(Integer surveyId) {
        Survey survey = surveyMapper.selectById(surveyId);
        if (survey == null) {
            return CommonResult.failed("问卷不存在");
        }
        //清空key为survey:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("survey:" + survey.getId())));
        //清空key为surveyPge的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        //清空key为surveyBrief:surveyId的redis缓存
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyBrief:" + survey.getId())));
        survey.setDeleteAt(LocalDateTime.now());
        surveyMapper.updateById(survey);
        SystemLog systemLog = new SystemLog();
        systemLog.setActionPeople("admin");
        systemLog.setActionDetail("admin删除了" + survey.getName() + "问卷。");
        systemLog.setActionTime(LocalDateTime.now());
        systemLog.setActionName("删除问卷");
        systemLogMapper.insert(systemLog);
        return CommonResult.success();
    }

    /**
     * 获取热门问卷列表
     *
     * @return 热门问卷列表
     */
    @Override
    public CommonResult<?> getHotSurveyList() {
        return null;
    }

    /**
     * 多条件模糊查询问卷
     *
     * @param survey 问卷
     * @return 问卷列表
     */
    @Override
    public CommonResult<?> searchSurvey(Survey survey, Integer currentPage, Integer pageSize) {
        //多条件模糊查询，筛选掉deleteAt不为空的数据，筛选传入的参数不为空的数据
        Page<Survey> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Survey> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Survey::getName, survey.getName())
                .like(Survey::getDescription, survey.getDescription())
                .eq(Survey::getType, survey.getType())
                .eq(Survey::getStatus, survey.getStatus())
                .isNull(Survey::getDeleteAt);
        Page<Survey> surveyPage = surveyMapper.selectPage(page, wrapper);
        return CommonResult.success(surveyPage);
    }

    /**
     * 对用户个性化推荐问卷
     *
     * @param userId 用户id
     * @return 个性化推荐问卷列表
     */

    @Override
    public CommonResult<?> getRecommendSurveyList(Integer userId) {

        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getUserID, userId);
        List<UserBehavior> userBehaviors = userBehaviorMapper.selectList(wrapper);
        /*基于机器学习的个性化推荐，自动计算用户对问卷的rating，rating只是一个衡量标准，应该由用户的行为决定
         * 基于用户行为计算评分
         */
        Map<Integer, Double> surveyToRating = new HashMap<>();
        for (UserBehavior behavior : userBehaviors) {
            int surveyId = behavior.getSurveyID();
            String action = behavior.getAction();
            double rating = surveyToRating.getOrDefault(surveyId, 0.0);
            // 根据行为类型分配评分
            switch (action) {
//                case "click":
//                    rating += 1.0;
//                    break;
                case "complete":
                    rating += 5.0;
                    break;
                case "browse":
                    rating += 0.5;
                    break;
//                case "collect":
//                    rating += 2.0;
//                    break;
//                case "share":
//                    rating += 3.0;
//                    break;
                case "comment":
                    rating += 4.0;
                    break;
                default:
                    break;
            }
            surveyToRating.put(surveyId, rating);
        }
        // 按照用户id和问卷id生成评分数据
        List<GenericPreference> preferences = surveyToRating.entrySet().stream()
                .map(e -> new GenericPreference(userId, e.getKey(), e.getValue().floatValue())) // convert double to float
                .collect(Collectors.toList());
        // 将数据按照用户id分组
        Map<Long, List<GenericPreference>> userToPreferences = preferences.stream()
                .collect(Collectors.groupingBy(
                        GenericPreference::getUserID,
                        Collectors.mapping(Function.identity(), Collectors.toList())
                ));
        // 将数据转换为FastByIDMap
        FastByIDMap<PreferenceArray> fastMap = new FastByIDMap<>();
        for (Map.Entry<Long, List<GenericPreference>> entry : userToPreferences.entrySet()) {
            fastMap.put(entry.getKey(), new GenericUserPreferenceArray(entry.getValue()));
        }

        // 将数据转换为DataModel
        DataModel model = new GenericDataModel(fastMap);

        // 使用SVD++算法，10个迭代次数，5个因子，得到推荐器
        SVDPlusPlusFactorizer factorizer = null;
        try {
            factorizer = new SVDPlusPlusFactorizer(model, 10, 5);
        } catch (TasteException e) {
            throw new RuntimeException(e);
        }
        SVDRecommender recommender = null;
        try {
            recommender = new SVDRecommender(model, factorizer);
        } catch (TasteException e) {
            throw new RuntimeException(e);
        }

        // 推荐5个问卷给用户
        List<RecommendedItem> recommendations = null;
        try {
            recommendations = recommender.recommend(userId, 5);
        } catch (TasteException e) {
            throw new RuntimeException(e);
        }
        return CommonResult.success(recommendations);
    }

    /**
     * 下载问卷
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:下载成功 500:下载失败
     */
    @Override
    public CommonResult<?> downloadSurvey(String username, Integer surveyId) {
        String key = username + "_survey:" + surveyId;
        String surveyJson = redisTemplate.opsForValue().get(key);
        Survey survey = null;
        if (surveyJson != null) {
            System.out.println("从redis中获取数据");
            survey = JSON.parseObject(surveyJson, new TypeReference<Survey>() {
            });
        }
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(survey.getName());
        String[] titles = {"名称", "类型", "描述", "开始时间", "结束时间", "状态"};
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(titles[i]);
        }
        //创建第二行
        HSSFRow row2 = sheet.createRow(1);
        //创建第二行的列
        HSSFCell cell0 = row2.createCell(0);
        cell0.setCellValue(survey.getName());
        HSSFCell cell1 = row2.createCell(1);
        cell1.setCellValue(survey.getType());
        HSSFCell cell2 = row2.createCell(2);
        cell2.setCellValue(survey.getDescription());
        HSSFCell cell3 = row2.createCell(3);
        cell3.setCellValue(survey.getStartTime().toString());
        HSSFCell cell4 = row2.createCell(4);
        cell4.setCellValue(survey.getEndTime().toString());
        HSSFCell cell5 = row2.createCell(5);
        cell5.setCellValue(survey.getStatus() == 0 ? "未发布" : "已发布");
        //创建第三行
        HSSFRow row3 = sheet.createRow(2);
        //创建第三行的列
        HSSFCell cell6 = row3.createCell(0);
        cell6.setCellValue("题目");
        HSSFCell cell7 = row3.createCell(1);
        cell7.setCellValue("类型");
        HSSFCell cell8 = row3.createCell(2);
        cell8.setCellValue("选项");
        //创建第四行
        int index = 3;
        List<Question> questions = survey.getQuestions();
        for (Question question : questions) {
            HSSFRow row5 = sheet.createRow(index);
            HSSFCell cell11 = row5.createCell(0);
            cell11.setCellValue(question.getContent());
            HSSFCell cell12 = row5.createCell(1);
            cell12.setCellValue(question.getType());
            HSSFCell cell13 = row5.createCell(2);
            //获取question的所有option
            List<Option> options = question.getOptions();
            String optionContent = "";
            for (Option option : options) {
                optionContent += option.getOptionContent().getContent() + ",";
            }
            cell13.setCellValue(optionContent);
            index++;
        }
        //设置列宽
        sheet.setColumnWidth(0, 40 * 256);
        sheet.setColumnWidth(1, 40 * 256);
        sheet.setColumnWidth(2, 40 * 256);
        sheet.setColumnWidth(3, 40 * 256);
        sheet.setColumnWidth(4, 40 * 256);
        sheet.setColumnWidth(5, 40 * 256);
        //设置行高
        row.setHeightInPoints(30);
        row2.setHeightInPoints(30);
        row3.setHeightInPoints(30);

//        try {
//            // Set the content type and attachment header.
//            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + survey.getName() + ".xls");
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//            // Write the workbook to the response body.
//            workbook.write(response.getOutputStream());
//            response.flushBuffer();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        SystemLog systemLog = new SystemLog();
        systemLog.setActionPeople(username);
        //获取当前时间，类型LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.now();
        systemLog.setActionTime(localDateTime);
        systemLog.setActionName("下载问卷");
        systemLog.setActionDetail(username + "下载了" + survey.getName() + "问卷。");
        systemLogMapper.insert(systemLog);
        return CommonResult.success();
    }

    /**
     * 根据所有填写过当前的问卷的用户的答案，生成问卷的统计信息
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    @Override
    public CommonResult<?> surveySubmittedStatistics(Integer surveyId) {
        Survey survey = surveyMapper.selectById(surveyId);
        List<SubmitSurveyRecord> submitSurveyRecords = submitSurveyRecordMapper.selectList(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, surveyId));
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getSurveyID, surveyId));
        questions.forEach(question -> {
            List<Option> options = optionBriefMapper.selectList(new LambdaQueryWrapper<Option>().eq(Option::getQuestionID, question.getId()));
            options.forEach(option -> {
                option.setOptionContent(optionContentMapper.selectByOptionContentId(option.getOptionContentID()));
            });
            question.setOptions(options);
        });
        survey.setQuestions(questions);
        Map<String, Map<String, Integer>> questionToOptionCount = new HashMap<>();
        for (Question question : questions) {
            Map<String, Integer> optionToCount = new HashMap<>();
            for (Option option : question.getOptions()) {
                optionToCount.put(option.getOptionContent().getContent(), 0);
            }
            questionToOptionCount.put(String.valueOf(question.getId()), optionToCount);
        }
        for (SubmitSurveyRecord submitSurveyRecord : submitSurveyRecords) {
            List<Answer> answers = answerMapper.selectList(new LambdaQueryWrapper<Answer>().eq(Answer::getSurveyID, surveyId).eq(Answer::getUserID, submitSurveyRecord.getUserID()));
            for (Answer answer : answers) {
                Question question = questionMapper.selectById(answer.getQuestionID());
                if (question.getType().equals("填空")) {
                    continue;
                }
                if (question.getType().equals("多选")) {
                    // 处理  ["教室","食堂","体育馆","宿舍","操场","图书馆"]，将其转换为教室,食堂,体育馆,宿舍,操场,图书馆
                    String content = answer.getContent().substring(1, answer.getContent().length() - 1);
                    //如果有双引号，去掉双引号
                    if (content.contains("\"")) {
                        content = content.replaceAll("\"", "");
                    }
                    String[] contents = content.split(",");
                    for (String s : contents) {
                        Map<String, Integer> optionToCount = questionToOptionCount.get(question.getContent());
                        optionToCount.put(s, optionToCount.get(s) + 1);
                        questionToOptionCount.put(String.valueOf(question.getId()), optionToCount);
                    }
                    continue;
                }
                Map<String, Integer> optionToCount = questionToOptionCount.get(question.getContent());
                optionToCount.put(answer.getContent(), optionToCount.get(answer.getContent()) + 1);
                questionToOptionCount.put(String.valueOf(question.getId()), optionToCount);
            }
        }
        Integer submitCount = Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, surveyId)));
        OptionStatisticsPercentCount optionStatisticsPercentCount = new OptionStatisticsPercentCount(submitCount, questionToOptionCount);
        return CommonResult.success(optionStatisticsPercentCount);
    }

    /**
     * 针对每个问题的每个选项，统计选择该选项的人数
     *
     * @param surveyId 问卷id
     * @return 200:生成成功 500:生成失败
     */
    @Override
    public CommonResult<?> surveySubmittedStatisticsByOption(Integer surveyId) {
        // 获取问卷、问题和选项
        List<Answer> answers = answerMapper.selectList(new LambdaQueryWrapper<Answer>().eq(Answer::getSurveyID, surveyId));
        Map<String, Map<String, Integer>> questionToOptionCount = new HashMap<>();
        // 遍历答案，统计每个问题的每个选项的人数，存入questionToOptionCount，key为问题的content，value为选项的content和人数
        for (Answer answer : answers) {
            Question question = questionMapper.selectById(answer.getQuestionID());
            Map<String, Integer> optionToCount = questionToOptionCount.getOrDefault(String.valueOf(question.getId()), new HashMap<>());
            // 如果是填空题，直接跳过
            if (question.getType().equals("填空")) {
                continue;
            }
            if (question.getType().equals("多选")) {
                // 处理  ["教室","食堂","体育馆","宿舍","操场","图书馆"]，将其转换为教室,食堂,体育馆,宿舍,操场,图书馆
                String content = answer.getContent().substring(1, answer.getContent().length() - 1);
                //如果有双引号，去掉双引号
                if (content.contains("\"")) {
                    content = content.replaceAll("\"", "");
                }
                String[] contents = content.split(",");
                for (String s : contents) {
                    optionToCount.put(s, optionToCount.getOrDefault(s, 0) + 1);
                }
                questionToOptionCount.put(String.valueOf(question.getId()), optionToCount);
                continue;
            }
            optionToCount.put(answer.getContent(), optionToCount.getOrDefault(answer.getContent(), 0) + 1);
            questionToOptionCount.put(String.valueOf(question.getId()), optionToCount);
        }
        Integer submitCount = Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, surveyId)));
        OptionStatisticsPercentCount optionStatisticsPercentCount = new OptionStatisticsPercentCount(submitCount, questionToOptionCount);
        return CommonResult.success(optionStatisticsPercentCount);
    }

    /**
     * 获取所有的提交问卷记录
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getAllSubmittedRecords(Integer currentPage, Integer pageSize) {
        String key = "submittedRecordsPage:" + currentPage + ":" + pageSize;
        String submittedRecordsJson = redisTemplate.opsForValue().get(key);
        Page<SubmitSurveyRecord> submittedRecordsPage = null;
        if (submittedRecordsJson != null) {
            System.out.println("从redis中获取数据");
            submittedRecordsPage = JSON.parseObject(submittedRecordsJson, new TypeReference<Page<SubmitSurveyRecord>>() {
            });
        }
        if (submittedRecordsJson == null) {
            System.out.println("从数据库中获取数据");
            Page<SubmitSurveyRecord> page = new Page<>(currentPage, pageSize);
            submittedRecordsPage = submitSurveyRecordMapper.selectPage(page, null);
            submittedRecordsPage.getRecords().sort((o1, o2) -> {
                if (o1.getSubmitTime().isBefore(o2.getSubmitTime())) {
                    return 1;
                } else if (o1.getSubmitTime().isAfter(o2.getSubmitTime())) {
                    return -1;
                } else {
                    return 0;
                }
            });
            String json = JSON.toJSONString(submittedRecordsPage);
            redisTemplate.opsForValue().set(key, json);
        }
        return CommonResult.success(submittedRecordsPage);
    }

    /**
     * 查询一条提交记录的详情
     *
     * @param submittedId 提交记录id
     * @param username    用户名
     * @return 200:获取成功 500:获取失败
     */
    //TODO check
    @Override
    public CommonResult<?> getSubmittedRecordDetail(Integer submittedId, String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        String key = "submittedRecordDetail:" + user.getId() + ":" + submittedId;
        String submittedRecordDetailJson = redisTemplate.opsForValue().get(key);
        SubmittedDetail submittedDetail = null;
        if (submittedRecordDetailJson != null) {
            System.out.println("从redis中获取数据");
            submittedDetail = JSON.parseObject(submittedRecordDetailJson, new TypeReference<SubmittedDetail>() {
            });
        } else {
            return CommonResult.failed("获取失败");
        }
        return CommonResult.success(submittedDetail);
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
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Page<SubmitSurveyRecord> page = new Page<>(currentPage, pageSize);
        Page<SubmitSurveyRecord> submitSurveyRecordPage = submitSurveyRecordMapper.selectPage(page, new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getUserID, user.getId()));
        // 将提交记录中的问卷id转换为问卷
        List<SubmittedBrief> submittedBriefs = new ArrayList<>();
        List<Survey> surveys = new ArrayList<>();
        submitSurveyRecordPage.getRecords().forEach(submitSurveyRecord -> {
            Survey survey = surveyMapper.selectById(submitSurveyRecord.getSurveyID());
            surveys.add(survey);
        });
        submitSurveyRecordPage.getRecords().forEach(submitSurveyRecord -> {
            SubmittedBrief submittedBrief = new SubmittedBrief();
            submittedBrief.setSubmitSurveyRecord(submitSurveyRecord);
            submittedBrief.setSurvey(surveys.get(submitSurveyRecordPage.getRecords().indexOf(submitSurveyRecord)));
            submittedBriefs.add(submittedBrief);
        });
        Page<SubmittedBrief> submittedBriefPage = new Page<>();
        submittedBriefPage.setRecords(submittedBriefs);
        submittedBriefPage.setTotal(submitSurveyRecordPage.getTotal());
        submittedBriefPage.setSize(submitSurveyRecordPage.getSize());
        submittedBriefPage.setCurrent(submitSurveyRecordPage.getCurrent());
        submittedBriefPage.setPages(submitSurveyRecordPage.getPages());
        return CommonResult.success(submittedBriefPage);
    }

    /**
     * 根据问卷id查询该问卷的提交总数
     *
     * @param surveyId 问卷id
     * @return 当前问卷的提交总数
     */
    @Override
    public CommonResult<?> getSubmittedCount(Integer surveyId) {
        Integer submittedCount = Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, surveyId)));
        return CommonResult.success(submittedCount);
    }

    /**
     * 用户填写提交问卷
     *
     * @param req 提交问卷详情
     * @return 200:提交成功 500:提交失败
     */
    //TODO CHECK
    @Override
    public CommonResult<?> submitSurvey(SubmitDetailReq req) throws JSONException {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("submittedCountByDay:" + LocalDateTime.now().minusDays(1))));
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        LambdaQueryWrapper<SubmitSurveyRecord> submitSurveyRecordExist = Wrappers.<SubmitSurveyRecord>lambdaQuery()
                .eq(SubmitSurveyRecord::getSurveyID, req.getSurveyId())
                .eq(SubmitSurveyRecord::getUserID, user.getId());
        if (submitSurveyRecordMapper.selectOne(submitSurveyRecordExist) != null) {
            return CommonResult.failed("您已经提交过该问卷!请勿重复提交！");
        }
        List<AnswerWithQuestionAndOptionContent> answerWithQuestionAndOptionContents = req.getAnswerWithQuestionAndOptionContents();
        String surveyKey = req.getPublisher() + "_survey:" + req.getSurveyId();
        String surveyJson = redisTemplate.opsForValue().get(surveyKey);
        Survey survey = JSON.parseObject(surveyJson, Survey.class);
        List<Question> questions = new ArrayList<>();
        answerWithQuestionAndOptionContents.forEach(answerWithQuestionAndOptionContent -> {
            Question question = survey.getQuestions().stream()
                    .filter(q -> q.getContent().equals(answerWithQuestionAndOptionContent.getQuestionContent()))
                    .findFirst()
                    .orElse(null);
            questions.add(question);
            Answer answer = new Answer();
            answer.setUserID(user.getId());
            answer.setSurveyID(req.getSurveyId());
            answer.setQuestionID(question.getId());
            if (question.getType().equals("填空")) {
                answer.setContent(answerWithQuestionAndOptionContent.getOptionContentWithAnswers().get(0).getOptionContent());
            } else if (question.getType().equals("多选")) {
                String content = "[";
                for (OptionContentWithAnswer optionContentWithAnswer : answerWithQuestionAndOptionContent.getOptionContentWithAnswers()) {
                    if (optionContentWithAnswer.getIsAnswer()) {
                        content += optionContentWithAnswer.getOptionContent() + ",";
                    }
                }
                // 移除最后一个逗号，并添加结束的方括号
                if (content.length() > 1) {
                    content = content.substring(0, content.length() - 1);
                }
                content += "]";
                answer.setContent(content);
            } else {
                for (OptionContentWithAnswer optionContentWithAnswer : answerWithQuestionAndOptionContent.getOptionContentWithAnswers()) {
                    if (optionContentWithAnswer.getIsAnswer()) {
                        answer.setContent(optionContentWithAnswer.getOptionContent());
                        break;
                    }
                }
            }
            answerMapper.insert(answer);
        });
        //TODO CHECK
        SubmitSurveyRecord submitSurveyRecord = new SubmitSurveyRecord();
        submitSurveyRecord.setSurveyID(req.getSurveyId());
        submitSurveyRecord.setUserID(user.getId());
        submitSurveyRecord.setSubmitTime(LocalDateTime.now());
        List<QuestionWithAnswerDTO> questionWithAnswerDTOS = new ArrayList<>();
        for (Question question : questions) {
            QuestionWithAnswerDTO questionWithAnswerDTO = new QuestionWithAnswerDTO();
            questionWithAnswerDTO.setQuestion(question.getContent());
            List<Answer> answers = answerMapper.selectList(new LambdaQueryWrapper<>(Answer.class)
                    .eq(Answer::getQuestionID, question.getId())
                    .eq(Answer::getUserID, submitSurveyRecord.getUserID()));
            if (!answers.isEmpty()) {
                questionWithAnswerDTO.setAnswer(answers.get(0).getContent());
            }
            questionWithAnswerDTOS.add(questionWithAnswerDTO);
        }
        //遍历问题和答案，进行情感分析，给问题加上问：，给答案加上答：
        StringBuilder content = new StringBuilder();
        for (QuestionWithAnswerDTO questionWithAnswerDTO : questionWithAnswerDTOS) {
            content.append("问：").append(questionWithAnswerDTO.getQuestion()).append("。");
            content.append("答：").append(questionWithAnswerDTO.getAnswer()).append("。");
        }
        this.contentText = content.toString();
        EmotionAnalyseResultResp emotion = emotionAnalyseService.analyseEmotion(content.toString());
        submitSurveyRecord.setPublisher(req.getPublisher());
        submitSurveyRecord.setPositiveProb(emotion.getPositiveProb());
        submitSurveyRecord.setNegativeProb(emotion.getNegativeProb());
        submitSurveyRecord.setSentiment(emotion.getSentiment());
        submitSurveyRecordMapper.insert(submitSurveyRecord);
        Comment comment = new Comment();
        comment.setSurveyID(req.getSurveyId());
        comment.setUserID(user.getId());
        comment.setContent(req.getComment().getContent());
        comment.setScore(req.getComment().getScore());
        comment.setSubmitTime(LocalDateTime.now());
        commentMapper.insert(comment);
        String key = "submittedCountByDay:" + LocalDateTime.now();
        String submittedCountByDayJson = redisTemplate.opsForValue().get(key);
        if (submittedCountByDayJson != null) {
            Integer submittedCountByDay = Integer.parseInt(submittedCountByDayJson);
            redisTemplate.opsForValue().set(key, String.valueOf(submittedCountByDay + 1));
        } else {
            redisTemplate.opsForValue().set(key, "1");
        }
        String submittedRecordDetailKey = "submittedRecordDetail:" + user.getId() + ":" + submitSurveyRecord.getId();
        SubmittedDetail submittedDetail = new SubmittedDetail();
        submittedDetail.setSurvey(survey);
        user.setCollege(CollegeEnum.getCollegeName(user.getCollege()));
        user.setMajor(MajorEnum.getName(user.getMajor()));
        user.setGrade(user.getGrade() + "级");
        user.setClasses(user.getClasses() + "班");
        submittedDetail.setUser(user);
        submittedDetail.setSubmitTime(String.valueOf(submitSurveyRecord.getSubmitTime()));
        submittedDetail.setPositiveProb(submitSurveyRecord.getPositiveProb());
        submittedDetail.setNegativeProb(submitSurveyRecord.getNegativeProb());
        submittedDetail.setSentiment(submitSurveyRecord.getSentiment());
        submittedDetail.setAnswerWithQuestionAndOptionContents(answerWithQuestionAndOptionContents);
        UserBehavior userBehavior = new UserBehavior();
        userBehavior.setUserID(user.getId());
        userBehavior.setSurveyID(req.getSurveyId());
        userBehavior.setAction("complete");
        userBehavior.setActionTime(LocalDateTime.now());
        userBehaviorMapper.insert(userBehavior);
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(submittedRecordDetailKey)));
        redisTemplate.opsForValue().set(submittedRecordDetailKey, JSON.toJSONString(submittedDetail));
        return CommonResult.success();
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
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return CommonResult.success(commentMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getSurveyID, surveyId).eq(Comment::getUserID, user.getId())));
    }

    /**
     * 根据问卷id获取该问卷的所有评论
     *
     * @param surveyId    问卷id
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 当前问卷的评论列表
     */
    @Override
    public CommonResult<?> getComments(Integer surveyId, Integer currentPage, Integer pageSize) {
        Page<Comment> page = new Page<>(currentPage, pageSize);
        Page<Comment> commentPage = commentMapper.selectPage(page, new LambdaQueryWrapper<Comment>().eq(Comment::getSurveyID, surveyId));
        Page<UserInfoWithComment> usernameWithComments = new Page<>();
        List<UserInfoWithComment> userInfoWithCommentList = new ArrayList<>();
        commentPage.getRecords().forEach(comment -> {
            User user = userMapper.selectById(comment.getUserID());
            UserInfoWithComment userInfoWithComment = new UserInfoWithComment();
            userInfoWithComment.setUsername(user.getName());
            userInfoWithComment.setComment(comment);
            userInfoWithComment.setAvatar(user.getPhoto());
            userInfoWithCommentList.add(userInfoWithComment);
        });
        usernameWithComments.setRecords(userInfoWithCommentList);
        usernameWithComments.setTotal(commentPage.getTotal());
        usernameWithComments.setSize(commentPage.getSize());
        usernameWithComments.setCurrent(commentPage.getCurrent());
        usernameWithComments.setPages(commentPage.getPages());
        return CommonResult.success(usernameWithComments);
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
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("Survey:" + surveyId + ":Comment*")));
        Comment commentToInsert = new Comment();
        commentToInsert.setSurveyID(surveyId);
        commentToInsert.setUserID(user.getId());
        commentToInsert.setContent(comment.getContent());
        commentToInsert.setScore(comment.getScore());
        commentToInsert.setSubmitTime(LocalDateTime.now());
        //如果用户已经评论过该问卷，更新评论
        Comment alreadyExistComment = commentMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getUserID, user.getId()).eq(Comment::getSurveyID, surveyId));
        if (alreadyExistComment != null) {
            commentMapper.update(commentToInsert, new LambdaQueryWrapper<Comment>().eq(Comment::getUserID, user.getId()).eq(Comment::getSurveyID, surveyId));
        } else {
            commentMapper.insert(commentToInsert);
        }
        UserBehavior userBehavior = new UserBehavior();
        userBehavior.setUserID(user.getId());
        userBehavior.setSurveyID(surveyId);
        userBehavior.setAction("comment");
        userBehavior.setActionTime(LocalDateTime.now());
        return CommonResult.success();
    }

    /**
     * 获取所有问卷的类型（去重做类型选择器）
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyTypes() {
        List<String> surveyTypes = surveyMapper.getSurveyTypes();
        return CommonResult.success(surveyTypes);
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
    public CommonResult<?> getSurveysByType(String surveyType, Integer currentPage, Integer pageSize) {
        Page<Survey> page = new Page<>(currentPage, pageSize);
        Page<Survey> surveyPage = surveyMapper.selectPage(page, new LambdaQueryWrapper<Survey>().eq(Survey::getType, surveyType).eq(Survey::getStatus, 1).isNull(Survey::getDeleteAt));
        return CommonResult.success(surveyPage);
    }

    /**
     * 统计一天内的问卷提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedCountByDay() {
        List<SubmitSurveyRecord> submitSurveyRecords = submitSurveyRecordMapper.selectList(new LambdaQueryWrapper<SubmitSurveyRecord>().ge(SubmitSurveyRecord::getSubmitTime, LocalDateTime.now().minusDays(1)));
        String key = "submittedCountByDay:" + LocalDateTime.now().minusDays(1);
        String submittedCountByDayJson = redisTemplate.opsForValue().get(key);
        Map<Integer, Integer> submittedCountByDay = null;
        if (submittedCountByDayJson != null) {
            System.out.println("从redis中获取数据");
            submittedCountByDay = JSON.parseObject(submittedCountByDayJson, new TypeReference<Map<Integer, Integer>>() {
            });
        }
        if (submittedCountByDayJson == null) {
            System.out.println("从数据库中获取数据");
            submittedCountByDay = new HashMap<>();
            for (SubmitSurveyRecord submitSurveyRecord : submitSurveyRecords) {
                Integer surveyId = submitSurveyRecord.getSurveyID();
                Integer count = submittedCountByDay.getOrDefault(surveyId, 0);
                submittedCountByDay.put(surveyId, count + 1);
            }
            String json = JSON.toJSONString(submittedCountByDay);
            redisTemplate.opsForValue().set(key, json);
        }
        return CommonResult.success(submittedCountByDay);
    }

    /**
     * 根据问卷id统计该问卷问题的类型
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getQuestionType(Integer surveyId) {
        Map<String, Integer> questionTypeMap = new HashMap<>();
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getSurveyID, surveyId));
        for (Question question : questions) {
            String type = question.getType();
            Integer count = questionTypeMap.getOrDefault(type, 0);
            questionTypeMap.put(type, count + 1);
        }
        return CommonResult.success(questionTypeMap);
    }

    /**
     * 按照问卷的提交量，按照时间段统计每天的问卷提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedCountByTime() {
        Map<String, Integer> submittedCountByTimeMap = new HashMap<>();
        submittedCountByTimeMap.put("00:00-04:00", 0);
        submittedCountByTimeMap.put("04:00-08:00", 0);
        submittedCountByTimeMap.put("08:00-12:00", 0);
        submittedCountByTimeMap.put("12:00-16:00", 0);
        submittedCountByTimeMap.put("16:00-20:00", 0);
        submittedCountByTimeMap.put("20:00-00:00", 0);
        List<SubmitSurveyRecord> submitSurveyRecords = submitSurveyRecordMapper.selectList(new LambdaQueryWrapper<SubmitSurveyRecord>().ge(SubmitSurveyRecord::getSubmitTime, LocalDateTime.now().minusDays(1)));

        submitSurveyRecords.stream()
                .map(SubmitSurveyRecord::getSubmitTime)
                .map(LocalDateTime::toLocalTime)
                .forEach(time -> {
                    String key = submittedCountByTimeMap.keySet().stream()
                            .filter(k -> isTimeInInterval(time, k))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Invalid time interval"));
                    submittedCountByTimeMap.put(key, submittedCountByTimeMap.get(key) + 1);
                });

        return CommonResult.success(submittedCountByTimeMap);
    }

    private boolean isTimeInInterval(LocalTime time, String interval) {
        String[] parts = interval.split("-");
        LocalTime start = LocalTime.parse(parts[0]);
        LocalTime end = LocalTime.parse(parts[1]);
        if (end.equals(LocalTime.MIDNIGHT)) {
            return !time.isBefore(start);
        } else {
            return !time.isBefore(start) && time.isBefore(end);
        }
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
        String key = "surveyListBySubmittedCount:" + currentPage + ":" + pageSize;
        String surveyListBySubmittedCountJson = redisTemplate.opsForValue().get(key);
        Page<Survey> surveyPage = null;
        if (surveyListBySubmittedCountJson != null) {
            System.out.println("从redis中获取数据");
            surveyPage = JSON.parseObject(surveyListBySubmittedCountJson, new TypeReference<Page<Survey>>() {
            });
        } else {
            System.out.println("从数据库中获取数据");
            Page<Survey> page = new Page<>(currentPage, pageSize);
            surveyPage = surveyMapper.selectPage(page, new LambdaQueryWrapper<Survey>().eq(Survey::getStatus, 1).isNull(Survey::getDeleteAt));
            surveyPage.getRecords().forEach(survey -> {
                Integer count = Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, survey.getId())));
                survey.setSubmitCount(count);
            });
            surveyPage.getRecords().sort(Comparator.comparing(Survey::getSubmitCount));
            String json = JSON.toJSONString(surveyPage);
            redisTemplate.opsForValue().set(key, json);
        }
        return CommonResult.success(surveyPage);
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
        String key = "surveyListBySubmittedCountDesc:" + currentPage + ":" + pageSize;
        String surveyListBySubmittedCountDescJson = redisTemplate.opsForValue().get(key);
        Page<Survey> surveyPage = null;
        if (surveyListBySubmittedCountDescJson != null) {
            System.out.println("从redis中获取数据");
            surveyPage = JSON.parseObject(surveyListBySubmittedCountDescJson, new TypeReference<Page<Survey>>() {
            });
        }
        if (surveyListBySubmittedCountDescJson == null) {
            System.out.println("从数据库中获取数据");
            Page<Survey> page = new Page<>(currentPage, pageSize);
            surveyPage = surveyMapper.selectPage(page, new LambdaQueryWrapper<Survey>().eq(Survey::getStatus, 1).isNull(Survey::getDeleteAt));
            surveyPage.getRecords().forEach(survey -> {
                Integer count = Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, survey.getId())));
                survey.setSubmitCount(count);
            });
            surveyPage.getRecords().sort(Comparator.comparing(Survey::getSubmitCount).reversed());
            String json = JSON.toJSONString(surveyPage);
            redisTemplate.opsForValue().set(key, json);
        }
        System.out.println(surveyPage);
        System.out.println(surveyPage.getRecords());
        return CommonResult.success(surveyPage);
    }

    /**
     * 根据问卷提交量按照星期统计所有问卷的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getActiveSurveyCountByWeek() {
        Map<String, Integer> activeSurveyCountByWeek = new TreeMap<>();
        activeSurveyCountByWeek.put("星期一", 0);
        activeSurveyCountByWeek.put("星期二", 0);
        activeSurveyCountByWeek.put("星期三", 0);
        activeSurveyCountByWeek.put("星期四", 0);
        activeSurveyCountByWeek.put("星期五", 0);
        activeSurveyCountByWeek.put("星期六", 0);
        activeSurveyCountByWeek.put("星期日", 0);
        List<SubmitSurveyRecord> submitSurveyRecords = submitSurveyRecordMapper.selectList(null);
        submitSurveyRecords.stream()
                .map(SubmitSurveyRecord::getSubmitTime)
                .map(LocalDateTime::getDayOfWeek)
                .forEach(dayOfWeek -> {
                    String key;
                    switch (dayOfWeek) {
                        case MONDAY:
                            key = "星期一";
                            break;
                        case TUESDAY:
                            key = "星期二";
                            break;
                        case WEDNESDAY:
                            key = "星期三";
                            break;
                        case THURSDAY:
                            key = "星期四";
                            break;
                        case FRIDAY:
                            key = "星期五";
                            break;
                        case SATURDAY:
                            key = "星期六";
                            break;
                        case SUNDAY:
                            key = "星期日";
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + dayOfWeek);
                    }
                    activeSurveyCountByWeek.put(key, activeSurveyCountByWeek.get(key) + 1);
                });
        return CommonResult.success(activeSurveyCountByWeek);
    }

    /**
     * 为指定条件的学生发布问卷
     *
     * @param publishConditionListReq 发布条件
     * @return 200:发布成功 500:发布失败
     */
    @Override
    public CommonResult<?> publishSurveyForCondition(PublishConditionListReq publishConditionListReq) {
        System.out.println(publishConditionListReq);
        log.info("发布问卷:{}", publishConditionListReq);
        log.debug("发布问卷:{}", publishConditionListReq);
        String publisherKey = "publisher_" + publishConditionListReq.getPublisher();
        String publishRecord = redisTemplate.opsForValue().get(publisherKey);
        Map<Integer, PublishInfoDTO> publishInfoDTOMap;
        if (publishRecord == null) {
            publishInfoDTOMap = new HashMap<>();
        } else {
            publishInfoDTOMap = JSON.parseObject(publishRecord, new TypeReference<Map<Integer, PublishInfoDTO>>() {
            });
        }
        PublishInfoDTO publishInfoDTO = publishInfoDTOMap.getOrDefault(publishConditionListReq.getSurveyId(), new PublishInfoDTO());
        publishInfoDTO.setPublisher(publishConditionListReq.getPublisher());
        List<PublishConditionReq> receiverList = publishInfoDTO.getReceiver() != null ? publishInfoDTO.getReceiver() : new ArrayList<>();
        for (PublishConditionReq publishConditionReq : publishConditionListReq.getPublishConditionReq()) {
            PublishConditionReq newPublishConditionReq = new PublishConditionReq();
            if (publishConditionReq.getCollege() == null) {
                newPublishConditionReq.setCollege(null);
            }
            if (publishConditionReq.getMajor() == null) {
                newPublishConditionReq.setMajor(null);
            }
            if (publishConditionReq.getGrade() == null) {
                newPublishConditionReq.setGrade(null);
            }
            if (publishConditionReq.getClasses() == null) {
                newPublishConditionReq.setClasses(null);
            }
            if (!Objects.equals(publishConditionReq.getCollege(), null)) {
                newPublishConditionReq.setCollege(publishConditionReq.getCollege());
            }
            if (!Objects.equals(publishConditionReq.getMajor(), null)) {
                newPublishConditionReq.setMajor(publishConditionReq.getMajor());
            }
            if (!Objects.equals(publishConditionReq.getGrade(), null)) {
                newPublishConditionReq.setGrade(publishConditionReq.getGrade());
            }
            if (!Objects.equals(publishConditionReq.getClasses(), null)) {
                newPublishConditionReq.setClasses(publishConditionReq.getClasses());
            }
            for (PublishConditionReq existingCondition : receiverList) {
                if (existingCondition.getCollege().equals(publishConditionReq.getCollege()) &&
                        (existingCondition.getMajor() == null || existingCondition.getMajor().equals(publishConditionReq.getMajor())) &&
                        (existingCondition.getGrade() == null || existingCondition.getGrade().equals(publishConditionReq.getGrade())) &&
                        (existingCondition.getClasses() == null || existingCondition.getClasses().equals(publishConditionReq.getClasses()))
                ) {
                    if (existingCondition.getPublishStatus() == 1) {
                        return CommonResult.failed("该发布条件已存在并且已发布");
                    } else {
                        existingCondition.setPublishStatus(1);
                        redisTemplate.opsForValue().set(publisherKey, JSON.toJSONString(publishInfoDTOMap));
                    }
                }
            }
            newPublishConditionReq.setPublishStatus(1);
            newPublishConditionReq.setPublishDate(String.valueOf(new Date()));
            receiverList.add(newPublishConditionReq);
        }
        publishInfoDTO.setReceiver(receiverList);
        publishInfoDTOMap.put(publishConditionListReq.getSurveyId(), publishInfoDTO);
        redisTemplate.opsForValue().set(publisherKey, JSON.toJSONString(publishInfoDTOMap));
        for (PublishConditionReq publishConditionReq : publishConditionListReq.getPublishConditionReq()) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.<User>lambdaQuery()
                    .eq(notEmpty(publishConditionReq.getCollege()), User::getCollege, publishConditionReq.getCollege())
                    .eq(notEmpty(publishConditionReq.getMajor()), User::getMajor, publishConditionReq.getMajor())
                    .eq(notEmpty(publishConditionReq.getGrade()), User::getGrade, publishConditionReq.getGrade())
                    .eq(notEmpty(publishConditionReq.getClasses()), User::getClasses, publishConditionReq.getClasses());
            List<Integer> userIds = userMapper.selectList(userLambdaQueryWrapper).stream().map(User::getId).collect(Collectors.toList());
            for (Integer userId : userIds) {
                String key = "student_" + userId + "_survey";
                String studentRecord = redisTemplate.opsForValue().get(key);
                if (studentRecord == null) {
                    List<SurveyIdWithPublisherDTO> surveyIdWithPublisherDTOList = new ArrayList<>();
                    surveyIdWithPublisherDTOList.add(new SurveyIdWithPublisherDTO(publishConditionListReq.getSurveyId(), publishConditionListReq.getPublisher()));
                    redisTemplate.opsForValue().set(key, JSON.toJSONString(surveyIdWithPublisherDTOList));
                } else {
                    List<SurveyIdWithPublisherDTO> surveyIdWithPublisherDTOList = JSON.parseArray(studentRecord, SurveyIdWithPublisherDTO.class);
                    if (surveyIdWithPublisherDTOList.stream().noneMatch(surveyIdWithPublisherDTO -> surveyIdWithPublisherDTO.getSurveyId()
                            .equals(publishConditionListReq.getSurveyId()))) {
                        surveyIdWithPublisherDTOList.add(new SurveyIdWithPublisherDTO(publishConditionListReq.getSurveyId(), publishConditionListReq.getPublisher()));
                        redisTemplate.opsForValue().set(key, JSON.toJSONString(surveyIdWithPublisherDTOList));
                    }
                }
            }
        }
        SystemLog systemLog = new SystemLog();
        systemLog.setActionPeople(publishConditionListReq.getPublisher());
        systemLog.setActionTime(LocalDateTime.now());
        systemLog.setActionName("发布问卷");
        systemLog.setActionDetail(systemLog.getActionPeople() + "发布了问卷" + publishConditionListReq.getSurveyId());
        systemLogMapper.insert(systemLog);
        return CommonResult.success();
    }

    /**
     * 为指定条件的学生取消发布问卷
     *
     * @param publishConditionListReq 取消发布条件
     * @return 200:发布成功 500:发布失败
     */
    @Override
    public CommonResult<?> cancelPublishSurveyForCondition(PublishConditionListReq publishConditionListReq) {
        String publisherKey = "publisher_" + publishConditionListReq.getPublisher();
        String publishRecord = redisTemplate.opsForValue().get(publisherKey);
        if (publishRecord == null) {
            return CommonResult.failed("该用户没有发布过问卷");
        } else {
            Map<Integer, PublishInfoDTO> publishInfoDTOMap = JSON.parseObject(publishRecord, new TypeReference<Map<Integer, PublishInfoDTO>>() {
            });
            for (PublishConditionReq publishConditionReq : publishConditionListReq.getPublishConditionReq()) {
                PublishInfoDTO publishInfoDTO = publishInfoDTOMap.get(publishConditionListReq.getSurveyId());
                if (publishInfoDTO == null) {
                    return CommonResult.failed("该用户没有发布过该问卷");
                } else {
                    List<PublishConditionReq> publishConditionReqs = publishInfoDTO.getReceiver();
                    if (publishConditionReqs == null) {
                        return CommonResult.failed("该用户没有发布过该问卷");
                    }
                    for (PublishConditionReq conditionReq : publishConditionReqs) {
                        if (conditionReq.getCollege().equals(CollegeEnum.getCollegeCode(publishConditionReq.getCollege()))) {
                            if (conditionReq.getMajor() != (null)) {
                                if (conditionReq.getMajor().equals(MajorEnum.getCode(publishConditionReq.getMajor()))) {
                                    if (conditionReq.getGrade() != (null)) {
                                        if (conditionReq.getGrade().equals(publishConditionReq.getGrade().split("级")[0])) {
                                            if (conditionReq.getClasses() != (null)) {
                                                if (conditionReq.getClasses().equals(publishConditionReq.getClasses().split("班")[0])) {
                                                    conditionReq.setPublishStatus(0);
                                                    conditionReq.setPublishDate(String.valueOf(new Date()));
                                                }
                                            } else {
                                                conditionReq.setPublishStatus(0);
                                                conditionReq.setPublishDate(String.valueOf(new Date()));
                                            }
                                        }
                                    } else {
                                        conditionReq.setPublishStatus(0);
                                        conditionReq.setPublishDate(String.valueOf(new Date()));
                                    }
                                }
                            } else {
                                conditionReq.setPublishStatus(0);
                                conditionReq.setPublishDate(String.valueOf(new Date()));
                            }
                        }
                    }
                }
            }
            redisTemplate.opsForValue().set(publisherKey, JSON.toJSONString(publishInfoDTOMap));
        }
        // 获取满足条件的学生
        for (PublishConditionReq publishConditionReq : publishConditionListReq.getPublishConditionReq()) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.<User>lambdaQuery()
                    .eq(notEmpty(publishConditionReq.getCollege()), User::getCollege, publishConditionReq.getCollege())
                    .eq(notEmpty(publishConditionReq.getMajor()), User::getMajor, publishConditionReq.getMajor())
                    .eq(notEmpty(publishConditionReq.getGrade()), User::getGrade, publishConditionReq.getGrade())
                    .eq(notEmpty(publishConditionReq.getClasses()), User::getClasses, publishConditionReq.getClasses());
            List<Integer> userIds = userMapper.selectList(userLambdaQueryWrapper).stream().map(User::getId).collect(Collectors.toList());
            for (Integer userId : userIds) {
                String key = "student_" + userId + "_survey";
                String studentRecord = redisTemplate.opsForValue().get(key);
                if (studentRecord != null) {
                    List<SurveyIdWithPublisherDTO> surveyIdWithPublisherDTOList = JSON.parseArray(studentRecord, SurveyIdWithPublisherDTO.class);
                    surveyIdWithPublisherDTOList.removeIf(surveyIdWithPublisherDTO -> surveyIdWithPublisherDTO.getSurveyId().equals(publishConditionListReq.getSurveyId()));
                    redisTemplate.opsForValue().set(key, JSON.toJSONString(surveyIdWithPublisherDTOList));
                }
            }
        }
        SystemLog systemLog = new SystemLog();
        systemLog.setActionPeople(publishConditionListReq.getPublisher());
        systemLog.setActionTime(LocalDateTime.now());
        systemLog.setActionName("取消发布问卷");
        systemLog.setActionDetail(systemLog.getActionPeople() + "取消发布了问卷");
        systemLogMapper.insert(systemLog);
        return CommonResult.success();
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
        String key = "publisher_" + username;
        String publishRecord = redisTemplate.opsForValue().get(key);
        if (publishRecord == null) {
            return CommonResult.failed("该用户没有发布过问卷");
        } else {
            Map<Integer, PublishInfoDTO> publishInfoDTOMap = JSON.parseObject(publishRecord, new TypeReference<Map<Integer, PublishInfoDTO>>() {
            });
            List<Integer> surveyIds = new ArrayList<>(publishInfoDTOMap.keySet());

            List<SurveyWithPublishInfo> surveys = new ArrayList<>();
            for (Integer surveyId : surveyIds) {
                String publishedSurveysKey = username + "_survey:" + surveyId;
                Survey survey = JSONObject.parseObject(redisTemplate.opsForValue().get(publishedSurveysKey.toString()), Survey.class);
                PublishInfoDTO publishInfoDTO = publishInfoDTOMap.get(surveyId);
                SurveyWithPublishInfo surveyWithPublishInfo = new SurveyWithPublishInfo();
                List<PublishConditionReq> publishConditionReqs = publishInfoDTO.getReceiver();
                if (publishConditionReqs == null) {
                    publishConditionReqs = new ArrayList<>();
                }
                for (PublishConditionReq publishConditionReq : publishConditionReqs) {
                    if (publishConditionReq.getCollege() != null) {
                        publishConditionReq.setCollege(CollegeEnum.getCollegeName(publishConditionReq.getCollege()));
                    }
                    if (publishConditionReq.getMajor() != null) {
                        publishConditionReq.setMajor(MajorEnum.getName(publishConditionReq.getMajor()));
                    }
                    if (publishConditionReq.getGrade() != null) {
                        publishConditionReq.setGrade(publishConditionReq.getGrade() + "级");
                    }
                    if (publishConditionReq.getClasses() != null) {
                        publishConditionReq.setClasses(publishConditionReq.getClasses() + "班");
                    }
                }
                publishInfoDTO.setReceiver(publishConditionReqs);
                surveyWithPublishInfo.setSurvey(survey);
                surveyWithPublishInfo.setPublishInfo(publishInfoDTO);
                surveys.add(surveyWithPublishInfo);
            }
            Page<SurveyWithPublishInfo> page = new Page<>(currentPage, pageSize);
            page.setRecords(surveys);
            page.setTotal(surveys.size());
            return CommonResult.success(page);
        }
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
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        String key = "student_" + user.getId() + "_survey";
        String studentRecord = redisTemplate.opsForValue().get(key);
        if (studentRecord == null) {
            return CommonResult.failed("该用户没有可以填写的问卷");
        } else {
            List<Integer> surveyIds = JSON.parseArray(studentRecord, Integer.class);
            Page<Survey> page = new Page<>(currentPage, pageSize);
            Page<Survey> surveyPage = surveyMapper.selectPage(page, new LambdaQueryWrapper<Survey>().in(Survey::getId, surveyIds));
            return CommonResult.success(surveyPage);
        }
    }

    /**
     * 获取所有的学院和专业
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getCollege() {
        List<EnumDTO> collegeList = Arrays.stream(CollegeEnum.values())
                .map(collegeEnum -> {
                    EnumDTO enumDTO = new EnumDTO();
                    enumDTO.setCode(collegeEnum.getCollegeCode());
                    enumDTO.setName(collegeEnum.getCollegeName());
                    return enumDTO;
                })
                .collect(Collectors.toList());
        return CommonResult.success(collegeList);
    }

    /**
     * 根据学院获取专业
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getMajor() {
        List<MajorEnumDTO> majorList = Arrays.stream(MajorEnum.values())
                .map(majorEnum -> {
                    MajorEnumDTO majorEnumDTO = new MajorEnumDTO();
                    majorEnumDTO.setCollegeCode(majorEnum.getCollegeCode());
                    majorEnumDTO.setMajorCode(majorEnum.getCode());
                    majorEnumDTO.setMajorName(majorEnum.getName());
                    return majorEnumDTO;
                })
                .collect(Collectors.toList());
        return CommonResult.success(majorList);
    }

    /**
     * 根据问卷的类型统计每种类型下的问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyTypeCount() {
        List<Map<String, Integer>> surveyTypeCount = surveyMapper.getSurveyTypeCount();
        return CommonResult.success(surveyTypeCount);
    }

    /**
     * 根据类型统计提交问卷的数量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedCountByType() {
        //获取所有问卷的id和类型
        List<Survey> surveys = surveyMapper.selectList(null);
        List<SubmittedCountByTypeResp> submittedCountByTypeRespList = new ArrayList<>();
        for (Survey survey : surveys) {
            SubmittedCountByTypeResp submittedCountByTypeResp = new SubmittedCountByTypeResp();
            submittedCountByTypeResp.setType(survey.getType());
            Integer count = Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, survey.getId())));
            submittedCountByTypeResp.setCount(count);
            submittedCountByTypeRespList.add(submittedCountByTypeResp);
        }
        return CommonResult.success(submittedCountByTypeRespList);
    }

    /**
     * 获取问卷的响应率
     *
     * @param username 用户名
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getResponseRate(String username, Integer surveyId) {
        String key = "publisher_" + username;
        Map<Integer, PublishInfoDTO> publishInfoDTOMap = JSON.parseObject(redisTemplate.opsForValue().get(key), new TypeReference<Map<Integer, PublishInfoDTO>>() {
        });
        PublishInfoDTO publishInfoDTO;
        if (publishInfoDTOMap != null) {
            publishInfoDTO = publishInfoDTOMap.get(surveyId);
        } else {
            return CommonResult.failed("该用户没有发布过问卷");
        }
        int receiverCount = 0;
        for (PublishConditionReq receiver : publishInfoDTO.getReceiver()) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.<User>lambdaQuery()
                    .eq(notEmpty(receiver.getCollege()), User::getCollege, receiver.getCollege())
                    .eq(notEmpty(receiver.getMajor()), User::getMajor, receiver.getMajor())
                    .eq(notEmpty(receiver.getGrade()), User::getGrade, receiver.getGrade())
                    .eq(notEmpty(receiver.getClasses()), User::getClasses, receiver.getClasses());
            receiverCount += userMapper.selectCount(userLambdaQueryWrapper);
        }
        int submitCount = Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, surveyId)));
        ResponseRateResp responseRateResp = new ResponseRateResp();
        responseRateResp.setTotal((long) receiverCount);
        responseRateResp.setSubmitCount((long) submitCount);
        return CommonResult.success(responseRateResp);
    }

    /**
     * 根据传入的被发布的学院或者专业或者年级或者班级，获取当前参数匹配的人群的提交信息
     *
     * @param publishConditionReq 查询条件
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmitInfoByCondition(PublishConditionReq publishConditionReq) {
//        LambdaQueryWrapper<User> userQuery = Wrappers.<User>lambdaQuery()
//                .eq(notEmpty(publishConditionReq.getCollege()), User::getCollege, publishConditionReq.getCollege())
//                .eq(notEmpty(publishConditionReq.getMajor()), User::getMajor, publishConditionReq.getMajor())
//                .eq(notEmpty(publishConditionReq.getGrade()), User::getGrade, publishConditionReq.getGrade())
//                .eq(notEmpty(publishConditionReq.getClasses()), User::getClasses, publishConditionReq.getClasses());
//        List<User> users = userMapper.selectList(userQuery);
//        List<SubmitInfoForCategoryResp> submitInfoForCategoryRespList = new ArrayList<>();
//        for (User user : users) {
//            SubmitInfoForCategoryResp submitInfoForCategoryResp = new SubmitInfoForCategoryResp();
//            submitInfoForCategoryResp.setUserId(user.getId());
//            submitInfoForCategoryResp.setUsername(user.getUsername());
//            List<SubmitSurveyRecord> submitSurveyRecords = submitSurveyRecordMapper.selectList(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getUserID, user.getId()));
//            List<SubmitInfoResp> submitInfoRespList = new ArrayList<>();
//            for (SubmitSurveyRecord submitSurveyRecord : submitSurveyRecords) {
//                SubmitInfoResp submitInfoResp = new SubmitInfoResp();
//                Survey survey = surveyMapper.selectById(submitSurveyRecord.getSurveyID());
//                submitInfoResp.setSurveyId(survey.getId());
//                submitInfoResp.setSurveyTitle(survey.getTitle());
//                submitInfoResp.setSubmitTime(submitSurveyRecord.getSubmitTime());
//                submitInfoRespList.add(submitInfoResp);
//            }
//            submitInfoForCategoryResp.setSubmitInfoRespList(submitInfoRespList);
//            submitInfoForCategoryRespList.add(submitInfoForCategoryResp);
//        }
        return CommonResult.success();
    }

    /**
     * 根据用戶名查询该用户发布的所有的问卷的提交列表
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getPublishedSurveySubmit(String username, Integer currentPage, Integer pageSize) {
        String key = "publisher_" + username;
        List<Integer> surveyIds = new ArrayList<>();
        Map<Integer, PublishInfoDTO> publishInfoDTOMap = JSON.parseObject(redisTemplate.opsForValue().get(key), new TypeReference<Map<Integer, PublishInfoDTO>>() {
        });
        if (publishInfoDTOMap != null) {
            surveyIds = new ArrayList<>(publishInfoDTOMap.keySet());
        }
        log.info("surveyIds:{}", surveyIds);
        Page<SubmitSurveyRecord> page = new Page<>(currentPage, pageSize);
        page = submitSurveyRecordMapper.selectPage(page, new LambdaQueryWrapper<SubmitSurveyRecord>().in(SubmitSurveyRecord::getSurveyID, surveyIds));
        List<SubmittedBrief> submittedBriefs = new ArrayList<>();
        for (SubmitSurveyRecord submitSurveyRecord : page.getRecords()) {
            SubmittedBrief submittedBrief = new SubmittedBrief();
            Survey survey = surveyMapper.selectById(submitSurveyRecord.getSurveyID());
            submittedBrief.setSurvey(survey);
            User user = userMapper.selectById(submitSurveyRecord.getUserID());
            if (user.getCollege() != null) {
                user.setCollege(CollegeEnum.getCollegeName(user.getCollege()));
            }
            if (user.getMajor() != null) {
                user.setMajor(MajorEnum.getName(user.getMajor()));
            }
            if (user.getGrade() != null) {
                user.setGrade(user.getGrade() + "级");
            }
            if (user.getClasses() != null) {
                user.setClasses(user.getClasses() + "班");
            }
            submittedBrief.setUser(user);
            submittedBrief.setSubmitSurveyRecord(submitSurveyRecord);
            submittedBriefs.add(submittedBrief);
        }
        Page<SubmittedBrief> submittedBriefPage = new Page<>();
        submittedBriefPage.setRecords(submittedBriefs);
        submittedBriefPage.setTotal(page.getTotal());
        submittedBriefPage.setSize(page.getSize());
        submittedBriefPage.setCurrent(page.getCurrent());
        submittedBriefPage.setPages(page.getPages());
        return CommonResult.success(submittedBriefPage);
    }

    /**
     * 根据问卷id统计该问卷的提交时间和提交数量
     *
     * @param surveyId 问卷id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedTotalByTime(Integer surveyId) {
        Map<LocalDateTime, Integer> submittedTotalByTime = new HashMap<>();
        List<SubmitSurveyRecord> submitSurveyRecords = submitSurveyRecordMapper.selectList(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getSurveyID, surveyId));
        for (SubmitSurveyRecord submitSurveyRecord : submitSurveyRecords) {
            LocalDateTime submitTime = submitSurveyRecord.getSubmitTime();
            Integer count = submittedTotalByTime.getOrDefault(submitTime, 0);
            submittedTotalByTime.put(submitTime, count + 1);
        }
        return CommonResult.success(submittedTotalByTime);
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
        String mySurveyListKey = username + "_mySurveyList";
        String surveyKey = username + "_survey:" + surveyId;
        String mySurveyListJson = redisTemplate.opsForValue().get(mySurveyListKey);
        Survey survey = surveyMapper.selectById(surveyId);
        if (mySurveyListJson == null) {
            List<Survey> mySurveyList = new ArrayList<>();
            mySurveyList.add(survey);
            redisTemplate.opsForValue().set(mySurveyListKey, JSON.toJSONString(mySurveyList));
        } else {
            List<Survey> mySurveyList = JSON.parseArray(mySurveyListJson, Survey.class);
            if (!mySurveyList.contains(survey)) {
                mySurveyList.add(survey);
                redisTemplate.opsForValue().set(mySurveyListKey, JSON.toJSONString(mySurveyList));
            } else {
                return CommonResult.failed("该问卷已经添加到我的问卷");
            }
        }
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getSurveyID, surveyId));
        questions.forEach(question -> {
            List<Option> options = optionBriefMapper.selectList(new LambdaQueryWrapper<Option>().eq(Option::getQuestionID, question.getId()));
            options.forEach(option -> {
                option.setOptionContent(optionContentMapper.selectById(option.getOptionContentID()));
            });
            question.setOptions(options);
        });
        survey.setQuestions(questions);
        redisTemplate.opsForValue().set(surveyKey, JSON.toJSONString(survey));
        SystemLog systemLog = new SystemLog();
        systemLog.setActionPeople(username);
        systemLog.setActionTime(LocalDateTime.now());
        systemLog.setActionName("添加问卷到我的问卷");
        systemLog.setActionDetail(systemLog.getActionPeople() + "添加了问卷" + survey.getName() + "到我的问卷");
        systemLogMapper.insert(systemLog);
        return CommonResult.success();
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
        String mySurveyListKey = username + "_mySurveyList";
        String mySurveyListJson = redisTemplate.opsForValue().get(mySurveyListKey);
        if (mySurveyListJson == null) {
            return CommonResult.failed("该用户没有添加过问卷");
        } else {
            List<Survey> mySurveyList = JSON.parseArray(mySurveyListJson, Survey.class);
            Page<Survey> page = new Page<>(currentPage, pageSize);
            page.setRecords(mySurveyList);
            page.setTotal(mySurveyList.size());
            return CommonResult.success(page);
        }
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
        String mySurveyListKey = username + "_mySurveyList";
        String mySurveyListJson = redisTemplate.opsForValue().get(mySurveyListKey);
        if (mySurveyListJson == null) {
            return CommonResult.failed("该用户没有添加过问卷");
        } else {
            List<Survey> mySurveyList = JSON.parseArray(mySurveyListJson, Survey.class);
            Survey survey = surveyMapper.selectById(surveyId);
            if (mySurveyList.contains(survey)) {
                mySurveyList.remove(survey);
                redisTemplate.opsForValue().set(mySurveyListKey, JSON.toJSONString(mySurveyList));
                return CommonResult.success();
            } else {
                SystemLog systemLog = new SystemLog();
                systemLog.setActionPeople(username);
                systemLog.setActionTime(LocalDateTime.now());
                systemLog.setActionName("删除我的问卷");
                systemLog.setActionDetail(systemLog.getActionPeople() + "删除了问卷" + survey.getName() + "从我的问卷");
                systemLogMapper.insert(systemLog);
                return CommonResult.failed("该用户没有添加过该问卷");
            }
        }
    }

    /**
     * 根据问卷id和用户名修改问卷信息
     *
     * @param editMySurveyWillReq 问卷
     * @return 200:修改成功 500:修改失败
     */
    @Override
    public CommonResult<?> editMySurveyWill(EditMySurveyWillReq editMySurveyWillReq) {
        String mySurveyListKey = editMySurveyWillReq.getUsername() + "_mySurveyList";
        String mySurveyListJson = redisTemplate.opsForValue().get(mySurveyListKey);
        String mySurveyKey = editMySurveyWillReq.getUsername() + "_survey:" + editMySurveyWillReq.getSurvey().getId();
        Survey mySurveyTemp = JSON.parseObject(redisTemplate.opsForValue().get(mySurveyKey), Survey.class);
        if (mySurveyListJson == null) {
            return CommonResult.failed("该用户没有添加过问卷");
        } else {
            List<Survey> mySurveyList = JSON.parseArray(mySurveyListJson, Survey.class);
            Survey survey = surveyMapper.selectById(editMySurveyWillReq.getSurvey().getId());
            if (mySurveyList.stream().anyMatch(s -> s.getId().equals(survey.getId()))) {
                //这里只修改简要信息，不修改问题和选项
                Survey toUpdateSurvey = editMySurveyWillReq.getSurvey();
                if (mySurveyTemp != null) {
                    toUpdateSurvey.setQuestions(mySurveyTemp.getQuestions());
                }
                toUpdateSurvey.getQuestions().forEach(question -> {
                    question.setOptions(question.getOptions());
                    question.getOptions().forEach(option -> {
                        option.setOptionContent(optionContentMapper.selectById(option.getOptionContentID()));
                    });
                });
                for (Survey mySurvey : mySurveyList) {
                    if (mySurvey.getId().equals(toUpdateSurvey.getId())) {
                        mySurveyList.remove(mySurvey);
                        break;
                    }
                }
                mySurveyList.add(toUpdateSurvey);
                redisTemplate.opsForValue().set(mySurveyListKey, JSON.toJSONString(mySurveyList));
                redisTemplate.opsForValue().set(mySurveyKey, JSON.toJSONString(toUpdateSurvey));
                return CommonResult.success();
            } else {
                SystemLog systemLog = new SystemLog();
                systemLog.setActionPeople(editMySurveyWillReq.getUsername());
                systemLog.setActionTime(LocalDateTime.now());
                systemLog.setActionName("修改我的问卷");
                systemLog.setActionDetail(systemLog.getActionPeople() + "修改了问卷" + survey.getName() + "的简要信息");
                systemLogMapper.insert(systemLog);
                return CommonResult.failed("该用户没有添加过该问卷");
            }
        }

    }

    /**
     * 根据问卷id和用户名修改问卷详细信息
     *
     * @param editMySurveyReq 问卷
     * @return 200:修改成功 500:修改失败
     */
    @Override
    public CommonResult<?> editMySurveyDetail(EditMySurveyReq editMySurveyReq) {
        String mySurveyListKey = editMySurveyReq.getUsername() + "_mySurveyList";
        String mySurveyDetailKey = editMySurveyReq.getUsername() + "_survey:" + editMySurveyReq.getSurvey().getId();
        String mySurveyListJson = redisTemplate.opsForValue().get(mySurveyListKey);
        if (mySurveyListJson == null) {
            return CommonResult.failed("该用户没有添加过问卷");
        } else {
            List<Survey> mySurveyList = JSON.parseArray(mySurveyListJson, Survey.class);
            //这里修改简要信息和详细信息
            Survey toUpdateSurvey = editMySurveyReq.getSurvey();
            for (Survey mySurvey : mySurveyList) {
                if (mySurvey.getId().equals(toUpdateSurvey.getId())) {
                    mySurveyList.remove(mySurvey);
                    break;
                }
            }
            mySurveyList.add(toUpdateSurvey);
            redisTemplate.opsForValue().set(mySurveyListKey, JSON.toJSONString(mySurveyList));
            redisTemplate.opsForValue().set(mySurveyDetailKey, JSON.toJSONString(toUpdateSurvey));
            SystemLog systemLog = new SystemLog();
            systemLog.setActionPeople(editMySurveyReq.getUsername());
            systemLog.setActionTime(LocalDateTime.now());
            systemLog.setActionName("修改我的问卷");
            systemLog.setActionDetail(systemLog.getActionPeople() + "修改了问卷" + toUpdateSurvey.getName() + "的详细信息");
            systemLogMapper.insert(systemLog);
            return CommonResult.success();
        }
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
        String mySurveyDetailKey = username + "_survey:" + surveyId;
        String mySurveyDetailJson = redisTemplate.opsForValue().get(mySurveyDetailKey);
        if (mySurveyDetailJson == null) {
            return CommonResult.failed("该用户没有添加过该问卷");
        } else {
            Survey survey = JSON.parseObject(mySurveyDetailJson, Survey.class);
            return CommonResult.success(survey);
        }
    }

    /**
     * 按照用户登录的时间段按星期统计所有用户的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getActiveUserByWeek() {
        List<AccessLog> accessLogs = accessLogMapper.selectList(null);
        List<String> weekOrder = Arrays.asList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");
        Map<String, Integer> map = new TreeMap<>(Comparator.comparingInt(weekOrder::indexOf));
        map.put("星期一", 0);
        map.put("星期二", 0);
        map.put("星期三", 0);
        map.put("星期四", 0);
        map.put("星期五", 0);
        map.put("星期六", 0);
        map.put("星期日", 0);
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = now.with(DayOfWeek.SUNDAY);
        for (AccessLog accessLog : accessLogs) {
            LocalDateTime date = accessLog.getAccessTime();
            LocalDate localDate = date.toLocalDate();
            if (!localDate.isBefore(weekStart) && !localDate.isAfter(weekEnd)) {
                String week = date.getDayOfWeek().toString();
                switch (week) {
                    case "MONDAY":
                        week = "星期一";
                        break;
                    case "TUESDAY":
                        week = "星期二";
                        break;
                    case "WEDNESDAY":
                        week = "星期三";
                        break;
                    case "THURSDAY":
                        week = "星期四";
                        break;
                    case "FRIDAY":
                        week = "星期五";
                        break;
                    case "SATURDAY":
                        week = "星期六";
                        break;
                    case "SUNDAY":
                        week = "星期日";
                        break;
                }
                map.put(week, map.get(week) + 1);
            }
        }
        return CommonResult.success(map);
    }

    /**
     * 获取今日提交的问卷数量和总提交的问卷数量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedStatics() {
        SubmittedStaticsResp submittedStaticsResp = new SubmittedStaticsResp();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(23, 59, 59);
        LambdaQueryWrapper<SubmitSurveyRecord> submitSurveyRecordLambdaQueryWrapper = Wrappers.<SubmitSurveyRecord>lambdaQuery()
                .between(SubmitSurveyRecord::getSubmitTime, startOfToday, endOfToday);
        submittedStaticsResp.setTodayCount(Math.toIntExact(submitSurveyRecordMapper.selectCount(submitSurveyRecordLambdaQueryWrapper)));
        submittedStaticsResp.setTotalCount(Math.toIntExact(submitSurveyRecordMapper.selectCount(null)));
        submittedStaticsResp.setIncreasePercent((double) (submittedStaticsResp.getTodayCount() / submittedStaticsResp.getTotalCount() * 100));
        return CommonResult.success(submittedStaticsResp);
    }

    /**
     * 获取问卷总数和本月新增问卷数
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyStatics() {
        SurveyStaticsResp surveyStaticsResp = new SurveyStaticsResp();
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = now.with(TemporalAdjusters.lastDayOfMonth());
        LambdaQueryWrapper<Survey> surveyLambdaQueryWrapper = Wrappers.<Survey>lambdaQuery()
                .between(Survey::getCreateTime, monthStart, monthEnd);
        surveyStaticsResp.setMonthCount(Math.toIntExact(surveyMapper.selectCount(surveyLambdaQueryWrapper)));
        surveyStaticsResp.setTotalCount(Math.toIntExact(surveyMapper.selectCount(null)));
        surveyStaticsResp.setIncreasePercent((double) (surveyStaticsResp.getMonthCount() / surveyStaticsResp.getTotalCount() * 100));
        return CommonResult.success(surveyStaticsResp);
    }

    /**
     * 获取系统总访问量、今日访问量和增加百分比
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUserStatics() {
        UserStaticsResp userStaticsResp = new UserStaticsResp();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(23, 59, 59);
        LambdaQueryWrapper<AccessLog> accessLogLambdaQueryWrapper = Wrappers.<AccessLog>lambdaQuery()
                .between(AccessLog::getAccessTime, startOfToday, endOfToday);
        userStaticsResp.setTodayCount(Math.toIntExact(accessLogMapper.selectCount(accessLogLambdaQueryWrapper)));
        userStaticsResp.setTotalCount(Math.toIntExact(accessLogMapper.selectCount(null)));
        userStaticsResp.setIncreasePercent((double) (userStaticsResp.getTodayCount() / userStaticsResp.getTotalCount() * 100));
        return CommonResult.success(userStaticsResp);
    }

    /**
     * 获取评论总数和今日新增评论数
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getCommentStatics() {
        CommentStaticsResp commentStaticsResp = new CommentStaticsResp();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(23, 59, 59);
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = Wrappers.<Comment>lambdaQuery()
                .between(Comment::getSubmitTime, startOfToday, endOfToday);
        commentStaticsResp.setTodayCount(Math.toIntExact(commentMapper.selectCount(commentLambdaQueryWrapper)));
        commentStaticsResp.setTotalCount(Math.toIntExact(commentMapper.selectCount(null)));
        commentStaticsResp.setIncreasePercent((double) (commentStaticsResp.getTodayCount() / commentStaticsResp.getTotalCount() * 100));
        return CommonResult.success(commentStaticsResp);
    }

    /**
     * 近十天的系统访问量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUserStaticsByTenDay() {
        List<AccessLog> accessLogs = accessLogMapper.selectList(null);
        Map<LocalDate, Integer> map = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            map.put(date, 0);
        }
        for (AccessLog accessLog : accessLogs) {
            LocalDateTime accessTime = accessLog.getAccessTime();
            LocalDate localDate = accessTime.toLocalDate();
            if (localDate.isAfter(LocalDate.now().minusDays(9))) {
                Integer count = map.get(localDate);
                map.put(localDate, count + 1);
            }
        }
        return CommonResult.success(map);
    }

    /**
     * 根据评论量来统计最热门的问卷
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getHotSurveyByComment() {
        HotSurveyResp hotSurveyResp = new HotSurveyResp();
        //通过查询survey表和comment表，根据评论量来统计最热门的问卷
        List<Survey> surveys = surveyMapper.selectList(null);
        List<HotSurveyDTO> hotSurveys = new ArrayList<>();
        for (Survey survey : surveys) {
            Integer count = Math.toIntExact(commentMapper.selectCount(new LambdaQueryWrapper<Comment>().eq(Comment::getSurveyID, survey.getId())));
            HotSurveyDTO hotSurveyDTO = new HotSurveyDTO();
            hotSurveyDTO.setId(survey.getId());
            hotSurveyDTO.setTitle(survey.getName());
            hotSurveyDTO.setDescription(survey.getDescription());
            hotSurveyDTO.setCommentCount(count);
            hotSurveys.add(hotSurveyDTO);
        }
        hotSurveys.sort(Comparator.comparing(HotSurveyDTO::getCommentCount).reversed());
        //只取前十个
        if (hotSurveys.size() > 10) {
            hotSurveys = hotSurveys.subList(0, 10);
        }
        hotSurveyResp.setHotSurveyList(hotSurveys);
        return CommonResult.success(hotSurveyResp);
    }

    /**
     * 统计每个学院的提交量
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSubmittedCountByCollege() {
        List<EnumDTO> collegeList = Arrays.stream(CollegeEnum.values())
                .map(collegeEnum -> {
                    EnumDTO enumDTO = new EnumDTO();
                    enumDTO.setCode(collegeEnum.getCollegeCode());
                    enumDTO.setName(collegeEnum.getCollegeName());
                    return enumDTO;
                })
                .collect(Collectors.toList());
        List<SubmittedCountByCollegeResp> submittedCountByCollegeRespList = new ArrayList<>();
        for (EnumDTO enumDTO : collegeList) {
            SubmittedCountByCollegeResp submittedCountByCollegeResp = new SubmittedCountByCollegeResp();
            submittedCountByCollegeResp.setCollege(enumDTO.getName());
            LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.<User>lambdaQuery()
                    .eq(User::getCollege, enumDTO.getCode());
            List<User> users = userMapper.selectList(userLambdaQueryWrapper);
            int count = 0;
            for (User user : users) {
                count += Math.toIntExact(submitSurveyRecordMapper.selectCount(new LambdaQueryWrapper<SubmitSurveyRecord>().eq(SubmitSurveyRecord::getUserID, user.getId())));
            }
            submittedCountByCollegeResp.setCount(count);
            submittedCountByCollegeRespList.add(submittedCountByCollegeResp);
        }
        return CommonResult.success(submittedCountByCollegeRespList);
    }

    /**
     * 统计所有反馈分数每个分段的人数
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getFeedbackScoreStatistics() {
        List<FeedbackScoreStatisticsResp> feedbackScoreStatisticsRespList = new ArrayList<>();
        for (double i = 0; i <= 5; i += 0.5) {
            FeedbackScoreStatisticsResp feedbackScoreStatisticsResp = new FeedbackScoreStatisticsResp();
            feedbackScoreStatisticsResp.setScore(i);
            LambdaQueryWrapper<Comment> feedbackLambdaQueryWrapper = Wrappers.<Comment>lambdaQuery()
                    .ge(Comment::getScore, i)
                    .lt(Comment::getScore, i + 0.5);
            feedbackScoreStatisticsResp.setCount(Math.toIntExact(commentMapper.selectCount(feedbackLambdaQueryWrapper)));
            feedbackScoreStatisticsRespList.add(feedbackScoreStatisticsResp);
        }
        return CommonResult.success(feedbackScoreStatisticsRespList);
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
        Page<SystemLog> page = new Page<>(currentPage, pageSize);
        page = systemLogMapper.selectPage(page, new LambdaQueryWrapper<SystemLog>().eq(SystemLog::getActionPeople, username));
        return CommonResult.success(page);
    }

    @Override
    public CommonResult<?> analyseEmotion(Integer id) throws JSONException {
        //百度文心一言情感分析
        SubmitSurveyRecord submitSurveyRecord = submitSurveyRecordMapper.selectById(id);
        Survey survey = surveyMapper.selectById(submitSurveyRecord.getSurveyID());
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getSurveyID, survey.getId()));
        List<QuestionWithAnswerDTO> questionWithAnswerDTOS = new ArrayList<>();
        for (Question question : questions) {
            QuestionWithAnswerDTO questionWithAnswerDTO = new QuestionWithAnswerDTO();
            questionWithAnswerDTO.setQuestion(question.getContent());
            Answer answer = answerMapper.selectOne(new LambdaQueryWrapper<>(Answer.class)
                    .eq(Answer::getQuestionID, question.getId())
                    .eq(Answer::getUserID, submitSurveyRecord.getUserID()));
            questionWithAnswerDTO.setAnswer(answer.getContent());
            questionWithAnswerDTOS.add(questionWithAnswerDTO);
        }
        //遍历问题和答案，进行情感分析，给问题加上问：，给答案加上答：
        StringBuilder content = new StringBuilder();
        for (QuestionWithAnswerDTO questionWithAnswerDTO : questionWithAnswerDTOS) {
            content.append("问：").append(questionWithAnswerDTO.getQuestion()).append("。");
            content.append("答：").append(questionWithAnswerDTO.getAnswer()).append("。");
        }
        EmotionAnalyseResultResp emotion = emotionAnalyseService.analyseEmotion(content.toString());
        return CommonResult.success(emotion);
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名
     * @param os       输出流
     */
    @Override
    public void downloadSurveyTemplate(String fileName, OutputStream os) throws IOException {
        //下载文件的路径
        String downloadFilePath = localUrlPath + fileName;
        File file = new File(downloadFilePath);
        InputStream is = new FileInputStream(file);
        if (is == null) {
            CommonResult.failed("文件不存在");
            return;
        }
        IOUtils.copy(is, os);
        os.flush();
        is.close();
        os.close();
        CommonResult.success();
    }

    /**
     * 上传问卷
     *
     * @param file 文件
     * @return 200:上传成功 500:上传失败
     */
    @Override
    public CommonResult<?> uploadSurvey(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String localDir = localUrlPath;
        File dir = new File(localDir);
        if (!dir.exists()) {
            boolean dirCreated = dir.mkdirs();
            if (!dirCreated) {
                log.error("Failed to create directory: " + localDir);
                return CommonResult.failed("Failed to create directory");
            }
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        assert fileName != null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        String realFileName = uuid + fileType;
        String filePathAll = localDir + realFileName;
        File realFile = new File(filePathAll);
        file.transferTo(realFile);
        String virtualPath = realFileName;
        SystemLog systemLog = new SystemLog();
        systemLog.setActionPeople("admin");
        systemLog.setActionTime(LocalDateTime.now());
        systemLog.setActionName("上传问卷");
        systemLog.setActionDetail(systemLog.getActionPeople() + "上传了问卷" + realFileName);
        systemLogMapper.insert(systemLog);
        return CommonResult.success(new FileVo(virtualPath, realFileName));
    }

    /**
     * 解析问卷
     *
     * @param filePath 文件路径
     * @return 200:解析成功 500:解析失败
     */
    @Override
    public CommonResult<?> parseSurvey(String filePath) {
        String realFilePath = localUrlPath + filePath;
        try {
            XWPFDocument document = new XWPFDocument(new FileInputStream(realFilePath));
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            Survey survey = new Survey();

            // 解析问卷基本信息
            XWPFParagraph headerInfo = paragraphs.get(20);
            survey.setName(headerInfo.getText().trim().split("：")[1]); // 问卷标题

            XWPFParagraph descriptionInfo = paragraphs.get(21);
            survey.setDescription(descriptionInfo.getText().trim().split("：")[1]); // 问卷描述

            // 解析开始/结束时间
            XWPFParagraph timeInfo = paragraphs.get(22);
            String[] timeInfoArray = timeInfo.getText().split("：");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = format.parse(timeInfoArray[1].trim());
            Instant instant = startTime.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
            survey.setStartTime(localDateTime); // 开始时间

            timeInfo = paragraphs.get(23);
            timeInfoArray = timeInfo.getText().split("：");
            Date endTime = format.parse(timeInfoArray[1].trim());
            instant = endTime.toInstant();
            localDateTime = instant.atZone(zoneId).toLocalDateTime();
            survey.setEndTime(localDateTime); // 结束时间

            // 解析问卷类型
            XWPFParagraph typeInfo = paragraphs.get(24);
            survey.setType(typeInfo.getText().split("：")[1].trim()); // 问卷类型

            // 解析问题列表
            List<Question> questions = new ArrayList<>();
            for (int i = 26; i < paragraphs.size(); i += 4) {
                if (i + 4 < paragraphs.size() && paragraphs.get(i + 4).getText().trim().isEmpty()) {
                    break;
                }
                //i迭代四次，就判断一次下一行是否有问题
                XWPFParagraph questionInfo = paragraphs.get(i);
                String questionContent = questionInfo.getText().split("：")[1].trim();
                Question question = new Question();
                question.setContent(questionContent); // 问题文本

                // 解析问题类型
                XWPFParagraph typeInfoQuestion = paragraphs.get(i + 1);
                String type = typeInfoQuestion.getText().split("：")[1].trim();
                question.setType(type); // 问题类型

                // 解析选项（仅适用于多选/单选问题）
                if (Objects.equals(question.getType(), "多选") || Objects.equals(question.getType(), "单选")) {
                    XWPFParagraph optionsInfo = paragraphs.get(i + 2);
                    String[] options = optionsInfo.getText().split("：")[1].split("\\s");
                    List<Option> optionList = new ArrayList<>();
                    for (String option : options) {
                        Option optionObj = new Option();
                        OptionContent optionContent = new OptionContent();
                        optionContent.setContent(option);
                        optionObj.setOptionContent(optionContent);
                        optionList.add(optionObj);
                    }
                    question.setOptions(optionList);
                }
                questions.add(question);
            }

            survey.setQuestions(questions);

            // 返回解析成功的问卷对象
            return CommonResult.success(survey);
        } catch (IOException | ParseException e) {
            log.error("Failed to parse survey template from file: {}", filePath, e);
            return CommonResult.failed("问卷解析失败");
        }
    }

    /**
     * 根据解析结果创建问卷
     *
     * @param survey 问卷
     * @return 200:创建成功 500:创建失败
     */
    @Override
    public CommonResult<?> createSurveyByParse(Survey survey) {
        Survey surveyToInsert = new Survey();
        surveyToInsert.setCreateTime(LocalDateTime.now());
        surveyToInsert.setName(survey.getName());
        surveyToInsert.setDescription(survey.getDescription());
        surveyToInsert.setStartTime(survey.getStartTime());
        surveyToInsert.setEndTime(survey.getEndTime());
        surveyToInsert.setType(survey.getType());
        surveyMapper.insert(surveyToInsert);
        log.info("surveyToInsert :{} ", surveyToInsert);
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("surveyPage*")));
        survey.getQuestions().forEach(question -> {
            Question questionToInsert = new Question();
            questionToInsert.setSurveyID(surveyToInsert.getId());
            questionToInsert.setContent(question.getContent());
            questionToInsert.setType(question.getType());
            questionMapper.insert(questionToInsert);
            if (!Objects.equals(question.getType(), "填空")) {
                question.getOptions().forEach(option -> {
                    OptionContent optionContentToInsert = option.getOptionContent();
                    optionContentMapper.insert(optionContentToInsert);
                    Option optionToInsert = new Option();
                    optionToInsert.setQuestionID(questionToInsert.getId());
                    optionToInsert.setOptionContentID(optionContentToInsert.getId());
                    optionBriefMapper.insert(optionToInsert);
                });
            }
        });
        SystemLog systemLog = new SystemLog();
        systemLog.setActionPeople("admin");
        systemLog.setActionTime(LocalDateTime.now());
        systemLog.setActionName("创建问卷");
        systemLog.setActionDetail(systemLog.getActionPeople() + "创建了问卷" + surveyToInsert.getName());
        systemLogMapper.insert(systemLog);
        return CommonResult.failed();
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
        Page<SurveyWithPublishDTO> surveyPage = new Page<>(currentPage, pageSize);
        List<SurveyWithPublishDTO> surveyWithPublishDTOList = new ArrayList<>();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        String key = "student_" + user.getId() + "_survey";
        List<SurveyIdWithPublisherDTO> surveyIdWithPublisherDTOS = JSON.parseArray(redisTemplate.opsForValue().get(key), SurveyIdWithPublisherDTO.class);
        if (surveyIdWithPublisherDTOS != null) {
            int start = (currentPage - 1) * pageSize;
            int end = Math.min(start + pageSize, surveyIdWithPublisherDTOS.size());
            if (end > start) {
                List<SurveyIdWithPublisherDTO> subIds = surveyIdWithPublisherDTOS.subList(start, end);
                for (SurveyIdWithPublisherDTO surveyIdWithPublisherDTO : subIds) {
                    Survey survey = surveyMapper.selectById(surveyIdWithPublisherDTO.getSurveyId());
                    SurveyWithPublishDTO surveyWithPublishDTO = new SurveyWithPublishDTO();
                    surveyWithPublishDTO.setSurvey(survey);
                    surveyWithPublishDTO.setPublisher(surveyIdWithPublisherDTO.getPublisher());
                    surveyWithPublishDTOList.add(surveyWithPublishDTO);
                }
            } else {
                return CommonResult.failed("没有更多问卷了");
            }
        }
        surveyPage.setRecords(surveyWithPublishDTOList);
        assert surveyIdWithPublisherDTOS != null;
        surveyPage.setTotal(surveyIdWithPublisherDTOS.size());
        return CommonResult.success(surveyPage);
    }

    /**
     * 通过根据发布人和问卷id查询redis中的问卷
     *
     * @param surveyIdWithPublisherReq 请求体
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getSurveyDetailByPublisher(SurveyIdWIthPublisherReq surveyIdWithPublisherReq) {
        String key = surveyIdWithPublisherReq.getPublisher() + "_survey:" + surveyIdWithPublisherReq.getSurveyId();
        Survey survey = JSON.parseObject(redisTemplate.opsForValue().get(key), Survey.class);
        SurveyWithPublishDTO surveyWithPublishDTO = new SurveyWithPublishDTO();
        surveyWithPublishDTO.setSurvey(survey);
        surveyWithPublishDTO.setPublisher(surveyIdWithPublisherReq.getPublisher());
        return CommonResult.success(surveyWithPublishDTO);
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
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Comment comment = commentMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getUserID, user.getId()).eq(Comment::getSurveyID, surveyId));
        return CommonResult.success(comment);
    }

    /**
     * 获取ai建议
     *
     * @param id 提交记录id
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getAiSuggest(Integer id) {
        String key = "AISuggestion:" + id;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return CommonResult.success(redisTemplate.opsForValue().get(key));
        }
        SubmitSurveyRecord submitSurveyRecord = submitSurveyRecordMapper.selectById(id);
        Survey survey = surveyMapper.selectById(submitSurveyRecord.getSurveyID());
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getSurveyID, survey.getId()));
        List<QuestionWithAnswerDTO> questionWithAnswerDTOS = new ArrayList<>();
        for (Question question : questions) {
            QuestionWithAnswerDTO questionWithAnswerDTO = new QuestionWithAnswerDTO();
            questionWithAnswerDTO.setQuestion(question.getContent());
            Answer answer = answerMapper.selectOne(new LambdaQueryWrapper<>(Answer.class)
                    .eq(Answer::getQuestionID, question.getId())
                    .eq(Answer::getUserID, submitSurveyRecord.getUserID()));
            questionWithAnswerDTO.setAnswer(answer.getContent());
            questionWithAnswerDTOS.add(questionWithAnswerDTO);
        }
        StringBuilder content = new StringBuilder();
        for (QuestionWithAnswerDTO questionWithAnswerDTO : questionWithAnswerDTOS) {
            content.append("问：").append(questionWithAnswerDTO.getQuestion()).append("。");
            content.append("答：").append(questionWithAnswerDTO.getAnswer()).append("。");
        }
        String contentText = content.toString();
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY;
        Map map = JSONObject.parseObject(HttpUtil.get(url), Map.class);
        String accessToken = map.get("access_token").toString();
        String accessUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token=" + accessToken;
        HashMap<String, String> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", "下面是一份问卷调查的回答情况，请帮我按照回答内容给出适当的建议。(全面分析，列出至少五条建议，注意针对回答人的建议，不涉及提问者，每条在30到40字左右)" + contentText);
        ArrayList<HashMap<String, String>> messagesList = new ArrayList<>();
        messagesList.add(msg);
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messagesList);
        String param = JSON.toJSONString(requestBody);
        Map resultMap = JSONObject.parseObject(HttpUtil.post(accessUrl, param), Map.class);
        String suggestion = resultMap.get("result").toString();
        redisTemplate.opsForValue().set(key, suggestion);
        return CommonResult.success(suggestion);
    }

    @Override
    public CommonResult<?> getMyComments(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        List<Comment> comments = commentMapper.selectList(new LambdaQueryWrapper<Comment>().eq(Comment::getUserID, user.getId()));
        return CommonResult.success(comments);
    }
}