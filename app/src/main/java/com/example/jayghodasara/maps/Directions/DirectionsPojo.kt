package com.example.jayghodasara.maps.Directions

data class DirectionsPojo(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)