package com;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        JSONObject obj = JSONUtil.readJSONObject(
                FileUtil.file("D:\\IdeaProjects\\zmu-cloud\\admin\\src\\main\\test\\com\\1.json"),
                Charset.defaultCharset());
        List<String> lines = new ArrayList<>();
        obj.forEach((levelAngleStr, vertical) -> {
            int levelAngle = Integer.parseInt(levelAngleStr);
            JSONObject verObj = (JSONObject) vertical;
            verObj.forEach((angStr, disStr) -> {
                double ang = Double.parseDouble(angStr) / 100;
                int dis = (Integer) disStr;
                if (dis < 10000) {
                    double z = Math.cos(Math.toRadians(ang)) * dis;
                    double temp = Math.sin(Math.toRadians(ang)) * dis;
                    double x = Math.cos(Math.toRadians(levelAngle)) * temp;
                    double y = Math.sin(Math.toRadians(levelAngle)) * temp;
                    lines.add(String.format("%s %s %s", x, y, z));
                }
            });
        });
        FileUtil.writeLines(lines, "D:\\IdeaProjects\\zmu-cloud\\admin\\src\\main\\test\\com\\1.txt", Charset.defaultCharset());
    }
}
