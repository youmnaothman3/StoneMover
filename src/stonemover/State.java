package stonemover;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class State {
    private final int width;
    private final int height;
    private final String[][] cells;
    private List<String> targetCells = new ArrayList<>();

    public State(String[][] cells) {
        this.width = cells[0].length;
        this.height = cells.length;
        String[][] copy = new String[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                String value = cells[r][c];
                if (value.contains("T")) targetCells.add(r + "" + c);
                copy[r][c] = value.replaceAll("T", "");
            }
        }
        this.cells = copy;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String[][] getCells() {
        String[][] copy = new String[height][width];
        for (int r = 0; r < height; r++) System.arraycopy(cells[r], 0, copy[r], 0, width);
        return copy;
    }
    public String getCellAt(int row, int col) { return cells[row][col]; }
    public void setCellAt(int row, int col, String value) { cells[row][col] = value; }
    public void setTargetCells(List<String> targets) { this.targetCells = targets; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String green = "\u001B[32m", red = "\u001B[31m", blue = "\u001B[34m", gray = "\u001B[37m", reset = "\u001B[0m";
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (targetCells.contains(r + "" + c)) sb.append(green);
                else {
                    String t = GameLogic.getType(cells[r][c]);
                    if ("push".equals(t)) sb.append(blue);
                    if ("pull".equals(t)) sb.append(red);
                    if ("empty".equals(t)) sb.append(gray);
                }
                sb.append(cells[r][c]).append(reset);
                if (c < width - 1) sb.append("\t\t");
            }
            if (r < height - 1) sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State other = (State) obj;
        if (width != other.width || height != other.height) return false;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (!Objects.equals(cells[r][c], other.cells[r][c])) return false;
            }
        }
        return true;
    }

    public boolean isFinal() {
        for (int r = 0; r < height; r++)
            for (int c = 0; c < width; c++)
                if (targetCells.contains(r + "" + c) && isEmpty(cells[r][c])) return false;
        return true;
    }

    public List<State> getNextStates() {
        List<State> next = new ArrayList<>();
        for (int r = 0; r < height; r++)
            for (int c = 0; c < width; c++)
                if (isActorCell(cells[r][c])) {
                    State moved = move(r, c);
                    if (moved != null) next.add(new State(moved.getCells()));
                }
        return next;
    }

    public List<int[]> getNextMoves() {
        List<int[]> moves = new ArrayList<>();
        for (int r = 0; r < height; r++)
            for (int c = 0; c < width; c++)
                if (isActorCell(cells[r][c])) moves.add(new int[]{r, c});
        return moves;
    }

    public State move(int row, int col) {
        String[][] copy = getCells();
        State nextState = new State(copy);
        nextState.setTargetCells(this.targetCells);
        String cell = nextState.getCellAt(row, col);
        if (cell == null) return nextState;
        String dirs = GameLogic.getDirections(cell);
        if (dirs.isEmpty()) return nextState;

        if ("push".equalsIgnoreCase(GameLogic.getType(cell))) {
            if (dirs.contains("L")) { for (int c = 1; c <= col - 1; c++) if (isMovable(nextState.getCellAt(row, c)) && isEmpty(nextState.getCellAt(row, c - 1))) {
                nextState.setCellAt(row, c - 1, nextState.getCellAt(row, c));
                nextState.setCellAt(row, c, "E");
            }}
            if (dirs.contains("R")) { for (int c = nextState.getWidth() - 2; c >= col + 1; c--) if (isMovable(nextState.getCellAt(row, c)) && isEmpty(nextState.getCellAt(row, c + 1))) {
                nextState.setCellAt(row, c + 1, nextState.getCellAt(row, c));
                nextState.setCellAt(row, c, "E");
            }}
            if (dirs.contains("U")) { for (int r = 1; r <= row - 1; r++) if (isMovable(nextState.getCellAt(r, col)) && isEmpty(nextState.getCellAt(r - 1, col))) {
                nextState.setCellAt(r - 1, col, nextState.getCellAt(r, col));
                nextState.setCellAt(r, col, "E");
            }}
            if (dirs.contains("D")) { for (int r = nextState.getHeight() - 2; r >= row + 1; r--) if (isMovable(nextState.getCellAt(r, col)) && isEmpty(nextState.getCellAt(r + 1, col))) {
                nextState.setCellAt(r + 1, col, nextState.getCellAt(r, col));
                nextState.setCellAt(r, col, "E");
            }}
        }

        if ("pull".equalsIgnoreCase(GameLogic.getType(cell))) {
            if (dirs.contains("L")) { for (int c = col - 2; c >= 0; c--) if (isMovable(nextState.getCellAt(row, c)) && isEmpty(nextState.getCellAt(row, c + 1))) {
                nextState.setCellAt(row, c + 1, nextState.getCellAt(row, c));
                nextState.setCellAt(row, c, "E");
            }}
            if (dirs.contains("R")) { for (int c = col + 2; c < nextState.getWidth(); c++) if (isMovable(nextState.getCellAt(row, c)) && isEmpty(nextState.getCellAt(row, c - 1))) {
                nextState.setCellAt(row, c - 1, nextState.getCellAt(row, c));
                nextState.setCellAt(row, c, "E");
            }}
            if (dirs.contains("U")) { for (int r = row - 2; r >= 0; r--) if (isMovable(nextState.getCellAt(r, col)) && isEmpty(nextState.getCellAt(r + 1, col))) {
                nextState.setCellAt(r + 1, col, nextState.getCellAt(r, col));
                nextState.setCellAt(r, col, "E");
            }}
            if (dirs.contains("D")) { for (int r = row + 2; r < nextState.getHeight(); r++) if (isMovable(nextState.getCellAt(r, col)) && isEmpty(nextState.getCellAt(r - 1, col))) {
                nextState.setCellAt(r - 1, col, nextState.getCellAt(r, col));
                nextState.setCellAt(r, col, "E");
            }}
        }

        return nextState;
    }

    private boolean isActorCell(String cell) {
        if (cell == null) return false;
        String t = GameLogic.getType(cell);
        return "push".equals(t) || "pull".equals(t);
    }

    private boolean isMovable(String cellValue) {
        if (cellValue == null) return false;
        String t = GameLogic.getType(cellValue);
        return t != null && !"empty".equals(t);
    }

    private boolean isEmpty(String cell) {
        if (cell == null) return false;
        String t = GameLogic.getType(cell);
        return t == null || "empty".equals(t);
    }
}
