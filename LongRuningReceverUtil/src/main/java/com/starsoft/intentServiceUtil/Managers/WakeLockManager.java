package com.starsoft.intentServiceUtil.Managers;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by StarkinDG on 26.02.2017.
 */

public class WakeLockManager {

    private static WakeLockManager sWakeLockManager = null;
    private final String TAG = getClass().getSimpleName();
    private int countWakeLock;
    private PowerManager.WakeLock myWakeLock = null;
    private int clientServiceCount = 0;

    private WakeLockManager(Context context) {

        myWakeLock = this.createWakeLock(context);
    }

    public static void create(Context context) {

        if (sWakeLockManager == null) {
            sWakeLockManager = new WakeLockManager(context);
            sWakeLockManager.acquireWakeLock();
        }
    }

    public synchronized static boolean isCreate() {


        return sWakeLockManager != null;
    }

    /**
     * helper methods
     */
    public synchronized static int enterWakeLock() {

        assertSetup();
        return sWakeLockManager.enter();
    }

    public synchronized static int leaveWakeLock() {

        assertSetup();
        return sWakeLockManager.leave();
    }

    /**
     * Do not call this method directly.
     * Instead, start the client methods
     * for registration and unRegistration.
     */
    public synchronized static void killWakeLockManager() {

        if (WakeLockManager.isCreate()) {
            sWakeLockManager.emptyWakeLockManager();
        }
    }

    public synchronized static void registerAsClient() {

        assertSetup();
        sWakeLockManager.registerClientService();
    }

    public synchronized static void unRegisterAsClient() {

        assertSetup();
        sWakeLockManager.unRegisterClientService();
    }

    public synchronized int getCountWakeLock() {

        return countWakeLock;
    }

    private static void assertSetup() {

        if (!WakeLockManager.isCreate()) {
            throw new RuntimeException("You need to create WakeLockManager first");
        }
    }

    private synchronized int enter() {

        countWakeLock++;
        return countWakeLock;
    }

    private int leave() {

        if (countWakeLock == 0) {
            return countWakeLock;
        }
        countWakeLock--;
        if (countWakeLock == 0) {
            releaseWakeLock();
        }
        return countWakeLock;
    }

    private void acquireWakeLock() {

        myWakeLock.acquire();
    }

    private void releaseWakeLock() {

        if (myWakeLock.isHeld()) {

            myWakeLock.release();
        }
    }

    private PowerManager.WakeLock createWakeLock(Context context) {

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    private int registerClientService() {

        return ++clientServiceCount;
    }

    private int unRegisterClientService() {

        if (clientServiceCount == 0) {

            return clientServiceCount;
        }
        clientServiceCount--;
        if (clientServiceCount == 0) {
            emptyWakeLockManager();
        }
        return clientServiceCount;
    }

    private void emptyWakeLockManager() {

        countWakeLock = 0;
        releaseWakeLock();
        sWakeLockManager = null;
    }
}
