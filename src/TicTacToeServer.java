import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeServer {
    private static byte[][] board = {{0, 0, 0},
                                     {0, 0, 0},
                                     {0, 0, 0}}; 
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        try (ServerSocket serverSocket = new ServerSocket(11444);
                Socket clientSocket = serverSocket.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
            while (checkWin() == 0 && !isFull()) {
                printBoard();
                System.out.print("Enter coordinates to place your piece: ");
                int x = scan.nextInt(), y = scan.nextInt();
                while (!placePiece(x, y)) {
                    System.out.print("Coordinates were not valid, try again: ");
                    x = scan.nextInt();
                    y = scan.nextInt();
                }
                if (checkWin() == 0 && !isFull()) {
                    out.writeBoolean(true);
                    out.write(boardToArray());
                    byte[] array = new byte[9];
                    in.read(array);
                    board = arrayToBoard(array);
                } else {
                    break;
                }
            }
            out.writeBoolean(false);
            out.write(boardToArray());
            System.out.println("Final board");
            printBoard();
            int winner = checkWin();
            out.writeInt(winner);
            if (isFull() && winner == 0) {
                System.out.println("You tied.");
            } else if (winner == 1) {
                System.out.println("You won!");
            } else {
                System.out.println("You lost.");
            }
        } catch (IOException e) {
            System.out.println("Error when listening to find port or when communicating with client.");
        }
        scan.close();
    }
    
    private static boolean placePiece(int x, int y) {
        if (x >= 0 && x < 3 && y >= 0 && y < 3) {
            if (board[x][y] == 0) {
                board[x][y] = 1;
                return true;
            }
        }
        return false;
    }
    
    private static byte checkWin() {
        if (checkRows() != 0) {
            return checkRows();
        } else if (checkColumns() != 0) {
            return checkColumns();
        } else if (checkDiagonals() != 0) {
            return checkDiagonals();
        }
        return 0;
    }
       
    private static byte checkRows() {
        for (int i = 0; i < board.length; i++) {
            byte winner = board[i][0];
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != winner) {
                    winner = 0;
                    break;
                }
            }
            if (winner != 0) {
                return winner;
            }
        }
        return 0;
    }
    
    private static byte checkColumns() {
        for (int i = 0; i < board[0].length; i++) {
            byte winner = board[0][i];
            for (int j = 0; j < board.length; j++) {
                if (board[j][i] != winner) {
                    winner = 0;
                    break;
                }
            }
            if (winner != 0) {
                return winner;
            }
        }
        return 0;
    }

    private static byte checkDiagonals() {
        byte winner = board[0][0];
        for (int i = 0; i < board.length; i++) {
            if (board[i][i] != winner) {
                winner = 0;
                break;
            }
        }
        if (winner != 0) {
            return winner;
        }
        
        winner = board[0][2];
        for (int i = 0; i < board.length; i++) {
            if (board[i][2 - i] != winner) {
                winner = 0;
                break;
            }
        }
        if (winner != 0) {
            return winner;
        }
        
        return 0;
    }
    
    private static boolean isFull() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static byte[] boardToArray() {
        byte[] result = new byte[9];
        for (int i = 0; i < result.length; i++) {
            result[i] = board[i / 3][i % 3];
        }
        return result;
    }
    
    private static byte[][] arrayToBoard(byte[] array) {
        byte[][] result = new byte[3][3];
        for (int i = 0; i < array.length; i++) {
            result[i / 3][i % 3] = array[i];
        }
        return result;
    }
    
    private static void printBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}