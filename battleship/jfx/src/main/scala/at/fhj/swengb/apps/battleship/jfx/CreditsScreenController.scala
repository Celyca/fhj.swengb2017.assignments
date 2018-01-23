package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.Initializable
import javafx.scene.Scene

class CreditsScreenController extends Initializable {

  var actualScene: Scene = _



  // GUI

  def backToMenu(): Unit = {
    actualScene = BattleShipFxApp.getWelcomeScreen
    BattleShipFxApp.setScene(actualScene, BattleShipFxApp.getActualStage)
  }

  def muteMedia(): Unit = {
    BattleShipFxApp.muteMedia()
  }



  // INIT CONTROLLER

  def empty(): Unit = {
  // NOTHING TO INIT
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit =
    empty()
}

