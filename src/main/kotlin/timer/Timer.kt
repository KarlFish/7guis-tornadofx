package timer

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.Slider
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import javafx.util.Duration
import tornadofx.*
import java.text.NumberFormat

/**
 *
 */
class Timer : View("Timer") {
    lateinit var progress: ProgressBar
    lateinit var elapsed: Label
    lateinit var slider: Slider
    lateinit var reset: Button

    val time = SimpleDoubleProperty()

    override val root = vbox {
        padding = Insets(10.0)
        spacing = 10.0

        hbox {
            spacing = 10.0
            label("Elapsed Time:")
            progress = progressbar {
                hboxConstraints { hGrow = Priority.ALWAYS }
                useMaxWidth = true
            }
        }

        elapsed = label("0.0s")

        hbox {
            spacing = 10.0
            label("Duration:")
            slider = slider(min = 1.0, max = 100.0, value = 20.0) {
                blockIncrement = 0.1
                hboxConstraints { hGrow = Priority.ALWAYS }
            }
        }

        reset = button("Reset") {
            useMaxWidth = true
            textAlignment = TextAlignment.CENTER
        }
    }

    init {
        val formatter = NumberFormat.getNumberInstance()
        formatter.isGroupingUsed = false
        formatter.maximumFractionDigits = 1
        elapsed.textProperty().bind(time.stringBinding(op = { num -> formatter.format(num) }))
        progress.progressProperty().bind(time.divide(slider.valueProperty()))
        reset.setOnAction { time.value = 0.0 }

        val timeline = Timeline(KeyFrame(Duration.millis(10.0), EventHandler<ActionEvent> {
            if (time.value < slider.value)
                time.value = time.value + 0.01
            else
                time.value = slider.value
        }))

        timeline.cycleCount = Animation.INDEFINITE
        timeline.play()

    }
}
