import javax.swing.*;
import java.awt.*;

public class AccountingSystemPinkUI extends JFrame {

    public AccountingSystemPinkUI() {
        setTitle("Accounting System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------------- HEADER ----------------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 80, 120)); // pink header
        JLabel title = new JLabel("Accounting System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        header.add(title, BorderLayout.WEST);

        // header buttons (top right)
        JPanel headerButtons = new JPanel();
        headerButtons.setOpaque(false);
        JButton btnSave = new JButton("💾");
        JButton btnAdd = new JButton("+");
        JButton btnHelp = new JButton("?");
        styleHeaderButton(btnSave);
        styleHeaderButton(btnAdd);
        styleHeaderButton(btnHelp);
        headerButtons.add(btnSave);
        headerButtons.add(btnAdd);
        headerButtons.add(btnHelp);
        header.add(headerButtons, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ---------------- TABS ----------------
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("New Transaction", createTransactionPanel());
        tabs.addTab("Transactions", new JPanel());
        tabs.addTab("Accounts", new JPanel());
        tabs.addTab("General Journal", new JPanel());
        tabs.addTab("General Ledger", new JPanel());
        tabs.addTab("Balance Sheet", new JPanel());
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    // --------------------------------------
    // Transaction Panel (the one in screenshot)
    // --------------------------------------
    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels and Inputs
        JTextField dateField = new JTextField(20);
        JTextField descField = new JTextField(20);
        JComboBox<String> debitBox = new JComboBox<>(new String[]{
            "Cash [ASSET]", "Accounts Receivable [ASSET]", "Supplies [ASSET]"
        });
        JComboBox<String> creditBox = new JComboBox<>(new String[]{
            "Owner's Capital [EQUITY]", "Service Revenue [REVENUE]"
        });
        JTextField amountField = new JTextField(20);

        // --- Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        // --- Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descField, gbc);

        // --- Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Debit Account:"), gbc);
        gbc.gridx = 1;
        formPanel.add(debitBox, gbc);

        // --- Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Credit Account:"), gbc);
        gbc.gridx = 1;
        formPanel.add(creditBox, gbc);

        // --- Row 5
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        // Add Button
        gbc.gridx = 1; gbc.gridy = 5;
        JButton addBtn = new JButton("Add Transaction");
        addBtn.setBackground(new Color(255, 105, 180)); // hot pink
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        formPanel.add(addBtn, gbc);

        // Status area at bottom
        JLabel status = new JLabel(" ");
        status.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        status.setForeground(new Color(0, 153, 51));

        addBtn.addActionListener(e -> {
            if (descField.getText().isEmpty() || amountField.getText().isEmpty()) {
                status.setText("⚠ Please fill in all fields.");
                status.setForeground(Color.RED);
            } else {
                status.setText("✅ Transaction added successfully.");
                status.setForeground(new Color(0, 153, 51));
            }
        });

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(status, BorderLayout.SOUTH);

        return panel;
    }

    private void styleHeaderButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(255, 105, 180)); // pink tone
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(45, 25));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public static void main(String[] args) {
        try {
            // Optional: modern Nimbus theme
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(AccountingSystemPinkUI::new);
    }
}
