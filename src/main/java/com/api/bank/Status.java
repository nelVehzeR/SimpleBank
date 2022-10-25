package com.api.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Status {
    @JsonProperty("status")
    private int status;

    public Status setStatus(int status) {
        this.status = status;
        return this;
    }
}
