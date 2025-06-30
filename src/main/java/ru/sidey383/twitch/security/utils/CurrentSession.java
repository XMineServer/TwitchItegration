package ru.sidey383.twitch.security.utils;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentSession {

    boolean required() default true;

}
