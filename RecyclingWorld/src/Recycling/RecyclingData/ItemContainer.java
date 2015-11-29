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

    /* Adding and items and emptying*/

        // Adds item with "name" to holder with amount "weight".
    public void depositItem(String name, double weight) {
        if (!contents.containsKey(name)) {
            contents.put(name, weight);
        }
        else {
            double w = contents.get(name);
            w += weight;
            contents.put(name, w);
        }
    }
        // Adds amount of item to holder represented by the given "item".
    public void depositItem(RecyclableItem item) {
        depositItem(item.getName(), item.getWeight());
    }

        // Empties the item holder.
    public void empty() {
        /* Loop to remove all items from list. */
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
}
