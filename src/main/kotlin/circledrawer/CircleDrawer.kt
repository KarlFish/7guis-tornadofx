package circledrawer

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import tornadofx.*
import java.util.*

/**
 *
 */
class CircleDrawer : View("My View") {

    lateinit var drawPane: Pane
    lateinit var undo: Button
    lateinit var redo: Button

    var nearestCir: Circle? = null
    val allCircles: MutableList<Circle> = arrayListOf()
    val actionManager: ActionManager = ActionManager()
    var lastMouseX: Double = 0.0
    var lastMouseY: Double = 0.0

    val transparent: Paint = Paint.valueOf("0x00000000") // transparent
    val gray: Paint = Paint.valueOf("0x00000020")        // light gray

    override val root = vbox {
        alignment = Pos.CENTER
        padding = Insets(10.0)
        spacing = 10.0

        hbox {
            alignment = Pos.CENTER
            padding = Insets(10.0)
            spacing = 10.0
            undo = button("Undo")
            redo = button("Redo")
        }

        drawPane = pane {
            vboxConstraints { vGrow = Priority.ALWAYS }
            prefWidth = 400.0
            prefHeight = 250.0
            style = "-fx-border-color:black"
        }

    }

    fun Pane.checkBounds(x: Double, y: Double): Boolean {
        return x >= 0 && y >= 0 && x < this.width && y < this.height
    }

    init {

        val clippingRect = Rectangle()
        clippingRect.widthProperty().bind(drawPane.widthProperty())
        clippingRect.heightProperty().bind(drawPane.heightProperty())
        drawPane.clip = clippingRect

        drawPane.setOnMouseClicked {

            if (drawPane.checkBounds(it.x, it.y)) {

                if (it.button == MouseButton.PRIMARY) {
                    val selectedCir = Circle(it.x, it.y, 10.0)
                    selectedCir.fill = transparent
                    selectedCir.stroke = Paint.valueOf("black")

                    actionManager.addAction(
                            redo = {
                                drawPane.addChildIfPossible(selectedCir)
                                allCircles.add(selectedCir)
                            },
                            undo = {
                                drawPane.getChildList()?.remove(selectedCir)
                                allCircles.remove(selectedCir)
                            }
                    )
                    redo.fire()

                }

                val currentNearestCir = nearestCir
                if (it.button == MouseButton.SECONDARY && currentNearestCir != null) {
                    val adjuster = CircleAdjuster(currentNearestCir)
                    openInternalWindow(adjuster)
                    adjuster.setOnUndock {
                        currentNearestCir.radiusProperty().unbind()
                        val newSize = currentNearestCir.radius
                        val oldSize = adjuster.oldSize
                        if (newSize != oldSize) {
                            actionManager.addAction(
                                    redo = { currentNearestCir.radius = newSize },
                                    undo = { currentNearestCir.radius = oldSize }
                            )
                            redo.fire()
                        }
                    }
                }

            }

        }

        drawPane.setOnMouseMoved {
            lastMouseX = it.x
            lastMouseY = it.y
        }

        undo.setOnAction { actionManager.undo() }
        redo.setOnAction { actionManager.redo() }

        val timeline = Timeline(KeyFrame(Duration.millis(80.0), EventHandler {
            fun distToM(x: Double, y: Double): Double {
                return Math.pow(lastMouseX - x, 2.0) + Math.pow(lastMouseY - y, 2.0)
            }

            val near = allCircles.filter {
                distToM(it.centerX, it.centerY) <= Math.pow(it.radius, 2.0)
            }

            nearestCir?.fill = transparent
            nearestCir = near.minBy { distToM(it.centerX, it.centerY) }

            if (drawPane.checkBounds(lastMouseX, lastMouseY))
                nearestCir?.fill = gray
        }))
        timeline.cycleCount = Animation.INDEFINITE
        timeline.play()
    }

}

class CircleAdjuster(circle: Circle) : View() {
    private var onUnDock = {}
    val oldSize = circle.radius

    override val root = vbox {
        padding = Insets(10.0)
        alignment = Pos.CENTER
        spacing = 10.0

        label("Adjust diameter of circle at (${circle.centerX}, ${circle.centerY}).")

        slider {
            min = 2.0
            max = 400.0
            blockIncrement = 1.0
            value = circle.radius
            circle.radiusProperty().bind(this.valueProperty())
        }
    }

    override fun onUndock() {
        onUnDock()
    }

    fun setOnUndock(function: () -> Unit) {
        onUnDock = function
    }
}


class ActionManager {

    private val actions = Stack<Action>()
    private var actionIndex = 0

    fun addAction(redo: () -> Unit, undo: () -> Unit) {
        while (actionIndex < actions.size) {
            actions.pop()
        }
        actions.push(Action(redo, undo))
    }

    fun redo() {
        if (actionIndex < actions.size) {
            actions[actionIndex].redo()
            actionIndex++
        }
    }

    fun undo() {
        if (actionIndex > 0) {
            actionIndex--
            actions[actionIndex].undo()
        }
    }

}

class Action(val redo: () -> Unit, val undo: () -> Unit)