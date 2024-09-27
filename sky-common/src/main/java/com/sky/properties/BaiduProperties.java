package com.sky.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
public class BaiduProperties {

    @Value("${sky.shop.address}")
    private String address;

    @Value("${sky.baidu.ak}")
    private String ak;
}
