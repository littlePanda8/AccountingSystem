import javax.swing.*;
import java.util.ArrayList;

public class LostAndFoundSystem {

    private ArrayList<Item> items = new ArrayList<>();

    public void start() {
        int roleChoice;
        Student student = new Student();
        Admin admin = new Admin();

        do {
            String[] roles = {"Student (Report Lost Item)", "Admin (System Manager)", "Exit"};
            roleChoice = JOptionPane.showOptionDialog(null,
                    "=== School Lost & Found System ===\nSelect your role:",
                    "Lost & Found System",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    roles,
                    roles[0]);

            switch (roleChoice) {
                case 0:
                    student.menu(items);
                    break;
                case 1:
                    admin.menu(items);
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "Exiting system. Goodbye!");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice. Try again.");
            }
        } while (roleChoice != 2);
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

    public void reportLostItem(ArrayList<Item> items) {
        String name = JOptionPane.showInputDialog("Enter item name:");
        if (name == null) return;

        String description = JOptionPane.showInputDialog("Enter description:");
        if (description == null) return;

        String location = JOptionPane.showInputDialog("Enter location found:");
        if (location == null) return;

        String date = JOptionPane.showInputDialog("Enter date found (e.g., 2025-12-11):");
        if (date == null) return;

        Item item = new Item(name, description, location, date);
        items.add(item);

        JOptionPane.showMessageDialog(null, "Lost item reported successfully!\n" +
                "Your item ID (ticket number) is: " + item.getId() +
                "\nKeep this ID for verification purposes.");
    }

    public void menu(ArrayList<Item> items) {
        int choice;
        do {
            String[] options = {"Report Lost Item", "Return to Main Menu"};
            choice = JOptionPane.showOptionDialog(null,
                    "--- Student Menu ---",
                    "Student",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0:
                    reportLostItem(items);
                    break;
                case 1:
                    JOptionPane.showMessageDialog(null, "Returning to main menu...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice. Try again.");
            }
        } while (choice != 1);
    }
}

// ================================
// ADMIN CLASS
// ================================
class Admin {

    public void viewAllItems(ArrayList<Item> items) {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items reported yet.");
            return;
        }
        StringBuilder sb = new StringBuilder("--- All Lost & Found Items ---\n");
        for (Item item : items) {
            sb.append(item).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public void markItemClaimed(ArrayList<Item> items) {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items to mark as claimed.");
            return;
        }

        StringBuilder sb = new StringBuilder("--- All Lost & Found Items ---\n");
        for (Item item : items) {
            sb.append(item).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());

        String input = JOptionPane.showInputDialog("Enter the item ID to mark as claimed:");
        if (input == null) return;

        try {
            int id = Integer.parseInt(input);
            boolean found = false;
            for (Item item : items) {
                if (item.getId() == id) {
                    if (item.isClaimed()) {
                        JOptionPane.showMessageDialog(null, "Item is already claimed.");
                    } else {
                        item.markClaimed();
                        JOptionPane.showMessageDialog(null, "Item marked as claimed!");
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(null, "No item found with ID " + id);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID entered!");
        }
    }

    public void menu(ArrayList<Item> items) {
        int choice;
        do {
            String[] options = {"View All Items", "Mark Item as Claimed", "Return to Main Menu"};
            choice = JOptionPane.showOptionDialog(null,
                    "--- Admin Menu ---",
                    "Admin",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0:
                    viewAllItems(items);
                    break;
                case 1:
                    markItemClaimed(items);
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "Returning to main menu...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice. Try again.");
            }
        } while (choice != 2);
    }
}
