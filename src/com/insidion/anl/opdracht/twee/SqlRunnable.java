package com.insidion.anl.opdracht.twee;

import com.insidion.anl.opdracht.twee.enums.StatType;

import java.sql.*;

import static java.lang.Thread.sleep;

/**
 * Created by mitchell on 5/25/2014.
 */
public class SqlRunnable implements Runnable {
    private String connectionInfo;
    private Connection connection;
    private Controller controller;
    private StatType statType;

    public SqlRunnable(Controller controller, String connectionInfo, StatType stat) {
        this.connectionInfo = connectionInfo;
        this.controller = controller;
        this.statType = stat;
    }

    @Override
    public void run() {
        try {
            prepareConnection();
            boolean readError = false;

            switch(this.statType) {
                case DirtyRead:
                    readError = dirtyReadProcedure();
                    break;
                case PhantomRead:
                    readError = phantomReadProcedure();
                    break;
                case UnrepeatableRead:
                    readError = unrepeatableReadProcedure();
                    break;
            }

            connection.close();

            controller.callback(this, null, readError);
        } catch (SQLException e) {

            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            controller.callback(this, e, false);
        } catch (InterruptedException e) {
            if (e.getMessage().equals("Deadlock found when trying to get lock; try restarting transaction"))
                e.printStackTrace();
        }
    }

    /**
     * Prepares the Mysql connection for the program
     *
     * @throws SQLException
     */
    private void prepareConnection() throws SQLException {
        this.connection = DriverManager.getConnection(connectionInfo);
        this.connection.setAutoCommit(false);
        this.connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
    }

    private boolean dirtyReadProcedure() throws SQLException, InterruptedException {

        int voorraadBefore = getCurrentVoorraad();
        int newM = (int) (Math.random() * (100 - voorraadBefore));

        Statement st = connection.createStatement();
        st.execute("INSERT INTO magazijnmutaties(product_id, verandering, omschrijving) VALUES" +
                "(1, " + newM + ", 'geleverd door Frank Biersma')");

        st.execute("UPDATE product SET aantal=" + (voorraadBefore + newM) + " WHERE id=1");


        //sleep((int)( Math.random() * 1000));
        connection.rollback();

        int newVoorraad = getCurrentVoorraad();

        return voorraadBefore != newVoorraad;
    }

    private boolean phantomReadProcedure() throws SQLException {
        Statement st = connection.createStatement();

        st.execute("SELECT id FROM magazijnmutaties WHERE product_id=1");
        ResultSet rs = st.getResultSet();
        rs.last();
        int before = rs.getRow();

        st.execute("SELECT id FROM magazijnmutaties WHERE product_id=1");
        rs = st.getResultSet();
        rs.last();

        int after = rs.getRow();
        return before != after;
    }

    private boolean unrepeatableReadProcedure() throws SQLException {

        Statement st = connection.createStatement();

        st.execute("SELECT aantal FROM product WHERE id=1");
        ResultSet rs = st.getResultSet();
        rs.first();
        int before = rs.getInt("aantal");

        st.execute("SELECT aantal FROM product WHERE id=1");
        rs = st.getResultSet();
        rs.first();

        int after = rs.getInt("aantal");
        return before != after;
    }


    private int getCurrentVoorraad() throws SQLException {
        Statement st = connection.createStatement();

        st.execute("SELECT aantal FROM product WHERE id=1 LIMIT 1");
        ResultSet rs = st.getResultSet();
        rs.first();
        int current = rs.getInt("aantal");
        st.close();

        return current;
    }

    public StatType getStatType() {
        return statType;
    }
}