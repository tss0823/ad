package com.usefullc.solm.common.proxy.nio;

import com.usefullc.solm.common.proxy.nio.parse.ReqParse;

import java.nio.channels.SelectionKey;
import java.util.*;
import java.util.concurrent.locks.*;

/**
 * Created by shengshan.tang on 2015/12/2 at 21:49
 */
public class NioDataConfig {

    static Lock lock = new ReentrantLock();

    private static Map<String,ReqParse> reqParseMap = new HashMap<>();

    public static void initReqParse(SelectionKey key){
        lock.lock();
        try{
            if(reqParseMap.get(key.toString()) == null){
                reqParseMap.put(key.toString(),new ReqParse(key));
            }
        }catch (Exception e){

        }finally {
            lock.unlock();
        }

    }



    public static ReqParse getReqParse(String key){
        return reqParseMap.get(key);
    }

    public static void clearReqParse(String key){
        reqParseMap.remove(key);
    }
}
