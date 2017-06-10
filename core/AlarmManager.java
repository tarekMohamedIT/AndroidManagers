package core;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import CSTime.DateTime;
import model_classes.Alarm;

/**
 * Created by tarek on 6/4/17.
 */

public class AlarmManager extends DatabaseManager {

    private ArrayList<Alarm> alarmsList;
    private Alarm activeAlarm;

    private String alarmKey = "act_alrm_key_local";
    private String sharedPrefsKey = "alarm_manager_prefs";

    private OnAlarmAdded onAlarmAdded;
    private OnAlarmUpdated onAlarmUpdated;
    private OnAlarmsListUpdated onAlarmsListUpdated;

    /**
     * A constructor to the AlarmManager class
     *
     * @param context The context of operation(Activity or fragment)
     */
    public AlarmManager(Context context) {
        super(context, "alarm12398462123");
        createAlarmsTable();
    }

    /**
     * global Setter for the alarm adding event
     *
     * @param onAlarmAdded the alarm adding event handler
     */
    public void setOnAlarmAdded(OnAlarmAdded onAlarmAdded) {
        this.onAlarmAdded = onAlarmAdded;
    }

    /**
     * global Setter for the alarm updating event
     *
     * @param onAlarmUpdated the alarm updating event handler
     */
    public void setOnAlarmUpdated(OnAlarmUpdated onAlarmUpdated) {
        this.onAlarmUpdated = onAlarmUpdated;
    }

    /**
     * global Setter for the alarms list updating event (triggered on reading data from the database)
     *
     * @param onAlarmsListUpdated the alarms list updating event handler
     */
    public void setOnAlarmsListUpdated(OnAlarmsListUpdated onAlarmsListUpdated) {
        this.onAlarmsListUpdated = onAlarmsListUpdated;
    }

    /**
     * Create the alarms table in the database
     * Used in the constructor
     */
    private void createAlarmsTable() {
        executeOrder("Create table if not exists alarms_table (key varchar primary key, alarmText varchar, timeFrom varchar, timeTo varchar);");
    }

    /**
     * gets all the alarms from the database and assigns the first alarm to run
     * @throws ParseException
     */
    private void updateAlarmsList() throws ParseException {
        Cursor cursor = executeQuery("Select * from alarms_table");
        SharedPreferences preferences = context.getSharedPreferences(sharedPrefsKey, Context.MODE_PRIVATE);

        if (alarmsList == null) alarmsList = new ArrayList<>();
        else alarmsList.clear();

        while (cursor.moveToNext()) {
            alarmsList.add(new Alarm(cursor.getString(0)
                    , cursor.getString(1)
                    , new DateTime(cursor.getString(2), DateTime.DEFAULT_DATE_TIME_24_HOUR)
                    , new DateTime(cursor.getString(3), DateTime.DEFAULT_DATE_TIME_24_HOUR)
            ));
        }

        Collections.sort(alarmsList);

        if (alarmsList.size() > 0) {
            activeAlarm = alarmsList.get(0);
            preferences.edit().putString(alarmKey, activeAlarm.getKey()).apply();
        }

        else {
            activeAlarm = null;
            preferences.edit().putString(alarmKey, "").apply();
        }

        if (onAlarmsListUpdated != null) onAlarmsListUpdated.onAlarmsListUpdate(alarmsList);
    }

