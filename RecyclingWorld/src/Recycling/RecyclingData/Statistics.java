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

        private static void update(String sql, String message){
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


        public static void dropTables(){
            String sql = "DROP TABLE IF EXISTS TRANSACTIONS";
            update(sql,"Tables dropped. Starting from a fresh slate!");
        }

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
            update(sql,"Tables built. Ready to run statistics.");
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
            update(sql,"Transaction logged to database.");
        }

        public static ResultSet query(String sql, String message) throws SQLException, ClassNotFoundException {
            Connection c = null;
            Statement stmt = null;
            ResultSet rs;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stats.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            rs = stmt.executeQuery( sql );

            System.out.println(message);
            return rs;
        }


        public static double getTotalItemWeightByID(String id) throws SQLException, ClassNotFoundException {
            String sql = "SELECT SUM(ITEM_WEIGHT) AS TOTAL_WEIGHT FROM TRANSACTIONS WHERE ID='"+id+"'";
            ResultSet rs = query(sql,"Finding total weight of items put into Machine ID: "+id);
            double total_weight = rs.getDouble("TOTAL_WEIGHT");
            return total_weight;
        }

    public static double getTotalMoneyByID(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT SUM(MONEY) AS TOTAL_MONEY FROM TRANSACTIONS WHERE ID='"+id+"'";
        ResultSet rs = query(sql,"Finding total weight of items put into Machine ID: "+id);
        double total_money = rs.getDouble("TOTAL_MONEY");
        return total_money;
    }


}

