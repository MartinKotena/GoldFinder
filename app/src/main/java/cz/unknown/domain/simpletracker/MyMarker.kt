package cz.unknown.domain.simpletracker

//data class MyMarker(var myMarker: Marker, var path: String )
data class MyMarker(
        var tag: String,
        var title: String,
        var snippet: String,
        var path: String,
        var longitude: Double,
        var latitude: Double
)
