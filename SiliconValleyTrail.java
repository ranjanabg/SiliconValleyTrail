import java.util.Scanner;

public class SiliconValleyTrail {
	public static void main (String[] args) {
		System.out.println("========================================");
		System.out.println("       Welcome to Silicon Valley Trail  ");
		System.out.println("========================================");
		System.out.println();
		System.out.println("  1. New Game");
		System.out.println("  2. Load Game");
		System.out.println("  3. Quit");
		System.out.println();
		System.out.print("Enter your choice: ");

		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine().trim();

		switch (input) {
			case "1":
				System.out.println("Starting a new game...");
				break;
			case "2":
				System.out.println("Loading saved game...");
				break;
			case "3":
				System.out.println("Thanks for playing! Goodbye.");
				break;
			default:
				System.out.println("Invalid option. Please enter 1, 2, or 3.");
		}

		scanner.close();
	}
}