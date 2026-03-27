package com.example.gosport.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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
                    "description TEXT " +
                    ");";

    // ================= CREATE TABLE FIELDS =================
    private static final String CREATE_FIELDS =
            "CREATE TABLE " + TABLE_FIELDS + " (" +
                    "field_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_id INTEGER NOT NULL, " +
                    "field_name TEXT NOT NULL, " +
                    "address TEXT NOT NULL," +
                    "description TEXT, " +
                    "price_per_hour REAL NOT NULL CHECK(price_per_hour >= 0), " +
                    "status TEXT CHECK(status IN ('Available','Maintenance')) DEFAULT 'Available', " +
                    "image_url TEXT, " +
                    "is_deleted INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(category_id) REFERENCES " +
                    TABLE_CATEGORIES + "(category_id) ON DELETE CASCADE" +
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
                    "status TEXT CHECK(status IN ('Pending','Confirmed','Checkin','Cancelled','Completed')) DEFAULT 'Pending', " +
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

        // ===== SEED CATEGORIES =====
        ContentValues cat1 = new ContentValues();
        cat1.put("category_name", "Sân bóng đá");
        cat1.put("description", "Sân 5 người, 7 người");
        long footballId = db.insert(TABLE_CATEGORIES, null, cat1);

        ContentValues cat2 = new ContentValues();
        cat2.put("category_name", "Sân cầu lông");
        cat2.put("description", "Sân trong nhà");
        long badmintonId = db.insert(TABLE_CATEGORIES, null, cat2);

        ContentValues cat3 = new ContentValues();
        cat3.put("category_name", "Sân tennis");
        cat3.put("description", "Sân ngoài trời");
        long tennisId = db.insert(TABLE_CATEGORIES, null, cat3);

        // ===== SEED FIELDS =====
        ContentValues field1 = new ContentValues();
        field1.put("category_id", footballId);
        field1.put("field_name", "Sân A1");
        field1.put("address", "212 Điện Biên Phủ, Phường 17, Bình Thạnh, Thành phố Hồ Chí Minh 700000, Việt Nam");
        field1.put("description", "Sân mới, cỏ nhân tạo");
        field1.put("price_per_hour", 300000);
        field1.put("status", "Available");
        field1.put("image_url", "");
        db.insert(TABLE_FIELDS, null, field1);

        ContentValues field2 = new ContentValues();
        field2.put("category_id", badmintonId);
        field2.put("field_name", "Sân Cầu Lông B2");
        field2.put("address", "Quận 3, TP.HCM");
        field2.put("description", "Sân trong nhà, máy lạnh");
        field2.put("price_per_hour", 150000);
        field2.put("status", "Available");
        field2.put("image_url", "");
        db.insert(TABLE_FIELDS, null, field2);

        ContentValues field3 = new ContentValues();
        field3.put("category_id", tennisId);
        field3.put("field_name", "Sân Tennis T1");
        field3.put("address", "Quận 7, TP.HCM");
        field3.put("description", "Sân chuẩn thi đấu");
        field3.put("price_per_hour", 400000);
        field3.put("status", "Maintenance");
        field3.put("image_url", "");
        db.insert(TABLE_FIELDS, null, field3);



        // ===== SEED USER =====
        ContentValues user1 = new ContentValues();
        user1.put(USER_FULL_NAME, "Nguyễn Văn A");
        user1.put(USER_EMAIL, "user1@gosport.com");
        user1.put(USER_PHONE, "0988888888");
        user1.put(USER_PASSWORD, hashPassword("User123"));
        user1.put(USER_ROLE, "USER");
        user1.put(USER_IS_ACTIVE, 1);
        user1.put(USER_IS_DELETED, 0);
        long userId1 = db.insert(TABLE_USERS, null, user1);

        ContentValues review1 = new ContentValues();
        review1.put("user_id", userId1);
        review1.put("field_id", 1);
        review1.put("rating", 5);
        review1.put("comment", "Sân cực đẹp, cỏ nhân tạo mềm mại và sạch sẽ. Đặt sân nhanh, chủ sân nhiệt tình. Sẽ quay lại!");
        review1.put("created_at", "2026-03-10 14:30:00");
        db.insert(TABLE_REVIEWS, null, review1);

        // Review 2: 4 sao
        ContentValues review2 = new ContentValues();
        review2.put("user_id", userId1);
        review2.put("field_id", 1);
        review2.put("rating", 4);
        review2.put("comment", "Sân tốt, giá cả hợp lý nhưng đèn hơi tối vào buổi tối.");
        review2.put("created_at", "2026-03-05 20:15:00");
        db.insert(TABLE_REVIEWS, null, review2);

        // seed 100+ review
        for(int i = 0; i < 100; i++) {
            ContentValues r = new ContentValues();
            r.put("user_id", userId1);
            r.put("field_id", 1);
            r.put("rating", (i % 2 == 0) ? 5 : 4);
            r.put("comment", "Sân ổn định, chất lượng tốt.");
            db.insert(TABLE_REVIEWS, null, r);
        }

        ContentValues user2 = new ContentValues();
        user2.put(USER_FULL_NAME, "Nguyễn Văn B");
        user2.put(USER_EMAIL, "user2@gosport.com");
        user2.put(USER_PHONE, "0988888212");
        user2.put(USER_PASSWORD, hashPassword("User123"));
        user2.put(USER_ROLE, "USER");
        user2.put(USER_IS_ACTIVE, 1);
        user2.put(USER_IS_DELETED, 0);
        db.insert(TABLE_USERS, null, user2);


        // ===== SEED BOOKINGS (Dữ liệu test cho Admin) =====
