package com.example.eidmock.dto;

import java.io.Serializable;

public class MidSessionStatus implements Serializable {

    private static final Long serialVersionUID = 1L;

    private String state;
    private String result;
    private MidSessionSignature signature;
    private String cert;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public MidSessionSignature getSignature() {
        return signature == null ? null : signature.clone();
    }

    public void setSignature(MidSessionSignature signature) {
        this.signature = signature;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

}
