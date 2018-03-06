package me.ebbo

import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.HtmlEmail
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.*
import java.text.DecimalFormat

fun main(args: Array<String>)
{
    launch(GUI::class.java)
}

//Configure
val mailDomain = "domain.com"
val imapAddress = "imap.domain.com"
val fritzPrice = 0.80
val matePrice = 0.80


fun sendMail(drinkersMail: String, price: Double) {
    val senderEmail = "drinklist@$mailDomain"
    val password = "" //No password needed
    val toMail = drinkersMail
    val paymeUsername = "username"

    val email = HtmlEmail()
    email.hostName = "$imapAddress" //Address to imap Server
    email.setAuthenticator(DefaultAuthenticator(senderEmail, password))
    email.isSSLOnConnect = false
    email.setFrom(senderEmail)
    email.addTo(toMail)
    email.subject = "DrinkList Payment"
    email.setHtmlMsg(
            "<html>" +
                    "<h1>DrinkList Payment</h1>" +
                    "Hello Drinker you need to pay $price Euro.<br>" +
                    "Please send the money to: <a href=\"https://www.paypal.me/$paymeUsername/$price\">Paypal.me/$paymeUsername</a>" +
                    "</html>")
    email.send()
}


class GUI : Application()
{

    private val data = FXCollections.observableArrayList(
            Person("Person1", "Person1@$mailDomain",0,0,0.0),
            Person("Person2", "Person2@$mailDomain",0,0,0.0),
            Person("Person3",  "Person3@$mailDomain",0,0,0.0)
    )


    override fun start(primaryStage: Stage)
    {
        primaryStage.title = "Drink List"

        var table = TableView<Person>()

        table.setEditable(true)

        val nameCol = TableColumn<Person,String>("Name")
        nameCol.setMinWidth(100.0)
        nameCol.setCellValueFactory(
                PropertyValueFactory<Person, String>("name"))
        nameCol.setCellFactory({ column -> EditCell.createStringEditCell() })

        val emailCol = TableColumn<Person,String>("Email")
        emailCol.setMinWidth(200.0)
        emailCol.setCellValueFactory(
                PropertyValueFactory<Person, String>("email"))
        emailCol.setCellFactory({ column -> EditCell.createStringEditCell() })
        emailCol.setOnEditCommit {
            it.rowValue.setEmail(it.newValue)
            table.refresh()
        }

        val fritzCol = TableColumn<Person,Int>("Fritz Cola")
        fritzCol.setMinWidth(100.0)
        fritzCol.setCellValueFactory(
                PropertyValueFactory<Person, Int>("fritz"))
        fritzCol.setCellFactory({ column -> EditCell.createIntegerEditCell() })
        fritzCol.setOnEditCommit {
            it.rowValue.setFritz(it.newValue)
            table.refresh()
        }

        val mateCol = TableColumn<Person,Int>("Mate")
        mateCol.setMinWidth(100.0)
        mateCol.setCellValueFactory(
                PropertyValueFactory<Person, Int>("mate"))
        mateCol.setCellFactory({ column -> EditCell.createIntegerEditCell() })
        mateCol.setOnEditCommit {
            it.rowValue.setMate(it.newValue)
            table.refresh()
        }

        val totalCol = TableColumn<Person,Double>("Total Price")
        totalCol.setMinWidth(100.0)
        totalCol.setCellValueFactory(
                PropertyValueFactory<Person, Double>("total"))

        table.setItems(data)
        table.getColumns().addAll(nameCol, emailCol, fritzCol, mateCol, totalCol)

        val layout = VBox().apply {
            val addPersonButton = Button("Add new Person")
            addPersonButton.setOnMouseClicked {
                data.add(Person("Person", "person@$mailDomain",0,0,0.0))
                table.refresh()
            }
            val sendEmailToAllButton = Button("Send email to all")
            sendEmailToAllButton.setOnMouseClicked {
               for (person in data)
                {
                    sendMail(person.getEmail(), person.getTotal())
                }
            }
            children.add(sendEmailToAllButton)
            children.add(addPersonButton)
            children.add(table)

        }
        primaryStage.run {
            scene = Scene(layout)
            show()
        }
    }

}

private fun <E> ObservableList<E>.addListener(changeListener: ChangeListener<Person>) {}

data class Person(var lName: String, var lEmail: String, var lFritz: Int,var lMate: Int,var lTotal: Double) {

    val username = SimpleStringProperty(lName)
    val email = SimpleStringProperty(lEmail)
    val fritz = SimpleIntegerProperty(lFritz)
    val mate = SimpleIntegerProperty(lMate)
    val price = SimpleDoubleProperty(lTotal)

    fun getName(): String {
        return username.get()
    }

    fun setName(lName: String) {
        username.set(lName)
    }

    fun getFritz(): Int {
        return fritz.get()
    }

    fun setFritz(setFritz: Int) {
        fritz.set(setFritz)
    }

    fun getEmail(): String {
        return email.get()
    }

    fun setEmail(setMail: String) {
        email.set(setMail)
    }

    fun getMate(): Int {
        return mate.get()
    }

    fun setMate(setMate: Int) {
        mate.set(setMate)
    }

    fun getTotal(): Double {
        var format = DecimalFormat("#.###")
        val price =  getMate() * matePrice + getFritz() * fritzPrice
        return format.format(price).toDouble()
    }

    fun setTotal(mate: Int, fritz:Int) {
        price.set(mate * matePrice + fritz * fritzPrice)
    }
}