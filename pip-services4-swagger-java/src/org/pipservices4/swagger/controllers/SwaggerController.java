package org.pipservices4.swagger.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.http.controllers.ISwaggerController;
import org.pipservices4.http.controllers.RestController;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class SwaggerController extends RestController implements ISwaggerController {
    private final HashMap<String, String> _routes = new HashMap<>();

    public SwaggerController() {
        this._baseRoute = "swagger";
    }

    private InputStreamReader getFileStream(String fileName) {
        return new InputStreamReader(Objects.requireNonNull(SwaggerController.class.getClassLoader().getResourceAsStream(fileName)));
    }

    private String calculateContentType(String fileName) {
        var ext = Optional.of(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1)).get();

        return switch (ext) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            case "png" -> "image/png";
            default -> "text/plain";
        };
    }

    private boolean checkFileExist(String fileName) {
        return SwaggerController.class.getClassLoader().getResource(fileName) != null;
    }

    private String loadFileContent(String fileName) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(this.getFileStream(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultStringBuilder.toString();
    }

    private Response getSwaggerFile(ContainerRequestContext req) {
        var fileName = req.getUriInfo().getPathParameters().get("file_name").get(0).toLowerCase();
        //req.params.file_name.toLowerCase();

        if (!this.checkFileExist(fileName))
            return Response.status(404).build();


        return Response
                .status(200)
                .entity(this.loadFileContent(fileName))
                .header("Content-Type", this.calculateContentType(fileName))
                .build();
    }

    private Response getIndex(ContainerRequestContext req) {
        var content = this.loadFileContent("index.html");

        // Inject urls
        var urls = new ArrayList<Map<String, String>>();
        for (var prop : this._routes.keySet()) {
            var url = Map.of("name", prop, "url", _routes.get(prop));
            urls.add(url);
        }

        try {
            content = content.replace("[/*urls*/]", JsonConverter.toJson(urls));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }

        return Response.status(200).type(MediaType.TEXT_HTML_TYPE).entity(content).build();
    }

    private Response redirectToIndex(ContainerRequestContext req) {
        var url = req.getUriInfo().getRequestUri().toString();
        if (!url.endsWith("/")) url = url + '/';

        try {
            return Response.seeOther(new URI(url + "index.html")).status(301).build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String composeSwaggerRoute(String baseRoute, String route) {
        if (baseRoute != null && !baseRoute.equals("")) {
            if (route == null || route.equals(""))
                route = "/";
            if (!route.startsWith("/"))
                route = "/" + route;
            if (!baseRoute.startsWith("/"))
                baseRoute = "/" + baseRoute;
            route = baseRoute + route;
        }

        return route;
    }


    @Override
    public void register() {
        // A hack to redirect default base route
        var baseRoute = this._baseRoute;
        this._baseRoute = null;
        this.registerRoute(
                HttpMethod.GET, baseRoute, null, this::redirectToIndex
        );
        this._baseRoute = baseRoute;

        this.registerRoute(
                HttpMethod.GET, "/index.html", null, this::getIndex

        );

        this.registerRoute(
                HttpMethod.GET, "/{file_name}", null, this::getSwaggerFile

        );
    }

    @Override
    public void registerOpenApiSpec(String baseRoute, String swaggerRoute) {
        if (swaggerRoute == null)
            super.registerOpenApiSpec(baseRoute);
        else {
            var route = this.composeSwaggerRoute(baseRoute, swaggerRoute);
            baseRoute = baseRoute != null ? baseRoute : "default";
            this._routes.put(baseRoute, route);
        }
    }
}
