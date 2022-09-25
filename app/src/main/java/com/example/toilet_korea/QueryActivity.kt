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

    val toiletList = ArrayList<Toilet>()
    val resultList = ArrayList<Toilet>()

    private lateinit var toilet:Toilet

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

    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng {
        // 위치를 측정하는 프로바이더를 GPS 센서로 지정
        val locationProvider: String = LocationManager.GPS_PROVIDER
        // 위치 서비스 객체를 불러옴
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 마지막으로 업데이트된 위치를 가져옴
        val lastKnownLocation: Location? = locationManager.getLastKnownLocation(locationProvider)
        // 위도 경도 객체로 반환
        return if (lastKnownLocation != null) {
            // 위도 경도 객체로 반환
            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
        } else {
            // 위치를 구하지 못한경우 기본값 반환
            CITY_HALL
        }
    }


    private fun getDistance(latitude: Double, longitude: Double): Int {

        val locationA = Location("A")
        val locationB = Location("B")

        locationA.latitude = currentLatLng.latitude
        locationA.longitude = currentLatLng.longitude
        locationB.latitude = latitude
        locationB.longitude = longitude

        //에뮬레이터에서 현재 위치를 구할 수 없어 임의로 기본값 선택.
        // 현재 위치 구할 수 있는 환경이라면, 위치 권한 거부했을 때의 기본값(현재 CITY_HALL)으로 바꿀 것 요망
        return if (currentLatLng != CITY_HALL)
            locationA.distanceTo(locationB).toInt()
        else 0
    }

    private fun distanceFilter(latitude: Double, longitude: Double) {

        val distance = getDistance(latitude, longitude)

        if ("distance1000" in arr) {
            if (distance <= 1000) { toiletList.add(toilet) }
        } else if ("distance500" in arr) {
            if (distance <= 500) { toiletList.add(toilet) }
        } else if ("distance200" in arr) {
            if (distance <= 200) { toiletList.add(toilet) }
        }
    }

    private fun unisexFilter() {
        if (toilet.unisexToiletYn == "N") { toiletList.add(toilet) }
    }

    private fun menDisabledFilter() {
        if (toilet.menHandicapToiletBowlNumber != 0 || toilet.menHandicapUrinalNumber != 0) { toiletList.add(toilet) }
    }

    private fun womenDisabledFilter() {
        if (toilet.ladiesHandicapToiletBowlNumber != 0) { toiletList.add(toilet) }
    }

    private fun menWithChildrenFilter() {
        if (toilet.menChildrenToiletBowlNumber != 0 || toilet.menChildrenUrinalNumber != 0) { toiletList.add(toilet) }
    }

    private fun womenWithChildrenFilter() {
        if (toilet.ladiesChildrenToiletBowlNumber != 0) { toiletList.add(toilet) }
    }

    override fun onStart() {
        super.onStart()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (i in dataSnapshot.children) {
                        toilet = i.getValue(Toilet::class.java)!!

                        if (toilet.latitude != null && toilet.longitude != null)
                            distanceFilter(toilet.latitude!!, toilet.longitude!!)
                        if ("notUnisex" in arr)
                            unisexFilter()
                        if ("menDisabled" in arr)
                            menDisabledFilter()
                        if ("womenDisabled" in arr)
                            womenDisabledFilter()
                        if ("menWithChildren" in arr)
                            menWithChildrenFilter()
                        if ("womenWithChildren" in arr)
                            womenWithChildrenFilter()

                    }
                    val result = toiletList.groupingBy { it }.eachCount().filter { it.value == arr.size }.keys
                    resultList.addAll(result)

                    if (resultList.isEmpty()){
                        Toast.makeText(applicationContext, "검색 결과가 없습니다.", Toast.LENGTH_LONG).show()
                    } else {
                        toiletAdapter.submitList(resultList)
                        binding.recyclerToilet.adapter = toiletAdapter
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

}