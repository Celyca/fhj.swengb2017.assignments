package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.{Calendar, ResourceBundle}
import javafx.application.Platform
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.GridPane
import java.io.{BufferedWriter, File, FileWriter}
import at.fhj.swengb.apps.battleship.model._
import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

class GameScreenController extends Initializable {
  @FXML private var myBattleGround: GridPane = _
  @FXML private var enemyBattleGround: GridPane = _
  @FXML private var statusLabel: Label = _
  @FXML private var enemyName: Label = _
  @FXML private var myName: Label = _
  @FXML private var battleName: Label = _
  @FXML private var enemy1: ProgressBar = _
  @FXML private var enemy2: ProgressBar = _
  @FXML private var enemy3: ProgressBar = _
  @FXML private var enemy4: ProgressBar = _
  @FXML private var enemy5: ProgressBar = _
  @FXML private var my1: ProgressBar = _
  @FXML private var my2: ProgressBar = _
  @FXML private var my3: ProgressBar = _
  @FXML private var my4: ProgressBar = _
  @FXML private var my5: ProgressBar = _

  var actualScene: Scene = _
  var player1Name: String = _
  var player2Name: String = _
  var nameOfGame: String = _
  var myActualClicks: List[BattlePos] = _
  BattleShipFxApp.gameController = this
  val format = new SimpleDateFormat("d.M.y")
  val date = format.format(Calendar.getInstance().getTime())
  var highScoreSaved: Boolean = false

  private def getMyCellHeight(y: Int): Double = myBattleGround.getRowConstraints.get(y).getPrefHeight
  private def getMyCellWidth(x: Int): Double = myBattleGround.getColumnConstraints.get(x).getPrefWidth
  private def getEnemyCellHeight(y: Int): Double = enemyBattleGround.getRowConstraints.get(y).getPrefHeight
  private def getEnemyCellWidth(x: Int): Double = enemyBattleGround.getColumnConstraints.get(x).getPrefWidth



  // INIT

  def ini(): Unit = {
    if (BattleShipFxApp.getPlayer1) {
      myName.setText(BattleShipFxApp.player1Game.player1Name)
      enemyName.setText(BattleShipFxApp.player2Game.player2Name)
      battleName.setText(BattleShipFxApp.player1Game.gameName)
      player1Name = BattleShipFxApp.player2Game.player2Name
      player2Name = BattleShipFxApp.player1Game.player1Name
      nameOfGame = BattleShipFxApp.player1Game.gameName

      BattleShipFxApp.player1Game = createMyGame(BattleShipFxApp.player1Game)
      BattleShipFxApp.player2Game = createEnemyGame(BattleShipFxApp.player2Game)

      initGrid(BattleShipFxApp.player1Game, BattleShipFxApp.player2Game)
      myActualClicks = BattleShipFxApp.player2Game.clicks
      statusLabel.setText("YOUR TURN")
    } else {
      myName.setText(BattleShipFxApp.player2Game.player2Name)
      enemyName.setText(BattleShipFxApp.player1Game.player1Name)
      battleName.setText(BattleShipFxApp.player1Game.gameName)
      player1Name = BattleShipFxApp.player2Game.player2Name
      player2Name = BattleShipFxApp.player1Game.player1Name
      nameOfGame = BattleShipFxApp.player1Game.gameName

      BattleShipFxApp.player1Game = createEnemyGame(BattleShipFxApp.player1Game)
      BattleShipFxApp.player2Game = createMyGame(BattleShipFxApp.player2Game)

      initGrid(BattleShipFxApp.player2Game, BattleShipFxApp.player1Game)
      myActualClicks = BattleShipFxApp.player1Game.clicks
      myBattleGround.setDisable(true)
      statusLabel.setText("ENEMY'S TURN")
    }

    BattleShipFxApp.gameStateEdit = false
    BattleShipFxApp.watchCycle = true
    BattleShipFxApp.initFileWatcher()
    BattleShipFxApp.getFileWatcher.start()
  }

