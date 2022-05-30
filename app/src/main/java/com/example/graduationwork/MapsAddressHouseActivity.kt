package com.example.graduationwork

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.view.WindowCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.Locale
import androidx.core.app.ActivityCompat
import com.example.graduationwork.activity.BaseActivity
import com.example.graduationwork.data.local.SharedPreference
import com.example.graduationwork.databinding.ActivityMapsBinding
import com.example.graduationwork.databinding.MyAddressBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

@DelicateCoroutinesApi
@InternalCoroutinesApi
class MapsAddressHouseActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    latLng = LatLng(location.latitude,location.longitude)
                    myLocationMarker =
                        mMap?.addMarker(
                            MarkerOptions().position(latLng!!).title(resources.getString(R.string.me))
                        )
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 17f))
                }
            }
    }

    private var mMap: GoogleMap? = null
    private lateinit var binding: MyAddressBinding

    var latLng: LatLng? = null
    var myLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitContentViewToInsets()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN

        getLastKnownLocation()

        var addresses: List<Address>
        val geocoder = Geocoder(this, Locale("RU"))

        mMap?.setOnMapClickListener {
            addresses = geocoder.getFromLocation(
                it.latitude,
                it.longitude,
                1
            )

            val country = addresses[0].countryName
            val republic = addresses[0].adminArea
            val city = addresses[0].locality
            val street = addresses[0].thoroughfare
            val house = addresses[0].subThoroughfare


         //   val address: String = addresses[0].getAddressLine(0)

            val address: String = "$country, $republic, $city, $street, $house"
            bottomSheetDialog(address)

        }
    }

    private fun fitContentViewToInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun bottomSheetDialog(address: String) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.map_house_bottom_sheet, null)
        dialog.setContentView(view)

        val button = view.findViewById<Button>(R.id.mapButton)
        val text = view.findViewById<TextView>(R.id.map_title)
        text.text = address

        button.setOnClickListener {

            val sharedPreference = SharedPreference(this@MapsAddressHouseActivity)
            sharedPreference.save(resources.getString(R.string.addressHouse), address)
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }
}