package objects.emulator

import algorithm.geometry.Line
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node

class EmulatedWall constructor(val line: Line, val xFrom: Int, val xTo: Int, val yFrom: Int, val yTo: Int) {

    companion object {
        fun wallFromXml(wall: Node): EmulatedWall? {
            val points = wall.attributes
            val startX: Int
            val endX: Int
            val endY: Int
            val startY: Int
            try {
                startX = getAttributeValueByName(points, "startX").toInt()
                startY = getAttributeValueByName(points, "startY").toInt()
                endX = getAttributeValueByName(points, "endX").toInt()
                endY = getAttributeValueByName(points, "endY").toInt()
            } catch (e: NumberFormatException) {
                return null
            }
            val line = Line((startY - endY).toDouble(), (endX - startX).toDouble(), (startX * endY - startY * endX).toDouble())
            return EmulatedWall(line, startX, endX, startY, endY)
        }

        private fun getAttributeValueByName(attributes: NamedNodeMap, attributeName: String): String {
            return attributes.getNamedItem(attributeName).nodeValue
        }
    }
}