package com.sedefbas.sedef.email;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
@Service
public class EmailTestService implements Predicate<String> {

    @Override
    public boolean test(String s) {
        //koşulları daha sonra yazacagım
        return true;
    }

}
