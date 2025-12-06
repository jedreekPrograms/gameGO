package pl.edu.go.client;

import pl.edu.go.model.Board;
import pl.edu.go.model.Color;

import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner;


    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayBoard(Board board) {
        int size = board.getSize();
        System.out.print("  ");
        for (int x = 0; x < size; x++) {
            System.out.print(x + " ");
        }
        System.out.println();

        for (int y = 0; y < size; y++) {
            System.out.print(y + " ");
            for (int x = 0; x < size; x++) {
                Color c = board.get(x, y);
                char symbol;

                switch(c) {
                    case BLACK: symbol = 'B'; break;
                    case WHITE: symbol = 'W'; break;
                    default: symbol = '.'; break;
                }
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }
}
