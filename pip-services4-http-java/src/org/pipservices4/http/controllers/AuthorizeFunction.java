package org.pipservices4.http.controllers;

@FunctionalInterface
public interface AuthorizeFunction<T, U, V> {
    V apply(T t, U u);
}
