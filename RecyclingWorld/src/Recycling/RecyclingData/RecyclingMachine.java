package Recycling.RecyclingData;

import java.io.*;
import java.util.*;

/**
 * Created by JHarder on 11/11/15.
 */
public class RecyclingMachine {
    // Used to track individual uses of the machine between emptying.
    class Session {
        private double moneyOwed;

        public Session(){
            moneyOwed = 0;
        }

        public double getMoneyOwed(){
            return moneyOwed;
        }

        public void setMoneyOwed(double amount){
            moneyOwed = amount;
        }

        public void addMoneyOwed(double amount){
            moneyOwed = moneyOwed + amount;
        }
    }

    private boolean active;
    private String ID;
    private String location;
    private MoneyManager moneyManager;
    private ItemContainer itemContainer;
    private HashMap<String, Double> priceList;
    private Session session;

    /* Constructors */
    public RecyclingMachine () {
        // Using below constructor in default constructor.
        this (
                false,                  // Default active status.
                "Default ID",           // Default RCM ID.
                "Default Location",     // Default RCM location.
                0.0,                    // Default money amount.
                100.0                   // Default weight capacity.
                // No default info needed for priceList.
        );
    }

    public RecyclingMachine (boolean active, String ID, String location, double moneyAmount, double weightCapacity) {
        this.active = active;
        this.ID = ID;
        this.location = location;
        moneyManager = new MoneyManager(moneyAmount);
        itemContainer = new ItemContainer(weightCapacity);
        priceList = new HashMap<String, Double>();
        startNewSession();
    }

    /* Getters and Setters */
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public MoneyManager getMoneyManager() {
        return moneyManager;
    }
    public void setMoneyManager(MoneyManager moneyManager) {
        this.moneyManager = moneyManager;
    }

    public ItemContainer getItemContainer() {
        return itemContainer;
    }
    public void setItemContainer(ItemContainer itemContainer) {
        this.itemContainer = itemContainer;
    }

    public double getWeightCapacity() {return itemContainer.getWeightCapacity();}
    public void setWeightCapacity(double wc) {itemContainer.setWeightCapacity(wc);}

    public HashMap<String, Double> getPriceList() {
        return priceList;
    }
    public void setPriceList(HashMap<String, Double> priceList) {
        this.priceList = priceList;
    }

    /* Session management */
    public void startNewSession() {session = new Session();}

    /* Item Container Manipulation */

    public double getContentsWeight() {return itemContainer.getContentsWeight();}
    public void depositItem(String name, double weight) {
        itemContainer.depositItem(name,weight);
        session.addMoneyOwed(priceForAmountOfItem(name,weight));
    }
    public void empty() {
        Statistics.logEmpty(this.getID());
        itemContainer.empty();
        session = new Session();
    }

    /* Price List Management */

        // Getting and Setting Prices
    public double getPrice(String name) {return priceList.get(name);}
    public void setPrice(String name, Double price) {priceList.put(name, price);}
    public double priceForAmountOfItem(String name, double weight) {return getPrice(name) * weight;}

    /* Money Manipulation */
    public double getCashReserves() {
        return moneyManager.getCashReserves();
    }
    public void setCashReserves(double amount) {
        moneyManager.setCashReserves(amount);
    }
    public void depositMoney(double amount) {
        moneyManager.deposit(amount);
    }

    /* Withdrawing money owed to user */

    // Called to see if machine has the cash to provide for the amount
    //      deposited in the current session.
    // Used to determine if machine provides cash or coupons.
    public boolean canWithdrawCashForSession() {
        return session.getMoneyOwed() <= getCashReserves();
    }

    // Called to see what is owed for the current session.
    public double amountOwedForSession () {return session.getMoneyOwed();}

    // Called to potentially reduce cash reserves in machine and start a new session.
    //      Also returns amount owed to user, because why not.
    public double withdrawCashForSession() {
        double amount = amountOwedForSession();

        if (canWithdrawCashForSession()) moneyManager.withdraw(amount);

        startNewSession();

        return amount;
    }

    public double valueOfContents() {
        double total = 0.0;
        for (Map.Entry<String,Double> entry:itemContainer.getContents().entrySet()) {
            String name = entry.getKey();
            double weight = entry.getValue();
            if (priceList.containsKey(name)) {
                total += weight * priceList.get(name);
            }
        }
        return total;
    }

        // Clearing the price List
    public void clearPriceList() {priceList.clear();}

    /* Persistence */
    public String nameForSaveFile() {
        return "RCMs/RCM" + ID + ".txt";
    }

    public void saveStatus() throws FileNotFoundException {
        try {
            PrintWriter writer = new PrintWriter(nameForSaveFile());

            writer.println(ID);
            writer.println(location);
            writer.println(moneyManager.getCashReserves());

            for (Map.Entry entry:itemContainer.getContents().entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }

            writer.close();
        }
        catch(IOException ex){ ex.printStackTrace();}
    }

    public void loadStatus() {
        try {
            FileInputStream fin = new FileInputStream(nameForSaveFile());
            Scanner sc = new Scanner(fin);
            ID = sc.nextLine();
            location = sc.nextLine();
            double cash = sc.nextDouble();
            moneyManager.setCashReserves(cash);
            while (sc.hasNext())
            {
                String itemString = sc.next();
                String[] parts = itemString.split(",");
                String name = parts[0];
                double weight = Double.parseDouble(parts[1]);
                itemContainer.depositItem(name,weight);
            }

            fin.close();
        }
        catch (IOException ex){ ex.printStackTrace();}
    }

    /* To String Method */
    public String toString() {
        String val = "--RecyclingMachine--\n";
        val = val + "Active: " + active + "\n";
        val = val + "ID: " + ID + "\n";
        val = val + "Location: " + location + "\n";
        val = val + moneyManager + "\n";

        val = val + "Prices:\n";
        for (Map.Entry<String,Double> entry:priceList.entrySet()) {
            val = val + "\t" + entry.getKey() + " - " + entry.getValue() + "\n";
        }

        val = val + "Contents: \n" + itemContainer;

        return val;
    }

    /* Setting up a state for testing other things with this RCM */
    public void testPrep() {
        setPrice("Stuff", 5.0);
        setPrice("Things",2.5);
        setPrice("Goodies",7.5);

        depositItem("Stuff",3.0);
        depositItem("Things",5.0);
    }
}
