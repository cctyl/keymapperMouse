package com.example.keymappermouse.util;

@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
