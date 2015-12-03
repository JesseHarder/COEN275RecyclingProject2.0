package Recycling.RecyclingData;

/**
 * Created by JHarder on 11/12/15.
 */
public class MoneyManager {
    private double cashReserves;

    /* Constructors */

    public MoneyManager () {
        this (0.0);
    }

    public MoneyManager (double amount) {
        cashReserves = amount;
    }

    /* Accessors and Mutators */
    public double getCashReserves() {
        return cashReserves;
    }

    public void setCashReserves(double cashReserves) {
        this.cashReserves = cashReserves;
    }

    /* Depositing  and Withdrawing */

    public boolean canWithdraw(double amount) {
        return  amount <= cashReserves;
    }

    public void deposit(double amount) {
        cashReserves += amount;
    }

    /* Reduces amount of cash in the manager by amount given if there is enough and returns true.
     * If there is not enough, returns false.
     */
    public boolean withdraw(double amount) {
        if (canWithdraw(amount)) {
            cashReserves -= amount;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Cash Reserves: " + cashReserves;
    }
}
