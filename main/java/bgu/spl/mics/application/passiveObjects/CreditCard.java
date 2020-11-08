package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class CreditCard implements Serializable {
    private AtomicInteger amount;
    private int number;

    public CreditCard(int amount, int number) {
        this.amount = new AtomicInteger(amount);
        this.number = number;
    }

    public int getAmount() {
        return amount.get();
    }

    public int getNumber() {
        return number;
    }

    public void setAmount(int amount) {
        int val;
        do {
            val = this.amount.get();
        } while (!this.amount.compareAndSet(val, amount));
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
