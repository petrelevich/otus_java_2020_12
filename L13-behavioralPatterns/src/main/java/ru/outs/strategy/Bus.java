package ru.outs.strategy;

/**
 * @author sergey
 * created on 11.09.18.
 */
public class Bus implements Strategy {
    @Override
    public void transportation() {
        System.out.println("доехать на автобусе");
    }
}
