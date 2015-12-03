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
        private ItemContainer itemContainer;

        public Session(){
            moneyOwed = 0;
            itemContainer = new ItemContainer();
            itemContainer.setWeightCapacity(Double.MAX_VALUE);
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

        public ItemContainer getItemContainer() {
            return itemContainer;
        }
        public void setItemContainer(ItemContainer itemContainer) {
            this.itemContainer = itemContainer;
        }
        public boolean depositItem(String name, double weight) {return itemContainer.depositItem(name,weight);}

    }

    private boolean active;
    private String ID;
    private String location;
    private MoneyManager moneyManager;
    private ItemContainer itemContainer;
    private HashMap<String, Double> priceList;
    private Session session;

    public static final double kilogramsPerPound = 0.453592;

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
    public double amountOfItemDepositedThisSession(String name) {
        double weight = 0.0;
        if (priceList.containsKey(name) && session.getItemContainer().containsItem(name))
            weight = session.itemContainer.amountOfItem(name);
        return weight;
    }
    public double priceForItemThisSession(String name) {
        double weight = amountOfItemDepositedThisSession(name);
        double cash = 0.0;
        if (weight != 0.0) cash = getPriceForAmountOfItem(name, weight);
        return cash;
    }

    /* Item Container Manipulation */

    public double getContentsWeight() {return itemContainer.getContentsWeight();}
    // Returns boolean for whether or not there was room to deposit.
    public boolean depositItem(String name, double weight) {
        boolean hasRoom = itemContainer.depositItem(name,weight);
        if (hasRoom) {
            session.addMoneyOwed(getPriceForAmountOfItem(name,weight));
            session.depositItem(name,weight);
        }
        return hasRoom;
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
    // Removes given item from the price list.
    // Returns boolean value for whether or not price was found in list.
    public boolean removePrice(String name) {
        if (priceList.containsKey(name)) {
            priceList.remove(name);
            return true;
        }
        return false;
    }
    public double getPriceForAmountOfItem(String name, double weight) {return getPrice(name) * weight;}

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
    public double withdrawCashAndStartNewSession() {
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

            writer.println(ID);                             // 1. Record machine ID.
            writer.println(location);                       // 2. Record machine location.
            writer.println(moneyManager.getCashReserves()); // 3. Record current cash reserves.

            // Record item holder info..
            writer.println(itemContainer.getWeightCapacity());  // 4. Record weight capactiy.
            writer.println(itemContainer.contentsString());     // 5. Record total machine contents.

            // Record current session information.
            writer.println(session.getMoneyOwed());                      // 6. Record money owed for session.
            writer.println(session.getItemContainer().contentsString()); // 7. Record contents of session.

            writer.close();
        }
        catch(IOException ex){ ex.printStackTrace();}
    }

    public void loadStatus() {
        try {
            FileInputStream fin = new FileInputStream(nameForSaveFile());
            Scanner sc = new Scanner(fin);

            ID = sc.nextLine();                             // 1. Read ID.
            location = sc.nextLine();                       // 2. Read location.

            double cash = sc.nextDouble();                  // 3. Load money reserves.
            moneyManager.setCashReserves(cash);

            double weightCapactiy = sc.nextDouble();        // 4. Load weight limit.
            itemContainer.setWeightCapacity(weightCapactiy);

            String contentsString = sc.nextLine();          // 5. Load itemContainer contents.
            itemContainer.initWithContents(contentsString);

            double owed = sc.nextDouble();                  // 6. Load money owed for session.
            session.setMoneyOwed(owed);

            String sessionContentsString = sc.nextLine();   // 7. Load session contents.
            session.getItemContainer().initWithContents(sessionContentsString);

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
        val = val + "Ammount owed for session: " + session.getMoneyOwed() + "\n";

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

        setCashReserves(100.0);
    }

    /* Helper Functions */

    public static String formatMoneyAmount(double amount) {
        return RecHelper.formatDoubleAmount(amount, 2);
    }

        /* Metric Unit Conversion Stuff */
    // Returns killograms for given number of pounds.
    public static double kilogramsForPounds(double lbs) {
        return kilogramsPerPound * lbs;
    }

    public static double convertPriceToPricePerKilogram(double price) {
        return price / kilogramsPerPound;
    }
}
