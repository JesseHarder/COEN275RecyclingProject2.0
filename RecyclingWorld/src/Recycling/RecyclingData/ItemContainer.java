package Recycling.RecyclingData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JHarder on 11/11/15.
 */
public class ItemContainer {
    // The weight capacity of the holder.
    private double weightCapacity;
    // A map of item names to weight of that item in the holder.
    private HashMap<String, Double> contents;

    /* Constructors */

        /* Default constructor. */
    public ItemContainer() {
        this (100.0);
    }

    public ItemContainer(double weightCapacity) {
        this.weightCapacity = weightCapacity;
        contents = new HashMap<String,Double>();
    }

    /* Accessors and Mutators */
        // Weight capacity getter an setter;
    public double getWeightCapacity() {
        return weightCapacity;
    }
    public void setWeightCapacity(double weightCapacity) {
        this.weightCapacity = weightCapacity;
    }

        // Contents getter.
    public HashMap<String, Double> getContents() {return contents;}

        // Derived getter for total weight of contents.
    public double getContentsWeight() {
        double total = 0.0;

        for (Double weight: contents.values()) total = total + weight;

        return total;
    }

    public double getRemainingSpace() {return getWeightCapacity() - getContentsWeight();}

    /* Adding and items and emptying*/

        // Adds item with "name" to holder with amount "weight" if holder has remaining space.
        // Returns boolean for whether or not there was space.
    public boolean depositItem(String name, double weight) {
        if (weight < getRemainingSpace()) {
            if (!contents.containsKey(name)) {
                contents.put(name, weight);
            }
            else {
                double w = contents.get(name);
                w += weight;
                contents.put(name, w);
            }

            return true;
        } else {
            return false;
        }

    }

    public boolean containsItem(String name) {
        return contents.containsKey(name);
    }

        // Returns amount of a given item currently in the container.
    public double amountOfItem(String name) {
        double weight = 0.0;
        if (containsItem(name)) weight = contents.get(name);
        return weight;
    }

        // Empties the item holder.
    public void empty() {
        contents.clear();
    }

    /* To String Method */
    @Override
    public String toString() {
        String val = "Weight Capacity: " + weightCapacity + "\n";

        for (Map.Entry<String, Double> entry : contents.entrySet()) {
            val = val + "\t" + entry.getKey() + " - " + entry.getValue() + "\n";
        }

        return val;
    }

    /* Persistence stuff */
    public String contentsString() {
        String message;
        if (contents.isEmpty()) {
            message = "empty";
        } else {
            message = "";
            for (Map.Entry entry:getContents().entrySet()) {
                message += entry.getKey() + "," + entry.getValue() + ";";
            }
        }

        return message;
    }

    public void initWithContents(String contentsString) {
        if (!contentsString.equals("empty")) {
            String [] elements = contentsString.split(";");
            for (String element:elements) {
                String [] parts = element.split(",");
                String name = parts[0];
                String weightString = parts[1];
                double weight = Double.parseDouble(weightString);
                depositItem(name, weight);
            }
        }
    }
}
