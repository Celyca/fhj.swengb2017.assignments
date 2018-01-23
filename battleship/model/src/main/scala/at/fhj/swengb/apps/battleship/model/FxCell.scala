package at.fhj.swengb.apps.battleship.model

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
;

case class FxCell(pos: BattlePos
                        , width: Double
                        , height: Double
                        , enter: BattlePos => Unit
                        , left: BattlePos => Unit
                        , clicked: BattlePos => Unit
                        , someVessel: Option[BattlePos] = None
                 ) extends Rectangle(width, height) {

  def init(): Unit = {
    someVessel match {
      case None =>
        setFill(Color.BLUE)
      case Some(v) =>
        setFill(Color.GREEN)
    }
  }

  def changeColorEnter() = {
    someVessel match {
      case None =>
        setFill(Color.YELLOW)
      case Some(v) =>
        setFill(Color.GREEN)
    }
  }

  def changeColorLeft() = {
    someVessel match {
      case None =>
        setFill(Color.BLUE)
      case Some(v) =>
        setFill(Color.GREEN)
    }
  }

  setOnMouseEntered(e => {
    enter(pos)
  })

  setOnMouseExited(e => {
    left(pos)
  })

  setOnMouseClicked(e => {
    clicked(pos)
  })
}


