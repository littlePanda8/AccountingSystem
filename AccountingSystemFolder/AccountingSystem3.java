//hehehehe this is my space now -Kobe the great
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * AccountingSystem3
 *
 * - Combines your second sample and requested improvements
 * - No "Remove" button in New Transaction tab
 * - "Remove Selected Account" in Accounts tab (prevents deletion if account used)
 * - Balance Sheet shows only accounts with transactions or non-zero balances
 * - General Ledger shows richer account titles
 * - Header has no "Accounts" button
 * - Auto-create accounts typed into Debit/Credit boxes with hybrid type detection:
 *     1) If account name contains a [TYPE] token (e.g. "Cash [ASSET]") that type is used
 *     2) Otherwise we attempt to guess by keywords
 *     3) When user explicitly adds a new account they can choose the type manually
 */
public class AccountingSystem3_fixed extends JFrame {

    enum AccountType { ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE, UNKNOWN }

    static class Account {
        final String name;       // canonical name (no [TYPE] token)
        AccountType type;
        double balance;
        Account(String name, AccountType type) { this.name = name; this.type = type; this.balance = 0.0; }
    }

    static class Transaction {
        final String date, description;
        final String debit;      // canonical names
        final String credit;     // canonical names
        final double amount;
        Transaction(String date, String description, String debit, String credit, double amount) {
            this.date = date; this.description = description; this.debit = debit; this.credit = credit; this.amount = amount;
        }
    }

    // Data stores
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    // Suggested accounts (with bracket tokens)
    private final String[] suggestedAccounts = {
        "Cash [ASSET]", "Petty Cash [ASSET]", "Accounts Receivable [ASSET]",
        "Notes Receivable [ASSET]", "Supplies [ASSET]", "Inventory [ASSET]",
        "Prepaid Rent [ASSET]", "Prepaid Insurance [ASSET]", "Equipment [ASSET]",
        "Accumulated Depreciation [ASSET]",

        "Accounts Payable [LIABILITY]", "Notes Payable [LIABILITY]", "Unearned Revenue [LIABILITY]",

        "Owner's Capital [EQUITY]", "Owner's Drawings [EQUITY]", "Retained Earnings [EQUITY]",

        "Sales Revenue [REVENUE]", "Service Revenue [REVENUE]",

        "Salaries Expense [EXPENSE]", "Rent Expense [EXPENSE]",
        "Utilities Expense [EXPENSE]", "Supplies Expense [EXPENSE]",
        "Depreciation Expense [EXPENSE]", "Insurance Expense [EXPENSE]"
    };

