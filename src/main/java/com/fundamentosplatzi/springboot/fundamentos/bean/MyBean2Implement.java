package com.fundamentosplatzi.springboot.fundamentos.bean;

public class MyBean2Implement implements MyBean {

    @Override
    public void print() {
        System.out.println("Saludo desde mi segunda implementación propia del segundo bean");
    }
}
