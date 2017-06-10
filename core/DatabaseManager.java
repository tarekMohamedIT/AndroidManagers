package core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by tarek on 6/4/17.
 */

/**
 * Basic class for database handling
 */
public class DatabaseManager {
    //instance of the database
    SQLiteDatabase sqLiteDatabase;

    /**
     * basic constructor for the DatabaseManager class
     * @param context The context of the application
     * @param databaseName The name of the database
     */
    public DatabaseManager(Context context, String databaseName){
        sqLiteDatabase = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
    }

    /**
     * basic constructor for the DatabaseManager class
     * @param context The context of the application
     * @param databaseName The name of the database
     * @param mode the mode in which the database should be opened or created
     */
    public DatabaseManager(Context context, String databaseName, int mode){
        sqLiteDatabase = context.openOrCreateDatabase(databaseName, mode, null);
    }

    /**
     * executes sql commands
     * @param sql string contains the sql statement
     */
    public void executeOrder(String sql){
        sqLiteDatabase.execSQL(sql);
    }

    /**
     * executes sql select query command
     * @param sql string contains the sql statement
     * @return a cursor to the query
     */
    public Cursor executeQuery(String sql){
        return sqLiteDatabase.rawQuery(sql, null);
    }
}
