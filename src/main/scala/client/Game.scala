package client

import com.thoughtworks.binding.Binding.Var
import com.thoughtworks.binding.{Binding, dom}
import org.scalajs.dom.raw.Event
import client.Game.Pawn

import scala.collection.mutable
//import scalaz.std.vector._
import client.Game._

class Game(val board: Vector[Vector[Cell]],
           val whitePieces: mutable.Set[Piece],
           val blackPieces: mutable.Set[Piece],
           val turn: Var[Color],
           var selectedPiece: Option[Piece] = None,
           var startedJump: Boolean = false) {

  def selectPiece(piece: Piece): Unit = {
    selectedPiece.foreach(_.unselect())
    piece.select()
    selectedPiece = Some(piece)
  }

  def removePiece(piece: Piece): Unit = {
    piece.remove
    if(piece.color == Black) blackPieces.remove(piece)
    else whitePieces.remove(piece)
  }

  def tryMove(piece: Piece, to: Cell): MoveResult = {
    val isPawn = piece.typ == Pawn
    val from = piece.position.get
    val (yourColor, nextColor) = if(turn.get == Black) (Black, White) else (White, Black)
    val fromCell = piece.position.get

    if(to.piece.get.nonEmpty) MoveResult(false, false)
    else if(isPawn){
      val dx = if(yourColor == Black) 1 else -1

      if(fromCell.x + dx == to.x && (fromCell.y - to.y).abs == dx.abs && to.piece.get.isEmpty){
        //move to empty cell
        piece.moveTo(to)
        MoveResult(true, false)
      } else {
        println(fromCell)
        //jump one to empty cell
        val dy = to.y - fromCell.y

        if(fromCell.x + 2*dx == to.x && dy.abs == dy.abs && to.piece.get.isEmpty) {
          val lastPreviousCell = board(to.x - dx)(to.y - dy.signum)
          val skippedPiece = lastPreviousCell.piece.get
          if (skippedPiece.exists(_.color == nextColor)) {
            piece.moveTo(to)
            skippedPiece.foreach(removePiece)
            MoveResult(true, true)
          } else {
            MoveResult(false, false)
          }
        } else {
          MoveResult(false, false)
        }
      }
    } else { // Queen
      val dx = (to.x - from.x).signum
      val dy = (to.y - from.y).signum
      val steps = (to.x - from.x).abs
      println(s"$dx $dy $steps")
      if((to.x-from.x)/dx == (to.y-from.y)/dy && (1 until steps-1).forall(i => board(from.x + dx*i)(from.y + dy*i).piece.get.isEmpty)){
        val lastCell = board(to.x-dx)(to.y-dy)
        println("lastCess " + lastCell)
        if(!lastCell.piece.get.exists(_.color == nextColor)){
          piece.moveTo(to)
          println("a")
          MoveResult(true, false)
        } else {
          println("b")
          piece.moveTo(to)
          lastCell.piece.get.foreach(removePiece)
          MoveResult(true, true)
        }
      } else MoveResult(false, false)
    }
  }

  def canJump(piece: Piece): Boolean = {
    def checkDirection(fromX: Int, fromY: Int, dx: Int, dy: Int, color: Color, maxStep: Int): Boolean = {
      var x = fromX + dx
      var y = fromY + dy
      var remainingSteps = maxStep-1
      while(x >= 0 && x < 8 && y >= 0 && y < 8 && remainingSteps >= 0){
        if(board(x)(y).piece.get.nonEmpty){
          if(board(x)(y).piece.get.exists(_.color == color)){
            val nextX = x + dx
            val nextY = y + dy
            if(nextX >= 0 && nextX < 8 && nextY >= 0 && nextY < 8 && board(nextX)(nextY).piece.get.isEmpty) return true
            else return false
          } else return false
        }
        x += dx
        y += dy
        remainingSteps -= 1
      }
      false
    }
    val enemyColor = if(turn.get == White) Black else White

    if(piece.typ == Pawn) {
      val dx = if(turn.get == White) -1 else 1

      checkDirection(piece.position.get.x, piece.position.get.y, dx, -1, enemyColor, 1) ||
        checkDirection(piece.position.get.x, piece.position.get.y, dx, 1, enemyColor, 1)
    } else { // queen
      checkDirection(piece.position.get.x, piece.position.get.y, 1, 1, enemyColor, 8) ||
        checkDirection(piece.position.get.x, piece.position.get.y, 1, -1, enemyColor, 8) ||
        checkDirection(piece.position.get.x, piece.position.get.y, -1, -1, enemyColor, 8) ||
        checkDirection(piece.position.get.x, piece.position.get.y, -1, 1, enemyColor, 8)
    }
  }

  def nextTurn(): Unit = {
    val nextColor = if(turn.get == Black) White else Black
    selectedPiece.foreach(_.unselect())
    selectedPiece = None
    startedJump = false
    turn := nextColor
  }

  def tryAddQueen(piece: Piece): Unit = {
    if(piece.typ == Pawn) {
      if(piece.color == White && piece.position.get.x == 0){
        val queen = piece.copy(typ = Queen)
        piece.position.get.piece := Some(queen)
        whitePieces.remove(piece)
        whitePieces.add(queen)
      } else if(piece.color == Black && piece.position.get.x == 7){
        val queen = piece.copy(typ = Queen)
        piece.position.get.piece := Some(queen)
        blackPieces.remove(piece)
        blackPieces.add(queen)
      }
    }
  }

  def handleClick(cell: Cell): Unit = {
    if(cell.piece.get.isEmpty) {
      if(selectedPiece.nonEmpty) {
        val movePiece = selectedPiece.get
        val moved = tryMove(movePiece, cell)
        if (moved.success && moved.jumped && canJump(movePiece)) {
          // continue in turn

        } else if(moved.success) {
          nextTurn()
        }

        if(moved.success) tryAddQueen(movePiece)
      }
    }
    else {
      if (cell.piece.get.get.color == turn.get && !startedJump) selectPiece(cell.piece.get.get)
    }
  }

  @dom def render() = {
    <div class="container">
      <p class="col s12 game-state-label"></p>
      <div class="col s12">
        <table>
          {
          import scalaz.std.vector._
          board.map { row =>
            <tr>
              {row.map { cell =>
              val color = if(cell.color == Black) " black-cell" else " white-cell"
              val selected = if(cell.selected.bind) " selected" else ""
              <td class={color + selected} onclick={_: Event => handleClick(cell)}>
                {
                cell.piece.bind match {
                  case None => ""
                  case Some(Piece(Black, Pawn, _, _)) => "CP"
                  case Some(Piece(White, Pawn, _, _)) => "BP"
                  case Some(Piece(Black, Queen, _, _)) => "CD"
                  case Some(Piece(White, Queen, _, _)) => "BD"
                }
                }
              </td>
            }}
            </tr>
          }}
        </table>
      </div>
    </div>
  }
}

