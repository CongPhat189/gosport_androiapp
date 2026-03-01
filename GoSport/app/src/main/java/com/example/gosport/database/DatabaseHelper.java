package com.example.gosport.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "gosport.db";
    public static final int DATABASE_VERSION = 1;

    // ================= TABLE NAMES =================
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_CATEGORIES = "Categories";
    public static final String TABLE_FIELDS = "Fields";
    public static final String TABLE_BOOKINGS = "Bookings";
    public static final String TABLE_PAYMENTS = "Payments";
    public static final String TABLE_REVIEWS = "Reviews";

    // ================= USERS COLUMNS =================
    public static final String USER_ID = "user_id";
    public static final String USER_FIREBASE_UID = "firebase_uid";
    public static final String USER_ROLE = "role";
    public static final String USER_FULL_NAME = "full_name";
    public static final String USER_PHONE = "phone_number";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password_hash";
    public static final String USER_AVATAR = "avatar";
    public static final String USER_IS_ACTIVE = "is_active";
    public static final String USER_IS_DELETED = "is_deleted";
    public static final String USER_CREATED_AT = "created_at";

    // ================= CREATE TABLE USERS =================
    private static final String CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_FIREBASE_UID + " TEXT UNIQUE, " +
                    USER_ROLE + " TEXT CHECK(" + USER_ROLE + " IN ('ADMIN','USER')) DEFAULT 'USER', " +
                    USER_FULL_NAME + " TEXT NOT NULL, " +
                    USER_PHONE + " TEXT UNIQUE, " +
                    USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    USER_PASSWORD + " TEXT, " +
                    USER_AVATAR + " TEXT, " +
                    USER_IS_ACTIVE + " INTEGER DEFAULT 1, " +
                    USER_IS_DELETED + " INTEGER DEFAULT 0, " +
                    USER_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP" +
                    ");";

    // ================= CREATE TABLE CATEGORIES =================
    private static final String CREATE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "image_url TEXT" +
                    ");";

    // ================= CREATE TABLE FIELDS =================
    private static final String CREATE_FIELDS =
            "CREATE TABLE " + TABLE_FIELDS + " (" +
                    "field_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_id INTEGER NOT NULL, " +
                    "field_name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "price_per_hour REAL NOT NULL CHECK(price_per_hour >= 0), " +
                    "status TEXT CHECK(status IN ('Available','Maintenance')) DEFAULT 'Available', " +
                    "is_deleted INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(category_id) ON DELETE CASCADE" +
                    ");";

    // ================= CREATE TABLE BOOKINGS =================
    private static final String CREATE_BOOKINGS =
            "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                    "booking_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "field_id INTEGER NOT NULL, " +
                    "start_time TEXT NOT NULL, " +
                    "end_time TEXT NOT NULL, " +
                    "booking_type TEXT CHECK(booking_type IN ('Daily','Fixed')), " +
                    "total_price REAL CHECK(total_price >= 0), " +
                    "status TEXT CHECK(status IN ('Pending','Confirmed','Cancelled','Completed')) DEFAULT 'Pending', " +
                    "note TEXT, " +
                    "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(field_id) REFERENCES " + TABLE_FIELDS + "(field_id) ON DELETE CASCADE" +
                    ");";

    // ================= CREATE TABLE PAYMENTS =================
    private static final String CREATE_PAYMENTS =
            "CREATE TABLE " + TABLE_PAYMENTS + " (" +
                    "payment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "booking_id INTEGER NOT NULL, " +
                    "amount REAL NOT NULL CHECK(amount > 0), " +
                    "payment_method TEXT CHECK(payment_method IN ('Cash','E-Wallet')), " +
                    "transaction_ref TEXT, " +
                    "payment_status TEXT, " +
                    "payment_time TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(booking_id) REFERENCES " + TABLE_BOOKINGS + "(booking_id) ON DELETE CASCADE" +
                    ");";

    // ================= CREATE TABLE REVIEWS =================
    private static final String CREATE_REVIEWS =
            "CREATE TABLE " + TABLE_REVIEWS + " (" +
                    "review_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "field_id INTEGER NOT NULL, " +
                    "rating INTEGER CHECK(rating BETWEEN 1 AND 5), " +
                    "comment TEXT, " +
                    "created_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(user_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(field_id) REFERENCES " + TABLE_FIELDS + "(field_id) ON DELETE CASCADE" +
                    ");";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_CATEGORIES);
        db.execSQL(CREATE_FIELDS);
        db.execSQL(CREATE_BOOKINGS);
        db.execSQL(CREATE_PAYMENTS);
        db.execSQL(CREATE_REVIEWS);

        // Seed tai khoan ADMIN mac dinh
        ContentValues adminValues = new ContentValues();
        adminValues.put(USER_FULL_NAME, "Quản trị viên");
        adminValues.put(USER_EMAIL, "admin@gosport.com");
        adminValues.put(USER_PHONE, "0123456789");
        adminValues.put(USER_PASSWORD, hashPassword("Admin123"));
        adminValues.put(USER_ROLE, "ADMIN");
        adminValues.put(USER_IS_ACTIVE, 1);
        adminValues.put(USER_IS_DELETED, 0);
        db.insert(TABLE_USERS, null, adminValues);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIELDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ================= HASH PASSWORD =================
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================= INSERT USER =================
    public long insertUser(String fullName, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_FULL_NAME, fullName);
        values.put(USER_EMAIL, email);
        values.put(USER_PHONE, phone);
        values.put(USER_PASSWORD, hashPassword(password));
        values.put(USER_ROLE, "USER");
        values.put(USER_IS_ACTIVE, 1);
        values.put(USER_IS_DELETED, 0);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // ================= CHECK LOGIN =================
    public Cursor checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE " + USER_EMAIL + " = ?" +
                        " AND " + USER_PASSWORD + " = ?" +
                        " AND " + USER_IS_ACTIVE + " = 1" +
                        " AND " + USER_IS_DELETED + " = 0",
                new String[]{email, hashedPassword}
        );
        return cursor;
    }

    // ================= CHECK EMAIL EXISTS =================
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + USER_EMAIL + " = ?",
                new String[]{email}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ================= CHECK PHONE EXISTS =================
    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS + " WHERE " + USER_PHONE + " = ?",
                new String[]{phone}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

}