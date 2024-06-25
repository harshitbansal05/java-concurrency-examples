package sa.com.barraq.chess.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sa.com.barraq.chess.conditions.PieceCellOccupyBlocker;
import sa.com.barraq.chess.exceptions.InvalidMoveException;
import sa.com.barraq.chess.moves.PossibleMovesProvider;

import java.util.ArrayList;
import java.util.List;

import static sa.com.barraq.chess.helpers.ListHelpers.removeDuplicates;

@Getter
public class Piece {
    private boolean isKilled = false;
    private final Color color;
    private final List<PossibleMovesProvider> movesProviders;
    private Integer numMoves = 0;
    PieceType pieceType;

    @Setter
    @NonNull
    private Cell currentCell;

    public Piece(@NonNull final Color color, @NonNull final List<PossibleMovesProvider> movesProviders, @NonNull final PieceType pieceType) {
        this.color = color;
        this.movesProviders = movesProviders;
        this.pieceType = pieceType;
    }

    public void killIt() {
        this.isKilled = true;
    }

    /**
     * Method to move piece from current cell to a given cell.
     */
    public void move(Player player, Cell toCell, Board board, List<PieceCellOccupyBlocker> additionalBlockers) {
        if (isKilled) {
            throw new InvalidMoveException();
        }
        List<Cell> nextPossibleCells = nextPossibleCells(board, additionalBlockers, player);
        if (!nextPossibleCells.contains(toCell)) {
            throw new InvalidMoveException();
        }

        killPieceInCell(toCell);
        this.currentCell.setCurrentPiece(null);
        this.currentCell = toCell;
        this.currentCell.setCurrentPiece(this);
        this.numMoves++;
    }

    /**
     * Helper method to kill a piece in a given cell.
     */
    private void killPieceInCell(Cell targetCell) {
        if (targetCell.getCurrentPiece() != null) {
            targetCell.getCurrentPiece().killIt();
        }
    }

    /**
     * Method which tells what are all next possible cells to which the current piece can move from the current cell.
     *
     * @param board              Board on which the piece is present.
     * @param additionalBlockers Blockers which make a cell non-occupiable for a piece.
     * @param player             Player who owns the piece.
     * @return List of all next possible cells.
     */
    public List<Cell> nextPossibleCells(Board board, List<PieceCellOccupyBlocker> additionalBlockers, Player player) {
        List<Cell> result = new ArrayList<>();
        for (PossibleMovesProvider movesProvider : this.movesProviders) {
            List<Cell> cells = movesProvider.possibleMoves(this, board, additionalBlockers, player);
            if (cells != null) {
                result.addAll(cells);
            }
        }
        return removeDuplicates(result);
    }

    /**
     * Helper method to check if two pieces belong to same player.
     */
    public boolean isPieceFromSamePlayer(Piece piece) {
        return piece.getColor().equals(this.color);
    }
}
