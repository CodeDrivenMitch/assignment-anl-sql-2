package com.insidion.anl.opdracht.twee;

import com.insidion.anl.opdracht.twee.enums.StatType;
import com.insidion.anl.opdracht.twee.ui.ManagerForm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mitchell on 5/25/2014.
 */
public class Controller  {
    public static final String connectionInfo = "jdbc:mysql://web.insidion.com/anl?user=anl&password=2675132";
    private Map<StatType,List<SqlRunnable>> currentThreads;
    private Map<StatType, Integer> nOfThreads;

    private ManagerForm window;


    public Controller() {

        this.currentThreads = new HashMap<StatType, List<SqlRunnable>>();
        this.nOfThreads = new HashMap<StatType, Integer>();

        for(StatType type : StatType.values()) {
            this.currentThreads.put(type, new LinkedList<SqlRunnable>());
            this.nOfThreads.put(type, 0);
        }
    }


    public synchronized void callback(SqlRunnable sqlRunnable, Exception e, boolean readError) {
        StatType oldStatType = sqlRunnable.getStatType();
        // Remove runnable from list
        this.currentThreads.get(oldStatType).remove(sqlRunnable);


        for (int i = 0; i < nOfThreads.get(oldStatType) - currentThreads.get(oldStatType).size(); i++) {
            startRunnable(oldStatType);
        }

        if(readError) {
            window.reportOccurrence(oldStatType);
        }

        if (e != null) {
            if(e.getMessage().equals("Deadlock found when trying to get lock; try restarting transaction")) {
                window.reportOccurrence(StatType.Deadlock);
            } else {
                e.printStackTrace();
            }
        }

    }

    public void startRunnable(StatType type) {
        SqlRunnable runnable = new SqlRunnable(this, connectionInfo, type);
        this.currentThreads.get(type).add(runnable);
        new Thread(runnable).start();
    }

    public void decreaseNumberOfThreads(StatType type) {
        this.nOfThreads.put(type, this.nOfThreads.get(type) -1);
    }


    public void increaseNumberOfThreads(StatType type) {
        this.nOfThreads.put(type, this.nOfThreads.get(type) +1);
        startRunnable(type);
    }

    public void setWindow(ManagerForm form) {
        this.window = form;
    }

}
