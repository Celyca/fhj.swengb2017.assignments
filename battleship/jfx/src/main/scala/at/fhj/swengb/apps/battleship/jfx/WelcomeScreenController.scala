package at.fhj.swengb.apps.battleship.jfx

import java.io.File
import java.net.URL
import java.nio.file._
import java.util.ResourceBundle
import java.util.concurrent.TimeUnit
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.{Button, Slider, TextArea}
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.{DirectoryChooser, FileChooser}

import at.fhj.swengb.apps.battleship.model._
import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

class WelcomeScreenController extends Initializable {

  var actualScene: Scene = _



  // GUI

  def startGame(): Unit = {
    createFile()
  }

  def joinGame(): Unit = {
    loadState()
  }

  def highScore(): Unit = {
    actualScene = BattleShipFxApp.getHighScoreScreen
    BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
  }

  def replay(): Unit = {
    actualScene = BattleShipFxApp.getReplayScreen
    BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
  }

  def credits(): Unit = {
    actualScene = BattleShipFxApp.getCreditsScreen
    BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
  }



  // START GAME

  def createFile(): Unit = {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Select directory for the sync file")
    fileChooser.setInitialFileName("BattleShip.ktv")
    fileChooser.getExtensionFilters.addAll(
      new ExtensionFilter("BattleShip Files", "*.ktv")
    )
    BattleShipFxApp.playFile = fileChooser.showSaveDialog(null)
    if (BattleShipFxApp.playFile != null) {
      BattleShipFxApp.playPath = BattleShipFxApp.playFile.getParent
      val convertStatus = BattleShipProtocol.convert(true, false)
      convertStatus.writeTo(Files.newOutputStream(BattleShipFxApp.playFile.toPath))
      BattleShipFxApp.player1 = true
      actualScene = BattleShipFxApp.getEditScreen
      BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
      BattleShipFxApp.getFileWatcher.start()
      BattleShipFxApp.gameStateEdit = true
    }
  }



  // JOIN GAME

  def loadState(): Unit = {
    val fileChooser = new FileChooser
    fileChooser.setTitle("Load sync file")
    fileChooser.setInitialFileName("BattleShip.ktv")
    fileChooser.getExtensionFilters.addAll(
      new ExtensionFilter("BattleShip Files", "*.ktv")
    )
    BattleShipFxApp.playFile = fileChooser.showOpenDialog(null)
    if (BattleShipFxApp.playFile != null) {
      BattleShipFxApp.playPath = BattleShipFxApp.playFile.getParent
      val loadStatus = BattleShipProtobuf.active.parseFrom(Files.newInputStream(BattleShipFxApp.playFile.toPath))
      val actualStatus = BattleShipProtocol.convert(loadStatus)
      if (actualStatus.head) {
        val convertStatus = BattleShipProtocol.convert(true, false)
        convertStatus.writeTo(Files.newOutputStream(BattleShipFxApp.playFile.toPath))
        BattleShipFxApp.player1 = false
        actualScene = BattleShipFxApp.getEditScreen
        BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
        BattleShipFxApp.getFileWatcher.start()
        BattleShipFxApp.gameStateEdit = true
      } else {
        println("File is not active")
      }
    }
  }



  // INIT CONTROLLER

  def empty(): Unit = {
    // NOTHING TO INIT
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    empty()
  }
}
