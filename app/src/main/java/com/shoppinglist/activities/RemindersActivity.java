/**
 * RemindersActivity - Reminder Management
 * Manages shopping reminders and notifications
 * Handles reminder scheduling and delivery
 * 
 * Features:
 * - Create reminders
 * - Edit reminders
 * - Delete reminders
 * - Set reminder time
 * - Set repeat frequency
 * - Reminder types (time-based, low stock, expiry)
 * - Notification delivery
 * - Reminder history
 * 
 * Reminder Types:
 * - Time-based reminders: Scheduled at specific time
 * - Low stock reminders: When pantry items run low
 * - Expiry reminders: When items are expiring
 * - Shopping day reminders: Recurring shopping days
 * 
 * Repeat Options:
 * - No repeat (one-time)
 * - Daily
 * - Weekly
 * - Monthly
 * - Custom intervals
 * 
 * Notification Features:
 * - Push notifications
 * - In-app notifications
 * - Sound alerts
 * - Vibration
 * - LED indicators
 * - Notification channels
 * 
 * UI Components:
 * - RecyclerView for reminder list
 * - Time picker for scheduling
 * - Date picker for dates
 * - Repeat selector
 * - FAB for adding reminders
 * - Alert dialogs for management
 * 
 * Data Management:
 * - ReminderViewModel for data
 * - ReminderAdapter for display
 * - Local database storage
 * - Cloud synchronization
 * - ReminderManager for scheduling
 * 
 * Permissions:
 * - POST_NOTIFICATIONS (Android 13+)
 * - SCHEDULE_EXACT_ALARM
 * - RECEIVE_BOOT_COMPLETED
 * 
 * Background Services:
 * - AlarmManager for scheduling
 * - BroadcastReceiver for triggers
 * - NotificationManager for delivery
 * - WorkManager for reliability
 * 
 * User Interactions:
 * - Click to view details
 * - Long press for context menu
 * - Swipe to delete
 * - Edit inline
 * - Enable/disable reminders
 * 
 * Localization:
 * - Multi-language support
 * - Locale applied via attachBaseContext
 * - Dynamic language switching
 * 
 * Performance:
 * - Efficient scheduling
 * - Minimal battery usage
 * - Smooth UI
 * - Memory optimization
 * 
 * Testing:
 * - Unit tests for reminder logic
 * - Integration tests with AlarmManager
 * - UI tests for interactions
 * - Notification testing
 */
