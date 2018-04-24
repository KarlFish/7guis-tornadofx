package temperature

import javafx.geometry.Insets
import javafx.scene.layout.Priority
import javafx.util.StringConverter
import tornadofx.*
import java.text.NumberFormat

/**
 *
 */
class TemperatureConverter : View("TempConv") {

    val formatter = NumberFormat.getNumberInstance()!!

    init {
        formatter.maximumFractionDigits = 1
        formatter.isGroupingUsed = false
    }


    private fun tryConvert(str:String, conversion: (Double) -> Double): String {
        return try {
            formatter.format(conversion(str.toDouble()))
        } catch (e: NumberFormatException) {
            ""
        }
    }


    override val root = anchorpane {
        padding = Insets(10.0)

        hbox {
            anchorpaneConstraints {
                topAnchor = 10.0
                bottomAnchor = 10.0
                leftAnchor = 10.0
                rightAnchor = 10.0
            }
            spacing = 10.0

            val cel = textfield {
                prefColumnCount = 5
                hboxConstraints { hGrow = Priority.ALWAYS }
            }

            label("Celsius")
            label("=")

            val fah = textfield {
                prefColumnCount = 5
                hboxConstraints { hGrow = Priority.ALWAYS }
            }
            label("Fahrenheit")


            cel.textProperty().bindBidirectional(fah.textProperty(), object : StringConverter<String>() {
                /**  */
                override fun toString(fahString: String): String {
                    return tryConvert(fahString, { temp -> (temp - 32) * 5 / 9.0 })
                }

                /** */
                override fun fromString(celString: String): String {
                    return tryConvert(celString, { temp -> temp * 9 / 5.0 + 32 })
                }
            })
        }

    }
}
