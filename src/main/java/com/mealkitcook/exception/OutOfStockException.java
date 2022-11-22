package com.mealkitcook.exception;

//상품의 주문 수량보다 재고수가 적을때 발생시킬 exception
public class OutOfStockException extends RuntimeException{

    public OutOfStockException(String message){
        super(message);
    }
}
