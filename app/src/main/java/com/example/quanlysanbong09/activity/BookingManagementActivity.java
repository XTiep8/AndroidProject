package com.example.quanlysanbong09.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlysanbong09.R;
import com.example.quanlysanbong09.adapter.BookingAdapter;
import com.example.quanlysanbong09.database.DatabaseHelper;
import com.example.quanlysanbong09.model.Booking;

import java.util.List;

public class BookingManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private DatabaseHelper dbHelper;
    private Button addButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_management);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_bookings);
        addButton = findViewById(R.id.button_add_booking);
        backButton = findViewById(R.id.button_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Booking> bookings = dbHelper.getAllBookings();
        adapter = new BookingAdapter(bookings, dbHelper,
                booking -> {
                    Intent intent = new Intent(this, AddEditBookingActivity.class);
                    intent.putExtra("BOOKING_ID", booking.getId());
                    intent.putExtra("FIELD_ID", booking.getFieldId());
                    startActivity(intent);
                },
                booking -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Xóa lịch đặt sân")
                            .setMessage("Bạn có chắc muốn xóa lịch đặt sân của " + booking.getCustomerName() + "?")
                            .setPositiveButton("Có", (dialog, which) -> {
                                dbHelper.deleteBooking(booking.getId());
                                updateBookingList();
                            })
                            .setNegativeButton("Không", null)
                            .show();
                },
                booking -> {
                    Intent intent = new Intent(this, BookingDetailActivity.class);
                    intent.putExtra("BOOKING_ID", booking.getId());
                    startActivity(intent);
                });
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> startActivity(new Intent(this, AddEditBookingActivity.class)));
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBookingList();
    }

    private void updateBookingList() {
        List<Booking> bookings = dbHelper.getAllBookings();
        adapter.updateBookings(bookings);
    }
}
