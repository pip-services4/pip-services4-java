package org.pipservices4.grpc.controllers;

@FunctionalInterface
public interface GrpcFunc<T, R> {
    void apply(T request, R responseObserver);
}
