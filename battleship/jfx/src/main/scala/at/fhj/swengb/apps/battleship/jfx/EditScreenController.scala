package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.nio.file.Files
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.{Button, TextField}
import javafx.scene.layout.GridPane
import javafx.application.Platform

import at.fhj.swengb.apps.battleship.model._
import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

import scala.util.Random

class EditScreenController extends Initializable {
  @FXML private var editGridPane: GridPane = _
  @FXML private var ship1Button: Button = _
  @FXML private var ship2Button: Button = _
  @FXML private var ship3Button: Button = _
  @FXML private var ship4Button: Button = _
  @FXML private var ship5Button: Button = _
  @FXML private var readyButton: Button = _
  @FXML private var clearButton: Button = _
  @FXML private var cancelButton: Button = _
  @FXML private var playerName: TextField = _

  var actualField: EditGame = _
  var actualScene: Scene = _
  BattleShipFxApp.editController = this

  var playerJoined: Boolean = !BattleShipFxApp.player1
  var player1Ready: Boolean = _
  var player2Ready: Boolean = _
  var player2Sync: Boolean = _
  var player2Placed: Boolean = _

  var player1Name: String = _
  var player2Name: String = _
  var gameName: String = getGameName

  private def getCellHeight(y: Int): Double = editGridPane.getRowConstraints.get(y).getPrefHeight
  private def getCellWidth(x: Int): Double = editGridPane.getColumnConstraints.get(x).getPrefWidth



  // INIT

  def init(game: EditGame): Unit = {
    actualField.init()
    editGridPane.getChildren.clear()
    for (c <- game.getCells) {
      editGridPane.add(c, c.pos.x, c.pos.y)
    }
    game.getCells.foreach(c => c.init)
  }

  private def initEdit(): Unit = {
    actualField = createField()
    init(actualField)
  }



  // CREATE VALUES

  def getGameName: String = {
    val w1: List[String] = List("Bloody ", "Final ", "Epic ", "Historic ", "Heroic ")
    val w2: List[String] = List("Battle ", "Assault ", "Fight ", "War " , "Slaughter ")
    val w3: List[String] = List("of ", "in ")
    val w4: List[String] = List("The Czech Republic", "Skellig Michael", "Hogwarts", "Springfield", "Narnia", "Kings Betrayal")

    def random(word: List[String]): String = {
      word(Random.nextInt(word.size))
    }

    val newGameName: String = random(w1) + random(w2) + random(w3) + random(w4)
    newGameName
  }

  private def createField(): EditGame = {
    EditGame(getCellWidth, getCellHeight)
  }

  def createGame(edit: EditGame): BattleShipGame = {
    val fleet: Fleet = edit.fleet
    val field: BattleField = BattleField(10, 10, fleet)
    val game: BattleShipGame = BattleShipGame(field, getCellHeight, getCellWidth)
    game.player1Name = player1Name
    game.player2Name = player2Name
    game.gameName = gameName
    game
  }



  // GUI

  def ship1(): Unit = {
    actualField.currentVessel = actualField.ship1
  }
  def ship2(): Unit = {
    actualField.currentVessel = actualField.ship2
  }
  def ship3(): Unit = {
    actualField.currentVessel = actualField.ship3
  }
  def ship4(): Unit = {
    actualField.currentVessel = actualField.ship4
  }
  def ship5(): Unit = {
    actualField.currentVessel = actualField.ship5
  }

  def change(): Unit = {
    actualField.direction match {
      case Horizontal =>
        actualField.direction = Vertical
      case Vertical =>
        actualField.direction = Horizontal
    }
    init(actualField)
  }

