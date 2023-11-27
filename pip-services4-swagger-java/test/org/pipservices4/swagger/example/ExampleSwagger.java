package org.pipservices4.swagger.example;

import org.pipservices4.swagger.example.controllers.DummyRestController;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.Referencer;
import org.pipservices4.components.refer.References;
import org.pipservices4.components.run.Closer;
import org.pipservices4.components.run.Opener;
import org.pipservices4.observability.count.LogCounters;
import org.pipservices4.observability.log.ConsoleLogger;
import org.pipservices4.http.controllers.HeartbeatRestController;
import org.pipservices4.http.controllers.HttpEndpoint;
import org.pipservices4.http.controllers.StatusRestController;
import org.pipservices4.swagger.controllers.SwaggerController;

import org.pipservices4.swagger.example.controllers.DummyCommandableHttpController;
import org.pipservices4.swagger.example.services.DummyService;

import java.util.List;
import java.util.concurrent.Semaphore;

public class ExampleSwagger {
    public static void main(String[] args) throws ConfigException {
        // Create components
        var logger = new ConsoleLogger();
        var service = new DummyService();
        var httpEndpoint = new HttpEndpoint();
        var restController = new DummyRestController();
        var httpController = new DummyCommandableHttpController();
        var statusController = new StatusRestController();
        var heartbeatController = new HeartbeatRestController();
        var swaggerController = new SwaggerController();

        var components = List.of(
                service,
                httpEndpoint,
                restController,
                httpController,
                statusController,
                heartbeatController,
                swaggerController,
                logger
        );

        // Configure components
        logger.configure(ConfigParams.fromTuples(
                "level", "trace"
        ));

        httpEndpoint.configure(ConfigParams.fromTuples(
                "connection.protocol", "http",
                "connection.host", "localhost",
                "connection.port", 8080
        ));

        restController.configure(ConfigParams.fromTuples(
                "swagger.enable", true
        ));

        httpController.configure(ConfigParams.fromTuples(
                "base_route", "dummies2",
                "swagger.enable", true
        ));

        try {
            // Set references
            var references = References.fromTuples(
                    new Descriptor("pip-services", "logger", "console", "default", "1.0"), logger,
                    new Descriptor("pip-services", "counters", "log", "default", "1.0"), new LogCounters(),
                    new Descriptor("pip-services", "endpoint", "http", "default", "1.0"), httpEndpoint,
                    new Descriptor("pip-services-dummies", "service", "default", "default", "1.0"), service,
                    new Descriptor("pip-services-dummies", "controller", "rest", "default", "1.0"), restController,
                    //new Descriptor("pip-services-dummies", "controller", "commandable-http", "default", "1.0"), httpController,
                    new Descriptor("pip-services", "status-controller", "rest", "default", "1.0"), statusController,
                    new Descriptor("pip-services", "heartbeat-controller", "rest", "default", "1.0"), heartbeatController,
                    new Descriptor("pip-services", "swagger-controller", "http", "default", "1.0"), swaggerController
            );

            Referencer.setReferences(references, components);

            Opener.open(null, components);

            System.out.println("Press Ctrl-C twice to stop the microservice...");

            // Signal handler
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Goodbye!");

                _exitEvent.release();

                // Runtime.getRuntime().exit(1);
            }));

            // Wait and close
            try {
                _exitEvent.acquire();
            } catch (InterruptedException ex) {
                // Ignore...
            }

            Closer.close(null, components);
            System.exit(0);
        } catch (Exception ex) {
            logger.error(null, ex, "Failed to execute the microservice");
            System.exit(1);
        }
    }

    private static final Semaphore _exitEvent = new Semaphore(0);
}
