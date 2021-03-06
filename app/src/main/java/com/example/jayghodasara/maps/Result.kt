package com.example.jayghodasara.maps

data class Result(
        val geometry: Geometry,
        val icon: String,
        val id: String,
        val name: String,
        val place_id: String,
        val reference: String,
        val scope: String,
        val types: List<String>,
        val vicinity: String,
        val rating: Double,
        val opening_hours: OpeningHours
)