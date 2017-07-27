package com.alextest.crawler.exception;

/**
 * Created by alexdrum on 2017/7/24.
 */
public class SimpleException extends Exception{
    private String errorMessage;
    public SimpleException(String errorMessage){
        this.errorMessage = errorMessage;
    }
}
