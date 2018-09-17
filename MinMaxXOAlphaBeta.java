package A2MinMaxXOAlphaBeta;

import java.util.LinkedList;
import java.util.Scanner;

public class MinMaxXOAlphaBeta {

	private final char EMPTY = ' ';
	private final char COMPUTER = 'X';
	private final char PLAYER = 'O';
	private final int MIN = 0;
	private final int MAX = 1;
	private final int LIMIT = 5;

	private class Board {
		private char[][] array;

		private Board(int size) {
			array = new char[size][size];

			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++)
					array[i][j] = EMPTY;
		}
	}

	private Board board;
	private int size;

	public MinMaxXOAlphaBeta(int size) {
		this.board = new Board(size);
		this.size = size;
	}

	public void play() {
		while (true) {
			board = playerMove(board);

			if (full(board)) {
				findWinner(board);
				break;
			}

			board = computerMove(board);

			if (full(board)) {
				findWinner(board);
				break;
			}
		}
	}

	// this method finds the winner
	private void findWinner(Board board) {
		if (computerWin(board)) {
			System.out.println("Computer Wins");
		} else if (playerWin(board)) {
			System.out.println("Player Wins");
		} else if (draw(board)) {
			System.out.println("Draw");
		}
		System.out.println("Player: " + checkScore(board, PLAYER));
		System.out.println("Computer: " + checkScore(board, COMPUTER));
	}

	// this just reads player input
	private Board playerMove(Board board) {
		System.out.println("Player Move: ");

		Scanner scanner = new Scanner(System.in);
		int i = scanner.nextInt();
		int j = scanner.nextInt();

		board.array[i][j] = PLAYER;

		displayBoard(board);

		return board;
	}

	// I dont think this changed from the example code. standard computer move
	// method
	private Board computerMove(Board board) {
		LinkedList<Board> children = generate(board, COMPUTER);

		int maxIndex = 0;
		int maxValue = minMax(children.get(0), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

		for (int i = 0; i < children.size(); i++) {
			int currentValue = minMax(children.get(i), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

			if (currentValue > maxValue) {
				maxIndex = i;
				maxValue = currentValue;
			}
		}

		Board result = children.get(maxIndex);

		System.out.println("Computer Move: ");

		displayBoard(result);

		return result;
	}

	// The minmax method now has to check the scores
	private int minMax(Board board, int level, int depth, int alpha, int beta) {
		if (full(board) || depth >= LIMIT)
			return checkScore(board, COMPUTER) - checkScore(board, PLAYER);
		else if (level == MAX) {
			int maxValue = Integer.MIN_VALUE;

			LinkedList<Board> children = generate(board, COMPUTER);

			for (int i = 0; i < children.size(); i++) {
				int currentValue = minMax(children.get(i), MIN, depth + 1, alpha, beta);
				if (currentValue > maxValue)
					maxValue = currentValue;
				if (maxValue >= beta)
					return maxValue;
				if (maxValue > alpha)
					alpha = maxValue;
			}

			return maxValue;
		} else {
			int minValue = Integer.MAX_VALUE;
			LinkedList<Board> children = generate(board, PLAYER);

			for (int i = 0; i < children.size(); i++) {
				int currentValue = minMax(children.get(i), MAX, depth + 1, alpha, beta);

				if (currentValue < minValue)
					minValue = currentValue;
				if (minValue <= alpha)
					return minValue;
				if (minValue < beta)
					beta = minValue;
			}

			return minValue;
		}
	}

	// Generate works the same way
	private LinkedList<Board> generate(Board board, char symbol) {
		LinkedList<Board> children = new LinkedList<Board>();

		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (board.array[i][j] == EMPTY) {
					Board child = copy(board);
					child.array[i][j] = symbol;
					children.addFirst(child);
				}
		return children;
	}

	// winning criteria now goes by score
	private boolean computerWin(Board board) {
		return checkScore(board, COMPUTER) > checkScore(board, PLAYER);
	}

	private boolean playerWin(Board board2) {
		return checkScore(board, PLAYER) > checkScore(board, COMPUTER);
	}

	private boolean draw(Board board) {
		return (full(board) && !computerWin(board) && !playerWin(board));
	}

	// still have a checkrow and check column function, they just look for
	// different things now.
	private int checkScore(Board board, char symbol) {
		int score = 0;
		score += checkRows(board, symbol);
		score += checkColumns(board, symbol);

		return score;
	}

	// now the two check methods count 2 and three symbols in a row and
	// increment the score appropriately.
	private int checkRows(Board board, char symbol) {
		int rowScore = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size - 1; j++) {
				if (board.array[i][j] == symbol) {
					if (board.array[i][j + 1] == symbol) {
						rowScore += 2;
					}
				}
			}
			for (int j = 0; j < size - 2; j++) {
				if (board.array[i][j] == symbol) {
					if (board.array[i][j + 1] == symbol && board.array[i][j + 2] == symbol) {
						rowScore += 3;
					}
				}
			}
		}
		return rowScore;
	}

	private int checkColumns(Board board, char symbol) {
		int rowScore = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size - 1; j++) {
				if (board.array[j][i] == symbol) {
					if (board.array[j + 1][i] == symbol) {
						rowScore += 2;
					}
				}
			}
			for (int j = 0; j < size - 2; j++) {
				if (board.array[j][i] == symbol) {
					if (board.array[j + 1][i] == symbol && board.array[j + 2][i] == symbol) {
						rowScore += 3;
					}
				}
			}
		}
		return rowScore;
	}

	private boolean full(Board board) {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (board.array[i][j] == EMPTY)
					return false;
		return true;
	}

	private int evaluate(Board board) {
		if (computerWin(board))
			return 4 * size;
		else if (playerWin(board))
			return -4 * size;
		else if (draw(board))
			return 3 * size;
		else
			return checkScore(board, COMPUTER) - checkScore(board, PLAYER);
	}

	private Board copy(Board board) {
		Board result = new Board(size);
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				result.array[i][j] = board.array[i][j];

		return result;
	}

	private void displayBoard(Board board) {

		System.out.println();
		for (int i = 0; i < size; i++) {
			System.out.print("|");
			for (int j = 0; j < size; j++) {
				System.out.print(board.array[i][j]);
				System.out.print("|");
			}
			System.out.println();
		}
		System.out.println();
	}

}
