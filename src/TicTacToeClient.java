import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class TicTacToeClient {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the ip address of the server: ");
        String hostIp = scan.nextLine();
        
        try (Socket socket = new Socket(hostIp, 11444);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            while (in.readBoolean()) {
                byte[] array = new byte[9];
                in.read(array);
                byte[][] board = arrayToBoard(array);
                printBoard(board);
                System.out.print("Enter coordinates to place your piece: ");
                int x = scan.nextInt(), y = scan.nextInt();
                while (!placePiece(x, y, board)) {
                    System.out.print("Coordinates were not valid, try again: ");
                    x = scan.nextInt();
                    y = scan.nextInt();
                }
                out.write(boardToArray(board));
            }
            System.out.println("Someone won or tied");
            byte[] array = new byte[9];
            in.read(array);
            int winner = in.readInt();
            byte[][] board = arrayToBoard(array);
            System.out.println("Final board");
            printBoard(board);
            if (winner == 0) {
                System.out.println("You tied.");
            } else if (winner == 1) {
                System.out.println("You lost.");
            } else {
                System.out.println("You won!");
            }
        } catch (IOException e) {
            System.out.println("Couldn't connect to host with ip " + hostIp);
        }
        scan.close();
    }
    
    private static boolean placePiece(int x, int y, byte[][] board) {
        if (x >= 0 && x < 3 && y >= 0 && y < 3) {
            if (board[x][y] == 0) {
                board[x][y] = 2;
                return true;
            }
        }
        return false;
    }
    
    private static byte[] boardToArray(byte[][] board) {
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
    
    private static void printBoard(byte[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
