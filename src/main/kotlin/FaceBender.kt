import org.openrndr.application
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadImage
import org.openrndr.math.Vector2
import org.openrndr.math.clamp
import org.openrndr.math.map
import org.openrndr.shape.Rectangle
import kotlin.math.atan2

fun main() = application {
    configure {
        width = 1024
        height = 1024
    }

    program {
        class Face(
            val rect: Rectangle,
            val facesFrames: MutableList<ColorBuffer>,
            val frontalFacesFrames: MutableList<ColorBuffer>
        ) {
            fun draw(position: Vector2) {

                // calculate angle
                val angle =  Math.toDegrees(atan2(position.y - rect.center.y, position.x - rect.center.x))

                // match to image
                var mapToImage = 0
                if(angle >0 && angle < 180.0) {
                    mapToImage = map(0.0, 180.0, 89.0, 179.0, angle).toInt().clamp(0, facesFrames.size - 1)
                } else {
                    mapToImage = map(0.0, -180.0, 89.0, 0.0, angle).toInt().clamp(0, facesFrames.size - 1)
                }

                // calculate distance
                val d = position.distanceTo(rect.center)

                //
                if(d < 150) {
                    mapToImage = map(0.0, 150.0, 0.0, 5.0, d).toInt().clamp(1, 5)
                    drawer.image(frontalFacesFrames[mapToImage], rect.x, rect.y, rect.width, rect.height)
                } else {
                    drawer.image(facesFrames[mapToImage], rect.x, rect.y, rect.width, rect.height)
                }
            }
        }

        val faces = mutableListOf<Face>()
        val facesFrames = mutableListOf<ColorBuffer>()
        (1 ..  179).forEach {
            facesFrames.add(loadImage("data/frames/rotate/out$it.png"))
        }
        val frontalFacesFrames = mutableListOf<ColorBuffer>()
        (1 ..  10).forEach {
            frontalFacesFrames.add(loadImage("data/frames/frontal/out$it.png"))
        }

        val count = 14
        (0..count).forEach { x ->
            (0..count).forEach { y ->
                val stepX = width / count*1.0
                val stepY = height / count*1.0
                faces.add(Face(Rectangle(x*stepX, y*stepY, stepX, stepY), facesFrames, frontalFacesFrames))
            }
        }

        extend {
            faces.forEach {
                it.draw(mouse.position)
            }
            drawer.stroke = null
            drawer.circle(mouse.position, 20.0)
        }
    }
}
