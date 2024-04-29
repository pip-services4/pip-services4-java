package org.pipservices4.grpc.controllers;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.stub.StreamObserver;
import org.pipservices4.components.context.Context;
import org.pipservices4.data.query.*;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.Descriptor;

import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.data.query.PagingParams;
import org.pipservices4.grpc.dummies.*;
import org.pipservices4.grpc.sample.Dummy;
import org.pipservices4.grpc.sample.IDummyService;

public class DummyGrpcController extends GrpcController {
    private IDummyService _service;
    private int _numberOfCalls = 0;

    public DummyGrpcController() {
        super(DummiesGrpc.getServiceDescriptor());
        this._dependencyResolver.put("service", new Descriptor("pip-services-dummies", "service", "default", "*", "*"));
    }

    public int getNumberOfCalls() {
        return this._numberOfCalls;
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> incrementNumberOfCalls(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        this._numberOfCalls++;
        return next.startCall(call, headers);
    }

    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        super.setReferences(references);
        this._service = this._dependencyResolver.getOneRequired(IDummyService.class, "service");
    }

    private void getPageByFilter(DummiesPageRequest request, StreamObserver<DummiesPage> responseObserver) {
        var filter = FilterParams.fromValue(request.getFilterMap());
        var paging = PagingParams.fromValue(request.getPaging());

        try {
            var page = _service.getPageByFilter(Context.fromTraceId(request.getTraceId()), filter, paging);
            var reply = DummiesPage.newBuilder();

            page.getData().forEach(
                    (item) -> reply.addData(dummyToObject(item)).build()
            );

            responseObserver.onNext(reply.build());
            responseObserver.onCompleted();

        } catch (ApplicationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void getOneById(org.pipservices4.grpc.dummies.DummyIdRequest request,
                           io.grpc.stub.StreamObserver<org.pipservices4.grpc.dummies.Dummy> responseObserver) {

        var result = this._service.getOneById(
                Context.fromTraceId(request.getTraceId()),
                request.getDummyId()
        );

        responseObserver.onNext(this.dummyToObject(result));
        responseObserver.onCompleted();
    }

    public void create(org.pipservices4.grpc.dummies.DummyObjectRequest request,
                       io.grpc.stub.StreamObserver<org.pipservices4.grpc.dummies.Dummy> responseObserver) {

        var result = this._service.create(
                Context.fromTraceId(request.getTraceId()),
                dummyToObject(request.getDummy())
        );

        responseObserver.onNext(this.dummyToObject(result));
        responseObserver.onCompleted();
    }

    public void update(org.pipservices4.grpc.dummies.DummyObjectRequest request,
                       io.grpc.stub.StreamObserver<org.pipservices4.grpc.dummies.Dummy> responseObserver) {
        var result = this._service.update(
                Context.fromTraceId(request.getTraceId()),
                dummyToObject(request.getDummy())
        );

        responseObserver.onNext(this.dummyToObject(result));
        responseObserver.onCompleted();
    }

    public void deleteById(org.pipservices4.grpc.dummies.DummyIdRequest request,
                           io.grpc.stub.StreamObserver<org.pipservices4.grpc.dummies.Dummy> responseObserver) {
        var result = this._service.deleteById(
                Context.fromTraceId(request.getTraceId()),
                request.getDummyId()
        );

        responseObserver.onNext(this.dummyToObject(result));
        responseObserver.onCompleted();
    }

    private org.pipservices4.grpc.dummies.Dummy dummyToObject(Dummy dummy) {
        if (dummy == null)
            return org.pipservices4.grpc.dummies.Dummy.getDefaultInstance();

        return org.pipservices4.grpc.dummies.Dummy.newBuilder()
                .setId(dummy.getId())
                .setContent(dummy.getContent())
                .setKey(dummy.getKey()).build();
    }

    private Dummy dummyToObject(org.pipservices4.grpc.dummies.Dummy dummy) {
        return new Dummy(dummy.getId(), dummy.getKey(), dummy.getContent());
    }

    @Override
    public void register() {
        this.registerInterceptor(this::incrementNumberOfCalls);

        this.registerMethod(
                "get_dummies",
                null,
                // new ObjectSchema(true)
                //     .withOptionalProperty("paging", new PagingParamsSchema())
                //     .withOptionalProperty("filter", new FilterParamsSchema()),
                this::getPageByFilter
        );

        this.registerMethod(
                "get_dummy_by_id",
                null,
                // new ObjectSchema(true)
                //     .withRequiredProperty("dummy_id", TypeCode.String),
                this::getOneById
        );

        this.registerMethod(
                "create_dummy",
                null,
                // new ObjectSchema(true)
                //     .withRequiredProperty("dummy", new DummySchema()),
                this::create
        );

        this.registerMethod(
                "update_dummy",
                null,
                // new ObjectSchema(true)
                //     .withRequiredProperty("dummy", new DummySchema()),
                this::update
        );

        this.registerMethod(
                "delete_dummy_by_id",
                null,
                // new ObjectSchema(true)
                //     .withRequiredProperty("dummy_id", TypeCode.String),
                this::deleteById
        );
    }
}
