package at.fhj.swengb.apps.battleship.jfx

import java.io.IOException
import java.net.URL
import java.nio.file._

import scala.collection.JavaConverters._
import scala.sys.process._
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.{Button, Label, Slider, TextArea}
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter

import at.fhj.swengb.apps.battleship.model._
import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}


class ReplayScreenController extends Initializable {
  @FXML private var battleGroundPlayer1: GridPane = _
  @FXML private var battleGroundPlayer2: GridPane = _
  @FXML private var player1Name: Label = _
  @FXML private var player2Name: Label = _
  @FXML private var replayGameName: Label = _
  @FXML private var historySlider: Slider = _

  var actualScene: Scene = _
  var player1Game: BattleShipGame = _
  var player2Game: BattleShipGame = _

  private def getCellHeight(y: Int): Double = battleGroundPlayer1.getRowConstraints.get(y).getPrefHeight
  private def getCellWidth(x: Int): Double = battleGroundPlayer1.getColumnConstraints.get(x).getPrefWidth



  // GUI

  def backToMenu(): Unit = {
    actualScene = BattleShipFxApp.getWelcomeScreen
    BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
  }



  // LOAD GAMES

  def loadGame(): Unit = {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Load sync file")
    fileChooser.setInitialFileName("BattleShip.ktv")
    fileChooser.getExtensionFilters.addAll(
      new ExtensionFilter("BattleShip Files", "*.ktv")
    )
    val selectedFile = fileChooser.showOpenDialog(null)
    if (selectedFile != null) {
      val loadGameProto = BattleShipProtobuf.Games.parseFrom(Files.newInputStream(selectedFile.toPath))
      val convGame = BattleShipProtocol.convert(loadGameProto)
      player1Name.setText(convGame.head.player1Name)
      player2Name.setText(convGame.last.player2Name)
      replayGameName.setText(convGame.head.gameName)
      player1Game = createGame(convGame.head)
      player2Game = createGame(convGame.last)
      initSlider()
    }
  }



  // CREATE VALUES (CONVERT)
  def createGame(game: BattleShipGame): BattleShipGame = {
    val field = game.battleField
    val newgame = BattleShipGame(field, getCellWidth, getCellHeight)
    newgame.loadOrder(game.clicks, false)
    newgame
  }



  // INIT GRIDS AFTER LOADING

  def init1(game: BattleShipGame) : Unit = {
    battleGroundPlayer1.getChildren.clear()
    for (c <- game.getCells) {
      battleGroundPlayer1.add(c, c.pos.x, c.pos.y)
    }
    game.getCells().foreach(c => c.init)
  }

  def init2(game: BattleShipGame) : Unit = {
    battleGroundPlayer2.getChildren.clear()
    for (c <- game.getCells) {
      battleGroundPlayer2.add(c, c.pos.x, c.pos.y)
    }
    game.getCells().foreach(c => c.init)
  }



  // SLIDER

  def initSlider(): Unit = {
    var player1Size: Int = player1Game.clicks.size
    var player2Size: Int = player2Game.clicks.size
    var actualSize: Int = 0
    if (player1Size > player2Size) actualSize = player1Size else actualSize = player2Size
    historySlider.setMax(actualSize)
    historySlider.setValue(actualSize)
    historySlider.setDisable(false)
    sliderAction()
  }

  def sliderAction(): Unit = {
    val currVal = historySlider.getValue.toInt

    val simClickPos1: List[BattlePos] = player1Game.clicks.take(currVal).reverse
    val simClickPos2: List[BattlePos] = player2Game.clicks.take(currVal).reverse

    battleGroundPlayer1.getChildren.clear()
    for (c <- player1Game.getCells()) {
      battleGroundPlayer1.add(c, c.pos.x, c.pos.y)
      c.init()
      c.setDisable(true)
    }

    battleGroundPlayer2.getChildren.clear()
    for (c <- player2Game.getCells()) {
      battleGroundPlayer2.add(c, c.pos.x, c.pos.y)
      c.init()
      c.setDisable(true)
    }

    player1Game.loadOrder(simClickPos1, true)
    player2Game.loadOrder(simClickPos2, true)
  }



  // INIT CONTROLLER

  def empty(): Unit = {
    // NOTHING TO INIT
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    empty()
  }
}
