package com.ervelus;

import com.ervelus.infrastructure.Application;
import com.ervelus.infrastructure.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args){
        Map<Class, Class> beanMap = new HashMap<>();
        ApplicationContext context = Application.run("com.ervelus", beanMap);
    }
}
