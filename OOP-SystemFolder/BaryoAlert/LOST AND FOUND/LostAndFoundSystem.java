import java.util.ArrayList;
import java.util.Scanner;

public class LostAndFoundSystem {

    private ArrayList<Item> items = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        int roleChoice;

        Student student = new Student(scanner);
        Admin admin = new Admin(scanner);

        do {
            System.out.println("\n=== School Lost & Found System ===");
            System.out.println("Select your role:");
            System.out.println("1. Student (Report Lost Item)");
            System.out.println("2. Admin (System Manager)");
            System.out.println("3. Exit");

            roleChoice = getIntInput("Enter choice: ");

            switch (roleChoice) {
                case 1:
                    student.menu(items);
                    break;
                case 2:
                    admin.menu(items);
                    break;
                case 3:
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (roleChoice != 3);
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    public static void main(String[] args) {
        LostAndFoundSystem system = new LostAndFoundSystem();
        system.start();
    }
}

// ================================
// ITEM CLASS
// ================================
class Item {
    private static int idCounter = 1000;
    private int id;
    private String name;
    private String description;
    private String location;
    private String dateFound;
    private boolean isClaimed;

    public Item(String name, String description, String location, String dateFound) {
        this.id = idCounter++;
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateFound = dateFound;
        this.isClaimed = false;
    }

    public int getId() {
        return id;
    }

    public void markClaimed() {
        isClaimed = true;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | " + (isClaimed ? "[Claimed] " : "[Available] ") +
                name + " - " + description + " (Location: " + location + ", Date: " + dateFound + ")";
    }
}

// ================================
// STUDENT CLASS
// ================================
class Student {
    private Scanner scanner;

    public Student(Scanner scanner) {
        this.scanner = scanner;
    }

    public void reportLostItem(ArrayList<Item> items) {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter location found: ");
        String location = scanner.nextLine();

        System.out.print("Enter date found (e.g., 2025-12-11): ");
        String date = scanner.nextLine();

        Item item = new Item(name, description, location, date);
        items.add(item);

        System.out.println("Lost item reported successfully!");
        System.out.println("Your item ID (ticket number) is: " + item.getId());
        System.out.println("Keep this ID for verification purposes.");
    }

    public void menu(ArrayList<Item> items) {
        int choice;
        do {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. Report Lost Item");
            System.out.println("2. Return to Main Menu");

            choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    reportLostItem(items);
                    break;
                case 2:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 2);
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
}

// ================================
// ADMIN CLASS
// ================================
class Admin {
    private Scanner scanner;

    public Admin(Scanner scanner) {
        this.scanner = scanner;
    }

    public void viewAllItems(ArrayList<Item> items) {
        if (items.isEmpty()) {
            System.out.println("No items reported yet.");
            return;
        }
        System.out.println("\n--- All Lost & Found Items ---");
        for (Item item : items) {
            System.out.println(item);
        }
    }

    public void markItemClaimed(ArrayList<Item> items) {
        if (items.isEmpty()) {
            System.out.println("No items to mark as claimed.");
            return;
        }

        viewAllItems(items);

        int id = getIntInput("Enter the item ID to mark as claimed: ");
        boolean found = false;
        for (Item item : items) {
            if (item.getId() == id) {
                if (item.isClaimed()) {
                    System.out.println("Item is already claimed.");
                } else {
                    item.markClaimed();
                    System.out.println("Item marked as claimed!");
                }
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("No item found with ID " + id);
        }
    }

    public void menu(ArrayList<Item> items) {
        int choice;
        do {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Items");
            System.out.println("2. Mark Item as Claimed");
            System.out.println("3. Return to Main Menu");

            choice = getIntInput("Enter choice: ");

            switch (choice) {
                case 1:
                    viewAllItems(items);
                    break;
                case 2:
                    markItemClaimed(items);
                    break;
                case 3:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 3);
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
}
