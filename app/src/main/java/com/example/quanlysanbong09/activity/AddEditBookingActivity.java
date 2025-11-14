package com.example.quanlysanbong09.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlysanbong09.R;
import com.example.quanlysanbong09.database.DatabaseHelper;
import com.example.quanlysanbong09.model.Booking;
import com.example.quanlysanbong09.model.Field;
import com.example.quanlysanbong09.model.Service;
import com.example.quanlysanbong09.model.ServiceUsage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditBookingActivity extends AppCompatActivity {
    private Spinner fieldSpinner;
    private EditText customerNameEditText, phoneEditText, dateEditText, timeEditText;
    private LinearLayout serviceContainer;
    private Button saveButton, backButton;
    private DatabaseHelper dbHelper;
    private Booking booking;
    private List<Service> services;
    private Map<Integer, Integer> serviceQuantities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_booking);

        dbHelper = new DatabaseHelper(this);
        fieldSpinner = findViewById(R.id.spinner_field);
        customerNameEditText = findViewById(R.id.edit_customer_name);
        phoneEditText = findViewById(R.id.edit_phone);
        dateEditText = findViewById(R.id.edit_date);
        timeEditText = findViewById(R.id.edit_time);
        serviceContainer = findViewById(R.id.service_container);
        saveButton = findViewById(R.id.button_save);
        backButton = findViewById(R.id.button_back);

        serviceQuantities = new HashMap<>();
        services = dbHelper.getAllServices();
        List<Field> fields = dbHelper.getAllFields();
        List<String> fieldNames = new ArrayList<>();
        List<Integer> fieldIds = new ArrayList<>();
        for (Field field : fields) {
            fieldNames.add(field.getName());
            fieldIds.add(field.getId());
        }
        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldNames);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldSpinner.setAdapter(fieldAdapter);

        for (Service service : services) {
            View serviceView = LayoutInflater.from(this).inflate(R.layout.item_service_usage, serviceContainer, false);
            TextView serviceName = serviceView.findViewById(R.id.text_service_name);
            TextView quantityText = serviceView.findViewById(R.id.text_quantity);
            Button plusButton = serviceView.findViewById(R.id.button_plus);
            Button minusButton = serviceView.findViewById(R.id.button_minus);

            serviceName.setText(service.getName());
            quantityText.setText("0");
            serviceQuantities.put(service.getId(), 0);

            plusButton.setOnClickListener(v -> {
                int quantity = serviceQuantities.get(service.getId()) + 1;
                serviceQuantities.put(service.getId(), quantity);
                quantityText.setText(String.valueOf(quantity));
            });

            minusButton.setOnClickListener(v -> {
                int quantity = serviceQuantities.get(service.getId());
                if (quantity > 0) {
                    quantity--;
                    serviceQuantities.put(service.getId(), quantity);
                    quantityText.setText(String.valueOf(quantity));
                }
            });

            serviceContainer.addView(serviceView);
        }

        int bookingId = getIntent().getIntExtra("BOOKING_ID", -1);
        if (bookingId != -1) {
            for (Booking b : dbHelper.getAllBookings()) {
                if (b.getId() == bookingId) {
                    booking = b;
                    break;
                }
            }
            if (booking != null) {
                customerNameEditText.setText(booking.getCustomerName());
                phoneEditText.setText(booking.getPhone());
                dateEditText.setText(booking.getDate());
                timeEditText.setText(booking.getTime());
                int fieldId = getIntent().getIntExtra("FIELD_ID", -1);
                for (int i = 0; i < fieldIds.size(); i++) {
                    if (fieldIds.get(i) == fieldId) {
                        fieldSpinner.setSelection(i);
                        break;
                    }
                }

                List<ServiceUsage> usages = dbHelper.getServiceUsagesForBooking(bookingId);
                for (ServiceUsage usage : usages) {
                    serviceQuantities.put(usage.getServiceId(), usage.getQuantity());
                    for (int i = 0; i < serviceContainer.getChildCount(); i++) {
                        View serviceView = serviceContainer.getChildAt(i);
                        TextView serviceName = serviceView.findViewById(R.id.text_service_name);
                        TextView quantityText = serviceView.findViewById(R.id.text_quantity);
                        if (serviceName.getText().toString().equals(dbHelper.getServiceNameById(usage.getServiceId()))) {
                            quantityText.setText(String.valueOf(usage.getQuantity()));
                            break;
                        }
                    }
                }
            }
        }

        saveButton.setOnClickListener(v -> {
            String customerName = customerNameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String time = timeEditText.getText().toString().trim();
            int fieldPosition = fieldSpinner.getSelectedItemPosition();

            if (customerName.isEmpty() || phone.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fieldPosition == -1) {
                Toast.makeText(this, "Vui lòng chọn sân bóng", Toast.LENGTH_SHORT).show();
                return;
            }

            int fieldId = fieldIds.get(fieldPosition);
            List<ServiceUsage> serviceUsages = new ArrayList<>();
            for (Service service : services) {
                int quantity = serviceQuantities.getOrDefault(service.getId(), 0);
                if (quantity > 0) {
                    ServiceUsage usage = new ServiceUsage();
                    usage.setServiceId(service.getId());
                    usage.setQuantity(quantity);
                    serviceUsages.add(usage);
                }
            }

            if (booking == null) {
                booking = new Booking();
                booking.setFieldId(fieldId);
                booking.setCustomerName(customerName);
                booking.setPhone(phone);
                booking.setDate(date);
                booking.setTime(time);
                booking.setPaid(false);
                if (dbHelper.addBooking(booking, serviceUsages)) {
                    Toast.makeText(this, "Thêm lịch đặt sân thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Thêm lịch đặt sân thất bại", Toast.LENGTH_SHORT).show();
                }
            } else {
                booking.setFieldId(fieldId);
                booking.setCustomerName(customerName);
                booking.setPhone(phone);
                booking.setDate(date);
                booking.setTime(time);
                if (dbHelper.updateBooking(booking, serviceUsages)) {
                    Toast.makeText(this, "Cập nhật lịch đặt sân thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật lịch đặt sân thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(v -> finish());
    }
}