object Game {
  sealed trait Color
  case object Black extends Color
  case object White extends Color

  sealed trait Typ
  case object Pawn extends Typ
  case object Queen extends Typ

  case class Piece(color: Color, typ: Typ, position: Var[Cell], selected: Var[Boolean]) {
    def moveTo(to: Cell): Unit = {
      position.get.selected := false
      position.get.piece := None
      position := to
      to.selected := selected.get
      to.piece := Some(this)
    }

    def unselect(): Unit = {
      position.get.selected := false
      selected := false
    }

    def select(): Unit = {
      position.get.selected := true
      selected := true
    }

    def remove: Unit = {
      position.get.piece := None
    }
  }

  case class Cell(x: Int, y: Int, color: Color, piece: Var[Option[Piece]], selected: Var[Boolean])

  def create(): Game = {
    val board: Vector[Vector[Cell]] = {for(i <- 0 until 8; j <- 0 until 8) yield {
      Cell(i, j, if((i+j)%2 == 0) Black else White, Var(None), Var(false))
    }}.grouped(8).toVector.map(_.toVector)

    val whitePieces: mutable.Set[Piece] = mutable.Set.empty
    val blackPieces: mutable.Set[Piece] = mutable.Set.empty

    for(i <- 0 until 8) {
      val p = Piece(Black, Pawn, Var(board(i%2)(i)), Var(false))
      blackPieces.add(p)
      board(i%2)(i).piece := Some(p)
    }
    for(i <- 0 until 8) {
      val p = Piece(White, Pawn, Var(board(6+i%2)(i)), Var(false))
      whitePieces.add(p)
      board(6+i%2)(i).piece := Some(p)
    }

    val turn: Var[Color] = Var(White)

    new Game(board, whitePieces, blackPieces, turn)
  }

  case class MoveResult(success: Boolean, jumped: Boolean)
}