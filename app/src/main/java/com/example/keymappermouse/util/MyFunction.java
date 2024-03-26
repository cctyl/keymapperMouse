package com.example.keymappermouse.util;

@FunctionalInterface
public interface MyFunction<T, R> {
    R apply(T t);
}
