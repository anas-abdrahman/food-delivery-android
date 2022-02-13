package com.kukus.library.remote

import com.kukus.library.model.Direction
import org.json.JSONObject



class DirectionJSONInfo{

    fun info(jObject: JSONObject): Direction {

        var startAddress = ""
        var endAddress   = ""

        var distanceAll = ""
        var durationAll = ""

        var distance    = ""
        var duration    = ""

        try {

            val jRoutes     = jObject.getJSONArray("routes")
            val jLegs       = (jRoutes.get(0)       as JSONObject).getJSONArray("legs")
            val jDistance   = (jLegs.get(0)         as JSONObject).getJSONArray("steps")

            startAddress   = ((jLegs.get(0)        as JSONObject).get("start_address")) as String
            endAddress     = ((jLegs.get(0)        as JSONObject).get("end_address")) as String

            distanceAll     = ((jLegs.get(0)        as JSONObject).get("distance") as JSONObject).get("text") as String
            durationAll     = ((jLegs.get(0)        as JSONObject).get("duration") as JSONObject).get("text") as String

            distance        = ((jDistance.get(0)    as JSONObject).get("distance") as JSONObject).get("text") as String
            duration        = ((jDistance.get(0)    as JSONObject).get("duration") as JSONObject).get("text") as String

        } catch (e: Exception) { e.printStackTrace()}

        return Direction(startAddress, endAddress, distanceAll, durationAll, distance, duration)
    }


}