package com.yuntao.solm;

import com.yuntao.solm.service.vo.AdType;
import com.yuntao.solm.service.vo.AdVo;
import com.yuntao.solm.task.AdTask;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by shengshan.tang on 2015/11/20 at 18:38
 */
public class SolmAdMain {

    static Logger log = org.slf4j.LoggerFactory.getLogger(SolmAdMain.class);

    private static List<AdVo> adVoList = new ArrayList<AdVo>();

    static {
        AdVo adVo = new AdVo();
        adVo.setAdType(AdType.QR);
        adVo.setUrl("http://www.ccdidi.com/index.html");
        adVo.setClickWeight(2);
        adVo.setWaitTimeWeight(5);
        adVoList.add(adVo);
    }

    public static void main(String[] args) {
        try{
            log.info("start ...");
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    //干掉01:00-05:00 这段时间
                    Date date = new Date();
                    String hour = DateFormatUtils.format(date,"HH");
                    int intHour = Integer.valueOf(hour);
                    if(intHour >= 1 && intHour <= 5){
                        log.info("this time not for task run");
                        return;
                    }
                    //高峰期，早上10点左右，下午2点左右，晚上11点左右
                    int tsize = 1;
                    String time = DateFormatUtils.format(date,"HHmm");
                    int intTime = Integer.valueOf(time);
                    if(intTime >= 930 && intTime <= 1030){
                        tsize = 2;
                    }
                    if(intTime >= 1330 && intTime <= 1430){
                        tsize = 2;
                    }
                    if(intTime >= 2230 && intTime <= 2330){
                        tsize = 2;
                    }
                    log.info("entry mainTask tsize="+tsize);
                    mainTaskRun(tsize);
                }
            };
            long delay = 1000 * 10;  //10秒钟后启动
            long stepTime = 1 * 60 * 60 * 1000;  //1个小时执行一次
            timer.schedule(timerTask, delay, stepTime);
        }catch (Exception e){
            log.error("ad failed",e);
        }
    }

    public static void mainTaskRun(int tsize){
        log.info("get init ad list size="+adVoList.size());
        //构建task队列
        Set<AdTask> taskSet = new HashSet<AdTask>();
        List<AdVo> newAdVoList = new ArrayList<AdVo>();

        //adType all 分别获取
        for(AdVo adVo : adVoList){
            AdType adType = adVo.getAdType();
            if(adType.equals(AdType.ALL)){
                AdVo newAdVo = (AdVo) adVo.clone();
                newAdVo.setAdType(AdType.QR);
                newAdVoList.add(newAdVo);
                adType = AdType.XF;
            }
            adVo.setAdType(adType);
            newAdVoList.add(adVo);
        }
        //clickWeight 分别获取
        for(AdVo adVo : adVoList){
            int clickWeight = adVo.getClickWeight();
            if(clickWeight > 1){
                for(int i = 1; i< clickWeight; i++){
                    AdVo newAdVo = (AdVo) adVo.clone();
                    newAdVoList.add(newAdVo);
                }
            }
        }
        log.info("get ad list size="+newAdVoList.size());

        //builder task
        for(AdVo adVo : newAdVoList){
            AdTask adTask = new AdTask();
            adTask.setId(UUID.randomUUID().toString());
            adTask.setAdType(adVo.getAdType());
            adTask.setUrl(adVo.getUrl());
            adTask.setOpenPageWaitTime(adVo.getWaitTimeWeight()*1000);
            taskSet.add(adTask);
        }

        //任务调度
        final long startMainTime = System.currentTimeMillis();
        ExecutorService ec = new ThreadPoolExecutor(tsize, tsize, 0, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>()){

            @Override protected void terminated() {
                super.terminated();
                //task close
                log.info("finish, take time = " + (System.currentTimeMillis() - startMainTime));
            }

            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                //single task finish

            }
        };

        //task add
        for(AdTask task : taskSet){
            ec.execute(task);
        }

        //shutdown wait all task fininshed!
        ec.shutdown();




    }
}
