package com.api.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Balance {
    @JsonProperty("balance")
    private String balance;

    public Balance setBalance(String balance) {
        this.balance = balance;
        return this;
    }
}
