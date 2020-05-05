package client


import client.Game._
import client.GameSpec._
import com.thoughtworks.binding.Binding.Var
import org.scalatest.FunSpec

import scala.collection.mutable

class GameSpec extends FunSpec {
  describe("handle click") {
    it("should doesn't select any piece") {
      val game = createSimpleGame
      game.click(1, 1)
      assert(game.isNothingSelected)
      game.click(6, 2)
      assert(game.isNothingSelected)
      game.click(1, 3)
      assert(game.isNothingSelected)
    }
    it("should select piece") {
      val game = createSimpleGame
      game.click(6, 3)
      assert(game.isSelected(game.whites.head))
    }
  }
  describe("move pawn") {
    it("should doesn't move pawn") {
      val game = createSimpleGame

      game.click(6, 3)
      assert(game.isSelected(game.whites.head))
      game.click(6, 2)
      game.click(6, 4)
      game.click(7, 4)
      game.click(5, 3)
      assert(game.isSelected(game.whites.head))
      assert(game.contain(6, 3, game.whites.head))
      assert(game.isCellEmpty(6, 2))
      assert(game.isCellEmpty(6, 4))
      assert(game.isCellEmpty(7, 4))
      assert(game.isCellEmpty(5, 3))
    }

    it("should move pawns") {
      val game = createSimpleGame

      game.click(6, 3)
      game.click(5, 2)
      assert(game.contain(5, 2, game.whites.head))
      assert(game.isCellEmpty(6, 3))
      assert(game.isNothingSelected)

      game.click(1, 3)
      game.click(2, 4)
      assert(game.contain(2, 4, game.blacks.head))
      assert(game.isCellEmpty(1, 3))
      assert(game.isNothingSelected)
    }
  }
  describe("pawn jump") {
    it("should jump one piece") {
      val game = createTestGame(
        """........
          |........
          |....b...
          |...w....
          |........
          |........
          |........
          |........""".stripMargin)

      game.click(3, 3)
      game.click(1, 5)
      assert(game.isCellEmpty(3, 3))
      assert(game.isCellEmpty(2, 4))
      assert(game.contain(1, 5, game.whites.head))
      assert(game.isPieceRemoved(game.blacks.head))
      assert(game.isBlackTurn)
    }
    it("should jump multiple piece") {
      val game = createTestGame(
        """........
          |........
          |...b....
          |........
          |...b....
          |..w.....
          |........
          |........""".stripMargin)

      game.click(5, 2)
      game.click(3, 4)
      assert(game.isCellEmpty(5, 2))
      assert(game.isCellEmpty(4, 3))
      assert(game.contain(3, 4, game.whites.head))
      assert(game.isPieceRemoved(game.blacks(1)))
      assert(game.isWhiteTurn)
      assert(game.isSelected(game.whites.head))
      game.click(1, 2)
      assert(game.isCellEmpty(3, 4))
      assert(game.isCellEmpty(2, 3))
      assert(game.contain(1, 2, game.whites.head))
      assert(game.isPieceRemoved(game.blacks(0)))
      assert(game.isNothingSelected)
    }
    it("should doesn't jump multiple piece") {
      val game = createTestGame(
        """........
          |..b...b.
          |........
          |........
          |...b.b..
          |..w.....
          |........
          |........""".stripMargin)

      game.click(5, 2)
      game.click(3, 4)
      assert(game.isCellEmpty(5, 2))
      assert(game.isCellEmpty(4, 3))
      assert(game.contain(3, 4, game.whites.head))
      assert(game.isPieceRemoved(game.blacks(2)))
      assert(game.isBlackTurn)
      assert(game.isNothingSelected)
    }
  }
  describe("queen add") {
    it("should add queen") {
      val game = createTestGame(
        """........
          |...w....
          |........
          |........
          |........
          |..b.....
          |...w....
          |........""".stripMargin)

      game.click(1, 3)
      game.click(0, 4)
      assert(game.pieceOn(0, 4).typ == Queen)
      assert(game.pieceOn(0, 4).color == White)
      game.click(5, 2)
      game.click(7, 4)
      assert(game.pieceOn(7, 4).typ == Queen)
      assert(game.pieceOn(7, 4).color == Black)
    }
  }
  describe("queen move") {
    it("should move queen 1") {
      val game = createTestGame(
        """........
          |........
          |........
          |........
          |..W.....
          |........
          |........
          |........""".stripMargin)

      game.click(4, 2)
      game.click(2, 0)
      assert(game.isCellEmpty(4, 2))
      assert(game.contain(2, 0, game.whites.head))
    }
    it("should move queen 2") {
      println("---------")
      val game = createTestGame(
        """........
          |........
          |........
          |........
          |..W.....
          |........
          |........
          |........""".stripMargin)

      game.click(4, 2)
      game.click(7, 5)
      assert(game.isCellEmpty(4, 2))
      assert(game.contain(7, 5, game.whites.head))
    }
  }
  describe("queen jump") {
    it("should jump one piece") {
      val game = createTestGame(
        """........
          |.....b..
          |........
          |........
          |..W.....
          |........
          |........
          |........""".stripMargin)

      game.click(4, 2)
      game.click(0, 6)
      assert(game.isCellEmpty(4, 2))
      assert(game.isCellEmpty(1, 5))
      assert(game.contain(0, 6, game.whites.head))
      assert(game.isPieceRemoved(game.blacks.head))
      assert(game.isBlackTurn)
      assert(game.isNothingSelected)
    }
    it("should jump more pieces") {
      val game = createTestGame(
        """........
          |........
          |....b...
          |.b......
          |........
          |.......W
          |........
          |........""".stripMargin)

      game.click(5, 7)
      game.click(1, 3)
      assert(game.isCellEmpty(5, 7))
      assert(game.isCellEmpty(2, 4))
      assert(game.contain(1, 3, game.whites.head))
      assert(game.isPieceRemoved(game.blacks.head))
      assert(game.isWhiteTurn)
      assert(game.isSelected(game.whites.head))
      game.click(4, 0)
      assert(game.isCellEmpty(1, 3))
      assert(game.isCellEmpty(3, 1))
      assert(game.contain(4, 0, game.whites.head))
      assert(game.isPieceRemoved(game.blacks(0)))
      assert(game.isBlackTurn)
      assert(game.isNothingSelected)
    }
    it("should not jump over row of pieces") {
      val game = createTestGame(
        """........
          |.W......
          |........
          |...b....
          |....b...
          |........
          |........
          |........""".stripMargin)

      game.click(1, 1)
      game.click(5, 5)
      assert(game.isCellEmpty(5, 5))
      assert(game.contain(1, 1, game.whites.head))
      assert(game.contain(3, 3, game.blacks.head))
      assert(game.contain(4, 4, game.blacks(1)))
      assert(game.isWhiteTurn)
      assert(game.isSelected(game.whites.head))
    }
  }
}

