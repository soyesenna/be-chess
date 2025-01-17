package chess;

import chess.board.Board;
import chess.board.BoardView;
import chess.enums.Color;
import chess.enums.TypeOfPiece;
import chess.pieces.Piece;
import chess.pieces.implement.King;
import chess.pieces.implement.Pawn;
import chess.pieces.implement.Queen;
import chess.pieces.implement.Rook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static chess.utils.StringUtils.appendNewLine;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.*;

public class BoardTest {

    private Board board;
    private ChessGame chessGame;

    @BeforeEach
    @DisplayName("테스트 시작 전 보드 객체를 초기화한다")
    void setup() {
        board = Board.getInstance();
        board.initialize();
        chessGame = new ChessGame();
    }

    @Test
    @DisplayName("보드 안의 기물이 제대로 초기화되어야 한다")
    void create() throws Exception {
        assertThat(32).isEqualTo(board.pieceCount());
        String blankRank = appendNewLine("........");
        BoardView view = new BoardView(board.getChessBoard());
        assertThat(
                appendNewLine("♖♘♗♕♔♗♘♖") +
                        appendNewLine("♙♙♙♙♙♙♙♙") +
                        blankRank + blankRank + blankRank + blankRank +
                        appendNewLine("♟♟♟♟♟♟♟♟") +
                        appendNewLine("♜♞♝♛♚♝♞♜"))
                .isEqualTo(view.showBoard());
    }

    @Test
    @DisplayName("기물의 색과 종류를 받아 보드에 몇 개 있는지 확인하는 메서드 검증")
    void pieceCount() {
        //보드 초기화 하자마자는
        //검은 룩 2개
        assertThat(board.numberOfSpecificPiece(TypeOfPiece.ROOK, Color.BLACK)).isEqualTo(2);
        //하얀 폰 8개
        assertThat(board.numberOfSpecificPiece(TypeOfPiece.PAWN, Color.WHITE)).isEqualTo(8);
    }

    @Test
    @DisplayName("좌표가 주어졌을 때 해당 위치의 기물을 가져오는지 검증")
    void findPiece() throws Exception {
        assertThat(Rook.rook.create(Color.BLACK)).isEqualTo(board.findPiece("a8"));
        assertThat(Rook.rook.create(Color.BLACK)).isEqualTo(board.findPiece("h8"));
        assertThat(Rook.rook.create(Color.WHITE)).isEqualTo(board.findPiece("a1"));
        assertThat(Rook.rook.create(Color.WHITE)).isEqualTo(board.findPiece("h1"));
    }

    @Test
    @DisplayName("임의의 위치의 체스판에 기물 추가되는지 검증")
    void addPieceInBoard() throws Exception {
        board.initializeEmpty();

        String position = "b5";
        Piece piece = Rook.rook.create(Color.BLACK);

        chessGame.move(position, piece);

        assertThat(piece).isEqualTo(board.findPiece(position));

        BoardView view = new BoardView(board.getChessBoard());
        System.out.println(view.showBoard());
    }

    @Test
    @DisplayName("점수 계산 로직 검증")
    public void calculatePoint() throws Exception {
        board.initializeEmpty();

        addAllPieces();

        assertThat(chessGame.calculatePoint(Color.BLACK)).isBetween(14.9, 15.1);
        assertThat(chessGame.calculatePoint(Color.WHITE)).isBetween(6.9, 7.1);

        BoardView view = new BoardView(board.getChessBoard());

        System.out.println(view.showBoard());
    }

    @Test
    @DisplayName("기물 점수로 정렬하는 로직 검증")
    void sortPoint() {
        board.initializeEmpty();

        addAllPieces();

        //black 낮은순
        List<Double> now = getSortResult(Color.BLACK, true);
        assertThat(now).isSortedAccordingTo(Comparator.reverseOrder());

        //black 높은순
        now = getSortResult(Color.BLACK, false);
        assertThat(now).isSorted();

        //white 낮은순
        now = getSortResult(Color.WHITE, true);
        assertThat(now).isSortedAccordingTo(Comparator.reverseOrder());

        //white 높은순
        now = getSortResult(Color.WHITE, false);
        assertThat(now).isSorted();
    }

    List<Double> getSortResult(Color color, boolean reverse) {
        return chessGame.sortPieceByScore(color, reverse)
                .stream()
                .map(piece -> piece.getType().getScore())
                .toList();
    }


    void addAllPieces() {
        addPiece("b6", Pawn.pawn.create(Color.BLACK));
        addPiece("e6", Queen.queen.create(Color.BLACK));
        addPiece("b8", King.king.create(Color.BLACK));
        addPiece("c8", Rook.rook.create(Color.BLACK));

        addPiece("f2", Pawn.pawn.create(Color.WHITE));
        addPiece("g2",  Pawn.pawn.create(Color.WHITE));
        addPiece("e1", Rook.rook.create(Color.WHITE));
        addPiece("f1", King.king.create(Color.WHITE));
    }

    void addPiece(String position, Piece piece) {
        chessGame.move(position, piece);
    }

    @Test
    @DisplayName("기물이 제대로 이동하는지 검증한다")
    void move() throws Exception {
        String sourcePosition = "b2";
        String targetPosition = "b3";

        //이동 전 검증
        assertThat(TypeOfPiece.NO_PIECE).isEqualTo(board.findPiece(targetPosition).getType());
        assertThat(TypeOfPiece.PAWN).isEqualTo(board.findPiece(sourcePosition).getType());
        assertThat(Color.WHITE).isEqualTo(board.findPiece(sourcePosition).getColor());

        //폰 이동
        chessGame.move(sourcePosition, targetPosition, Color.WHITE);

        //이동 후 검증
        assertThat(TypeOfPiece.NO_PIECE).isEqualTo(board.findPiece(sourcePosition).getType());
        assertThat(TypeOfPiece.PAWN).isEqualTo(board.findPiece(targetPosition).getType());
        assertThat(Color.WHITE).isEqualTo(board.findPiece(targetPosition).getColor());

        //잘못된 위치로 비숍 이동
        assertThatThrownBy(() -> chessGame.move("c1", "b1", Color.WHITE)).isInstanceOf(IllegalArgumentException.class);


    }

}