package com.shoppinglist.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.Date;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.shoppinglist.R;
import com.shoppinglist.adapters.ReminderAdapter;
import com.shoppinglist.database.entities.ReminderEntity;
import com.shoppinglist.services.ReminderManager;
import com.shoppinglist.utils.LocaleHelper;
import com.shoppinglist.viewmodels.ReminderViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RemindersActivity extends AppCompatActivity implements ReminderAdapter.OnReminderListener {
    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    
    private ReminderViewModel viewModel;
    private ReminderAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private ReminderManager reminderManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.reminders);
        }

        viewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        reminderManager = new ReminderManager(this);

        initializeViews();
        setupRecyclerView();
        observeData();
        checkNotificationPermission();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_reminders);
        emptyView = findViewById(R.id.empty_view);

        FloatingActionButton fab = findViewById(R.id.fab_add_reminder);
        fab.setOnClickListener(v -> showAddReminderDialog(null));
    }

    private void setupRecyclerView() {
        adapter = new ReminderAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getAllReminders().observe(this, reminders -> {
            adapter.setReminders(reminders);
            updateEmptyView(reminders);
        });
    }

    private void updateEmptyView(java.util.List<ReminderEntity> reminders) {
        if (reminders == null || reminders.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied - show message
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.notification_permission_required)
                        .setMessage("Reminders require notification permission to work properly.")
                        .setPositiveButton(R.string.enable_notifications, (d, w) -> checkNotificationPermission())
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        }
    }

    @Override
    public void onReminderClick(ReminderEntity reminder) {
        showAddReminderDialog(reminder);
    }

    @Override
    public void onEditClick(ReminderEntity reminder) {
        showAddReminderDialog(reminder);
    }

    @Override
    public void onDeleteClick(ReminderEntity reminder) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete)
                .setMessage("Delete this reminder?")
                .setPositiveButton(R.string.delete, (d, w) -> {
                    viewModel.deleteReminder(reminder);
                    reminderManager.cancelReminder(reminder.getId());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onToggleActive(ReminderEntity reminder, boolean isActive) {
        viewModel.setReminderActive(reminder.getId(), isActive);
        if (isActive) {
            reminderManager.scheduleReminder(reminder);
        } else {
            reminderManager.cancelReminder(reminder.getId());
        }
    }

    private void showAddReminderDialog(ReminderEntity existingReminder) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reminder, null);

        TextInputEditText etTitle = dialogView.findViewById(R.id.et_reminder_title);
        TextInputEditText etMessage = dialogView.findViewById(R.id.et_reminder_message);
        TextInputEditText etDateTime = dialogView.findViewById(R.id.et_date_time);
        AutoCompleteTextView etRepeatInterval = dialogView.findViewById(R.id.et_repeat_interval);

        // Setup repeat interval dropdown
        String[] intervals = {"No Repeat", "Daily", "Weekly", "Monthly"};
        ArrayAdapter<String> intervalAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, intervals);
        etRepeatInterval.setAdapter(intervalAdapter);

        // Store selected date/time
        final Calendar selectedDateTime = Calendar.getInstance();
        if (existingReminder != null) {
            selectedDateTime.setTimeInMillis(existingReminder.getScheduledTime());
        }

        // Date/Time picker
        etDateTime.setOnClickListener(v -> showDateTimePicker(etDateTime, selectedDateTime));

        // Pre-fill if editing
        if (existingReminder != null) {
            etTitle.setText(existingReminder.getTitle());
            etMessage.setText(existingReminder.getMessage());
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            etDateTime.setText(sdf.format(new Date(existingReminder.getScheduledTime())));
            
            String interval = formatRepeatIntervalForDisplay(existingReminder.getRepeatInterval());
            etRepeatInterval.setText(interval, false);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(existingReminder == null ? R.string.add_reminder : R.string.edit_reminder)
                .setView(dialogView)
                .create();

        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String message = etMessage.getText().toString().trim();
            String repeatIntervalDisplay = etRepeatInterval.getText().toString().trim();

            if (title.isEmpty() || message.isEmpty()) {
                return;
            }

            String repeatInterval = parseRepeatInterval(repeatIntervalDisplay);
            long scheduledTime = selectedDateTime.getTimeInMillis();

            if (existingReminder == null) {
                viewModel.createReminder(title, message, "TIME_BASED", scheduledTime, repeatInterval);
            } else {
                existingReminder.setTitle(title);
                existingReminder.setMessage(message);
                existingReminder.setScheduledTime(scheduledTime);
                existingReminder.setRepeatInterval(repeatInterval);
                viewModel.updateReminder(existingReminder);
                
                // Reschedule
                if (existingReminder.isActive()) {
                    reminderManager.cancelReminder(existingReminder.getId());
                    reminderManager.scheduleReminder(existingReminder);
                }
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDateTimePicker(TextInputEditText etDateTime, Calendar selectedDateTime) {
        // First show date picker
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    // Then show time picker
                    TimePickerDialog timePicker = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                selectedDateTime.set(Calendar.SECOND, 0);
                                
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
                                etDateTime.setText(sdf.format(selectedDateTime.getTime()));
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            false);
                    timePicker.show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));
        
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }

    private String formatRepeatIntervalForDisplay(String interval) {
        switch (interval) {
            case "DAILY": return "Daily";
            case "WEEKLY": return "Weekly";
            case "MONTHLY": return "Monthly";
            case "NONE":
            default: return "No Repeat";
        }
    }

    private String parseRepeatInterval(String display) {
        switch (display) {
            case "Daily": return "DAILY";
            case "Weekly": return "WEEKLY";
            case "Monthly": return "MONTHLY";
            case "No Repeat":
            default: return "NONE";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
