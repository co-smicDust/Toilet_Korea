package com.example.toilet_korea

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toilet_korea.databinding.ActivityQueryBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*


class QueryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQueryBinding
    private lateinit var database: DatabaseReference

    private val toiletAdapter: ToiletAdapter by lazy {
        ToiletAdapter(resultList)
    }

    var toilets: ArrayList<Toilet>? = null

    var toiletList = ArrayList<Toilet>()
    var resultList = ArrayList<Toilet>()

    private var checked: String? = null
    private lateinit var arr: List<String>

    private val CITY_HALL = MapFragment().CITY_HALL
    private var currentLatLng:LatLng = CITY_HALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainActivity().finish()

        binding = ActivityQueryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing recyclerview
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerToilet.layoutManager = llm
        binding.recyclerToilet.adapter = toiletAdapter

        database = FirebaseDatabase.getInstance().getReference("toilet")

        checked = intent.getStringExtra("checked")
        arr = checked?.split("/")!!

        currentLatLng = getMyLocation()
    }

    fun getDistance(latitude: Double, longitude: Double): Int {
        val currentLatLng = MapFragment().getMyLocation()

        val locationA = Location("A")
        val locationB = Location("B")

        locationA.latitude = currentLatLng.latitude
        locationA.longitude = currentLatLng.longitude
        locationB.latitude = latitude
        locationB.longitude = longitude

        return if (currentLatLng != CITY_HALL)
            locationA.distanceTo(locationB).toInt()
        else 0
    }

    override fun onStart() {
        super.onStart()

        toilets = DataHolder.instance.data

        toiletList.clear()
        toiletList.addAll(toilets!!)
        when {
            "distance1000" in arr -> {
                toiletList = toiletList.filter { getDistance(it.latitude!!, it.longitude!!) <= 1000 } as ArrayList<Toilet>
            }
            "distance500" in arr -> {
                toiletList = toiletList.filter { getDistance(it.latitude!!, it.longitude!!) <= 500 } as ArrayList<Toilet>
            }
            "distance200" in arr -> {
                toiletList = toiletList.filter { getDistance(it.latitude!!, it.longitude!!) <= 200 } as ArrayList<Toilet>
            }
        }

        if ("notUnisex" in arr)
            toiletList = toiletList.filter { it.unisexToiletYn == "N" } as ArrayList<Toilet>
        if ("menDisabled" in arr)
            toiletList = toiletList.filter { it.menHandicapToiletBowlNumber != 0 || it.menHandicapUrinalNumber != 0 } as ArrayList<Toilet>
        if ("womenDisabled" in arr)
            toiletList = toiletList.filter { it.ladiesHandicapToiletBowlNumber != 0 } as ArrayList<Toilet>
        if ("menWithChildren" in arr)
            toiletList = toiletList.filter { it.menChildrenToiletBowlNumber != 0 || it.menChildrenUrinalNumber != 0 } as ArrayList<Toilet>
        if ("womenWithChildren" in arr)
            toiletList = toiletList.filter { it.ladiesChildrenToiletBowlNumber != 0 } as ArrayList<Toilet>

        resultList.clear()
        resultList.addAll(toiletList)

        if (resultList.isEmpty()){
            Toast.makeText(applicationContext, "검색 결과가 없습니다.", Toast.LENGTH_LONG).show()
        } else {
            toiletAdapter.submitList(resultList)
            binding.recyclerToilet.adapter = toiletAdapter
        }

    }

    override fun onStop() {
        super.onStop()

        DataHolder.instance.data = ArrayList<Toilet>(toilets!!)
    }

}