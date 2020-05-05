package client


import client.Game.{Black, White}
import client.GameSpec._
import org.scalatest.FunSpec

class GameSpecSpec extends FunSpec {
  describe("GameSpec object") {
    it("create test game") {
      val game = createTestGame(
        """b.b.b.b.
          |.b.b.b.b
          |........
          |........
          |........
          |........
          |.w.w.w.w
          |w.w.w.w.""".stripMargin)

      assert(game.blacks.size == 8)
      assert(game.whites.size == 8)
      assert(game.whites.forall(_.color == White))
      assert(game.blacks.forall(_.color == Black))
      assert(game.game.board(0)(0).piece.get.contains(game.blacks(0)))
      assert(game.game.board(0)(2).piece.get.contains(game.blacks(1)))
      assert(game.game.board(0)(4).piece.get.contains(game.blacks(2)))
      assert(game.game.board(0)(6).piece.get.contains(game.blacks(3)))
      assert(game.game.board(1)(1).piece.get.contains(game.blacks(4)))
      assert(game.game.board(1)(3).piece.get.contains(game.blacks(5)))
      assert(game.game.board(1)(5).piece.get.contains(game.blacks(6)))
      assert(game.game.board(1)(7).piece.get.contains(game.blacks(7)))
      assert(game.game.board(6)(1).piece.get.contains(game.whites(0)))
      assert(game.game.board(6)(3).piece.get.contains(game.whites(1)))
      assert(game.game.board(6)(5).piece.get.contains(game.whites(2)))
      assert(game.game.board(6)(7).piece.get.contains(game.whites(3)))
      assert(game.game.board(7)(0).piece.get.contains(game.whites(4)))
      assert(game.game.board(7)(2).piece.get.contains(game.whites(5)))
      assert(game.game.board(7)(4).piece.get.contains(game.whites(6)))
      assert(game.game.board(7)(6).piece.get.contains(game.whites(7)))
    }
  }
}