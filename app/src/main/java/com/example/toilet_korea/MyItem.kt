package com.example.toilet_korea

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


class MyItem(toilet: Toilet) : ClusterItem {

    private var latLng: LatLng
    private var title: String? = null
    private var tag: String? = null

    init{
        latLng = LatLng(toilet.latitude!!, toilet.longitude!!)
        title = toilet.toiletNm
        tag = toilet.rdnmadr + "/" +
                toilet.unisexToiletYn + "/" +
                toilet.phoneNumber + "/" +
                toilet.openTime + "/" +
                toilet.emgBellYn + "/" +
                toilet.enterentCctvYn + "/" +
                toilet.dipersExchgPosi + "/" +

                toilet.menToiletBowlNumber.toString() + "/" +
                toilet.menUrineNumber.toString() + "/" +
                toilet.menHandicapToiletBowlNumber.toString() + "/" +
                toilet.menHandicapUrinalNumber.toString() + "/" +
                toilet.menChildrenToiletBowlNumber.toString() + "/" +
                toilet.menChildrenUrinalNumber.toString() + "/" +
                toilet.ladiesToiletBowlNumber.toString() + "/" +
                toilet.ladiesHandicapToiletBowlNumber.toString() + "/" +
                toilet.ladiesChildrenToiletBowlNumber.toString()
    }

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return null
    }

    fun getTag(): String? {
        return tag
    }
}