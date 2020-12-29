package com.fenix.app.service;

import com.fenix.app.util.LocationUtil;
import com.google.android.gms.maps.model.LatLng;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import lombok.var;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocationServiceTest {

    @Test
    public void t001_map_number_calc() {

        var location1_1 = new LatLng(55.52700430316578, 37.80795498761681);
        var location1_2 = new LatLng(55.52615411436204, 37.80477925209608);
        var location2_1 = new LatLng(55.526554919944495, 37.78945847397581);
        var location2_2 = new LatLng(55.52679783043131, 37.7879564369052);

        System.out.println("Distance 1_1 -> 1_2 = " + LocationUtil.distance(location1_1, location1_2));
        System.out.println("Distance 2_1 -> 2_2 = " + LocationUtil.distance(location2_1, location2_2));
        System.out.println("Distance 1_1 -> 2_1 = " + LocationUtil.distance(location1_1, location2_1));
        System.out.println("Distance 1_1 -> 2_2 = " + LocationUtil.distance(location1_1, location2_2));
        System.out.println();
        var grain = 10;
        System.out.println("Map number 1_1 = " + LocationUtil.calcMapNumber(location1_1, grain));
        System.out.println("Map number 1_2 = " + LocationUtil.calcMapNumber(location1_2, grain));
        System.out.println("Map number 2_1 = " + LocationUtil.calcMapNumber(location2_1, grain));
        System.out.println("Map number 2_2 = " + LocationUtil.calcMapNumber(location2_2, grain));

    }


}
