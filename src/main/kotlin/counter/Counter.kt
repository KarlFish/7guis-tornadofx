package counter

import javafx.geometry.Insets
import tornadofx.*

class Counter : View("Counter") {
    override val root = borderpane {
        padding = Insets(10.0)
        center {
            hbox {
                spacing = 10.0
                val field = textfield {
                    text = "0"
                    prefColumnCount = 8
                    isEditable = false
                }
                button {
                    text = "Count"
                    setOnAction { field.text = "${field.text.toInt() + 1}"}
                }
            }
        }
    }
}
