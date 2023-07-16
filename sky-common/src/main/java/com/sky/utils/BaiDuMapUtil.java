package com.sky.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xb
 * @description TODO
 * @create 2023-07-16 15:24
 * @vesion 1.0
 */
@Component
public class BaiDuMapUtil {

    @Value("${sky.shop.address}")
    String address;
    @Value("${sky.baidu.ak}")
    String ak;

    public static final String url = "https://api.map.baidu.com/geocoding/v3";

    /**
     * 校验两地距离是否大于5km 大于返回true 小于返回false
     * @param userAddress
     * @return
     */
    public boolean checkDistance(String userAddress){
        Map<String, String> map = new HashMap<>();
        map.put("address",address);
        map.put("output", "json");
        map.put("ak",ak);
        String shopJson = HttpClientUtil.doGet(url, map);
        JSONObject shop = JSONObject.parseObject(shopJson);
        System.out.println(shopJson);
        // 获取result对象
        JSONObject result = shop.getJSONObject("result");
        // 获取location对象
        JSONObject location = result.getJSONObject("location");
        // 获取lng值
        double shopLng = location.getDoubleValue("lng");
        double shopLat = location.getDoubleValue("lat");



        map.put("address",userAddress);
        map.put("output", "json");
        map.put("ak",ak);
        String userJson = HttpClientUtil.doGet(url,map);
        System.out.println(userJson);
        JSONObject user = JSONObject.parseObject(userJson);
        // 获取result对象
        JSONObject userResult = user.getJSONObject("result");
        // 获取location对象
        JSONObject userLocation = userResult.getJSONObject("location");
        // 获取lng值
        double userLng = userLocation.getDoubleValue("lng");
        double userLat = userLocation.getDoubleValue("lat");
        double distance = DistanceUtils.getDistance(shopLng, shopLat, userLng, userLat);
        if(distance>5000){
            return true;
        }
        return false;
    }

}
