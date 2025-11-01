package com.kingfish.langchain4j.tools;

import com.kingfish.langchain4j.config.TemperatureUnit;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author : haowl
 * @Date : 2025/11/1 10:38
 * @Desc :
 */
@Slf4j
public class WeatherTools {

    @Tool("返回给定城市的天气")
    public String getWeather(
            @P("应返回天气的城市") String city,
            TemperatureUnit temperatureUnit) {
        if (city.equals("北京")) {
            if (temperatureUnit == TemperatureUnit.CELSIUS) {
                return "25°C";
            } else if (temperatureUnit == TemperatureUnit.FAHRENHEIT) {
                return "77°F";
            } else {
                return "未知温度单位";
            }
        } else if (city.equals("上海")) {
            if (temperatureUnit == TemperatureUnit.CELSIUS) {
                return "28°C";
            } else if (temperatureUnit == TemperatureUnit.FAHRENHEIT) {
                return "82°F";
            } else {
                return "未知温度单位";
            }
        } else {
            return "未支持的城市";
        }

    }
}