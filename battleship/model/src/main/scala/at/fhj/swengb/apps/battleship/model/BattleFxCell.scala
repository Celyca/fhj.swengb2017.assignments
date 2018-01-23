package at.fhj.swengb.apps.battleship.model

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
;

case class BattleFxCell(pos: BattlePos
                        , width: Double
                        , height: Double
                        , someVessel: Option[Vessel] = None
                        , fn: (Vessel, BattlePos) => Unit
                        , clicked: BattlePos => Unit
                       ) extends Rectangle(width, height) {

  def initDisabled(): Unit = {
    someVessel match {
      case None =>
        setFill(Color.BLUE)
      case Some(v) =>
        setFill(Color.GREEN)
    }
  }

  def init(): Unit = {
    setFill(Color.BLUE)
  }


  setOnMouseClicked(e => {
    mouseClick
  })

  def mouseClick() = {
    someVessel match {
      case None =>
        setFill(Color.DODGERBLUE)
        clicked(pos)
      case Some(v) =>
        fn(v, pos)
        setFill(Color.DARKRED)
        clicked(pos)
    }
  }

  def simClick() = {
    someVessel match {
      case None =>
        setFill(Color.DODGERBLUE)
      case Some(v) =>
        setFill(Color.DARKRED)
    }
  }
}