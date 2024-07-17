package com.spzx.user.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @classname spzx-parent
 * @Auther d3Lap1ace
 * @Time 17/7/2024 11:55 周三
 * @description
 * @Version 1.0
 * From the Laplace Demon
 */
public interface ISmsService {

    Boolean send(String phone, HashMap<String, String> map);
}
