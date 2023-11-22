package org.pipservices4.mysql.fixtures;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.pipservices4.data.data.IIdentifiable;
import org.pipservices4.data.keys.IdGenerator;

public class Dummy2 implements IIdentifiable<Long> {

    public Dummy2() {
    }

    public Dummy2(Long id, String key, String content) {
        super();
        this._id = id;
        this._key = key;
        this._content = content;
    }

    @JsonProperty("id")
    private Long _id;

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    @Override
    public Long withGeneratedId() {
        _id = Long.parseLong(IdGenerator.nextLong());
        return _id;
    }

    @JsonProperty("key")
    private String _key;

    public String getKey() {
        return _key;
    }

    public void setKey(String key) {
        this._key = key;
    }

    @JsonProperty("content")
    private String _content;

    public String getContent() {
        return _content;
    }

    public void setContent(String content) {
        this._content = content;
    }

}
