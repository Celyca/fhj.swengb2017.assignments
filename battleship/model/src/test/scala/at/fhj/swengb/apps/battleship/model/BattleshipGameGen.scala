package at.fhj.swengb.apps.battleship.model

import org.scalacheck.Gen


/**
  * Implement in the same manner like MazeGen from the lab, adapt it to requirements of BattleShip
  */
object BattleshipGameGen {

  val maxW: Int = 10
  val maxH: Int = 10

  val fleetConfigSeq: Seq[FleetConfig] =
    Seq(FleetConfig.OneShip, FleetConfig.TwoShips, FleetConfig.Standard)

  val battlefieldGen: Gen[BattleField] = for {
    width <- Gen.chooseNum[Int](1, maxW)
    height <- Gen.chooseNum[Int](1, maxH)
    x <- Gen.chooseNum[Int](0, fleetConfigSeq.size - 1)
  } yield BattleField(width, height, Fleet(fleetConfigSeq(x)))

  val clickedPosGen: Gen[List[BattlePos]] = for {
    i <- Gen.chooseNum[Int](0, maxW * maxH)
    x <- Gen.chooseNum[Int](0, maxW - 1)
    y <- Gen.chooseNum[Int](0, maxH - 1)
  } yield {
    List.fill(i)(BattlePos(x, y))
  }

  val battleShipGameGen: Gen[BattleShipGame] = for {
    battlefield <- battlefieldGen
    clickedPos <- clickedPosGen
  } yield {
    val game = BattleShipGame(
      battlefield,
      x => x.toDouble,
      x => x.toDouble
    )
    game.clicks = clickedPos
    game
  }
}
