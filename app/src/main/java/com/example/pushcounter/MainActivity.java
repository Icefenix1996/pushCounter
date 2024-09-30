package com.example.pushupcounter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etPushups;
    private TextView tvTotalPushups;
    private int totalPushups;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        etPushups = findViewById(R.id.etPushups);
        tvTotalPushups = findViewById(R.id.tvTotalPushups);
        Button btnAddPushups = findViewById(R.id.btnAddPushups);
        Button btnReset = findViewById(R.id.btnReset);

        // Инициализация SharedPreferences для сохранения данных
        sharedPreferences = getSharedPreferences("PushupCounter", MODE_PRIVATE);
        totalPushups = sharedPreferences.getInt("totalPushups", 0);
        tvTotalPushups.setText("Общее количество отжиманий за все дни: " + totalPushups);

        // Добавление отжиманий
        btnAddPushups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pushupInput = etPushups.getText().toString();

                if (!pushupInput.isEmpty()) {
                    int pushupsToday = Integer.parseInt(pushupInput);
                    totalPushups += pushupsToday;

                    // Сохранение в SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("totalPushups", totalPushups);
                    editor.apply();

                    // Обновление текстового поля
                    tvTotalPushups.setText("Общее количество отжиманий за все дни: " + totalPushups);
                    etPushups.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Введите количество отжиманий", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Сброс счётчика
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPushups = 0;

                // Сохранение в SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("totalPushups", 0);
                editor.apply();

                // Обновление текстового поля
                tvTotalPushups.setText("Общее количество отжиманий за все дни: " + totalPushups);
            }
        });
    }
}
