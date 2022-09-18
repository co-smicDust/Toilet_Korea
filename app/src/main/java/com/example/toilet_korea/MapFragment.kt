package com.example.toilet_korea

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.maps.android.clustering.ClusterManager

class MapFragment : Fragment(), OnMapReadyCallback {

    private var clusterManager: ClusterManager<MyItem?>? = null

    val database: DatabaseReference = Firebase.database.reference

    var rootView: View? = null
    var mapView: MapView? = null

    var toilet: Toilet? = null

    // 런타임에서 권한이 필요한 퍼미션 목록
    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // 퍼미션 승인 요청시 사용하는 요청 코드
    val REQUEST_PERMISSION_CODE = 1

    // 기본 맵 줌 레벨
    val DEFAULT_ZOOM_LEVEL = 17f

    // 현재위치를 가져올수 없는 경우 서울 시청의 위치로 지도를 보여주기 위해 서울시청의 위치를 변수로 선언
    // LatLng 클래스는 위도와 경도를 가지는 클래스
    val CITY_HALL = LatLng(37.4272309, 126.99090478)

    // 구글 맵 객체를 참조할 멤버 변수
    var googleMap: GoogleMap? = null

    val bottomSheet = BottomSheet()

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        rootView = inflater.inflate(R.layout.map_view, container, false)
        mapView = rootView!!.findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)
        return rootView
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FloatingActionButton>(R.id.myLocationButton)?.setOnClickListener {
            when {
                hasPermissions() -> googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL)
                )
                else -> Toast.makeText(requireContext().applicationContext, "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onResume() {
        mapView!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(this.requireActivity())

        // 앱이 실행될때 런타임에서 위치 서비스 관련 권한체크
        if (hasPermissions()) {

            // 클러스터 매니저 세팅
            clusterManager = ClusterManager(context, googleMap)
            googleMap.setOnCameraIdleListener(clusterManager)
            googleMap.setOnMarkerClickListener(clusterManager)

            val clusterRenderer: MarkerClusterRenderer =
                MarkerClusterRenderer(context, googleMap, clusterManager)
            clusterManager!!.renderer = clusterRenderer

            // 권한이 있는 경우 맵 초기화
            initMap()
        } else {
            // 권한 요청
            requestPermissions(PERMISSIONS, REQUEST_PERMISSION_CODE)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 맵 초기화
        initMap()
    }

    // 앱에서 사용하는 권한이 있는지 체크하는 함수
    fun hasPermissions(): Boolean {
        // 퍼미션목록중 하나라도 권한이 없으면 false 반환
        for (permission in PERMISSIONS) {
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it.applicationContext,
                        permission
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    // 맵 초기화하는 함수
    @SuppressLint("MissingPermission")
    fun initMap() {
        // 맵뷰에서 구글 맵을 불러오는 함수. 컬백함수에서 구글 맵 객체가 전달됨
        mapView?.getMapAsync {
            // 구글맵 멤버 변수에 구글맵 객체 저장
            googleMap = it

            // 현재위치로 이동 버튼 비활성화
            it.uiSettings.isMyLocationButtonEnabled = false
            // 위치 사용 권한이 있는 경우
            when {
                hasPermissions() -> {
                    // 현재위치 표시 활성화
                    it.isMyLocationEnabled = true
                    // 현재위치로 카메라 이동
                    it.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            getMyLocation(),
                            DEFAULT_ZOOM_LEVEL
                        )
                    )
                }
                else -> {
                    // 권한이 없으면 서울시청의 위치로 이동
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
                }
            }
            searching()
        }
    }

    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng {
        // 위치를 측정하는 프로바이더를 GPS 센서로 지정
        val locationProvider: String = LocationManager.GPS_PROVIDER
        // 위치 서비스 객체를 불러옴
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        // 마지막으로 업데이트된 위치를 가져옴
        val lastKnownLocation: Location? = locationManager?.getLastKnownLocation(locationProvider)
        // 위도 경도 객체로 반환
        return if (lastKnownLocation != null) {
            // 위도 경도 객체로 반환
            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
        } else {
            // 위치를 구하지 못한경우 기본값 반환
            CITY_HALL
        }
    }

    //DB의 toilet reference
    val toiletRef = database.child("toilet")

    inner class ToiletThread : Thread() {
        override fun run() {
            handler.sendEmptyMessage(0)
        }
    }

    fun getDistance(latitude: Double, longitude: Double): Int {
        val currentLatLng = getMyLocation()

        val locationA = Location("A")
        val locationB = Location("B")

        locationA.latitude = currentLatLng.latitude
        locationA.longitude = currentLatLng.longitude
        locationB.latitude = latitude
        locationB.longitude = longitude

        //에뮬레이터에서 현재 위치를 구할 수 없어 임의로 기본값 선택.
        // 현재 위치 구할 수 있는 환경이라면, 위치 권한 거부했을 때의 기본값(현재 CITY_HALL)으로 바꿀 것 요망
        return if (currentLatLng != LatLng(0.0, 0.0))
            locationA.distanceTo(locationB).toInt()
        else 0
    }

    // 스레드가 다운로드 받아서 파싱한 결과를 가지고 맵 뷰에 마커를 출력해달라고 요청
    val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {

            toiletRef.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("PotentialBehaviorOverride")
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists()) {
                        // looping through to values
                        for (i in dataSnapshot.children) {
                            toilet = i.getValue(Toilet::class.java)

                            if (toilet?.latitude != null && toilet?.longitude != null){

                                val item = MyItem(toilet!!)
                                clusterManager?.addItem(item)

                            }

                            googleMap?.setOnInfoWindowClickListener(GoogleMap.OnInfoWindowClickListener {

                                val arr = it.tag.toString().split("/") //마커에 붙인 태그
                                val args = Bundle()
                                args.putString("toiletNm", it.title.toString())
                                args.putString("rdnmadr", arr[0])
                                args.putString("lnmadr", it.snippet.toString())
                                args.putString("unisexToiletYn", arr[1])
                                args.putString("menToiletBowlNumber", arr[7])
                                args.putString("menUrineNumber", arr[8])
                                args.putString("menHandicapToiletBowlNumber", arr[9])
                                args.putString("menHandicapUrinalNumber", arr[10])
                                args.putString("menChildrenToiletBowlNumber", arr[11])
                                args.putString("menChildrenUrinalNumber", arr[12])
                                args.putString("ladiesToiletBowlNumber", arr[13])
                                args.putString("ladiesHandicapToiletBowlNumber", arr[14])
                                args.putString("ladiesChildrenToiletBowlNumber", arr[15])
                                args.putString("phoneNumber", arr[2])
                                args.putString("openTime", arr[3])
                                args.putString("position", it.position.toString())
                                args.putString("emgBellYn", arr[4])
                                args.putString("enterentCctvYn", arr[5])
                                args.putString("dipersExchgPosi", arr[6])

                                args.putParcelable("latlng", it.position)

                                bottomSheet.arguments = args
                                bottomSheet.show(parentFragmentManager, bottomSheet.tag)

                                googleMap?.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(it.position, DEFAULT_ZOOM_LEVEL))

                                return@OnInfoWindowClickListener
                            })
                        }
                        // 데이터 파싱 후 줌아웃을 함으로써 마커 표출 가능
                        // 그렇지 않으면 사용자가 직접 줌아웃을 한 이후에 마커 확인 가능
                        googleMap?.animateCamera(CameraUpdateFactory.zoomOut())
                    }

                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    var toiletThread: ToiletThread? = null

    // 앱이 활성화될때 화장실 데이터를 읽어옴
    @SuppressLint("PotentialBehaviorOverride")
    override fun onStart() {
        super.onStart()
        if (toiletThread == null) {
            toiletThread = ToiletThread()
            toiletThread!!.start()
        }
    }

    // 앱이 비활성화 될때 백그라운드 작업 취소
    override fun onStop() {
        super.onStop()
        toiletThread!!.isInterrupted
        toiletThread = null
    }

    fun searching(){
        if (arguments != null && requireArguments().containsKey("toilet")) {

            val toilet = arguments?.getSerializable("toilet") as Toilet
            val searching = arguments?.getBoolean("searching")
            val chosenPosition = LatLng(toilet.latitude!!, toilet.longitude!!)

            if (searching == true){
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(chosenPosition, DEFAULT_ZOOM_LEVEL))

                val args = Bundle()
                args.putString("toiletNm", toilet.toiletNm)
                args.putString("rdnmadr", toilet.rdnmadr)
                args.putString("lnmadr", toilet.lnmadr)
                args.putString("unisexToiletYn", toilet.unisexToiletYn)
                args.putString("menToiletBowlNumber", toilet.menToiletBowlNumber.toString())
                args.putString("menUrineNumber", toilet.menUrineNumber.toString())
                args.putString("menHandicapToiletBowlNumber", toilet.menHandicapToiletBowlNumber.toString())
                args.putString("menHandicapUrinalNumber", toilet.menHandicapUrinalNumber.toString())
                args.putString("menChildrenToiletBowlNumber", toilet.menChildrenToiletBowlNumber.toString())
                args.putString("menChildrenUrinalNumber", toilet.menChildrenUrinalNumber.toString())
                args.putString("ladiesToiletBowlNumber", toilet.ladiesToiletBowlNumber.toString())
                args.putString("ladiesHandicapToiletBowlNumber", toilet.ladiesHandicapToiletBowlNumber.toString())
                args.putString("ladiesChildrenToiletBowlNumber", toilet.ladiesChildrenToiletBowlNumber.toString())
                args.putString("phoneNumber", toilet.phoneNumber)
                args.putString("openTime", toilet.openTime)
                args.putString("position", chosenPosition.toString())
                args.putString("emgBellYn", toilet.emgBellYn)
                args.putString("enterentCctvYn", toilet.enterentCctvYn)
                args.putString("dipersExchgPosi", toilet.dipersExchgPosi)

                bottomSheet.arguments = args
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }

        }
    }
}