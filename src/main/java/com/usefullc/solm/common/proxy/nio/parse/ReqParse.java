package com.usefullc.solm.common.proxy.nio.parse;

import org.apache.commons.collections.CollectionUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.*;

/**
 * Created by shengshan.tang on 2015/12/1 at 17:32
 */
public class ReqParse {

    String headerMethods [] = {"GET","POST","OPTIONS","HEAD","PUT","DELETE","TRACE"};

    boolean first = true;

    int contentLen;

    int headerLen;

    int reqTotalLen;

    String curReqStr;

    StringBuilder sb = new StringBuilder();

    int loadedLen = 0;

    private SelectionKey key;

    private boolean parseState = true;



    public ReqParse(SelectionKey key) {
        this.key = key;
//        dbObjs = (int[]) key.attachment();
//        if(dbObjs != null){
//            first = false;
//            hasContentLen = true;
//        }
    }

    public int getLoadedLen() {
        return loadedLen;
    }

    public boolean isParseState() {
        return parseState;
    }

    public String getUrl(){
        //get req url
        String reqStr = sb.toString();
        int start = reqStr.indexOf("GET ");
        if (start == -1) {
            start = reqStr.indexOf("POST ");
            if(start == -1){
                return "not found,"+reqStr;
            }
            start+=5;
        }else{
            start += 4;
        }
        int end = reqStr.indexOf(" ", start);
        String url = reqStr.substring(start, end);
        return url;
    }



    public  void parse(int n,ByteBuffer buffer){
        if(n <= 0){
            return;
        }
        loadedLen +=n;
        curReqStr = new String(buffer.array(), 0, buffer.position());
        sb.append(curReqStr);

        if(first){  //第一次read 处理
            first = false;
            //处理头部

            int index = curReqStr.indexOf(" ");
            if(index == -1){
                System.err.println("not parse http req \n "+curReqStr);
                parseState = false;
                return;
            }
            String method = curReqStr.substring(0,index);
            List<String> methods = Arrays.asList(headerMethods);
            if(!methods.contains(method)){
                System.err.println("not http req \n "+curReqStr);
                parseState = false;
                return;
            }
            // c处理header
            if(curReqStr.indexOf("Content-Length:") > -1){
                int startLen = curReqStr.indexOf("Content-Length:") + 15;
                int endLen = curReqStr.indexOf("\r\n", startLen);
                contentLen =  Integer.valueOf(curReqStr.substring(startLen, endLen).trim());
                headerLen = curReqStr.indexOf("\r\n\r\n")+4;
//                                    System.err.println(Thread.currentThread().getName()+",Content-Length=" + contentLen+",headerLen="+headerLen);
                reqTotalLen = contentLen+headerLen;
//                hasContentLen = true;
            }
        }
    }

    public boolean isReadEnd(){
        //根据加载长度判断 （200）
        if(reqTotalLen > 0 && loadedLen >= reqTotalLen){
            return true;
        }
        //根据字符结尾判断（304）
        String eqStr = curReqStr.length() >= 4 ? curReqStr.substring(curReqStr.length()-4) : "";
//            System.err.println("curReqStr=" + eqStr);
        if(eqStr.equals("") || eqStr.equals("\r\n\r\n")){
//                                System.err.println(Thread.currentThread().getName()+" break r n; ");
            return true;
        }
        return false;
    }
}
