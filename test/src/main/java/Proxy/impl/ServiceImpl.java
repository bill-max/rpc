package Proxy.impl;

import Proxy.Service;

public class ServiceImpl implements Service {
    @Override
    public String send(String msg) {
        System.out.println("sending=====>");
        return msg+" is sent!";
    }
}
