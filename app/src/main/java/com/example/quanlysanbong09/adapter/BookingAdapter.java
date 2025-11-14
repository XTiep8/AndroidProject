package com.example.quanlysanbong09.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlysanbong09.R;
import com.example.quanlysanbong09.database.DatabaseHelper;
import com.example.quanlysanbong09.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookings;
    private DatabaseHelper dbHelper;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private OnCalculateClickListener calculateListener;

    public interface OnItemClickListener {
        void onItemClick(Booking booking);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Booking booking);
    }

    public interface OnCalculateClickListener {
        void onCalculateClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookings, DatabaseHelper dbHelper,
                          OnItemClickListener clickListener, OnItemLongClickListener longClickListener,
                          OnCalculateClickListener calculateListener) {
        this.bookings = bookings;
        this.dbHelper = dbHelper;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.calculateListener = calculateListener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        String fieldName = dbHelper.getFieldNameById(booking.getFieldId());
        holder.fieldTextView.setText(fieldName);
        holder.customerTextView.setText(booking.getCustomerName());
        holder.dateTextView.setText(booking.getDate());
        holder.statusTextView.setText(booking.isPaid() ? "Đã thanh toán" : "Chưa thanh toán");
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(booking));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(booking);
            return true;
        });
        holder.calculateButton.setOnClickListener(v -> calculateListener.onCalculateClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void updateBookings(List<Booking> newBookings) {
        bookings.clear();
        bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView fieldTextView, customerTextView, dateTextView, statusTextView;
        Button calculateButton;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldTextView = itemView.findViewById(R.id.text_booking_field);
            customerTextView = itemView.findViewById(R.id.text_booking_customer);
            dateTextView = itemView.findViewById(R.id.text_booking_date);
            statusTextView = itemView.findViewById(R.id.text_booking_status);
            calculateButton = itemView.findViewById(R.id.button_calculate);
        }
    }
}