// ===== SEED BOOKINGS (Dữ liệu test cho Admin) =====

        // Đơn 1: Chờ duyệt (Pending) - Ngày 15/03/2026
        ContentValues b1 = new ContentValues();
        b1.put("user_id", 2); // ID của Nguyễn Văn A vừa tạo ở trên
        b1.put("field_id", 1); // Sân A1
        b1.put("start_time", "2026-03-15 08:00:00");
        b1.put("end_time", "2026-03-15 10:00:00");
        b1.put("total_price", 600000);
        b1.put("status", "Pending");
        db.insert(TABLE_BOOKINGS, null, b1);

        // Đơn 2: Đã Check-in (Checkin) - Ngày 20/03/2026
        ContentValues b2 = new ContentValues();
        b2.put("user_id", 2);
        b2.put("field_id", 2); // Sân B2
        b2.put("start_time", "2026-03-20 17:00:00");
        b2.put("end_time", "2026-03-20 19:00:00");
        b2.put("total_price", 300000);
        b2.put("status", "Checkin");
        db.insert(TABLE_BOOKINGS, null, b2);

        // Đơn 3: Hoàn thành (Completed) - Ngày 10/02/2026
        ContentValues b3 = new ContentValues();
        b3.put("user_id", 2);
        b3.put("field_id", 1);
        b3.put("start_time", "2026-02-10 14:00:00");
        b3.put("end_time", "2026-02-10 16:00:00");
        b3.put("total_price", 600000);
        b3.put("status", "Completed");
        db.insert(TABLE_BOOKINGS, null, b3);

        // Đơn 4: Đã hủy (Cancelled) - Ngày 01/01/2026
        ContentValues b4 = new ContentValues();
        b4.put("user_id", 2);
        b4.put("field_id", 3);
        b4.put("start_time", "2026-01-01 09:00:00");
        b4.put("end_time", "2026-01-01 11:00:00");
        b4.put("total_price", 800000);
        b4.put("status", "Cancelled");
        db.insert(TABLE_BOOKINGS, null, b4);

        ContentValues pay1 = new ContentValues();
        pay1.put("booking_id", 1);
        pay1.put("amount", 100000);
        pay1.put("payment_method", "CASH");
        pay1.put("payment_status", "FINISH");
        db.insert(TABLE_PAYMENTS, null, pay1);

        ContentValues pay4 = new ContentValues();
        pay4.put("booking_id", 4);
        pay4.put("amount", 100000);
        pay4.put("payment_method", "E-Wallet");
        pay4.put("payment_status", "FINISH");
        db.insert(TABLE_PAYMENTS, null, pay4);

        ContentValues pay2 = new ContentValues();
        pay2.put("booking_id", 2);
        pay2.put("amount", 100000);
        pay2.put("payment_method", "CASH");
        pay2.put("payment_status", "FINISH");
        db.insert(TABLE_PAYMENTS, null, pay2);

        ContentValues pay3 = new ContentValues();
        pay3.put("booking_id", 3);
        pay3.put("amount", 100000);
        pay3.put("payment_method", "E-Wallet");
        pay3.put("payment_status", "FINISH");
        db.insert(TABLE_PAYMENTS, null, pay3);
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

    // ==========CATEGORY================
    // Insert Category
    public long insertCategory(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", name);
        values.put("description", description);
        long result = db.insert(TABLE_CATEGORIES, null, values);
        db.close();
        return result;
    }

    // Get All
    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
    }

    // Update
    public int updateCategory(int id, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", name);
        values.put("description", description);
        int result = db.update(TABLE_CATEGORIES, values,
                "category_id=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // Delete
    public int deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CATEGORIES,
                "category_id=?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }


    // ============ FIELDS ================
    public boolean insertField(int categoryId,
                               String fieldName,
                               String address,
                               String description,
                               double pricePerHour,
                               String status,
                               String imageUrl) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("category_id", categoryId);
        values.put("field_name", fieldName);
        values.put("address", address);
        values.put("description", description);
        values.put("price_per_hour", pricePerHour);
        values.put("status", status);
        values.put("image_url", imageUrl);

        long result = db.insert(TABLE_FIELDS, null, values);
        return result != -1;
    }


    public Cursor getAllFields() {

        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT f.field_id, f.category_id, c.category_name, " +
                        "f.field_name,f.address, f.description, f.price_per_hour, " +
                        "f.status, f.image_url " +
                        "FROM " + TABLE_FIELDS + " f " +
                        "INNER JOIN " + TABLE_CATEGORIES + " c " +
                        "ON f.category_id = c.category_id " +
                        "WHERE f.is_deleted = 0";

        return db.rawQuery(query, null);
    }


    public boolean updateField(int fieldId,
                               int categoryId,
                               String fieldName,
                               String address,
                               String description,
                               double pricePerHour,
                               String status,
                               String imageUrl) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("category_id", categoryId);
        values.put("field_name", fieldName);
        values.put("address", address);
        values.put("description", description);
        values.put("price_per_hour", pricePerHour);
        values.put("status", status);
        values.put("image_url", imageUrl);

        int result = db.update(TABLE_FIELDS,
                values,
                "field_id = ?",
                new String[]{String.valueOf(fieldId)});

        return result > 0;
    }

    public boolean deleteField(int fieldId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_deleted", 1);

        int result = db.update(TABLE_FIELDS,
                values,
                "field_id = ?",
                new String[]{String.valueOf(fieldId)});

        return result > 0;
    }

    public Cursor getFieldsByCategory(int categoryId) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT f.field_id, f.category_id, c.category_name, " +
                "f.field_name, f.address, f.description, " +
                "f.price_per_hour, f.status, f.image_url " +
                "FROM Fields f " +
                "INNER JOIN Categories c ON f.category_id = c.category_id " +
                "WHERE f.category_id = ? AND f.is_deleted = 0";

        return db.rawQuery(query,
                new String[]{String.valueOf(categoryId)});
    }

    // ==========BOOKING================
    public long insertBooking(int userId,
                              int fieldId,
                              String startTime,
                              String endTime,
                              String bookingType,
                              double totalPrice,
                              String note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("field_id", fieldId);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("booking_type", bookingType);
        values.put("total_price", totalPrice);
        values.put("status", "Pending");
        values.put("note", note);

        long result = db.insert(TABLE_BOOKINGS, null, values);

        return result;
    }

    public boolean cancelBooking(int bookingId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "Cancelled");

        int result = db.update(TABLE_BOOKINGS,
                values,
                "booking_id=?",
                new String[]{String.valueOf(bookingId)});

        return result > 0;
    }

    public long insertPayment(int bookingId,
                              double amount,
                              String method,
                              String transactionRef,
                              String paymentStatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("booking_id", bookingId);
        values.put("amount", amount);
        if(method.equals("Cash")) {
            values.put("payment_method", "Cash");
        } else {
            values.put("payment_method", "E-Wallet");
        }
        values.put("transaction_ref", transactionRef);
        values.put("payment_status", paymentStatus);

        long result = db.insert(TABLE_PAYMENTS, null, values);

        return result;
    }

    public boolean confirmBankBooking(int bookingId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "Confirmed");

        int result = db.update(TABLE_BOOKINGS,
                values,
                "booking_id=?",
                new String[]{String.valueOf(bookingId)});

        return result > 0;
    }
    public boolean confirmCashBooking(int bookingId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "Pending");

        int result = db.update(TABLE_BOOKINGS,
                values,
                "booking_id=?",
                new String[]{String.valueOf(bookingId)});

        return result > 0;
    }

    public boolean completeBooking(int bookingId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "Completed");

        int result = db.update(
                TABLE_BOOKINGS,
                values,
                "booking_id=?",
                new String[]{String.valueOf(bookingId)}
        );

        return result > 0;
    }

    public boolean cancelBookingWithRule(int bookingId, String startTime) {

        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                    );

            Date bookingDate = sdf.parse(startTime);

            long diff =
                    bookingDate.getTime() -
                            System.currentTimeMillis();

            long hours = diff / (1000 * 60 * 60);

            if (hours < 24) {
                return false;
            }

            return cancelBooking(bookingId);

        } catch (Exception e) {
            return false;
        }
    }

    public Cursor getBookingsByUser(int userId) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT b.booking_id, f.field_name, f.address, " +
                        "b.start_time, b.end_time, " +
                        "b.total_price, b.status " +
                        "FROM " + TABLE_BOOKINGS + " b " +
                        "INNER JOIN " + TABLE_FIELDS + " f " +
                        "ON b.field_id = f.field_id " +
                        "WHERE b.user_id = ? " +
                        "ORDER BY b.created_at DESC";

        return db.rawQuery(
                query,
                new String[]{String.valueOf(userId)}
        );
    }

    public Cursor getBookingsForFieldOnDate(int fieldId, String dateString) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT start_time, end_time FROM " + TABLE_BOOKINGS +
                " WHERE field_id = ? AND start_time LIKE ? AND status != 'Cancelled'";

        return db.rawQuery(query, new String[]{String.valueOf(fieldId), dateString + "%"});
    }

    // ==========REVIEW================
    public long insertReview(int userId,
                             int fieldId,
                             int rating,
                             String comment) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("field_id", fieldId);
        values.put("rating", rating);
        values.put("comment", comment);

        long result = db.insert(TABLE_REVIEWS, null, values);

        db.close();
        return result;
    }

    public Cursor getReviewsByField(int fieldId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT r.*, u." + USER_FULL_NAME + " as full_name " +
                        "FROM " + TABLE_REVIEWS + " r " +
                        "INNER JOIN " + TABLE_USERS + " u ON r.user_id = u." + USER_ID + " " +
                        "WHERE r.field_id = ? " +
                        "ORDER BY r.created_at DESC";

        return db.rawQuery(query, new String[]{String.valueOf(fieldId)});
    }

    public Cursor getBookingsAdminFiltered(String fromDate, String toDate, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, u.full_name, u.phone_number, f.field_name, f.address, p.payment_method, " +
                "strftime('%d/%m/%Y', b.start_time) as only_start_date, " +
                "strftime('%d/%m/%Y %H:%M', b.created_at) as full_created_at " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN fields f ON b.field_id = f.field_id " +
                "LEFT JOIN payments p ON b.booking_id = p.booking_id " +
                "WHERE b.start_time >= ? AND b.start_time <= ?";

        List<String> args = new ArrayList<>();
        args.add(fromDate + " 00:00:00");
        args.add(toDate + " 23:59:59");

        if (!status.equals("Tất cả")) {
            query += " AND b.status = ?";
            args.add(status);
        }

        query += " ORDER BY b.created_at DESC"; // Sắp xếp theo đơn mới nhất
        return db.rawQuery(query, args.toArray(new String[0]));
    }

    // Hàm cập nhật trạng thái đơn (Dùng cho nút Check-in và Hoàn thành)
    public boolean updateBookingStatus(int bookingId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        return db.update(TABLE_BOOKINGS, values, "booking_id = ?", new String[]{String.valueOf(bookingId)}) > 0;
    }

    public Cursor getBookingsForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, f.field_name, f.address " +
                "FROM bookings b " +
                "JOIN fields f ON b.field_id = f.field_id " +
                "WHERE b.user_id = ? " +
                "ORDER BY b.start_time DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public Cursor getBookingsUserFiltered(int userId, String from, String to) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Thêm b.created_at vào danh sách SELECT
        String baseQuery = "SELECT b.*, f.field_name, f.address, p.payment_method, b.created_at " +
                "FROM bookings b " +
                "JOIN fields f ON b.field_id = f.field_id " +
                "LEFT JOIN payments p ON b.booking_id = p.booking_id " +
                "WHERE b.user_id = ? ";

        if (from == null || to == null) {
            return db.rawQuery(baseQuery + "ORDER BY b.created_at DESC", // Sắp xếp theo đơn mới nhất
                    new String[]{String.valueOf(userId)});
        } else {
            return db.rawQuery(baseQuery + "AND date(b.start_time) BETWEEN date(?) AND date(?) " +
                            "ORDER BY b.created_at DESC",
                    new String[]{String.valueOf(userId), from, to});
        }
    }

    // 1. Lấy dữ liệu thống kê tổng hợp theo năm
    public Cursor getYearlyKpis(int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "COUNT(*) as total_orders, " +
                "SUM(total_price) as total_revenue, " +
                "(SELECT COUNT(*) FROM bookings WHERE strftime('%Y', start_time) = ?) as prev_year_orders " +
                "FROM bookings " +
                "WHERE strftime('%Y', start_time) = ? AND status = 'Completed'";

        return db.rawQuery(query, new String[]{String.valueOf(year - 1), String.valueOf(year)});
    }

    // 2. Lấy chi tiết từng tháng (Số đơn, Doanh thu, Trạng thái)
    public Cursor getMonthlyDetails(int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "strftime('%m', start_time) as month, " +
                "COUNT(*) as count, " +
                "SUM(CASE WHEN status = 'Completed' THEN total_price ELSE 0 END) as revenue, " +
                "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as completed, " +
                "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled " +
                "FROM bookings " +
                "WHERE strftime('%Y', start_time) = ? " +
                "GROUP BY month ORDER BY month ASC";
        return db.rawQuery(query, new String[]{String.valueOf(year)});
    }

    public double getTotalRevenueOfYear(int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(totalPrice) FROM Orders WHERE strftime('%Y', date) = ?", new String[]{String.valueOf(year)});
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public int getTotalOrdersOfYear(int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Orders WHERE strftime('%Y', date) = ?", new String[]{String.valueOf(year)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public Cursor getStatBySpecificMonth(int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Format tháng thành "01", "02"... để khớp với strftime
        String monthStr = String.format("%02d", month);

        String query = "SELECT strftime('%m', date) as Month, " +
                "COUNT(*) as Total, " +
                "SUM(totalPrice) as Revenue, " +
                "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as Completed, " +
                "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as Cancelled " +
                "FROM Orders " +
                "WHERE strftime('%Y', date) = ? AND strftime('%m', date) = ? " +
                "GROUP BY Month";

        return db.rawQuery(query, new String[]{String.valueOf(year), monthStr});
    }

    // deleteAccount
    public boolean deleteAccount(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_IS_DELETED, 1);
        int result = db.update(TABLE_USERS, values,
                USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    public boolean updateProfile(int userId, String fullName, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_FULL_NAME, fullName);
        values.put(USER_PHONE, phone);
        int result = db.update(TABLE_USERS, values,
                USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    public boolean isPhoneExistsExcluding(String phone, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_USERS +
                        " WHERE " + USER_PHONE + " = ? AND " + USER_ID + " != ?",
                new String[]{phone, String.valueOf(userId)}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


}