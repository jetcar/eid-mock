package com.example.eidmock.dto;

import java.io.Serializable;

public class MidSessionSignature implements Serializable, Cloneable {

    private static final Long serialVersionUID = 1L;

    private String algorithm;
    private String value;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public MidSessionSignature clone() {
        try {
            return (MidSessionSignature) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}