package com.mingchu.newcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalendarControl calendarControl = (CalendarControl) findViewById(R.id.calendar_control);

        calendarControl.setOnLongClickListener(new CalendarControl.OnItemLongClickListener() {

            @Override
            public void onLongItemClick(View view, String tip) {
                Toast.makeText(MainActivity.this, "长按事件", Toast.LENGTH_SHORT).show();
            }
        });

        calendarControl.setOnItemClickListener(new CalendarControl.OnItemClickListener() {
            @Override
            public void ItemClick(View view, Date date) {
                DateFormat instance = SimpleDateFormat.getInstance();
                Toast.makeText(MainActivity.this, instance.format(date), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