    /**
     * returns a list of all alarms in the database
     * @return a list of all alarms in the database
     */
    public ArrayList<Alarm> getAllAlarms() {
        try {
            updateAlarmsList();
            return alarmsList;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * adds new alarm to the database
     * @param alarm The alarm to be added
     */
    public void addNewAlarm(Alarm alarm) {

        if (onAlarmAdded != null) onAlarmAdded.onPreAlarmAdd(alarm);

        executeOrder(String.format("insert into alarms_table values ('%s', '%s', '%s', '%s')"
                , alarm.getKey()
                , alarm.getText()
                , alarm.getTimeFrom().getDateTime(true, true)
                , alarm.getTimeTo().getDateTime(true, true)));

        if (onAlarmAdded != null) onAlarmAdded.onAlarmAdd(alarm);
    }

    /**
     * adds new alarm to the database
     * @param alarm The alarm to be added
     * @param onAlarmAdded Local on add event handler
     */
    public void addNewAlarm(Alarm alarm, OnAlarmAdded onAlarmAdded) {

        if (onAlarmAdded != null) onAlarmAdded.onPreAlarmAdd(alarm);

        executeOrder(String.format("insert into alarms_table values ('%s', '%s', '%s', '%s')"
                , alarm.getKey()
                , alarm.getText()
                , alarm.getTimeFrom().getDateTime(true, true)
                , alarm.getTimeTo().getDateTime(true, true)));

        if (onAlarmAdded != null) onAlarmAdded.onAlarmAdd(alarm);
    }

    /**
     * updates an existing alarm in the database
     * @param key The key of the old alarm
     * @param alarm The new alarm data (the key will be replaced by the old alarm's key)
     */
    public void updateAlarm(String key, Alarm alarm) {
        Alarm oldAlarm = getAlarmByKey(key);

        if (onAlarmUpdated != null) onAlarmUpdated.onPreAlarmUpdate(oldAlarm, alarm);

        executeOrder(String.format("update alarms_table set alarmText='%s', timeFrom='%s', timeTo='%s' where key='%s'"
                , alarm.getText()
                , alarm.getTimeFrom().getDateTime(true, true)
                , alarm.getTimeTo().getDateTime(true, true)
                , key));

        if (onAlarmUpdated != null) onAlarmUpdated.onPostAlarmUpdate(oldAlarm, alarm);
    }

    /**
     * updates an existing alarm in the database
     * @param key The key of the old alarm
     * @param alarm The new alarm data (the key will be replaced by the old alarm's key)
     * @param onAlarmUpdated local on update event handler
     */
    public void updateAlarm(String key, Alarm alarm, OnAlarmUpdated onAlarmUpdated) {
        Alarm oldAlarm = getAlarmByKey(key);

        if (onAlarmUpdated != null) onAlarmUpdated.onPreAlarmUpdate(oldAlarm, alarm);

        executeOrder(String.format("update alarms_table set alarmText='%s', timeFrom='%s', timeTo='%s' where key='%s'"
                , alarm.getText()
                , alarm.getTimeFrom().getDateTime(true, true)
                , alarm.getTimeTo().getDateTime(true, true)
                , key));

        if (onAlarmUpdated != null) onAlarmUpdated.onPostAlarmUpdate(oldAlarm, alarm);
    }

    /**
     * gets an Alarm instance by it's key from the database.
     * @param key The key to the alarm.
     * @return null if there was no alarm with the defined key or the Alarm instance with the data of the alarm.
     */
    public Alarm getAlarmByKey(String key) {
        Cursor cursor = executeQuery("Select * from alarms_table where key='" + key + "'");
        if (cursor.moveToNext()) {
            try {
                return new Alarm(cursor.getString(0)
                        , cursor.getString(1)
                        , new DateTime(cursor.getString(2), DateTime.DEFAULT_DATE_TIME_24_HOUR)
                        , new DateTime(cursor.getString(3), DateTime.DEFAULT_DATE_TIME_24_HOUR)
                );
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public interface OnAlarmAdded {
        void onPreAlarmAdd(Alarm alarm);

        void onAlarmAdd(Alarm alarm);
    }

    public interface OnAlarmsListUpdated {
        void onAlarmsListUpdate(ArrayList<Alarm> alarmArrayList);
    }

    public interface OnAlarmUpdated {
        void onPreAlarmUpdate(Alarm oldAlarm, Alarm newAlarm);

        void onPostAlarmUpdate(Alarm oldAlarm, Alarm newAlarm);
    }

}