object GameSpec {
  def createSimpleGame: TestGame = createTestGame(
    """........
      |...b....
      |........
      |........
      |........
      |........
      |...w....
      |........""".stripMargin)

  def createTestGame(boardString: String): TestGame = {
    val board: Vector[Vector[Cell]] = {for(i <- 0 until 8; j <- 0 until 8) yield {
      Cell(i, j, if((i+j)%2 == 0) Black else White, Var(None), Var(false))
    }}.grouped(8).toVector.map(_.toVector)

    var whitePieces = List[Piece]()
    var blackPieces = List[Piece]()

    boardString.split("\n").zipWithIndex.foreach{ case (row, x) =>
      row.zipWithIndex.map{ case (char, y) =>
        val piece = char match {
          case 'b' => Some(Piece(Black, Pawn, Var(board(x)(y)), Var(false)))
          case 'B' => Some(Piece(Black, Queen, Var(board(x)(y)), Var(false)))
          case 'w' => Some(Piece(White, Pawn, Var(board(x)(y)), Var(false)))
          case 'W' => Some(Piece(White, Queen, Var(board(x)(y)), Var(false)))
          case _ => None
        }

        board(x)(y).piece := piece

        if(piece.exists(_.color == Black)) blackPieces = piece.get :: blackPieces
        if(piece.exists(_.color == White)) whitePieces = piece.get :: whitePieces
      }
    }

    TestGame(new Game(board, mutable.Set(whitePieces:_*), mutable.Set(blackPieces:_*), Var(White)),
      whitePieces.reverse.toVector,
      blackPieces.reverse.toVector)
  }

  case class TestGame(game: Game, whites: Vector[Piece], blacks: Vector[Piece]) {
    def click(x: Int, y: Int): Unit = game.handleClick(game.board(x)(y))
    def isCellEmpty(x: Int, y: Int): Boolean = game.board(x)(y).piece.get.isEmpty
    def contain(x: Int, y: Int, piece: Piece): Boolean = game.board(x)(y).piece.get.contains(piece)
    def isSelected(piece: Piece): Boolean = game.selectedPiece.contains(piece)
    def isNothingSelected: Boolean = game.selectedPiece.isEmpty
    def isPieceRemoved(piece: Piece): Boolean = !game.whitePieces.contains(piece) && !game.blackPieces.contains(piece)
    def isWhiteTurn: Boolean = game.turn.get == White
    def isBlackTurn: Boolean = game.turn.get == Black
    def pieceOn(x: Int, y: Int): Piece = game.board(x)(y).piece.get.get
  }
}