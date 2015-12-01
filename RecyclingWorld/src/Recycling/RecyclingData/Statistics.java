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
                            "MONEY REAL,"+
                            "EMPTIED INT);";
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
            String sql="INSERT INTO TRANSACTIONS VALUES ('"+id+"','"+month+"','"+day+"','"+year+"','"+item_type+"','"+units+"','"+money_dispensed+"','0');";
            update(sql,"Transaction logged to database.");
        }

    public static void logEmpty(String id){

        java.util.Date date= new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        String sql="INSERT INTO TRANSACTIONS VALUES ('"+id+"','"+month+"','"+day+"','"+year+"','Emptied','0','0','1');";
        update(sql,"Transaction logged to database.");
    }



        public static String stringQuery(String sql, String message) throws SQLException, ClassNotFoundException {
            Connection c = null;
            Statement stmt = null;
            ResultSet rs;
            String answer = "No data yet.";
            try{Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stats.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            rs = stmt.executeQuery( sql );
            answer = rs.getString("ANSWER");
            rs.close();
            stmt.close();
            c.close();


//            System.out.println(message);
            }
            catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            return answer;

        }

    public static double doubleQuery(String sql, String message) throws SQLException, ClassNotFoundException {
        Connection c = null;
        Statement stmt = null;
        ResultSet rs;
        double answer =0;
        try{Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:stats.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            rs = stmt.executeQuery( sql );
            answer = rs.getDouble("TOTAL");
            rs.close();
            stmt.close();
            c.close();

//            System.out.println(message);
        }
        catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return answer;

    }


        public static double timesEmptied(String id) throws SQLException, ClassNotFoundException {
            String sql = "SELECT COUNT(*) AS TOTAL FROM TRANSACTIONS WHERE ID='"+id+"' AND EMPTIED='1'";
            double total = doubleQuery(sql,"Finding total number of empties performed on Machine ID "+id);
            return total;
        }

        public static double getTotalItemWeightByID(String id) throws SQLException, ClassNotFoundException {
            String sql = "SELECT SUM(ITEM_WEIGHT) AS TOTAL FROM TRANSACTIONS WHERE ID='"+id+"' AND EMPTIED='0'";
            double total = doubleQuery(sql,"Finding total money issued by Machine ID "+id);
            return total;
        }

        public static double getTotalMoneyByID(String id) throws SQLException, ClassNotFoundException {
            String sql = "SELECT SUM(MONEY) AS TOTAL FROM TRANSACTIONS WHERE ID='"+id+"' AND EMPTIED='0'";
            double total = doubleQuery(sql,"Finding total weight of items put into Machine ID "+id);
            return total;
        }

        public static String mostUsedByWeight() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(item_weight) AS TOTAL,ID AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL DESC LIMIT 1";
            String answer = stringQuery(sql,"Finding machine with most recycled by weight.");
            return answer;
        }

        public static double highestWeight() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(item_weight) AS TOTAL,ID AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL DESC LIMIT 1";
            double total = doubleQuery(sql,"Finding total weight of items put into Machine ID ");
            return total;
        }

        public static String mostUsedByPayout() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(money) AS TOTAL,ID AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL DESC LIMIT 1";
            String answer = stringQuery(sql,"Finding machine with most recycled by weight.");
            return answer;
        }

        public static double highestPayout() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(money) AS TOTAL,ID AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL DESC LIMIT 1";
            double total = doubleQuery(sql,"Finding total weight of items put into Machine ID ");
            return total;
    }

        public static String leastUsedByWeight() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(item_weight) AS TOTAL,ID AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL ASC LIMIT 1";
            String answer = stringQuery(sql,"Finding machine with most recycled by weight.");
            return answer;
        }

        public static double lowestWeight() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(item_weight) AS TOTAL,ID AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL ASC LIMIT 1";
            double total = doubleQuery(sql,"Finding total weight of items put into Machine ID ");
            return total;
        }

        public static String leastUsedByPayout() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(money) AS TOTAL,ID AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL ASC LIMIT 1";
            String answer = stringQuery(sql,"Finding machine with most recycled by weight.");
            return answer;
        }

        public static double lowestPayout() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(money) AS TOTAL,ID AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL ASC LIMIT 1";
            double total = doubleQuery(sql,"Finding total weight of items put into Machine ID ");
            return total;
        }

        public static String mostRecycledItem() throws SQLException, ClassNotFoundException {
            String sql = "SELECT DISTINCT SUM(ITEM_WEIGHT) AS TOTAL,ITEM_TYPE AS ANSWER FROM TRANSACTIONS GROUP BY ANSWER ORDER BY TOTAL DESC LIMIT 1";
            String answer = stringQuery(sql,"Finding machine with most recycled by weight.");
            return answer;
        }






}
