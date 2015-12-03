package Recycling.RecyclingData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by JHarder on 11/24/15.
 */
public class RecyclingMonitoringStation {
    private List<RecyclingMachine> machines;
    private Map<String,String> userPaswordMap;
    private HashMap<String, Double> priceList;

    public static final String nameForSaveFile = "RMOS Save.txt";

    public RecyclingMonitoringStation() {
        machines = new ArrayList<RecyclingMachine>();
        userPaswordMap = new HashMap<String,String>();
        priceList = new HashMap<String, Double>();
    }

    /* Getters and Setters */
    public List<RecyclingMachine> getMachines() {return machines;}

    public Map<String,String> getUserPasswordMap() {return userPaswordMap;}

    public HashMap<String, Double> getPriceList() {
        return priceList;
    }
    public void setPriceList(HashMap<String, Double> priceList) {
        this.priceList = priceList;
    }

    /* Price List Management */

    // Getting and Setting Prices
    public double getPrice(String name) {return priceList.get(name);}

    public void setPrice(String name, Double price) {
        priceList.put(name, price);

        // Add price to each machine's price list.
        for (RecyclingMachine RCM:machines) {
            RCM.setPrice(name, price);
        }
    }

    // Removes given item from the price list.
    // Returns boolean value for whether or not price was found in list.
    public boolean removePrice(String name) {
        if (priceList.containsKey(name)) {
            priceList.remove(name);
            for (RecyclingMachine RCM:machines) {
                RCM.removePrice(name);
            }
            return true;
        }
        return false;
    }

    /* Machine Manipulation */

    public String newID() {
        boolean used = true;
        String ID;
        Random rand = new Random();

        do {
            used = false;
            int val = rand.nextInt(Integer.MAX_VALUE);
            ID = String.valueOf(val);

            // Check that ID is not currently used.
            for (RecyclingMachine RCM:machines) {
                if (ID.equals(RCM.getID())) {
                    used = true;
                    break;
                }
            }
        } while (used); // Keep trying until new ID is obtained.

        return ID;
    }

    public String newLocation() {
        boolean used = true;
        String location;
        Random rand = new Random();

        do {
            used = false;
            int val = rand.nextInt(1000);
            location = "Room " + String.valueOf(val);

            // Check that ID is not currently used.
            for (RecyclingMachine RCM:machines) {
                if (location.equals(RCM.getID())) {
                    used = true;
                    break;
                }
            }
        } while (used); // Keep trying until new ID is obtained.

        return location;
    }

    // Adds a new machine to the list of machines managed by this RMOS.
    public void addMachine() {
        RecyclingMachine RCM = new RecyclingMachine();
        RCM.setID(newID());
        RCM.setLocation(newLocation());
        // Copy over the price list.
        for (Map.Entry<String,Double> entry:priceList.entrySet()) {
            RCM.setPrice(entry.getKey(), entry.getValue());
        }
        machines.add(RCM);
    }

    // Removes the first machine with the given ID, if found, from the list.
    // Returns boolean representing whether or not a machine was removed.
    public boolean removeMachineWithID(String ID) {
        for (int i = 0; i < machines.size(); i++) {
            RecyclingMachine RCM = machines.get(i);
            if (ID.equals(RCM.getID())) {
                machines.remove(i);
                return true;
            }
        }

        return false;
    }

    // Removes the machine with the given index from the list if index is in bounds.
    // Returns boolean representing whether or not a machine was removed.
    public boolean removeMachineAtIndex(int index) {
        if (index < machines.size()) {
            machines.remove(index);
            return true;
        }
        return false;
    }

    /* Username and Password Verification */

    // Returns true if the given Username and Password are in the userPasswordMap.
    // Otherwise returns false.
    public boolean verifyCredentials(String username, String password) {
        if (!userPaswordMap.containsKey(username))  // Username not in map.
            return false;
        else    // Username is in map. Proceed.
        {
            if (password.equals(userPaswordMap.get(username)))  // Password matches.
                return true;
            else    // Password does not match.
                return false;
        }
    }

