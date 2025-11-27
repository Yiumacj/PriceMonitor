package com.pricemonitorbot.interfaces.service;

public interface IApi<T> {
    T getInfo(String url, String currency);
}
