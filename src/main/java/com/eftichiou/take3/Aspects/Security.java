package com.eftichiou.take3.Aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Security {
    @Before("execution(* login(*))")
    public void checkToken() {
        System.out.println("-------------------> Checked");
    }
}
