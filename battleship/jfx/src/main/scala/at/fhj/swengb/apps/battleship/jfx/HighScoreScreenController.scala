package at.fhj.swengb.apps.battleship.jfx

import java.io._
import java.net.URL
import java.util.ResourceBundle
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import scala.collection.JavaConverters._
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.{TableColumn, TableView}

class HighScoreScreenController extends Initializable {
  @FXML private var tableHighscore: TableView[HighScore] = _
  @FXML private var date: TableColumn[HighScore, String] = new TableColumn[HighScore, String]("Date")
  @FXML private var winner: TableColumn[HighScore, String] = new TableColumn[HighScore, String]("Winner")
  @FXML private var nameOfGame: TableColumn[HighScore, String] = new TableColumn[HighScore, String]("nameOfGame")
  @FXML private var moves: TableColumn[HighScore, String] = new TableColumn[HighScore, String]("Moves")

  var actualScene: Scene = _

  // INIT

  case class HighScore(date: String, nameOfBattle: String, winner: String, rounds: String) {

    val battleDate = new SimpleStringProperty(date)
    def getDate : String = battleDate.get()
    def setDate(d: String): Unit = battleDate.set(d)

    val nameOfFight = new SimpleStringProperty(nameOfBattle)
    def getBattleName : String = nameOfFight.get()
    def setBattleName(s: String): Unit = nameOfFight.set(s)

    val nameOfWinner = new SimpleStringProperty(winner)
    def getWinner : String = nameOfWinner.get()
    def setWinner(w: String): Unit = nameOfWinner.set(w)

    val takenRounds = new SimpleStringProperty(rounds)
    def getTakenRounds: String = takenRounds.get()
    def setTakenRounds(tr: String): Unit = takenRounds.set(tr)
  }

  def init(): Unit = {
    date.setCellValueFactory(new PropertyValueFactory[HighScore, String]("date"))
    winner.setCellValueFactory(new PropertyValueFactory[HighScore, String]("battleName"))
    nameOfGame.setCellValueFactory(new PropertyValueFactory[HighScore, String]("winner"))
    moves.setCellValueFactory(new PropertyValueFactory[HighScore, String]("takenRounds"))

    setTable()
  }

  def setTable(): Unit = {
    val data = readCSV()
    if (data != Seq()) {
      tableHighscore.setItems(FXCollections.observableArrayList(data.asJava))
    }
  }



  // GUI

  def backToMenu(): Unit = {
    actualScene = BattleShipFxApp.getWelcomeScreen
    BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
  }

  def reset(): Unit = {
    val file = new File("battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/csv/HighScore.csv")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("")
    bw.close()
    val emptyHighScore = Seq(HighScore("","","",""))
    tableHighscore.setItems(FXCollections.observableArrayList(emptyHighScore.asJava))
  }



  // CSV READER

  private def readCSV(): Seq[HighScore] = {
    val CsvFile = "battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/csv/HighScore.csv"
    var data: Seq[HighScore]= Seq()
    val br = new BufferedReader(new FileReader(CsvFile))
    var line = br.readLine
      while (line != null) {
        val fields: Seq[String] = line.split(";").toSeq
        if (fields != Seq()) {
          val newHighScore: HighScore = HighScore(fields(0), fields(1), fields(2), fields(3))
          data = data :+ newHighScore
          line = br.readLine
        } else {
          data = Seq()
        }
      }
    data
  }



  // INIT CONTROLLER

  override def initialize(location: URL, resources: ResourceBundle): Unit =
    init()
}