  def initGrid(myGame: BattleShipGame, enemyGame: BattleShipGame): Unit = {
    myBattleGround.getChildren.clear()
    enemyBattleGround.getChildren.clear()

    for (c <- myGame.getCells) {
      enemyBattleGround.add(c, c.pos.x, c.pos.y)
      c.setDisable(true)
    }
    myGame.getCells().foreach(c => c.initDisabled())
    myGame.loadOrder(myGame.clicks, true)

    for (c <- enemyGame.getCells) {
      myBattleGround.add(c, c.pos.x, c.pos.y)
    }
    enemyGame.getCells().foreach(c => c.init)
    enemyGame.loadOrder(enemyGame.clicks, false)
  }



  // CREATE GAME STATES

  def createMyGame(game: BattleShipGame): BattleShipGame = {
    val field = game.battleField
    val newgame = BattleShipGame(field, getMyCellWidth, getMyCellHeight)
    newgame.loadOrder(game.clicks, false)
    newgame.player1Name = myName.getText
    newgame.gameName = battleName.getText
    newgame
  }

  def createEnemyGame(game: BattleShipGame): BattleShipGame = {
    val field = game.battleField
    val newgame = BattleShipGame(field, getEnemyCellWidth, getEnemyCellHeight)
    newgame.loadOrder(game.clicks, false)
    newgame.player2Name = enemyName.getText
    newgame
  }



  // GUI

  def muteMedia(): Unit = {
    BattleShipFxApp.muteMedia()
  }

  def click(): Unit = {
    val newVals1 = BattleShipFxApp.player1Game.clicks
    val newVals2 = BattleShipFxApp.player2Game.clicks
    var OK: Boolean = false
    if (BattleShipFxApp.player1) {
      if (myActualClicks != newVals2) {
        OK = true
      }
    } else {
      if (myActualClicks != newVals1) {
        OK = true
      }
    }
    if (OK) {
      Platform.runLater(() => {
        def test(): Unit = {
          myBattleGround.setDisable(true)
          statusLabel.setText("ENEMY'S TURN")
        }

        test()
      })
      saveGame()
      gameOver()
    }
  }

  def setBars(): Unit = {
    enemy1.setProgress(getProgress("Ship 1", BattleShipFxApp.player1))
    enemy2.setProgress(getProgress("Ship 2", BattleShipFxApp.player1))
    enemy3.setProgress(getProgress("Ship 3", BattleShipFxApp.player1))
    enemy4.setProgress(getProgress("Ship 4", BattleShipFxApp.player1))
    enemy5.setProgress(getProgress("Ship 5", BattleShipFxApp.player1))
    my1.setProgress(getProgress("Ship 1", !BattleShipFxApp.player1))
    my2.setProgress(getProgress("Ship 2", !BattleShipFxApp.player1))
    my3.setProgress(getProgress("Ship 3", !BattleShipFxApp.player1))
    my4.setProgress(getProgress("Ship 4", !BattleShipFxApp.player1))
    my5.setProgress(getProgress("Ship 5", !BattleShipFxApp.player1))

  }

  def getProgress(ship: String, player1: Boolean): Double = {
    if (player1) {
      val vessel = BattleShipFxApp.player1Game.battleField.fleet.findByName(ship)
      vessel match {
        case Some(v) =>
          if (BattleShipFxApp.player1Game.hits.contains(v)) {
            val hits: Int = BattleShipFxApp.player1Game.hits(v).size
            val size: Int = v.size
            hits.toDouble / size.toDouble
          } else 0
        case None => 0
      }

    } else {
      val vessel = BattleShipFxApp.player2Game.battleField.fleet.findByName(ship)
      vessel match {
        case Some(v) =>
          if (BattleShipFxApp.player2Game.hits.contains(v)) {
            val hits: Int = BattleShipFxApp.player2Game.hits(v).size
            val size: Int = v.size
            hits.toDouble / size.toDouble
          } else 0
        case None => 0
      }
    }
  }



