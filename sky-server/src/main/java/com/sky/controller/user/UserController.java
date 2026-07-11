package com.sky.controller.user;


import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequestMapping("/user/user")
@RestController
@Slf4j
public class UserController {


    @Autowired
    UserService userService;
    @Autowired
    JwtProperties jwtProperties;//jwt 配置属性类 存放了提前配置好的jwt基础
    @PostMapping("/login")
    public Result<UserLoginVO> weChatLogin(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户请求登录：{}" ,userLoginDTO.getCode());
        //微信登录 调用service 层返回一个user对象。
        User user =  userService.weChatLogin(userLoginDTO);

        //成功返回user对象之后，构造userLoginVO对象，返回给前端，userLogin对象需要构造jwt token, 唯一id , 微信服务器构造的唯一用户标识openid

        //构造jwt token 传入配置的密钥，失效时间，和构造的map集合claims
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);

        //build构造对象属性
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }

}
