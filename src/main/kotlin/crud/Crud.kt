package crud

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import tornadofx.*

/**
 *
 */
class Crud : View("CRUD") {
    lateinit var create: Button
    lateinit var update: Button
    lateinit var delete: Button
    lateinit var nameField: TextField
    lateinit var surnameField: TextField
    lateinit var prefix: TextField
    lateinit var listView: ListView<Person>

    val people = DataBase(Person("Hans", "Emil"), Person("Max", "Mustermann"), Person("Roman", "Tisch"))

    override val root = vbox {
        padding = Insets(10.0)
        spacing = 10.0

        hbox {
            spacing = 10.0
            vbox {
                spacing = 10.0
                hboxConstraints { hGrow = Priority.ALWAYS }
                hbox {
                    spacing = 10.0
                    label("Filter prefix:")
                    prefix = textfield {
                        prefColumnCount = 4
                    }
                }

                listView = listview <Person> {
                    items = people.peopleFXList.filtered { true }
                }
            }


            fun Node.at(x: Int, y: Int) {
                gridpaneConstraints { columnRowIndex(x, y) }
            }

            gridpane {
                alignment = Pos.CENTER
                padding = Insets(10.0)
                vgap = 10.0
                hgap = 10.0
                hboxConstraints { hGrow = Priority.ALWAYS }
                label("Name:") { at(0, 0) }
                nameField = textfield { at(1, 0) }
                label("Surname:") { at(0, 1) }
                surnameField = textfield { at(1, 1) }
            }

        }

        hbox {
            spacing = 10.0
            create = button("Create")
            update = button("Update")
            delete = button("Delete")
        }
    }

    init {
        update.disableProperty().bind(listView.selectionModel.selectedIndexProperty().isEqualTo(-1))
        delete.disableProperty().bind(listView.selectionModel.selectedIndexProperty().isEqualTo(-1))

        create.setOnAction {
            people.add(Person(nameField.text, surnameField.text))
            listView.refresh()
        }
        update.setOnAction {
            val p = listView.selectionModel.selectedItem
            people.update(p, nameField.text, surnameField.text)
        }
        delete.setOnAction {
            people.remove(listView.selectionModel.selectedItem)
            listView.refresh()
        }
        prefix.textProperty().onChange {
            listView.items = people.filtered { it.surname.startsWith(prefix.text) }
            listView.refresh()
        }

    }
}

data class Person(var firstname: String, var surname: String) {
    override fun toString(): String {
        return "$surname, $firstname"
    }
}

class DataBase(vararg people: Person) {
    private val peopleArrayList = people.toMutableList()
    val peopleFXList: ObservableList<Person> = FXCollections.observableArrayList(peopleArrayList)

    init {
        peopleFXList.addListener(
                ListChangeListener<Person> { c ->
                    while (c.next()) {
                        if (c.wasReplaced()) peopleArrayList[c.from] = c.addedSubList[0]
                        else {
                            if (c.wasAdded()) peopleArrayList.add(c.addedSubList[0])
                            if (c.wasRemoved()) peopleArrayList.removeAt(c.from)
                        }
                    }
                })
    }

    fun filtered(predicate: (Person) -> Boolean): FilteredList<Person> {
        return peopleFXList.filtered { predicate(it) }
    }

    fun remove(selectedItem: Person) {
        peopleFXList.remove(selectedItem)
    }

    fun update(p: Person, name: String, surname: String) {
        p.firstname = name
        p.surname = surname
    }

    fun add(person: Person) {
        peopleFXList.add(person)
    }
}