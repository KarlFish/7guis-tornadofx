package flightbooker

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

/**
 *
 */
class FlightBooker : View("Flight Booker") {
    lateinit var flightType: ComboBox<String>
    lateinit var startDate: TextField
    lateinit var returnDate: TextField
    lateinit var bookButon: Button

    val validDates = SimpleBooleanProperty()
    val parsedStart = SimpleObjectProperty<LocalDate?>()
    val parsedReturn = SimpleObjectProperty<LocalDate?>()


    override val root = vbox {
        padding = Insets(10.0)
        spacing = 10.0

        flightType = combobox <String> {
            listOf("one-way flight", "return flight").forEach { items.add(it) }
            value = "one-way flight"
            useMaxWidth = true
        }

        startDate = textfield {
            text = LocalDate.now().toString()
        }

        returnDate = textfield {
            text = LocalDate.now().toString()
        }

        bookButon = button {
            text = "Book"
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
        }
    }

    init {

        parsedStart.bind(startDate.textProperty().objectBinding(op = String?::asDate))
        parsedReturn.bind(returnDate.textProperty().objectBinding(op = String?::asDate))

        startDate.styleProperty().bind(parsedStart.stringBinding(op = {
            if (it == null) "-fx-background-color: lightcoral" else ""
        }))
        returnDate.styleProperty().bind(parsedReturn.stringBinding(op = {
            if (it == null) "-fx-background-color: lightcoral" else ""
        }))

        returnDate.disableProperty().bind(flightType.valueProperty().booleanBinding(op = {
            it != null && it == "one-way flight"
        }))

        validDates.bind(Bindings.createBooleanBinding(Callable <Boolean> {
            parsedStart.get() != null && parsedReturn.get() != null &&
                    parsedStart.get()!! <= parsedReturn.get()
        }, parsedStart, parsedReturn))

        bookButon.disableProperty().bind(validDates.not())

    }
}

val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
fun String?.asDate(): LocalDate? {
    try {
        return LocalDate.from(formatter.parse(this))
    } catch (e: Exception) {
        return null
    }
}
