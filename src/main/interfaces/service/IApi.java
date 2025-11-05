package main.interfaces.service;

public interface IApi<T> {
    public T getInfo(String url, String currency);
}
