package com.example.toilet_korea

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.maps.android.clustering.ClusterItem
import java.io.Serializable


@IgnoreExtraProperties
data class Toilet(val toiletNm: String? = null, val rdnmadr: String? = null, val lnmadr: String? = null, val unisexToiletYn: String? = null,
                  val menToiletBowlNumber: Int? = null, val menUrineNumber: Int? = null, val menHandicapToiletBowlNumber: Int? = null,
                  val menHandicapUrinalNumber: Int? = null, val menChildrenToiletBowlNumber: Int? = null, val menChildrenUrinalNumber: Int? = null,
                  val ladiesToiletBowlNumber: Int? = null, val ladiesHandicapToiletBowlNumber: Int? = null, val ladiesChildrenToiletBowlNumber: Int? = null,
                  val phoneNumber: String? = null, val openTime: String? = null, val latitude: Double? = null, val longitude: Double? = null,
                  val emgBellYn: String? = null, val enterentCctvYn: String? = null, val dipersExchgPosi: String? = null): Serializable {


}