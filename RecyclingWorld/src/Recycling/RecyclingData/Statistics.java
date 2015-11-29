package Recycling.RecyclingData;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("SqlResolve")
public class Statistics{

        public Statistics() {
//                Connection c = null;
//                try {
//                        Class.forName("org.sqlite.JDBC");
//                        c = DriverManager.getConnection("jdbc:sqlite:stats.db");
//                } catch (Exception e) {
//                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
//                        System.exit(0);
//                }
//                System.out.println("Opened database successfully");
        }

        private static void query(String sql, String message){
            Connection c = null;
            Statement stmt = null;

            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:stats.db");
                System.out.println("Opened database successfully");

                stmt = c.createStatement();
                System.out.println(sql);
                stmt.executeUpdate(sql);
                stmt.close();
                c.close();
            }
            catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            System.out.println(message);}

        public static void makeTables() {

            String sql =
                    "CREATE TABLE IF NOT EXISTS TRANSACTIONS" +
                            "(ID TEXT," +
                            "DATE_M INT," +
                            "DATE_D INT," +
                            "DATE_Y INT," +
                            "ITEM_TYPE TEXT," +
                            "ITEM_WEIGHT REAL," +
                            "MONEY REAL);";
            query(sql,"Tables built.");
        }

        public static void logTransaction(String id, String item_type, double units, double price){

            double money_dispensed = price * units;
            java.util.Date date= new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int year = cal.get(Calendar.YEAR);
            String sql="INSERT INTO TRANSACTIONS VALUES ('"+id+"','"+month+"','"+day+"','"+year+"','"+item_type+"','"+units+"','"+money_dispensed+"');";
            query(sql,"Transaction logged to database.");
        }
}

