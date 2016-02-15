package app.robo.fhv.roboapp.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import app.robo.fhv.roboapp.communication.GlobalSettings;

public class SharedPreferencesPersistence {

    private static SharedPreferencesPersistence instance;
    private final Context context;
    private static final String SHARED_PREFERENCES_ID = "at.fhv.itm14.fhvrobot";

    private SharedPreferencesPersistence (Context context) {
        this.context = context;
    }

    public static void createInstance(Context context) {
        if(instance != null) {
            throw new IllegalStateException("Only one instance allowed!");
        }

        instance = new SharedPreferencesPersistence(context);
    }

    public static SharedPreferencesPersistence getInstance() {
        if(instance == null) {
            throw new IllegalStateException("Create a new instance first!");
        }

        return instance;
    }

    public static boolean isInstanceCreated() {
        return (instance != null);
    }

    public long getLastLoginTime() {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_ID, Context.MODE_PRIVATE);
        return prefs.getLong("lastLoginTime", 0);
    }

    public void persistLastLoginTime(long loginTime) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong("lastLoginTime", loginTime);
        edit.commit();
    }

    public String getLoginName() {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_ID, Context.MODE_PRIVATE);
        return prefs.getString("lastLoginName", "");
    }

    public void persistLoginName(String name) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("lastLoginName", name);
        edit.commit();
    }

    public String getServerAddress() {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_ID, Context.MODE_PRIVATE);
        return prefs.getString("serverAddress", GlobalSettings.SERVER_ADDRESS);
    }

    public void persistServerAddress(String serverAddress) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("serverAddress", serverAddress);
        edit.commit();
    }
}
