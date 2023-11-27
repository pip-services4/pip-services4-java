package org.pipservices4.swagger.example.data;


import com.fasterxml.jackson.annotation.*;
import org.pipservices4.data.data.IStringIdentifiable;
import org.pipservices4.data.keys.IdGenerator;

public class Dummy implements IStringIdentifiable {
    private String _id;
    private String _key;
    private String _content;

    public Dummy() {
    }

    public Dummy(String id, String key, String content) {
        _id = id;
        _key = key;
        _content = content;
    }

    @JsonProperty("id")
    public String getId() {
        return _id;
    }

    public void setId(String value) {
        _id = value;
    }

    @Override
    public String withGeneratedId() {
        _id = IdGenerator.nextLong();
        return _id;
    }

    @JsonProperty("key")
    public String getKey() {
        return _key;
    }

    public void setKey(String value) {
        _key = value;
    }

    @JsonProperty("content")
    public String getContent() {
        return _content;
    }

    public void setContent(String value) {
        _content = value;
    }
}