    /* Status Saving and Loading */

    public void saveStatus() {
        try {
            PrintWriter writer = new PrintWriter(nameForSaveFile);

            // 1. First line is Username and Password pairs.
            // There is a comma between each username and password.
            // There is a semicolon separating the pairs.
            if (userPaswordMap.isEmpty()){
                writer.print("no users");
            } else {
                for (Map.Entry<String,String> entry:userPaswordMap.entrySet()) {
                    writer.print(entry.getKey() + "," + entry.getValue() + ";");
                }
            }

            writer.print("\n");

            // 2. Second line is comma-separated list of RCM IDs.
            if (machines.isEmpty()) {
                writer.print("no machines");
            } else {
                for (RecyclingMachine RCM:machines) {
                    // Tell machines to save their status.
                    RCM.saveStatus();

                    // Record Machine IDs so they can load their status.
                    writer.print(RCM.getID());
                    writer.print(";");
                }
                for (int i = 0; i < machines.size(); i++) {

                }
            }
            writer.print("\n");

            // 3. Third line is item and price pairs.
            // There is a comma between each item name and price.
            // There is a semicolon separating the pairs.
            if (priceList.isEmpty()) {
                writer.print("no prices");
            } else {
                for (Map.Entry<String,Double> entry:priceList.entrySet()) {
                    writer.print(entry.getKey() + "," + entry.getValue() + ";");
                }
            }



            writer.close();
        }
        catch(IOException ex){ ex.printStackTrace();}
    }

    public void loadStatus() {
        try {
            FileInputStream fin = new FileInputStream(nameForSaveFile);
            Scanner sc = new Scanner(fin);

            // 1. Loading Usernames and passwords.
            String userPassString = sc.nextLine();
            if (!userPassString.equals("no users")) {
                String[] userPassPairs = userPassString.split(";");
                for (String pair:userPassPairs) {
                    String [] s = pair.split(",");
                    String username = s[0];
                    String password = s[1];
                    userPaswordMap.put(username,password);
                }
            }

            // 2. Loading RCMs.
            String IDString = sc.nextLine();
            if (!IDString.equals("no machines")) {
                String[] IDs = IDString.split(";");
                for (String ID:IDs) {
                    RecyclingMachine RCM = new RecyclingMachine();
                    if (!ID.equals("")) {
                        RCM.setID(ID);
                        RCM.loadStatus();
                        machines.add(RCM);
                    }
                }
            }

            // 3. Loading price list.
            String pricesString = sc.nextLine();
            if (!pricesString.equals("no prices")) {
                String[] pricePairs = pricesString.split(";");
                for (String pair:pricePairs) {
                    if (!pair.equals("")) {
                        String [] s = pair.split(",");
                        String itemName = s[0];
                        double price = Double.parseDouble(s[1]);
                        priceList.put(itemName,price);
                    }
                }
            }

            fin.close();
        }
        catch (IOException ex){ ex.printStackTrace();}
    }

    /* toString Mathod */
    @Override
    public String toString() {
        String str = "";

        // Passwords
        str = str + "Users - Password Pairs:\n";
        for (Map.Entry<String,String> entry:userPaswordMap.entrySet()) {
            str = str + entry.getKey() + " - " + entry.getValue() + "\n";
        }

        str = str + "\n";

        // Machines
        for (int i = 0; i < machines.size(); i++) {
            RecyclingMachine machine = machines.get(i);
            str = str + "--Machine " + (i+1) + "--\n" + machine.toString();
        }

        return str;
    }

    /* For Testing with this RMOS */
    public void testPrep() {

        addMachine();
        addMachine();
        addMachine();

        for (Integer i = 0; i < machines.size(); i++) {
            RecyclingMachine RCM = machines.get(i);
            RCM.setLocation("Location "+i);
            RCM.setCashReserves(100.0);
        }

        setPrice("Wood", 5.0);
        setPrice("Metal", 10.0);
        setPrice("Junk", 1.0);

        userPaswordMap.put("","");
        userPaswordMap.put("test","test");
    }

}
