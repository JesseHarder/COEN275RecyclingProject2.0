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

    /* Machine Manipulation */

    public String newID() {
        boolean used = false;
        String ID;

        do {
            Random rand = new Random();
            int val = rand.nextInt();
            ID = String.valueOf(val);

            // Check that ID is not currently used.
            for (RecyclingMachine RCM:machines) {
                if (ID.equals(RCM.getID())) {
                    used = true;
                    break;
                }
            }
        } while (!used); // Keep trying until new ID is obtained.

        return ID;
    }

    // Adds a new machine to the list of machines managed by this RMOS.
    public void addMachine() {
        RecyclingMachine RCM = new RecyclingMachine();
        RCM.setID(newID());
    }

    /* Status Saving and Loading */

    public void saveStatus() {
        try {
            PrintWriter writer = new PrintWriter(nameForSaveFile);

            // First line is Username and Password pairs.
            // There is a comma between each username and password.
            // There is a semicolon separating the pairs.
            for (Map.Entry<String,String> entry:userPaswordMap.entrySet()) {
                writer.print(entry.getKey() + "," + entry.getValue() + ";");
            }

            // Second line is comma-separated list of RCM IDs.
            for (int i = 0; i < machines.size(); i++) {
                // Tell machines to save their status.
                machines.get(i).saveStatus();

                // Record Machine IDs so they can load their status.
                writer.print(machines.get(i).getID());
                if (!(i < (machines.size()-1)))
                    writer.print(",");
            }
            writer.print("\n");



            writer.close();
        }
        catch(IOException ex){ ex.printStackTrace();}
    }

    public void loadStatus() {
        try {
            FileInputStream fin = new FileInputStream(nameForSaveFile);
            Scanner sc = new Scanner(fin);

            // Loading Usernames and passwords.
            String userPassString = sc.nextLine();
            String[] userPassPairs = userPassString.split(";");
            for (String pair:userPassPairs) {
                String [] s = pair.split(",");
                String username = s[0];
                String password = s[1];
                userPaswordMap.put(username,password);
            }

            // Loading RCMs.
            String IDString = sc.nextLine();
            String[] IDs = IDString.split(",");
            for (String ID:IDs) {
                RecyclingMachine RCM = new RecyclingMachine();
                RCM.setID(ID);
                RCM.loadStatus();
                machines.add(RCM);
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


}
