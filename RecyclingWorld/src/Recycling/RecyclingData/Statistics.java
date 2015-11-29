package Recycling.RecyclingData;

import java.sql.*;

public class Statistics{

        public Statistics() {
                Connection c = null;
                try {
                        Class.forName("org.sqlite.JDBC");
                        c = DriverManager.getConnection("jdbc:sqlite:stats.db");
                } catch (Exception e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                        System.exit(0);
                }
                System.out.println("Opened database successfully");
        }

        public void makeTables(){
                Connection c = null;
                Statement stmt = null;
                try {
                        Class.forName("org.sqlite.JDBC");
                        c = DriverManager.getConnection("jdbc:sqlite:stats.db");
                        System.out.println("Opened database successfully");

                        stmt = c.createStatement();
                        String sql = "CREATE TABLE  IF NOT EXISTS RCMS " +
                                "(MACHINEID INT PRIMARY KEY," +
                                " LOCATION           TEXT, " +
                                " MONEY            REAL, " +
                                " CAPACITY        INT, " +
                                " LOAD         INT,"+
                                "ACTIVE INT);"+
                                "CREATE TABLE IF NOT EXISTS TRANSACTIONS"+
                                "(MACHINEID INT,"+
                                "DATE_M INT,"+
                                "DATE_D INT,"+
                                "DATE_Y INT,"+
                                "ITEM_TYPE TEXT,"+
                                "ITEM_WEIGHT INT,"+
                                "MONEY REAL,"+
                                "FOREIGN KEY (MACHINEID) REFERENCES RCM(MACHINEID),"+
                                "PRIMARY KEY(MACHINEID,DATE_M,DATE_D,DATE_Y,ITEM_TYPE,ITEM_WEIGHT));";
                        System.out.println(sql);
                        stmt.executeUpdate(sql);
                        stmt.close();
                        c.close();
                }
                catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                }
                System.out.println("Tables created successfully.");
        }

}