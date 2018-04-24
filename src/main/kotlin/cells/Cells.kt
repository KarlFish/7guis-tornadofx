package cells

import javafx.geometry.Insets
import tornadofx.View
import tornadofx.label
import tornadofx.vbox

/**
 *
 */
class Cells : View("Cells") {
    override val root = vbox {
        prefWidth = 300.0
        prefHeight = 200.0
        padding = Insets(10.0)
        label("Not implemented yet")
    }
}