  // SYNC GAME STATES

  def saveGame(): Unit = {
    val convertStatus = BattleShipProtocol.convert(BattleShipFxApp.player1Game, BattleShipFxApp.player2Game )
    convertStatus.writeTo(Files.newOutputStream(BattleShipFxApp.playFile.toPath))
  }

  def loadGame(): Unit = {
    val loadStatus = BattleShipProtobuf.Games.parseFrom(Files.newInputStream(BattleShipFxApp.playFile.toPath))
    val actualStatus = BattleShipProtocol.convert(loadStatus)
    if (actualStatus.head.battleField.fleet.vessels.nonEmpty || actualStatus.last.battleField.fleet.vessels.nonEmpty) {
      if (BattleShipFxApp.player1) {
        if (BattleShipFxApp.player1Game.clicks != createMyGame(actualStatus.head).clicks) {
          myBattleGround.setDisable(false)
          statusLabel.setText("YOUR TURN")
          BattleShipFxApp.player1Game = createMyGame(actualStatus.head)
          BattleShipFxApp.player2Game = createEnemyGame(actualStatus.last)
          initGrid(BattleShipFxApp.player1Game, BattleShipFxApp.player2Game)
          myActualClicks = BattleShipFxApp.player2Game.clicks
        }
      } else {
        if (BattleShipFxApp.player2Game.clicks != createEnemyGame(actualStatus.last).clicks) {
          myBattleGround.setDisable(false)
          statusLabel.setText("YOUR TURN")
          BattleShipFxApp.player1Game = createEnemyGame(actualStatus.head)
          BattleShipFxApp.player2Game = createMyGame(actualStatus.last)
          initGrid(BattleShipFxApp.player2Game, BattleShipFxApp.player1Game)
          myActualClicks = BattleShipFxApp.player1Game.clicks
        }
      }
      setBars()
      gameOver()
    }
  }



  // GAME OVER AND APPEND HighScore.csv

  def gameOver(): Unit = {

    def disable: Unit = {
      for (c <- BattleShipFxApp.player1Game.getCells) {
        c.setDisable(true)
      }
      for (c <- BattleShipFxApp.player2Game.getCells) {
        c.setDisable(true)
      }
    }
    if (BattleShipFxApp.player1Game.sunkShips == BattleShipFxApp.player1Game.battleField.fleet.vessels) {
      disable
      statusLabel.setText("GAME OVER")
      BattleShipFxApp.watchCycle = false

      val winner = player1Name
      val gameName = nameOfGame
      val moves = BattleShipFxApp.player2Game.clicks.size.toString
      if (winner != "" && gameName != "" && moves != "" && !highScoreSaved) {
        append(date, winner, gameName, moves)
        highScoreSaved = true
      }
    }
    if (BattleShipFxApp.player2Game.sunkShips == BattleShipFxApp.player2Game.battleField.fleet.vessels) {
      disable
      statusLabel.setText("GAME OVER")
      BattleShipFxApp.watchCycle = false

      val winner = player2Name
      val gameName = nameOfGame
      val moves = BattleShipFxApp.player2Game.clicks.size.toString
      if (winner != "" && gameName != "" && moves != "" && !highScoreSaved) {
        append(date, winner, gameName, moves)
        highScoreSaved = true
      }
    }
    def append (date: String, winner: String, gameName: String, moves: String): Unit = {
      val file = new File("battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/csv/HighScore.csv")
      val bw = new BufferedWriter(new FileWriter(file, true))
      bw.append(date + ";" + winner + ";" + gameName + ";" + moves + "\n")
      bw.close()
    }
  }



  // WATCH SERVICE

  def watcher(): Unit = {
    Platform.runLater(() => {
      def run(): Unit = {
        loadGame()
      }

      run()
    })
  }



  // INIT CONTROLLER

  def empty(): Unit = {
    // EMPTY BECAUSE INIT AFTER EDIT SCREEN
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    empty()
  }
}



