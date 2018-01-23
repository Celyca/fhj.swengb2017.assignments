package at.fhj.swengb.apps.battleship.model



case class EditGame(      getCellWidth: Int => Double,
                          getCellHeight: Int => Double) {



  var fleet: Fleet = Fleet(Set[Vessel]())

  val defaultShip: (NonEmptyString, Int) = (NonEmptyString("Default"), 0)
  val ship1: (NonEmptyString, Int) = (NonEmptyString("Ship 1"), 5)
  val ship2: (NonEmptyString, Int) = (NonEmptyString("Ship 2"), 4)
  val ship3: (NonEmptyString, Int) = (NonEmptyString("Ship 3"), 4)
  val ship4: (NonEmptyString, Int) = (NonEmptyString("Ship 4"), 3)
  val ship5: (NonEmptyString, Int) = (NonEmptyString("Ship 5"), 3)
  var currentVessel: (NonEmptyString, Int) = defaultShip
  var shipSet: Boolean = false

  var direction: Direction = Vertical

  var allMove: Set[BattlePos]= _

  var cells: Seq[FxCell] = _

  def init() = {
    cells = for    {x <- 0 until 10
                    y <- 0 until 10
                    pos = BattlePos(x, y)} yield {
      FxCell(BattlePos(x, y),
        getCellWidth(x),
        getCellHeight(y),
        mouseEnter,
        mouseLeft,
        setVessel,
        findInFleet(fleet, pos)
      )
    }
  }

  def setMove(pos: BattlePos): Unit = {
      direction match {
        case Horizontal =>
          if (pos.x <= 10 - currentVessel._2) {
            allMove = (pos.x until (pos.x + currentVessel._2)).map(x => BattlePos(x, pos.y)).toSet
          } else allMove = Set()
        case Vertical =>
          if (pos.y <= 10 - currentVessel._2) {
            allMove = (pos.y until (pos.y + currentVessel._2)).map(y => BattlePos(pos.x, y)).toSet
          } else allMove = Set()
      }
    }


  def getCells: Seq[FxCell] = cells

  def mouseEnter(pos: BattlePos): Unit = {
    setMove(pos)
    for (p <- allMove) {
      val cell: FxCell = cells.filter(x => x.pos.equals(p)).head
      cell.changeColorEnter()
    }
  }

  def mouseLeft(pos: BattlePos): Unit = {
    setMove(pos)
    for (p <- allMove) {
      val cell: FxCell = cells.filter(x => x.pos.equals(p)).head
      cell.changeColorLeft()
    }
  }

  def findInFleet(x: Fleet, pos: BattlePos): Option[BattlePos] = {
    val value = x.occupiedPositions.find(v => v.x == pos.x && v.y == pos.y)
    value
  }

  def setVessel(pos: BattlePos): Unit = {
    val newVessel: Vessel = Vessel(currentVessel._1, pos, direction, currentVessel._2)
    val result: List[Option[Vessel]] = newVessel.occupiedPos.map(x => fleet.findByPos(x)).filter(x => x.isDefined).toList
    if (result.isEmpty && currentVessel != defaultShip) {
      direction match {
        case Horizontal =>
          if (pos.x <= 10 - currentVessel._2) {
            fleet = Fleet(fleet.vessels + newVessel)
            shipSet = true
          }
        case Vertical =>
          if (pos.y <= 10 - currentVessel._2) {
            fleet = Fleet(fleet.vessels + newVessel)
            shipSet = true
          }
      }
    }
  }
}
