package Recycling.RecyclingData;

/**
 * Created by weysc on 11/30/2015.
 */
public class Session {
    private double moneyOwed;
    private String RCM_id;

    public Session(String id){
        moneyOwed = 0;
        RCM_id = id;
    }

    public double getMoneyOwed(){
        return moneyOwed;
    }

    public void setMoneyOwed(double d){
        moneyOwed = d;
    }

    public void addMoneyOwed(double d){
        moneyOwed = moneyOwed +d;
    }

    public String getRCM_id(){
        return RCM_id;
    }

    public void setRCM_id(String s){
        RCM_id = s;
    }
}

