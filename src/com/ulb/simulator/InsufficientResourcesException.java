package com.ulb.simulator;

public class InsufficientResourcesException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public InsufficientResourcesException() {
        super("The provider does not have enough resources to provide the consumer");
    }
}