  def click(): Unit = {
    init(actualField)
    if(actualField.shipSet) {
      if (actualField.currentVessel == actualField.ship1) {
        ship1Button.setDisable(true)
      } else {
        if (actualField.currentVessel == actualField.ship2) {
          ship2Button.setDisable(true)
        } else {
          if (actualField.currentVessel == actualField.ship3) {
            ship3Button.setDisable(true)
          } else {
            if (actualField.currentVessel == actualField.ship4) {
              ship4Button.setDisable(true)
            } else {
              if (actualField.currentVessel == actualField.ship5) {
                ship5Button.setDisable(true)
              }
            }
          }
        }
      }
      actualField.shipSet = false
      actualField.currentVessel = actualField.defaultShip
      if (actualField.fleet.vessels.toList.size == 5 && BattleShipFxApp.player1) {
        readyButton.setDisable(false)
      }
      if (actualField.fleet.vessels.toList.size == 5 && !BattleShipFxApp.player1) {
        player2Placed = true
        player2ReadyButton()
      }
    }
  }

  def clear(): Unit = {
    initEdit()
    ship1Button.setDisable(false)
    ship2Button.setDisable(false)
    ship3Button.setDisable(false)
    ship4Button.setDisable(false)
    ship5Button.setDisable(false)
    readyButton.setDisable(true)
    player2Placed = false

  }

  def cancel(): Unit = {
    val convertStatus = BattleShipProtocol.convert(false, false)
    convertStatus.writeTo(Files.newOutputStream(BattleShipFxApp.playFile.toPath))
    clear()
    actualScene = BattleShipFxApp.getWelcomeScreen
    BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
  }



  // SYNC GAME STATES

  def sync(): Unit ={
    val vessels1: Int = BattleShipFxApp.player1Game.battleField.fleet.vessels.size
    val vessels2: Int = BattleShipFxApp.player2Game.battleField.fleet.vessels.size
    if (vessels1 == 5 && !BattleShipFxApp.player1) {
      player2Sync = true
      player2ReadyButton()
    }
    if (vessels1 == 5 && vessels2 == 5){
      BattleShipFxApp.watchCycle = false
      Platform.runLater(() => {
        def run(): Unit = {
          actualScene = BattleShipFxApp.getGameScreen
          BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
          BattleShipFxApp.getGameController.ini()
        }
        run()
      })
    }
  }

  def syncGame(): Unit ={
    if (BattleShipFxApp.player1 && !player1Ready) {
      BattleShipFxApp.player1Game = createGame(actualField)
      val convertStatus = BattleShipProtocol.convert(BattleShipFxApp.player1Game , BattleShipFxApp.player2Game)
      convertStatus.writeTo(Files.newOutputStream(BattleShipFxApp.playFile.toPath))
    } else {
      if (!BattleShipFxApp.player1 && !player2Ready) {
        BattleShipFxApp.player2Game = createGame(actualField)
        val convertStatus = BattleShipProtocol.convert(BattleShipFxApp.player1Game, BattleShipFxApp.player2Game )
        convertStatus.writeTo(Files.newOutputStream(BattleShipFxApp.playFile.toPath))
      }
    }
  }

  def loadGame(): Unit = {
    val loadStatus = BattleShipProtobuf.Games.parseFrom(Files.newInputStream(BattleShipFxApp.playFile.toPath))
    val actualStatus = BattleShipProtocol.convert(loadStatus)
    BattleShipFxApp.player1Game = actualStatus.head
    BattleShipFxApp.player2Game = actualStatus.last
    sync()
  }



  // PLAYERS READY

  def player2ReadyButton(): Unit = {
    if (player2Placed && player2Sync) {
      readyButton.setDisable(false)
    }
  }

  def ready(): Unit = {
    if (playerJoined) {
      if (BattleShipFxApp.player1) {
        player1Name = playerName.getText
      } else {
        player2Name = playerName.getText
      }
      clearButton.setDisable(true)
      cancelButton.setDisable(true)
      syncGame()
      if (BattleShipFxApp.player1) {
        player1Ready = true
      } else {
        player2Ready = true
      }
    }
  }



  // WATCH SERVICE

  def watcher(): Unit = {
    if (!playerJoined){
      playerJoined = true
    } else {
      loadGame()
    }
  }





  // INIT CONTROLLER

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    initEdit()
  }
}