    // UI models
    private final DefaultTableModel transModel = new DefaultTableModel(new String[]{"Date","Description","Debit","Credit","Amount"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final DefaultTableModel accModel = new DefaultTableModel(new String[]{"Account","Type","Balance"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final DefaultTableModel journalModel = new DefaultTableModel(new String[]{"Date","Description","Account","Debit","Credit"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final DefaultTableModel ledgerModel = new DefaultTableModel(new String[]{"Date","Description","Debit","Credit","Running"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final DefaultTableModel assetsModel = new DefaultTableModel(new String[]{"Asset","Amount"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };
    private final DefaultTableModel leModel = new DefaultTableModel(new String[]{"Liabilities & Equity","Amount"},0){
        @Override public boolean isCellEditable(int r,int c){ return false; }
    };

    // UI components
    private JTextField dateField, descField;
    private JFormattedTextField amountField;
    private JComboBox<String> debitCombo, creditCombo;
    private JTable transTable, accTable, journalTable, ledgerTable, assetsTable, leTable;
    private JList<String> ledgerList;

    // Formatting
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("en","PH"));
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Theme
    private final Color softBlue = new Color(235,243,250);
    private final Color accentBlue = new Color(30,100,180);
    private final Color white = Color.WHITE;

    public AccountingSystem3_fixed() {
        super("Accounting System 3 (fixed)");
        initDefaults();
        initLookAndFeel();
        initUI();
        refreshAllViews();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100,720);
        setLocationRelativeTo(null);
    }

    private void initDefaults(){
        
        for(String s : suggestedAccounts) {
            String name = s.contains("[") ? s.substring(0, s.indexOf('[')).trim() : s.trim();
            AccountType t = detectTypeFromBracket(s);
            if(t == AccountType.UNKNOWN) t = detectTypeByKeyword(name);
            accounts.putIfAbsent(name, new Account(name, t));
        }
    }

    private void initLookAndFeel(){
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("Table.font", new FontUIResource("SansSerif", Font.PLAIN, 13));
                    UIManager.put("TableHeader.font", new FontUIResource("SansSerif", Font.BOLD, 13));
                    break;
                }
            }
        } catch(Exception ignored){}
    }

    private void initUI(){
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createTabbedPane(), BorderLayout.CENTER);
    }

    private Component createHeader(){
        JLabel header = new JLabel("   Accounting System 3");
        header.setOpaque(true);
        header.setBackground(accentBlue);
        header.setForeground(white);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(12,14,12,10));
        return header;
    }

    private JComponent createTabbedPane(){
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab("New Transaction", createNewTransactionPanel());
        tabs.addTab("Transactions", createTransactionsPanel());
        tabs.addTab("Accounts", createAccountsPanel());
        tabs.addTab("General Journal", createJournalPanel());
        tabs.addTab("General Ledger", createLedgerPanel());
        tabs.addTab("Balance Sheet", createBalancePanel());

        return tabs;
    }

    // ---------------------- New Transaction ----------------
    private JComponent createNewTransactionPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(softBlue);
        p.setBorder(new EmptyBorder(12,12,12,12));

        JLabel title = new JLabel("Add New Transaction", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(accentBlue);
        title.setBorder(new EmptyBorder(8,0,12,0));
        p.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(softBlue);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,16,8,16);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx=0; gbc.gridy=0; gbc.weightx=1;

        dateField = new JTextField(dateFmt.format(LocalDate.now()));
        descField = new JTextField();
        NumberFormatter nf = new NumberFormatter(NumberFormat.getNumberInstance());
        nf.setValueClass(Double.class); nf.setMinimum(0.0); nf.setAllowsInvalid(false);
        amountField = new JFormattedTextField(nf); amountField.setValue(0.0);

        debitCombo = new JComboBox<>(getAccountNames()); debitCombo.setEditable(true);
        creditCombo = new JComboBox<>(getAccountNames()); creditCombo.setEditable(true);

        form.add(new JLabel("Date (YYYY-MM-DD)"), gbc);
        gbc.gridy++; form.add(dateField, gbc);
        gbc.gridy++; form.add(new JLabel("Description"), gbc);
        gbc.gridy++; form.add(descField, gbc);
        gbc.gridy++; form.add(new JLabel("Debit Account"), gbc);
        gbc.gridy++; form.add(debitCombo, gbc);
        gbc.gridy++; form.add(new JLabel("Credit Account"), gbc);
        gbc.gridy++; form.add(creditCombo, gbc);
        gbc.gridy++; form.add(new JLabel("Amount"), gbc);
        gbc.gridy++; form.add(amountField, gbc);

        gbc.gridy++;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(softBlue);
        JButton addBtn = new JButton("Add Transaction");
        addBtn.setBackground(accentBlue); addBtn.setForeground(white); addBtn.setFocusPainted(false);
        buttons.add(addBtn);
        form.add(buttons, gbc);

        addBtn.addActionListener(e -> onAddTransaction());

        p.add(form, BorderLayout.CENTER);
        return p;
    }

    private void onAddTransaction(){
        String date = dateField.getText().trim();
        String desc = descField.getText().trim();
        String debitRaw = Objects.toString(debitCombo.getEditor().getItem(),"").trim();
        String creditRaw = Objects.toString(creditCombo.getEditor().getItem(),"").trim();

        double amount;
        try { amount = ((Number) amountField.getValue()).doubleValue(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this,"Invalid amount.","Validation",JOptionPane.WARNING_MESSAGE); return; }

        if(date.isEmpty() || desc.isEmpty() || debitRaw.isEmpty() || creditRaw.isEmpty()){
            JOptionPane.showMessageDialog(this,"Complete all fields.","Validation",JOptionPane.WARNING_MESSAGE); return;
        }
        if(canonicalize(debitRaw).equals(canonicalize(creditRaw))){
            JOptionPane.showMessageDialog(this,"Debit and Credit cannot be the same.","Validation",JOptionPane.WARNING_MESSAGE); return;
        }
        try { LocalDate.parse(date, dateFmt); }
        catch(Exception ex){ JOptionPane.showMessageDialog(this,"Invalid date format. Use YYYY-MM-DD.","Validation",JOptionPane.WARNING_MESSAGE); return; }

        // canonical names (strip any [TYPE] token)
        String debit = canonicalize(debitRaw);
        String credit = canonicalize(creditRaw);

        // ensure accounts exist; hybrid type detection uses raw for bracket if present
        ensureAccountExistsWithHybridType(debitRaw);  // will canonicalize inside as needed
        ensureAccountExistsWithHybridType(creditRaw);

        Transaction tx = new Transaction(date, desc, debit, credit, amount);
        transactions.add(tx);

        // recompute balances & refresh models
        recomputeBalances();
        transModel.addRow(new Object[]{tx.date, tx.description, tx.debit, tx.credit, currencyFmt.format(tx.amount)});
        journalModel.addRow(new Object[]{tx.date, tx.description, tx.debit, currencyFmt.format(tx.amount), ""});
        journalModel.addRow(new Object[]{tx.date, tx.description, tx.credit, "", currencyFmt.format(tx.amount)});

        refreshAllViews();

        // reset form
        descField.setText("");
        amountField.setValue(0.0);
        dateField.setText(dateFmt.format(LocalDate.now()));
    }

    private String canonicalize(String raw){
        if(raw == null) return "";
        String r = raw.trim();
        int idx = r.indexOf('[');
        if(idx >= 0) r = r.substring(0, idx).trim();
        return r;
    }

    private void ensureAccountExistsWithHybridType(String rawName){
        if(rawName == null) return;
        String trimmed = rawName.trim();
        AccountType bracket = detectTypeFromBracket(trimmed);
        String canonical = canonicalize(trimmed);

        if(!accounts.containsKey(canonical)){
            AccountType guessed = bracket != AccountType.UNKNOWN ? bracket : detectTypeByKeyword(canonical);
            if(guessed == AccountType.UNKNOWN) guessed = AccountType.ASSET; // safe fallback
            accounts.put(canonical, new Account(canonical, guessed));
            updateComboModels();
        } else {
            // if bracket present and differs, update the stored account type
            if(bracket != AccountType.UNKNOWN){
                Account a = accounts.get(canonical);
                if(a != null && a.type != bracket) a.type = bracket;
            }
        }
    }

    private AccountType detectTypeFromBracket(String s){
        if(s == null) return AccountType.UNKNOWN;
        String u = s.toUpperCase();
        if(u.contains("[ASSET]")) return AccountType.ASSET;
        if(u.contains("[LIABILITY]")) return AccountType.LIABILITY;
        if(u.contains("[EQUITY]")) return AccountType.EQUITY;
        if(u.contains("[REVENUE]")) return AccountType.REVENUE;
        if(u.contains("[EXPENSE]")) return AccountType.EXPENSE;
        return AccountType.UNKNOWN;
    }

    private AccountType detectTypeByKeyword(String name){
        if(name==null) return AccountType.UNKNOWN;
        String s = name.toLowerCase();
        if(s.contains("cash") || s.contains("receivable") || s.contains("inventory") || s.contains("equipment") || s.contains("prepaid") || s.contains("supplies")) return AccountType.ASSET;
        if(s.contains("payable") || s.contains("note") || s.contains("unearned")) return AccountType.LIABILITY;
        if(s.contains("capital") || s.contains("owner") || s.contains("retained") || s.contains("draw")) return AccountType.EQUITY;
        if(s.contains("revenue") || s.contains("sales") || s.contains("service")) return AccountType.REVENUE;
        if(s.contains("expense") || s.contains("rent") || s.contains("salary") || s.contains("utilities") || s.contains("cost") || s.contains("depreciation") || s.contains("insurance")) return AccountType.EXPENSE;
        return AccountType.UNKNOWN;
    }

    private void recomputeBalances(){
        // reset
        for(Account a : accounts.values()) a.balance = 0.0;
        // apply transactions in insertion order
        for(Transaction tx : transactions){
            Account da = accounts.get(tx.debit);
            Account ca = accounts.get(tx.credit);
            if(da != null){
                if(da.type == AccountType.ASSET || da.type == AccountType.EXPENSE) da.balance += tx.amount;
                else da.balance -= tx.amount;
            }
            if(ca != null){
                if(ca.type == AccountType.LIABILITY || ca.type == AccountType.EQUITY || ca.type == AccountType.REVENUE) ca.balance += tx.amount;
                else ca.balance -= tx.amount;
            }
        }
    }

    // ---------------------- Transactions Tab ----------------
    private JComponent createTransactionsPanel(){
        JPanel p = new JPanel(new BorderLayout());
        transTable = new JTable(transModel);
        configureRightAlign(transTable, 4);
        p.add(new JScrollPane(transTable), BorderLayout.CENTER);
        return p;
    }

    // --------------------- Accounts Tab ----------------
    private JComponent createAccountsPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(softBlue);

        accTable = new JTable(accModel);
        configureRightAlign(accTable, 2);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(softBlue);

        JButton addAcct = new JButton("Add Account");
        addAcct.setBackground(accentBlue); addAcct.setForeground(white);
        addAcct.addActionListener(e -> onAddAccountManually());
        top.add(addAcct);

        JButton removeAcct = new JButton("Remove Selected Account");
        removeAcct.setBackground(new Color(180,50,50)); removeAcct.setForeground(white);
        removeAcct.addActionListener(e -> onRemoveAccount());
        top.add(removeAcct);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(accTable), BorderLayout.CENTER);
        return p;
    }

    private void onAddAccountManually(){
        JTextField name = new JTextField();
        JComboBox<AccountType> typeBox = new JComboBox<>(AccountType.values());
        Object[] fields = {"Account name:", name, "Account type:", typeBox};
        int res = JOptionPane.showConfirmDialog(this, fields, "Add Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(res == JOptionPane.OK_OPTION){
            String raw = name.getText().trim();
            if(raw.isEmpty()){ JOptionPane.showMessageDialog(this,"Name required.","Validation",JOptionPane.WARNING_MESSAGE); return; }
            String canonical = canonicalize(raw);
            if(accounts.containsKey(canonical)){ JOptionPane.showMessageDialog(this,"Account already exists.","Validation",JOptionPane.WARNING_MESSAGE); return; }
            accounts.put(canonical, new Account(canonical, (AccountType) typeBox.getSelectedItem()));
            updateComboModels();
            refreshAllViews();
        }
    }

    private void onRemoveAccount(){
        int sel = accTable.getSelectedRow();
        if(sel < 0){ JOptionPane.showMessageDialog(this,"Select an account to remove.","Validation",JOptionPane.WARNING_MESSAGE); return; }
        String name = (String) accModel.getValueAt(sel, 0);
        boolean used = transactions.stream().anyMatch(tx -> tx.debit.equals(name) || tx.credit.equals(name));
        if(used){
            JOptionPane.showMessageDialog(this,"Cannot remove account used in transactions.","Validation",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,"Remove account '"+name+"'?","Confirm",JOptionPane.YES_NO_OPTION);
        if(c == JOptionPane.YES_OPTION){
            accounts.remove(name);
            updateComboModels();
            refreshAllViews();
        }
    }

    // ---------------------- Journal Tab ----------------
    private JComponent createJournalPanel(){
        JPanel p = new JPanel(new BorderLayout());
        journalTable = new JTable(journalModel);
        configureRightAlign(journalTable, 3);
        configureRightAlign(journalTable, 4);
        p.add(new JScrollPane(journalTable), BorderLayout.CENTER);
        return p;
    }

    // ---------------------- Ledger Tab ----------------
    private JComponent createLedgerPanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(softBlue);

        ledgerList = new JList<>(new DefaultListModel<>());
        ledgerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ledgerList.setFixedCellWidth(360);
        ledgerList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()){
                String sel = ledgerList.getSelectedValue();
                if(sel != null) {
                    String name = extractName(sel);
                    showLedgerFor(name);
                }
            }
        });

        ledgerTable = new JTable(ledgerModel);
        configureRightAlign(ledgerTable, 2);
        configureRightAlign(ledgerTable, 3);
        configureRightAlign(ledgerTable, 4);

        p.add(new JScrollPane(ledgerList), BorderLayout.WEST);
        p.add(new JScrollPane(ledgerTable), BorderLayout.CENTER);
        return p;
    }

    private String extractName(String rich){
        int idx = rich.indexOf(" (");
        return idx>0? rich.substring(0, idx) : rich;
    }

    private void showLedgerFor(String accountName){
        ledgerModel.setRowCount(0);
        if(accountName == null || !accounts.containsKey(accountName)) return;
        Account acc = accounts.get(accountName);
        double running = 0.0;
        for(Transaction tx : transactions){
            boolean dr = tx.debit.equals(accountName);
            boolean cr = tx.credit.equals(accountName);
            if(!dr && !cr) continue;

            if(dr){
                if(acc.type == AccountType.ASSET || acc.type == AccountType.EXPENSE) running += tx.amount;
                else running -= tx.amount;
                ledgerModel.addRow(new Object[]{tx.date, tx.description, currencyFmt.format(tx.amount), "", currencyFmt.format(running)});
            }
            if(cr){
                if(acc.type == AccountType.LIABILITY || acc.type == AccountType.EQUITY || acc.type == AccountType.REVENUE) running += tx.amount;
                else running -= tx.amount;
                ledgerModel.addRow(new Object[]{tx.date, tx.description, "", currencyFmt.format(tx.amount), currencyFmt.format(running)});
            }
        }
    }

    // ---------------------- Balance Sheet ----------------
    private JComponent createBalancePanel(){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(softBlue);
        p.setBorder(new EmptyBorder(8,8,8,8));

        assetsTable = new JTable(assetsModel); leTable = new JTable(leModel);
        configureRightAlign(assetsTable,1); configureRightAlign(leTable,1);

        JPanel columns = new JPanel(new GridLayout(1,2,12,0));
        JPanel left = new JPanel(new BorderLayout()); left.add(new JLabel("Assets", SwingConstants.LEFT), BorderLayout.NORTH); left.add(new JScrollPane(assetsTable), BorderLayout.CENTER);
        JPanel right = new JPanel(new BorderLayout()); right.add(new JLabel("Liabilities & Equity", SwingConstants.LEFT), BorderLayout.NORTH); right.add(new JScrollPane(leTable), BorderLayout.CENTER);
        columns.add(left); columns.add(right);
        p.add(columns, BorderLayout.CENTER);
        return p;
    }

    // -------------------- Utilities ----------------
    private void configureRightAlign(JTable t, int col){
        if(t.getColumnModel().getColumnCount() > col){
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setHorizontalAlignment(SwingConstants.RIGHT);
            t.getColumnModel().getColumn(col).setCellRenderer(r);
        }
    }

    private void updateComboModels(){
        String[] names = getAccountNames();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(names);
        if(debitCombo != null) debitCombo.setModel(model);
        if(creditCombo != null) creditCombo.setModel(new DefaultComboBoxModel<>(names));
    }

    private String[] getAccountNames(){
        return accounts.keySet().toArray(new String[0]);
    }

    private void refreshAllViews(){
        // accounts table
        accModel.setRowCount(0);
        for(Account a : accounts.values()){
            accModel.addRow(new Object[]{a.name, a.type.name(), currencyFmt.format(a.balance)});
        }

        // ledger list (rich titles)
        DefaultListModel<String> lm = new DefaultListModel<>();
        for(Account a : accounts.values()){
            lm.addElement(String.format("%s (%s) — %s", a.name, a.type.name(), currencyFmt.format(a.balance)));
        }
        if(ledgerList != null) ledgerList.setModel(lm);

        // transactions table
        transModel.setRowCount(0);
        for(Transaction t : transactions) transModel.addRow(new Object[]{t.date, t.description, t.debit, t.credit, currencyFmt.format(t.amount)});

        // journal
        journalModel.setRowCount(0);
        for(Transaction t : transactions){
            journalModel.addRow(new Object[]{t.date, t.description, t.debit, currencyFmt.format(t.amount), ""});
            journalModel.addRow(new Object[]{t.date, t.description, t.credit, "", currencyFmt.format(t.amount)});
        }

        // balance sheet: only accounts used in transactions or non-zero
        Set<String> used = new HashSet<>();
        for(Transaction t : transactions){ used.add(t.debit); used.add(t.credit); }

        assetsModel.setRowCount(0); leModel.setRowCount(0);
        double totalAssets = 0.0, totalLE = 0.0;
        for(Account a : accounts.values()){
            boolean include = used.contains(a.name) || Math.abs(a.balance) > 0.0;
            if(!include) continue;
            if(a.type == AccountType.ASSET){
                assetsModel.addRow(new Object[]{a.name, currencyFmt.format(a.balance)});
                totalAssets += a.balance;
            } else if(a.type == AccountType.LIABILITY || a.type == AccountType.EQUITY){
                leModel.addRow(new Object[]{a.name, currencyFmt.format(a.balance)});
                totalLE += a.balance;
            }
        }
        assetsModel.addRow(new Object[]{"", ""});
        assetsModel.addRow(new Object[]{"Total Assets", currencyFmt.format(totalAssets)});
        leModel.addRow(new Object[]{"", ""});
        leModel.addRow(new Object[]{"Total Liabilities & Equity", currencyFmt.format(totalLE)});

        // ensure combos are updated
        updateComboModels();
    }

    // ---------------- Main ----------------------
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            AccountingSystem3_fixed app = new AccountingSystem3_fixed();
            app.setVisible(true);
        });
    }
}

