package com.example.graduationwork


import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.graduationwork.activity.BaseActivity
import com.example.graduationwork.data.local.SharedPreference

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.graduationwork.databinding.ActivityMapsBinding
import com.example.graduationwork.extension.addSystemWindowInsetToMargin
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitContentViewToInsets()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    // Переопределение функции onMapReady для задания необходимых параметров карты.
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Установка типа карты
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN

        // Установка ширины и долготы для маркеров, которые будут размещены на карте.
        val shop = LatLng(54.18450044600056, 45.17315326140166)
        val shop2 = LatLng(54.181975050231564, 45.19251562698364)

        var title = ""

        // Размещение маркера на карте с указанием его координат,заголовка и описания.
        // Установка собственного изображения для маркера.
        mMap.addMarker(
            MarkerOptions().position(shop).title("Ул. Коммунистическая, 62")
                .snippet("Открыто до 18:00").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.marker)
                )
        )

        mMap.addMarker(
            MarkerOptions().position(shop2).title("Ул. Рабочая, 8")
                .snippet("Открыто до 18:00").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.marker)

                )
        )
        // Фокусировка камеры на маркере. Установка начального фокусного расстояния карты.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shop, 13f))

        binding.mapButton.addSystemWindowInsetToMargin(bottom = true)


        binding.mapButton.setOnClickListener {
            if (title != "") {
                val sharedPreference = SharedPreference(this@MapsActivity)
                sharedPreference.save("address", title)
                finish()
            } else {
                val sb = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Вы не выбрали пункт выдачи",
                    Snackbar.LENGTH_LONG
                )
                sb.view.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.snackBarError
                    )
                )
                sb.anchorView = binding.mapButton
                sb.show()
            }
        }

        // Добавление слушателя для маркеров для сохранения и отображения их заголовков.
        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            title = marker.title.toString()

            true
        }
    }
    private fun fitContentViewToInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

// Узнать координаты любого места https://snipp.ru/tools/address-coord
// изменить размерность png
//https://www.iloveimg.com/ru/resize-image/resize-png#resize-options,pixels