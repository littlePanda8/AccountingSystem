import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;

// ================================
// REPORT CLASS
// ================================
class Report {
    private static int counter = 1;
    private int id;
    private String title;
    private String description;
    private String reporterUsername;
    private String status;
    private LocalDate dateSubmitted;

    public Report(String title, String description, String reporterUsername) {
        this.id = counter++;
        this.title = title;
        this.description = description;
        this.reporterUsername = reporterUsername;
        this.status = "Pending";
        this.dateSubmitted = LocalDate.now();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getReporterUsername() { return reporterUsername; }
    public String getStatus() { return status; }
    public LocalDate getDateSubmitted() { return dateSubmitted; }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nTitle: " + title + "\nDescription: " + description +
               "\nReporter: " + reporterUsername + "\nDate: " + dateSubmitted +
               "\nStatus: " + status + "\n---------------------";
    }
}

// ================================
// USER (ABSTRACT)
// ================================
abstract class User {
    protected String username;
    protected String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public abstract void menu(BaryoAlert systemManager);
}

// ================================
// REPORTER CLASS
// ================================
class Reporter extends User {
    private ArrayList<Report> myReports;

    public Reporter(String username, String password) {
        super(username, password);
        myReports = new ArrayList<>();
    }

    public void submitReport(BaryoAlert systemManager) {
        String title = JOptionPane.showInputDialog("Enter report title:");
        if(title == null) return; // Cancel pressed
        String description = JOptionPane.showInputDialog("Enter report description:");
        if(description == null) return;

        Report report = new Report(title, description, username);
        systemManager.addReport(report);
        myReports.add(report);

        JOptionPane.showMessageDialog(null, "Report submitted successfully! ID: " + report.getId());
    }

    public void viewMyReports() {
        if(myReports.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have no reports.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(Report r : myReports) {
            sb.append(r.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    @Override
    public void menu(BaryoAlert systemManager) {
        String[] options = {"Submit Report", "View My Reports", "Logout"};
        while(true) {
            int choice = JOptionPane.showOptionDialog(null, "Select an action:", "Reporter Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if(choice == 0) submitReport(systemManager);
            else if(choice == 1) viewMyReports();
            else break;
        }
    }
}

// ================================
// ADMIN CLASS
// ================================
class Admin extends User {

    public Admin(String username, String password) {
        super(username, password);
    }

    public void viewAllReports(BaryoAlert systemManager) {
        ArrayList<Report> reports = systemManager.getReports();
        if(reports.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No reports available.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(Report r : reports) {
            sb.append(r.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public void updateReportStatus(BaryoAlert systemManager) {
        String input = JOptionPane.showInputDialog("Enter Report ID to update:");
        if(input == null) return;
        int id;
        try {
            id = Integer.parseInt(input);
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID!");
            return;
        }

        Report report = systemManager.findReportById(id);
        if(report == null) {
            JOptionPane.showMessageDialog(null, "Report not found!");
            return;
        }

        String[] statuses = {"Pending", "In Progress", "Resolved"};
        int choice = JOptionPane.showOptionDialog(null, "Select new status:", "Update Status",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, statuses, statuses[0]);
        if(choice >= 0) {
            report.setStatus(statuses[choice]);
            JOptionPane.showMessageDialog(null, "Status updated successfully!");
        }
    }

    @Override
    public void menu(BaryoAlert systemManager) {
        String[] options = {"View All Reports", "Update Report Status", "Logout"};
        while(true) {
            int choice = JOptionPane.showOptionDialog(null, "Select an action:", "Admin Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if(choice == 0) viewAllReports(systemManager);
            else if(choice == 1) updateReportStatus(systemManager);
            else break;
        }
    }
}

// ================================
// MAIN SYSTEM CLASS
// ================================
public class BaryoAlert {
    private ArrayList<User> users;
    private ArrayList<Report> reports;

    public BaryoAlert() {
        users = new ArrayList<>();
        reports = new ArrayList<>();

        // Sample users
        users.add(new Admin("admin", "admin"));
        users.add(new Reporter("resident", "resident"));
    }

    public void addReport(Report report) {
        reports.add(report);
    }

    public ArrayList<Report> getReports() {
        return reports;
    }

    public Report findReportById(int id) {
        for(Report r : reports) {
            if(r.getId() == id) return r;
        }
        return null;
    }

    public void start() {
        while(true) {
            String[] options = {"Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Welcome to BaryoAlert System", "BaryoAlert",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if(choice == 0) login();
            else break;
        }
    }

    private void login() {
        String username = JOptionPane.showInputDialog("Enter username:");
        if(username == null) return;
        String password = JOptionPane.showInputDialog("Enter password:");
        if(password == null) return;

        for(User user : users) {
            if(user.getUsername().equals(username) && user.getPassword().equals(password)) {
                JOptionPane.showMessageDialog(null, "Login successful! Welcome " + username);
                user.menu(this);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Invalid username or password!");
    }

    public static void main(String[] args) {
        BaryoAlert system = new BaryoAlert();
        system.start();
    }
}
