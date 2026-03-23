package com.example.pt9_googlemaps_gonzalez_adrian;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchByLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private final int ZOOM_LEVEL = 15;
    private static final String TAG = "SearchByLocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_by_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.location_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Geocoder
        geocoder = new Geocoder(this, Locale.getDefault());

        // Obtener el SupportMapFragment e inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar el FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab_search);
        fab.setOnClickListener(v -> showLocationDialog());
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

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese Nombre de Población");

        EditText etLocationName = new EditText(this);
        etLocationName.setHint("Ej: Barcelona, Madrid, Valencia");
        etLocationName.setPadding(16, 16, 16, 16);
        builder.setView(etLocationName);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String locationName = etLocationName.getText().toString().trim();

            if (locationName.isEmpty()) {
                Toast.makeText(SearchByLocationActivity.this,
                        "Por favor ingrese un nombre de población",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Usar Geocoder para obtener coordenadas
            searchLocationByName(locationName);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void searchLocationByName(String locationName) {
        try {
            // Verificar si Geocoder está disponible
            if (!Geocoder.isPresent()) {
                Toast.makeText(this,
                        "Geocoder no está disponible. Intenta en un dispositivo físico.",
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "Geocoder no está presente");
                return;
            }

            // Para API 33+, usar el método con callback
            int apiLevel = android.os.Build.VERSION.SDK_INT;
            
            if (apiLevel >= android.os.Build.VERSION_CODES.TIRAMISU) {
                // API 33+ - usar método con callback
                Log.d(TAG, "Usando método callback para API 33+ con: " + locationName);
                geocoder.getFromLocationName(locationName, 5, new Geocoder.GeocodeListener() {
                    @Override
                    public void onGeocode(List<Address> addresses) {
                        if (addresses == null || addresses.isEmpty()) {
                            Log.w(TAG, "No se encontraron resultados para: " + locationName);
                            Toast.makeText(SearchByLocationActivity.this,
                                    "Población no encontrada: " + locationName + "\nIntenta con el nombre en español o inglés",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        Address address = addresses.get(0);
                        Log.d(TAG, "Dirección encontrada: " + address.getAddressLine(0));
                        
                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                        // Limpiar el mapa y agregar marcador
                        mMap.clear();
                        String markerTitle = address.getAddressLine(0) != null ? 
                                address.getAddressLine(0) : locationName;
                        mMap.addMarker(new MarkerOptions()
                                .position(location)
                                .title(markerTitle));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL));

                        Toast.makeText(SearchByLocationActivity.this,
                                "Población encontrada: " + markerTitle,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // API < 33 - usar método deprecado
                Log.d(TAG, "Usando método deprecado para API < 33 con: " + locationName);
                new Thread(() -> {
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(locationName, 5);

                        if (addresses == null || addresses.isEmpty()) {
                            Log.w(TAG, "No se encontraron resultados para: " + locationName);
                            runOnUiThread(() -> Toast.makeText(SearchByLocationActivity.this,
                                    "Población no encontrada: " + locationName + "\nIntenta con el nombre en español o inglés",
                                    Toast.LENGTH_LONG).show());
                            return;
                        }

                        Address address = addresses.get(0);
                        Log.d(TAG, "Dirección encontrada: " + address.getAddressLine(0));
                        
                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                        runOnUiThread(() -> {
                            // Limpiar el mapa y agregar marcador
                            mMap.clear();
                            String markerTitle = address.getAddressLine(0) != null ? 
                                    address.getAddressLine(0) : locationName;
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(markerTitle));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL));

                            Toast.makeText(SearchByLocationActivity.this,
                                    "Población encontrada: " + markerTitle,
                                    Toast.LENGTH_SHORT).show();
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "Error en búsqueda de ubicación", e);
                        runOnUiThread(() -> Toast.makeText(SearchByLocationActivity.this,
                                "Error al buscar: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
                    }
                }).start();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error inesperado: " + e.getMessage(), e);
            Toast.makeText(SearchByLocationActivity.this,
                    "Error inesperado: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
