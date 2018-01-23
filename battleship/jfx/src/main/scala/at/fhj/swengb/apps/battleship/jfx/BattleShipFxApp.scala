package at.fhj.swengb.apps.battleship.jfx

import java.io.File
import javafx.application.{Application, Preloader}
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, StageStyle}
import javafx.application.Preloader.StateChangeNotification
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.media.{Media, MediaPlayer}
import javafx.scene.paint.Color
import at.fhj.swengb.apps.battleship.model.BattleShipGame
import com.sun.javafx.application.LauncherImpl
import scala.util.{Failure, Success, Try}

object BattleShipFxApp {

  var actualStage: Stage = _
  var gameStateEdit: Boolean = _

  var fileWatcher: Thread = _
  var mediaPlayer: MediaPlayer = _
  var mediaPlayerActive: Boolean = _

  var playPath: String = _
  var playFile: File = _

  var player1: Boolean = _
  var player1Game: BattleShipGame = _
  var player2Game: BattleShipGame = _

  var watchCycle = true

  var welcomeScreen: Scene = _
  var gameScreen: Scene = _
  var editScreen: Scene = _
  var highScoreScreen: Scene = _
  var creditsScreen: Scene = _
  var replayScreen: Scene = _
  var editController: EditScreenController = _
  var gameController: GameScreenController = _

  val welcomeScreenFXML = "/at/fhj/swengb/apps/battleship/jfx/fxml/welcomeScreen.fxml"
  val gameScreenFXML = "/at/fhj/swengb/apps/battleship/jfx/fxml/gameScreen.fxml"
  val editScreenFXML = "/at/fhj/swengb/apps/battleship/jfx/fxml/editScreen.fxml"
  val highScoreScreenFXML = "/at/fhj/swengb/apps/battleship/jfx/fxml/highScoreScreen.fxml"
  val creditsScreenFXML = "/at/fhj/swengb/apps/battleship/jfx/fxml/creditsScreen.fxml"
  val replayScreenFXML = "/at/fhj/swengb/apps/battleship/jfx/fxml/replayScreen.fxml"


  def getPlayPath: String = playPath
  def getPlayFile: File = playFile
  def getPlayer1: Boolean = player1

  def getActualStage: Stage = actualStage
  def getGameStateEdit: Boolean = gameStateEdit

  def getFileWatcher: Thread = fileWatcher

  def getWelcomeScreen: Scene = welcomeScreen
  def getGameScreen: Scene = gameScreen
  def getEditScreen: Scene = editScreen
  def getHighScoreScreen: Scene = highScoreScreen
  def getCreditsScreen: Scene = creditsScreen
  def getReplayScreen: Scene = replayScreen
  def getEditController: EditScreenController = editController
  def getGameController: GameScreenController = gameController

  def initScene(url: String): Scene = {
    val triedRoot = Try(FXMLLoader.load[Parent](getClass.getResource(url)))
    triedRoot match {
      case Success(root) =>
        val scene: Scene = new Scene(root)
        scene
      case Failure(e) => e.printStackTrace()
        null
    }
  }

  def initFileWatcher(): Unit = {
    fileWatcher = new Thread (new BattleShipWatchService)
  }

  def initScenes(): Unit = {
    welcomeScreen = initScene(welcomeScreenFXML)
    gameScreen = initScene(gameScreenFXML)
    editScreen = initScene(editScreenFXML)
    highScoreScreen = initScene(highScoreScreenFXML)
    creditsScreen = initScene(creditsScreenFXML)
    replayScreen = initScene(replayScreenFXML)
  }

  def setScene(scene: Scene, stage: Stage): Unit = {
    if (stage == null) {
      println("Error: Window not set")
      System.exit(1)
    } else {
      stage.setScene(scene)
      stage.show()
    }
  }

  def main(args: Array[String]): Unit = {
    LauncherImpl.launchApplication(classOf[BattleShipFxApp], classOf[BattleShipFxAppSplash], args)
  }

  def muteMedia(): Unit = {
    if (mediaPlayerActive) {
      mediaPlayer.pause()
      mediaPlayerActive = false
    } else {
      mediaPlayer.play()
      mediaPlayerActive = true
    }
  }
}

class BattleShipFxApp extends Application {

  override def init(): Unit = {
    val musicFile = "/at/fhj/swengb/apps/battleship/jfx/effects/background.mp3"

    Thread.sleep(5000)

    BattleShipFxApp.mediaPlayer = new MediaPlayer(new Media(getClass.getResource(musicFile).toExternalForm))
    BattleShipFxApp.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE)
    BattleShipFxApp.mediaPlayer.play()
    BattleShipFxApp.mediaPlayerActive = true
  }

  override def start(stage: Stage) = {
    stage.setTitle("BattleShip by KTV")
    stage.setResizable(false)

    BattleShipFxApp.initScenes()

    BattleShipFxApp.initFileWatcher()

    BattleShipFxApp.actualStage = stage
    BattleShipFxApp.setScene(BattleShipFxApp.getWelcomeScreen, stage)
  }
}

class BattleShipFxAppSplash extends Preloader {
  var splashStage: Stage = _

  def newScene: Scene = {
    val pane: BorderPane = new BorderPane()
    val image: ImageView = new ImageView(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/images/splashscreen.gif").toString)

    image.setFitWidth(600)
    image.setFitHeight(364)

    pane.setCenter(image)
    val scene = new Scene(pane)
    scene.setFill(Color.TRANSPARENT)
    scene
  }

  override def start(stage: Stage) = {
    splashStage = stage

    splashStage.initStyle(StageStyle.TRANSPARENT)

    splashStage.setWidth(600)
    splashStage.setHeight(364)

    splashStage.setScene(newScene)
    splashStage.show()
  }

  override def handleStateChangeNotification(evt: Preloader.StateChangeNotification): Unit = {
    if (evt.getType.equals(StateChangeNotification.Type.BEFORE_START)) {
      splashStage.hide()
    }
  }
}
