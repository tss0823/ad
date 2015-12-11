package com.yuntao.solm.utils;

import com.yuntao.solm.constant.AppConstant;
import com.yuntao.solm.service.vo.AdType;
import com.yuntao.solm.service.vo.AdVo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by shengshan.tang on 2015/11/24 at 16:46
 */
public class DataUtils {

    public static List<AdVo> getDataList(){
        //获取数据文件路径
        String dataPath = ConfigUtils.getString(AppConstant.DATA_PATH);
        try {
            String content = FileUtils.readFileToString(new File(dataPath));
            List<AdVo> list = JsonUtils.json2List(content, AdVo.class);
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        List<AdVo> list = new ArrayList<>();
        AdVo adVo = new AdVo();
        adVo.setAdType(AdType.QR);
        adVo.setUrl("http://www.ccdidi.com/index.html");
        adVo.setClickWeight(2);
        adVo.setWaitTimeWeight(5);
        list.add(adVo);

        adVo = new AdVo();
        adVo.setAdType(AdType.QR);
        adVo.setUrl("http://www.ccdidi.com/index.html");
        adVo.setClickWeight(2);
        adVo.setWaitTimeWeight(5);
        list.add(adVo);

        String content = JsonUtils.object2Json(list);
        System.out.println(content);
    }
}
