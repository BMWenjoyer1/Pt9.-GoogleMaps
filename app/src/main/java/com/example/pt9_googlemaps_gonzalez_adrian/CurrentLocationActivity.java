package com.example.pt9_googlemaps_gonzalez_adrian;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CurrentLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private final int ZOOM_LEVEL = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_current_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.current_location_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtener el SupportMapFragment e inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar el FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab_current_location);
        fab.setOnClickListener(v -> getCurrentLocation());
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

        // Intentar obtener ubicación actual automáticamente
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permisos, solicitarlos
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        // Obtener ubicación actual
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), 
                                location.getLongitude());
                        
                        // Limpiar el mapa
                        mMap.clear();
                        
                        // Agregar marcador en la ubicación actual
                        mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("Mi Ubicación Actual")
                                .snippet("Latitud: " + location.getLatitude() + 
                                        "\nLongitud: " + location.getLongitude()));
                        
                        // Mover la cámara a la ubicación actual
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL));
                        
                        Toast.makeText(CurrentLocationActivity.this,
                                "Ubicación actual encontrada",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CurrentLocationActivity.this,
                                "No se pudo obtener la ubicación actual",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Toast.makeText(CurrentLocationActivity.this,
                            "Error al obtener la ubicación: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, 
            String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, intentar obtener ubicación
                getCurrentLocation();
            } else {
                // Permiso denegado
                Toast.makeText(this,
                        "Permiso de ubicación denegado",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
