package Recycling.RecyclingData;

/**
 * Created by JHarder on 11/11/15.
 */
public class RecyclableItem {
    // Properties
    private String name;
    private double weight;

    /* Constructors */

    public RecyclableItem() {
        name = "";
        weight = 1.0;
    }

    public RecyclableItem (String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    /* Getters and Setters */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
