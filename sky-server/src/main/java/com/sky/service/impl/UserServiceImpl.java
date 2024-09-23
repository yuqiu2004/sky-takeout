package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.constant.WeChatConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private WeChatProperties weChatProperties;

    @Resource
    private UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO code) {
        String openid = getOpenId(code.getCode());
        if(StringUtils.isBlank(openid)){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断是否为新用户
        User user = userMapper.getByOpenId(openid);
        if(user == null){
            // 自动进行注册
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    /**
     * 调用微信接口服务 获取用户的openId
     * @param code
     * @return
     */
    private String getOpenId(String code) {
        //调用微信接口服务，获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put(WeChatConstant.WX_KEY_APPID,weChatProperties.getAppid());
        map.put(WeChatConstant.WX_KEY_SECRET,weChatProperties.getSecret());
        map.put(WeChatConstant.WX_KEY_JS_CODE,code);
        map.put(WeChatConstant.WX_KEY_GRANT_TYPE,WeChatConstant.WX_GRANT_TYPE);
        String json = HttpClientUtil.doGet(WeChatConstant.WX_LOGIN_URL, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString(WeChatConstant.WX_KEY_OPEN_ID);
        return openid;
    }
}
