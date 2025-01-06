package com.zhy.questionnairesystem.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhy.domain.AccessLog;
import com.zhy.domain.SubmitSurveyRecord;
import com.zhy.domain.User;
import com.zhy.enums.CollegeEnum;
import com.zhy.enums.MajorEnum;
import com.zhy.questionnairesystem.mapper.AccessLogMapper;
import com.zhy.questionnairesystem.mapper.SubmitSurveyRecordMapper;
import com.zhy.questionnairesystem.mapper.UserMapper;
import com.zhy.questionnairesystem.service.SmsService;
import com.zhy.questionnairesystem.service.UserService;
import com.zhy.resp.UserWithTokenResp;
import com.zhy.utils.CommonResult;
import com.zhy.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class UserServiceImpl implements UserService {
    //注入mapper
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AccessLogMapper accessLogMapper;
    @Autowired
    private SubmitSurveyRecordMapper submitSurveyRecordMapper;
    //注入redis
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SmsService smsService;

    /**
     * 用户登录方法
     *
     * @param userName 用户名
     * @param passWord 密码
     * @return 200:登录成功 500:登录失败
     */
    @Override
    public CommonResult<?> login(String userName, String passWord) {

        //先检查redis中是否有该用户
        String cacheUserString = redisTemplate.opsForValue().get("user:" + userName);
        UserWithTokenResp cacheUser = JSON.parseObject(cacheUserString, UserWithTokenResp.class);
        //如果没有则从数据库中查询
        if (cacheUserString == null) {
            //从数据库中查询
            User user = new User();
            user.setUsername(userName);
            user.setPassword(passWord);
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("username", user.getUsername());
            wrapper.eq("password", user.getPassword());
            User dbUser = userMapper.selectOne(wrapper);

            System.out.println("从数据库中查询");
            System.out.println(dbUser);
            //如果数据库中有该用户，则将该用户存入redis中
            if (dbUser != null) {
                accessLogMapper.insert(new AccessLog(dbUser.getId()));
                UserWithTokenResp userWithTokenResp = new UserWithTokenResp();
                Map<String, String> payload = new HashMap<>();
                payload.put("id", dbUser.getId().toString());
                payload.put("username", dbUser.getUsername());
                String token = JwtUtil.getToken(payload);
                userWithTokenResp.setUser(dbUser);
                userWithTokenResp.setToken(token);
                redisTemplate.opsForValue().set("user:" + userName, JSON.toJSONString(userWithTokenResp));
                return CommonResult.success(userWithTokenResp);
            }
            //如果数据库中没有该用户，则返回0
            return CommonResult.failed("用户名或密码错误");
        }
        System.out.println("从redis中查询");
        if (cacheUser.getUser().getPassword().equals(passWord)) {
            //生成token
            UserWithTokenResp userWithTokenResp = new UserWithTokenResp();
            Map<String, String> payload = new HashMap<>();
            payload.put("id", cacheUser.getUser().getId().toString());
            payload.put("username", cacheUser.getUser().getUsername());
            String token = JwtUtil.getToken(payload);
            userWithTokenResp.setUser(cacheUser.getUser());
            userWithTokenResp.setToken(token);
            accessLogMapper.insert(new AccessLog(cacheUser.getUser().getId()));
            return CommonResult.success(userWithTokenResp);
        }
        return CommonResult.failed("用户名或密码错误");
    }

    /**
     * 用户登录方法（使用手机验证码）
     *
     * @param phone            手机号码
     * @param verificationCode 验证码
     * @return 200:登录成功 500:登录失败
     */
    public CommonResult<?> loginByVerificationCode(String phone, String verificationCode) {
        //先检查redis中是否有该用户
        String cacheUserString = redisTemplate.opsForValue().get("user:" + phone);
        User cacheUser = JSON.parseObject(cacheUserString, User.class);
        //
        //获取redis中的验证码
        String code = redisTemplate.opsForValue().get("verificationCode:" + phone);
        //检测验证码是否正确
        if (code != null && code.equals(verificationCode)) {
            //验证码正确，登录
            //如果没有则从数据库中查询
            if (cacheUserString == null) {
                //从数据库中查询
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("phone", phone);
                User dbUser = userMapper.selectOne(wrapper);
                //如果数据库中有该用户，则将该用户存入redis中
                if (dbUser != null) {
                    String dbUserJson = JSON.toJSONString(dbUser);
                    redisTemplate.opsForValue().set("user:" + phone, dbUserJson);
                    accessLogMapper.insert(new AccessLog(dbUser.getId()));
                    return CommonResult.success(dbUser);
                }
                //如果数据库中没有该用户，则返回0
                return CommonResult.failed("手机号码错误");
            }
            accessLogMapper.insert(new AccessLog(cacheUser.getId()));
            return CommonResult.success(cacheUser);
        }
        return CommonResult.failed("验证码错误!");
    }

    /**
     * 用户注册方法
     *
     * @param user 用户信息
     * @return 200:注册成功 500:注册失败
     */
    @Override
    public CommonResult<?> register(User user) {
        //先检查redis中是否有该用户
        String cacheUserString = redisTemplate.opsForValue().get("user:" + user.getUsername());
        //获取redis中的验证码
        String code = redisTemplate.opsForValue().get("verificationCode:" + user.getPhone());
        //检测验证码是否正确
//        if (code != null && code.equals(verificationCode)) {
//
//        }
        //验证码正确，注册
        //如果没有则从数据库中查询
        if (cacheUserString == null) {
            //从数据库中查询
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("username", user.getUsername());
            User dbUser = userMapper.selectOne(wrapper);
            //如果数据库中有该用户，则将该用户存入redis中
            if (dbUser != null) {
                return CommonResult.failed("用户名已存在，请重新注册！");
            } else {
                user.setCreateTime(LocalDateTime.now());
                user.setCollege(CollegeEnum.getCollegeCode(user.getCollege()));
                user.setMajor(MajorEnum.getCode(user.getMajor()));
                int i = userMapper.insert(user);
                if (i == 0) {
                    return CommonResult.failed("注册失败，请重试！");
                } else {
                    String dbUserJson = JSON.toJSONString(user);
                    redisTemplate.opsForValue().set("user:" + user.getUsername(), dbUserJson);
                    return CommonResult.success();
                }
            }
        } else {
            return CommonResult.failed("用户名已存在，请重新注册！");
        }
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUserByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User dbUser = userMapper.selectOne(wrapper);
        return CommonResult.success(dbUser);
    }

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户id
     * @return 用户信息 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUserById(Integer id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            return CommonResult.success(user);
        }
        return CommonResult.failed("用户id错误");
    }

    /**
     * 用户更新头像
     *
     * @param username 用户名
     * @param avatar   头像文件
     * @return 200:更新成功 500:更新失败
     */
    @Override
    public CommonResult<?> updateAvatar(String username, MultipartFile avatar) {
        //处理MultiPartFile
        String avatarUrl = "http://localhost:8080/avatar/" + avatar.getOriginalFilename();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User dbUser = userMapper.selectOne(wrapper);
        dbUser.setPhoto(avatarUrl);
        int i = userMapper.updateById(dbUser);
        if (i == 0) {
            return CommonResult.failed("更新失败");
        } else {
            String dbUserJson = JSON.toJSONString(dbUser);
            redisTemplate.opsForValue().set("user:" + username, dbUserJson);
            // TODO：将头像上传到本地文件夹 /avatar 2024/1/3
            return CommonResult.success();
        }
    }

    /**
     * 用户更新信息
     *
     * @param user   用户信息
     * @param avatar 头像文件
     * @return 200:更新成功 500:更新失败
     */
    @Override
    public CommonResult<?> updateUserInfo(User user, MultipartFile avatar) {
        User dbUser = userMapper.selectById(user.getId());
        dbUser.setName(user.getName());
        dbUser.setUsername(user.getUsername());
        dbUser.setAddress(user.getAddress());
        dbUser.setEmail(user.getEmail());
        dbUser.setPhone(user.getPhone());
        dbUser.setBirthday(user.getBirthday());
        //检测是否有头像
        if (avatar != null) {
            //调用更新头像方法
            updateAvatar(user.getUsername(), avatar);
        }
        int i = userMapper.updateById(dbUser);
        if (i == 0) {
            return CommonResult.failed("更新失败");
        } else {
            String dbUserJson = JSON.toJSONString(dbUser);
            redisTemplate.opsForValue().set("user:" + user.getUsername(), dbUserJson);
            return CommonResult.success();
        }
    }


    /**
     * 修改密码（发送手机验证码）
     *
     * @param verificationCode 验证码
     * @param user             用户信息
     * @return 200:发送成功且修改成功 500:发送失败或修改失败
     */
    @Override
    public CommonResult<?> updatePassword(String verificationCode, User user) {
        //获取redis中的验证码
        String code = redisTemplate.opsForValue().get("verificationCode:" + user.getPhone());
        //检测验证码是否正确
        if (code != null && code.equals(verificationCode)) {
            //验证码正确，修改密码
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("username", user.getUsername());
            User dbUser = userMapper.selectOne(wrapper);
            dbUser.setPassword(user.getPassword());
            int i = userMapper.updateById(dbUser);
            if (i == 0) {
                return CommonResult.failed("修改失败");
            } else {
                String dbUserJson = JSON.toJSONString(dbUser);
                redisTemplate.opsForValue().set("user:" + user.getUsername(), dbUserJson);
                return CommonResult.success();
            }
        }
        return CommonResult.failed("验证码错误!");
    }

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return 200:发送成功 500:发送失败
     */
    @Override
    public CommonResult<?> sendVerificationCode(String phone) {
        try {
            smsService.sendVerificationCode(phone);
            return CommonResult.success();
        } catch (ClientException e) {
            e.printStackTrace();
            return CommonResult.failed("发送失败");
        }
    }

    /**
     * 退出登录
     *
     * @param username 用户名
     * @return 200:注销成功 500:注销失败
     */
    @Override
    public CommonResult<?> logout(String username) {
        if (Boolean.TRUE.equals(redisTemplate.delete("user:" + username))) {
            return CommonResult.success();
        }
        return CommonResult.failed("注销失败");
    }

    /**
     * 账户彻底注销删除
     *
     * @param username         用户名
     * @param verificationCode 验证码
     * @return 200:注销成功 500:注销失败
     */
    @Override
    public CommonResult<?> deleteUser(String username, String verificationCode) {
        //获取redis中的验证码
        String code = redisTemplate.opsForValue().get("verificationCode:" + username);
        //检测验证码是否正确
        if (code != null && code.equals(verificationCode)) {
            //验证码正确，删除用户
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("username", username);
            User dbUser = userMapper.selectOne(wrapper);
            int i = userMapper.deleteById(dbUser.getId());
            if (i == 0) {
                return CommonResult.failed("注销失败");
            } else {
                redisTemplate.delete("user:" + username);
                return CommonResult.success();
            }
        }
        return CommonResult.failed("验证码错误!");
    }

    /**
     * 获取用户列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getUserList(Integer currentPage, Integer pageSize) {
        Page<User> page = new Page<>(currentPage, pageSize);
        Page<User> userPage = userMapper.selectPage(page, null);
        return CommonResult.success(userPage);
    }

    /**
     * 测试断路器
     *
     * @param id id
     * @return 200:调用成功 500:调用失败
     */
    @Override
    public CommonResult<?> testCircuitBreaker(Integer id) {
        if (id < 0) {
            throw new RuntimeException("id不能为负数");
        }
        String serialNumber = IdUtil.simpleUUID();
        return CommonResult.success(Thread.currentThread().getName() + "\t" + "调用成功，流水号：" + serialNumber);
    }

    /**
     * 按照用户登录的时间段按星期统计所有用户的活跃度
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getActiveUserByWeek() {
        List<AccessLog> accessLogs = accessLogMapper.selectList(null);
        Map<String, Integer> map = new TreeMap<>();
        // 初始化所有星期的数量为0
        map.put("星期一", 0);
        map.put("星期二", 0);
        map.put("星期三", 0);
        map.put("星期四", 0);
        map.put("星期五", 0);
        map.put("星期六", 0);
        map.put("星期日", 0);
        // 获取当前日期所在周的开始和结束日期
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
     * 按照用户提交问卷的时间段，每天刷一次，统计所有用户的活跃度，每四个时间段为一组
     *
     * @return 200:获取成功 500:获取失败
     */
    @Override
    public CommonResult<?> getActiveUserByDay() {
        List<SubmitSurveyRecord> submitSurveyRecords = submitSurveyRecordMapper.selectList(null);
        Map<String, Integer> map = new TreeMap<>();
        // 初始化所有时间段的数量为0
        map.put("0:00-6:00", 0);
        map.put("6:00-12:00", 0);
        map.put("12:00-18:00", 0);
        map.put("18:00-24:00", 0);
        // 获取当前时间
        LocalDate today = LocalDateTime.now().toLocalDate();
        for (SubmitSurveyRecord submitSurveyRecord : submitSurveyRecords) {
            LocalDateTime date = submitSurveyRecord.getSubmitTime();
            if (date.toLocalDate().equals(today)) {
                String hour = String.valueOf(date.getHour());
                if (Integer.parseInt(hour) < 6) {
                    hour = "0:00-6:00";
                } else if (Integer.parseInt(hour) < 12) {
                    hour = "6:00-12:00";
                } else if (Integer.parseInt(hour) < 18) {
                    hour = "12:00-18:00";
                } else {
                    hour = "18:00-24:00";
                }
                map.put(hour, map.get(hour) + 1);
            }
        }
        return CommonResult.success(map);
    }
}
