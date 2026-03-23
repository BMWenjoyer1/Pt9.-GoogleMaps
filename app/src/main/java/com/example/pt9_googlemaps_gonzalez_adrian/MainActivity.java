package com.example.pt9_googlemaps_gonzalez_adrian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botón para búsqueda por coordenadas
        Button btnCoordinates = findViewById(R.id.btn_coordinates);
        btnCoordinates.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchByCoordinatesActivity.class);
            startActivity(intent);
        });

        // Botón para búsqueda por población
        Button btnLocation = findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchByLocationActivity.class);
            startActivity(intent);
        });

        // Botón para ubicación actual
        Button btnCurrentLocation = findViewById(R.id.btn_current_location);
        btnCurrentLocation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CurrentLocationActivity.class);
            startActivity(intent);
        });
    }
}