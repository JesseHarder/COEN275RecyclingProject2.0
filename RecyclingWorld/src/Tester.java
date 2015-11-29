/**
 * Created by JHarder on 11/24/15.
 */
import Recycling.*;
import Recycling.RecyclingData.*;



import java.io.FileNotFoundException;

public class Tester {

    public static void main(String [] args) throws FileNotFoundException {
        RecyclingDemo RD = new RecyclingDemo();
    }

    // Main for testing RMOS.
    public static void main1(String [] args) throws FileNotFoundException {
        RecyclingMonitoringStation RMOS = new RecyclingMonitoringStation();
        RMOS.loadStatus();
        System.out.println(RMOS);
    }

    // Main for testing RCM.
    public static void main2(String [] args) throws FileNotFoundException {
        RecyclingMachine RCM = new RecyclingMachine();
        RCM.setPrice("Metal", 10.0);
        RCM.setPrice("Glass", 5.0);
        RCM.setPrice("Paper", 3.0);

        RCM.depositItem("Metal", 7.2);
        RCM.depositItem("Glass", 3.2);
        RCM.depositItem("Glass", 0.5);

        System.out.println(RCM);

        try {
            RCM.saveStatus();
        }
        catch(FileNotFoundException ex) {ex.printStackTrace();}

        RCM.empty();

        System.out.println(RCM);

        RCM.loadStatus();

        System.out.println(RCM);
    }
}
