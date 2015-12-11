package com.usefullc.solm.common.proxy.nio;

/**
 * Created by shengshan.tang on 2015/12/1 at 15:27
 */
public class NioTest {

    public static void main(String[] args) {
        int i = 0;
        while(true){
            System.out.println(i++);
            if(i == 10){
                return;
            }
        }
    }
}
