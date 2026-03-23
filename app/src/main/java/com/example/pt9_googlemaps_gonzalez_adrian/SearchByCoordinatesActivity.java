package com.example.pt9_googlemaps_gonzalez_adrian;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SearchByCoordinatesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final int ZOOM_LEVEL = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_by_coordinates);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinates_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener el SupportMapFragment e inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar el FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab_search);
        fab.setOnClickListener(v -> showCoordinatesDialog());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Ubicación predeterminada (Barcelona)
        LatLng barcelona = new LatLng(41.3851, 2.1734);
        mMap.addMarker(new MarkerOptions()
                .position(barcelona)
                .title("Barcelona"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, ZOOM_LEVEL));
    }

    private void showCoordinatesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese Coordenadas");

        // Crear el layout con EditTexts
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        EditText etLatitude = new EditText(this);
        etLatitude.setHint("Latitud");
        etLatitude.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        layout.addView(etLatitude);

        EditText etLongitude = new EditText(this);
        etLongitude.setHint("Longitud");
        etLongitude.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        layout.addView(etLongitude);

        builder.setView(layout);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            try {
                String latitudeStr = etLatitude.getText().toString().trim();
                String longitudeStr = etLongitude.getText().toString().trim();

                // Validar que los campos no estén vacíos
                if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                    Toast.makeText(SearchByCoordinatesActivity.this, 
                            "Por favor complete ambos campos", 
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);

                // Validar rangos válidos
                if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                    Toast.makeText(SearchByCoordinatesActivity.this, 
                            "Coordenadas fuera de rango válido", 
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Navegar a la ubicación
                LatLng location = new LatLng(latitude, longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title("Ubicación: " + latitude + ", " + longitude));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL));

                Toast.makeText(SearchByCoordinatesActivity.this, 
                        "Ubicación encontrada", 
                        Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(SearchByCoordinatesActivity.this, 
                        "Coordenadas inválidas", 
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
