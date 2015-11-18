package com.dbsearch.app.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dbsearch.app.model.ClassModel;
import com.dbsearch.app.model.ClassType;

import java.util.List;

public class JsonUtils {

    public static <T> String json(T classModel){
        if (classModel == null)
            return "";
        try {
            return JSON.toJSONString(classModel);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static <T> T parse(String json, Class<T> clazz){
        if (TextUtils.isEmpty(json))
            return null;
        try {
            return JSON.parseObject(json, clazz);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static ClassModel parseClass(String json){
        return parse(json, ClassModel.class);
    }

    public static String jsonClass(ClassModel classModel){
        return json(classModel);
    }

    public static List<String> parseNoteType(String json){
        ClassType type = parse(json, ClassType.class);
        if (type == null)
            return null;
        return type.getTypes();
    }

    public static String jsonClassType(ClassType type){
        return json(type);
    }

}
