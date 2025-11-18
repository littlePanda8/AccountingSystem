import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class AccountingSystem extends JFrame {
    enum AccountType { ASSET, LIABILITY, EQUITY, INCOME, EXPENSE }

    static class Account {
        String name;
        AccountType type;
        double balance; 

        Account(String name, AccountType type) {
            this.name = name;
            this.type = type;
            this.balance = 0.0;
        }
    }

    static class Transaction {
        String date; 
        String description;
        String debitAccount;
        String creditAccount;
        double amount;

        Transaction(String date, String description, String debitAccount, String creditAccount, double amount) {
            this.date = date;
            this.description = description;
            this.debitAccount = debitAccount;
            this.creditAccount = creditAccount;
            this.amount = amount;
        }
    }

    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    private final DefaultTableModel transactionsModel = new DefaultTableModel(new Object[]{"Date", "Description", "Debit Account", "Credit Account", "Amount"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultTableModel accountsModel = new DefaultTableModel(new Object[]{"Account", "Type", "Balance"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultTableModel journalModel = new DefaultTableModel(new Object[]{"Date", "Description", "Account", "Debit", "Credit"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final DefaultTableModel assetsModel = new DefaultTableModel(new Object[]{"Asset", "Amount"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultTableModel liabilitiesEquityModel = new DefaultTableModel(new Object[]{"Liabilities & Equity", "Amount"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final DefaultTableModel ledgerModel = new DefaultTableModel(new Object[]{"Date", "Description", "Debit", "Credit", "Balance"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };


    @SuppressWarnings("deprecation")
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AccountingSystem() {
        super("Accounting System");
        initDefaultAccounts();
        initUI();
        refreshAllTables();
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initDefaultAccounts() {
        addAccount(new Account("Cash", AccountType.ASSET));
        addAccount(new Account("Accounts Receivable", AccountType.ASSET));
        addAccount(new Account("Inventory", AccountType.ASSET));
        addAccount(new Account("Prepaid Expenses", AccountType.ASSET));
        addAccount(new Account("Equipment", AccountType.ASSET));
        addAccount(new Account("Accounts Payable", AccountType.LIABILITY));
        addAccount(new Account("Notes Payable", AccountType.LIABILITY));
        addAccount(new Account("Owner's Capital", AccountType.EQUITY));
        addAccount(new Account("Sales Revenue", AccountType.INCOME));
        addAccount(new Account("Service Revenue", AccountType.INCOME));
        addAccount(new Account("Cost of Goods Sold", AccountType.EXPENSE));
        addAccount(new Account("Rent Expense", AccountType.EXPENSE));
        addAccount(new Account("Salaries Expense", AccountType.EXPENSE));
        addAccount(new Account("Utilities Expense", AccountType.EXPENSE));
    }

    private void addAccount(Account a) {
        accounts.put(a.name, a);
    }

    private void initUI() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    // tweak default fonts a bit
                    UIManager.put("Table.font", new FontUIResource("SansSerif", Font.PLAIN, 13));
                    UIManager.put("TableHeader.font", new FontUIResource("SansSerif", Font.BOLD, 13));
                    break;
                }
            }
        } catch (Exception ignored) {}

        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainTabbedPane(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(12, 35, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0,0,0,50)));
        header.setPreferredSize(new Dimension(0, 72));

        JLabel title = new JLabel("Accounting System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setBorder(new EmptyBorder(8, 16, 8, 8));
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        right.setOpaque(false);

        JButton addBtn = styledButton("Add");
        addBtn.setToolTipText("Add Transaction");
        addBtn.addActionListener(e -> showNewTransactionTab());
        JButton acctBtn = styledButton("Accounts");
        acctBtn.setToolTipText("Manage Accounts");
        acctBtn.addActionListener(e -> showAccountsTab());
        JButton helpBtn = styledButton("Help?");
        helpBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Simple Accounting System\nDouble-entry transactions update account balances.\nUse tabs to navigate.",
                "Help", JOptionPane.INFORMATION_MESSAGE));

        right.add(addBtn);
        right.add(acctBtn);
        right.add(helpBtn);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(18, 94, 214));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return b;
    }

    private JTabbedPane tabbedPane;
    private Component newTransactionTab;

    private JComponent createMainTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(8,10,8,10));
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newTransactionTab = createNewTransactionPanel();
        tabbedPane.addTab("New Transaction", newTransactionTab);
        tabbedPane.addTab("Transactions", createTransactionsPanel());
        tabbedPane.addTab("Accounts", createAccountsPanel());
        tabbedPane.addTab("General Journal", createJournalPanel());
        tabbedPane.addTab("General Ledger", createLedgerPanel());
        tabbedPane.addTab("Balance Sheet", createBalanceSheetPanel());
        return tabbedPane;
    }

    private void showNewTransactionTab() {
        tabbedPane.setSelectedComponent(newTransactionTab);
    }
    private void showAccountsTab() {
        tabbedPane.setSelectedIndex(2);
    }

    private JComboBox<String> debitCombo, creditCombo;
    private JFormattedTextField amountField;
    private JTextField dateField, descField;

    private JComponent createNewTransactionPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));

        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                new EmptyBorder(12,12,12,12)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Date (YYYY-MM-DD)"), gbc);
        dateField = new JTextField(dateFmt.format(LocalDate.now()));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        form.add(dateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Description"), gbc);
        descField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
        form.add(descField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(new JLabel("Debit Account"), gbc);
        debitCombo = new JComboBox<>(getAccountNames());
        debitCombo.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1;
        form.add(debitCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(new JLabel("Credit Account"), gbc);
        creditCombo = new JComboBox<>(getAccountNames());
        creditCombo.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1;
        form.add(creditCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        form.add(new JLabel("Amount"), gbc);

        NumberFormatter formatter = new NumberFormatter(NumberFormat.getNumberInstance());
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(false);
        amountField = new JFormattedTextField(formatter);
        amountField.setValue(0.0);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1;
        form.add(amountField, gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton postBtn = styledButton("Save Transaction");
        JButton clearBtn = new JButton("Remove");
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearTransactionForm());
        postBtn.addActionListener(e -> postTransactionAction());
        btns.add(clearBtn);
        btns.add(postBtn);

        p.add(form, BorderLayout.NORTH);
        p.add(btns, BorderLayout.SOUTH);

        return p;
    }

    private String[] getAccountNames() {
        return accounts.keySet().toArray(new String[0]);
    }

    private void clearTransactionForm() {
        dateField.setText(dateFmt.format(LocalDate.now()));
        descField.setText("");
        if (debitCombo.getItemCount() > 0) debitCombo.setSelectedIndex(0);
        if (creditCombo.getItemCount() > 0) creditCombo.setSelectedIndex(0);
        amountField.setValue(0.0);
    }

    private void postTransactionAction() {
        String date = dateField.getText().trim();
        String desc = descField.getText().trim();
        String debit = (String) debitCombo.getSelectedItem();
        String credit = (String) creditCombo.getSelectedItem();
        double amount;
        try {
            amount = ((Number) amountField.getValue()).doubleValue();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (debit == null || credit == null || debit.equals(credit)) {
            JOptionPane.showMessageDialog(this, "Please choose different debit and credit accounts.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (amount <= 0.0) {
            JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate.parse(date, dateFmt);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Transaction tx = new Transaction(date, desc, debit, credit, amount);
        transactions.add(tx);
        applyTransactionToAccounts(tx);
        addTransactionToModels(tx);
        refreshAllTables();
        clearTransactionForm();
    }

    private void applyTransactionToAccounts(Transaction tx) {
        Account debitAcc = accounts.get(tx.debitAccount);
        Account creditAcc = accounts.get(tx.creditAccount);
        if (debitAcc == null || creditAcc == null) return;

        if (isDebitNormal(debitAcc.type)) {
            debitAcc.balance += tx.amount;
        } else {
            debitAcc.balance -= tx.amount;
        }

        if (isCreditNormal(creditAcc.type)) {
            creditAcc.balance += tx.amount;
        } else {
            creditAcc.balance -= tx.amount;
        }
    }

    private boolean isDebitNormal(AccountType t) {
        return t == AccountType.ASSET || t == AccountType.EXPENSE;
    }
    private boolean isCreditNormal(AccountType t) {
        return t == AccountType.LIABILITY || t == AccountType.EQUITY || t == AccountType.INCOME;
    }

    private JComponent createTransactionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8,8,8,8));

        JTable t = new JTable(transactionsModel);
        configureAmountColumn(t, 4);
        JScrollPane sp = new JScrollPane(t);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void addTransactionToModels(Transaction tx) {
        transactionsModel.addRow(new Object[]{tx.date, tx.description, tx.debitAccount, tx.creditAccount, currencyFmt.format(tx.amount)});
        journalModel.addRow(new Object[]{tx.date, tx.description, tx.debitAccount, currencyFmt.format(tx.amount), ""});
        journalModel.addRow(new Object[]{tx.date, tx.description, tx.creditAccount, "", currencyFmt.format(tx.amount)});
    }

    private JComponent createAccountsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8,8,8,8));

        JTable t = new JTable(accountsModel);
        configureAmountColumn(t, 2);
        JScrollPane sp = new JScrollPane(t);
        p.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addAcct = styledButton("Add Account");
        addAcct.addActionListener(e -> showAddAccountDialog());
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshAllTables());
        bottom.add(addAcct);
        bottom.add(refresh);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private void showAddAccountDialog() {
        JTextField name = new JTextField();
        JComboBox<AccountType> typeCombo = new JComboBox<>(AccountType.values());
        Object[] fields = {
                "Account name:", name,
                "Account type:", typeCombo
        };
        if (JOptionPane.showConfirmDialog(this, fields, "Add Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String nm = name.getText().trim();
            if (nm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (accounts.containsKey(nm)) {
                JOptionPane.showMessageDialog(this, "Account already exists.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            addAccount(new Account(nm, (AccountType) typeCombo.getSelectedItem()));
            debitCombo.setModel(new DefaultComboBoxModel<>(getAccountNames()));
            creditCombo.setModel(new DefaultComboBoxModel<>(getAccountNames()));
            refreshAllTables();
        }
    }

    private JComponent createJournalPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8,8,8,8));
        JTable t = new JTable(journalModel);
        configureAmountColumn(t, 3);
        configureAmountColumn(t, 4);
        JScrollPane sp = new JScrollPane(t);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JComponent createLedgerPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8,8,8,8));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String nm : accounts.keySet()) listModel.addElement(nm);
        JList<String> acctList = new JList<>(listModel);
        acctList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        acctList.setVisibleRowCount(10);
        acctList.setFixedCellWidth(220);
        JScrollPane left = new JScrollPane(acctList);
        left.setPreferredSize(new Dimension(260, 0));
        p.add(left, BorderLayout.WEST);

        JTable ledgerTable = new JTable(ledgerModel);
        configureAmountColumn(ledgerTable, 2);
        configureAmountColumn(ledgerTable, 3);
        JScrollPane right = new JScrollPane(ledgerTable);
        p.add(right, BorderLayout.CENTER);

        acctList.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                String sel = acctList.getSelectedValue();
                showLedgerForAccount(sel);
            }
        });

        return p;
    }

    private void showLedgerForAccount(String accountName) {
        ledgerModel.setRowCount(0);
        if (accountName == null) return;
        Account acc = accounts.get(accountName);

        double running = 0.0;
        for (Transaction tx : transactions) {
            boolean debitHere = tx.debitAccount.equals(accountName);
            boolean creditHere = tx.creditAccount.equals(accountName);
            if (!debitHere && !creditHere) continue;

            double debitAmt = debitHere ? tx.amount : 0.0;
            double creditAmt = creditHere ? tx.amount : 0.0;

            if (debitHere) {
                if (isDebitNormal(acc.type)) running += tx.amount; else running -= tx.amount;
            }
            if (creditHere) {
                if (isCreditNormal(acc.type)) running += tx.amount; else running -= tx.amount;
            }

            ledgerModel.addRow(new Object[]{tx.date, tx.description, debitAmt == 0.0 ? "" : currencyFmt.format(debitAmt), creditAmt == 0.0 ? "" : currencyFmt.format(creditAmt), currencyFmt.format(running)});
        }
    }

    private JComponent createBalanceSheetPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(8,8,8,8));

        JPanel columns = new JPanel(new GridLayout(1,2,12,0));
        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Assets", SwingConstants.LEFT), BorderLayout.NORTH);
        JTable assetsTable = new JTable(assetsModel);
        configureAmountColumn(assetsTable, 1);
        left.add(new JScrollPane(assetsTable), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel("Liabilities & Equity", SwingConstants.LEFT), BorderLayout.NORTH);
        JTable leTable = new JTable(liabilitiesEquityModel);
        configureAmountColumn(leTable, 1);
        right.add(new JScrollPane(leTable), BorderLayout.CENTER);

        columns.add(left);
        columns.add(right);
        p.add(columns, BorderLayout.CENTER);

        return p;
    }

    private void configureAmountColumn(JTable t, int colIndex) {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        t.getColumnModel().getColumn(colIndex).setCellRenderer(right);
    }

    private void refreshAllTables() {
        accountsModel.setRowCount(0);
        for (Account a : accounts.values()) {
            accountsModel.addRow(new Object[]{a.name, a.type.name(), currencyFmt.format(a.balance)});
        }

        assetsModel.setRowCount(0);
        liabilitiesEquityModel.setRowCount(0);
        double totalAssets = 0.0;
        double totalLiabilitiesEquity = 0.0;
        for (Account a : accounts.values()) {
            if (a.type == AccountType.ASSET) {
                assetsModel.addRow(new Object[]{a.name, currencyFmt.format(a.balance)});
                totalAssets += a.balance;
            } else if (a.type == AccountType.LIABILITY || a.type == AccountType.EQUITY) {
                liabilitiesEquityModel.addRow(new Object[]{a.name, currencyFmt.format(a.balance)});
                totalLiabilitiesEquity += a.balance;
            }
        }
        assetsModel.addRow(new Object[]{"", ""});
        assetsModel.addRow(new Object[]{"Total Assets", currencyFmt.format(totalAssets)});

        liabilitiesEquityModel.addRow(new Object[]{"", ""});
        liabilitiesEquityModel.addRow(new Object[]{"Total Liabilities & Equity", currencyFmt.format(totalLiabilitiesEquity)});

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccountingSystem app = new AccountingSystem();
            app.setVisible(true);
        });
    }
}
