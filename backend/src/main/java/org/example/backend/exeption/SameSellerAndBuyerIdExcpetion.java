package org.example.backend.exeption;

public class SameSellerAndBuyerIdExcpetion extends RuntimeException {
    public SameSellerAndBuyerIdExcpetion(String message) {
        super(message);
    }
}
