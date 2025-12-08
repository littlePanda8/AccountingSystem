    import javax.swing.*;
    import javax.swing.border.*;
    import javax.swing.event.TableModelEvent;
    import javax.swing.plaf.basic.BasicTabbedPaneUI;
    import javax.swing.table.*;
    import java.awt.*;
    import java.text.DecimalFormat;
    import java.text.NumberFormat;
    import java.util.Vector;

    public class AccountingSystem extends JFrame {

        private static final Color BTN_GREEN = new Color(21, 120, 55);       
        private static final Color BTN_GREEN_HOVER = new Color(34, 180, 90); 
        private static final Color BTN_RED = new Color(200, 40, 40);
        private static final Color BTN_RED_HOVER = new Color(230, 60, 60);
        private static final Color HEADER_GREEN = new Color(21, 120, 55);
        private static final Color TAB_SELECTED = new Color(34, 139, 70);
        private static final Color TAB_UNSELECTED = new Color(225, 238, 230);
        private static final Color PANEL_BG = new Color(235, 247, 237);
        private static final Color TABLE_ALT_ROW = new Color(244, 252, 244);
        private static final Color TABLE_ROW = new Color(255, 255, 255);
        private static final Color TABLE_HEADER_BG = new Color(194, 225, 200);
        private static final Color TABLE_SELECTION = new Color(34, 139, 70);

        // 
        private JComboBox<String> debit;
        private JComboBox<String> credit;

        private final DefaultTableModel transactionsModel; //stores all transactions
        private final DefaultTableModel accountsModel; // account info and balances
        private final DefaultTableModel journalModel;
        private final DefaultTableModel ledgerModel;
        private final DefaultTableModel balanceLeftModel;   
        private final DefaultTableModel balanceRightModel;  
        private final DefaultListModel<String> ledgerAccountListModel; // FIX: Added list model for Ledger left side

        private final DecimalFormat moneyFmt = new DecimalFormat("\u20B1#,##0.00");

        public AccountingSystem() {

            super("Accounting System");

            transactionsModel = new DefaultTableModel(new String[]{"Date", "Description", "Debit", "Credit", "Amount"}, 0);
            accountsModel = new DefaultTableModel(new String[]{"Account", "Type", "Balance"}, 0);
            journalModel = new DefaultTableModel(new String[]{"Date", "Description", "Account", "Debit", "Credit"}, 0);
            ledgerModel = new DefaultTableModel(new String[]{"Date", "Description", "Debit", "Credit", "Running"}, 0);
            balanceLeftModel = new DefaultTableModel(new String[]{"Asset", "Amount"}, 0);
            balanceRightModel = new DefaultTableModel(new String[]{"Liability/Equity", "Amount"}, 0);
            ledgerAccountListModel = new DefaultListModel<>(); // FIX: Initialized list model

            for (String s : sampleAccounts()) {
                String type = deduceAccountType(s);
                // Use accounting format for initial zero balance
                accountsModel.addRow(new Object[]{s, type, formatAccountingMoney(0.0)});
                
                // FIX: Populate the Ledger account list on startup
                addAccountToLedgerList(s);
            }
            balanceLeftModel.addRow(new Object[]{"Total Assets", formatAccountingMoney(0.0)});
            balanceRightModel.addRow(new Object[]{"Total Liabilities & Equity", formatAccountingMoney(0.0)});

            UIManager.put("List.background", new Color(235, 247, 237));
            UIManager.put("List.foreground", Color.BLACK);
            UIManager.put("List.selectionBackground", new Color(34, 139, 70));
            UIManager.put("List.selectionForeground", Color.WHITE);

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1200, 700);
            setLocationRelativeTo(null);

            setLayout(new BorderLayout());

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(HEADER_GREEN);
            header.setBorder(new EmptyBorder(10, 14, 10, 14));
            JLabel title = new JLabel("Accounting System");
            title.setForeground(Color.WHITE);
            title.setFont(new Font("Segoe UI", Font.BOLD, 30));
            header.add(title, BorderLayout.WEST);
            add(header, BorderLayout.NORTH);

            JTabbedPane tabs = new JTabbedPane();
            tabs.setUI(new GreenTabbedPaneUI());
            tabs.setBackground(TAB_UNSELECTED);
            tabs.setBorder(new CompoundBorder(new MatteBorder(2, 0, 0, 0, new Color(120, 120, 120)), new EmptyBorder(4, 4, 4, 4)));
            tabs.setFont(new Font("Segoe UI", Font.BOLD, 13)); 

            tabs.addTab("New Transaction", createNewTransactionPanel());
            tabs.addTab("Transactions", createTransactionsPanel());
            tabs.addTab("Accounts", createAccountsPanel());
            tabs.addTab("General Journal", createBlankJournalPanel());
            tabs.addTab("General Ledger", createGeneralLedgerPanel());
            tabs.addTab("Balance Sheet", createBalanceSheetPanel());

            add(tabs, BorderLayout.CENTER);

            applyUIManagerTheme();
        }

        private void applyUIManagerTheme() {
            UIManager.put("Table.background", TABLE_ROW);
            UIManager.put("Table.alternateRowColor", TABLE_ALT_ROW);
            UIManager.put("Table.selectionBackground", TABLE_SELECTION);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("TableHeader.background", TABLE_HEADER_BG);
            UIManager.put("TableHeader.foreground", Color.DARK_GRAY);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.selectionBackground", TABLE_SELECTION);
            UIManager.put("List.selectionBackground", TABLE_SELECTION);
            UIManager.put("List.selectionForeground", Color.WHITE);
            UIManager.put("ScrollBar.width", 12);
        }

        private JPanel createNewTransactionPanel() {
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(PANEL_BG);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(14, 32, 0, 32);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;

            JLabel heading = new JLabel("Add New Transaction");
            heading.setFont(new Font("SansSerif", Font.BOLD, 25));
            heading.setForeground(new Color(34, 139, 70));
            heading.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel headingWrapper = new JPanel(new BorderLayout());
            headingWrapper.setOpaque(false);
            headingWrapper.setBorder(new EmptyBorder(10, 0, 20, 0));
            headingWrapper.add(heading, BorderLayout.CENTER);

            gbc.gridwidth = 2;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 0, 10, 0);
            p.add(headingWrapper, gbc);

            gbc.insets = new Insets(6, 40, 6, 40);
            gbc.gridwidth = 1;
            gbc.gridy++;

            p.add(makeLabel("Date (YYYY-MM-DD)"), gbc);
            gbc.gridy++;
            JTextField dateField = new JTextField("");
            dateField.setPreferredSize(new Dimension(1000, 28));
            p.add(dateField, gbc);

            gbc.gridy++;
            p.add(makeLabel("Description"), gbc);
            gbc.gridy++;
            JTextField desc = new JTextField();
            desc.setPreferredSize(new Dimension(1000, 28));
            p.add(desc, gbc);

            gbc.gridy++;
            p.add(makeLabel("Debit Account"), gbc);
            gbc.gridy++;
            debit = new JComboBox<>(sampleAccounts()); // <- use field, not local variable
            styleLargeComboBox(debit);
            applyComboPopupRenderer(debit);
            debit.setPreferredSize(new Dimension(1000, 28));
            debit.setSelectedIndex(-1);
            p.add(debit, gbc);

            gbc.gridy++;
            p.add(makeLabel("Credit Account"), gbc);
            gbc.gridy++;
            credit = new JComboBox<>(sampleAccounts()); // <- use field, not local variable
            styleLargeComboBox(credit);
            applyComboPopupRenderer(credit);
            credit.setPreferredSize(new Dimension(1000, 28));
            credit.setSelectedIndex(-1);
            p.add(credit, gbc);

            gbc.gridy++;
            p.add(makeLabel("Amount"), gbc);
            gbc.gridy++;
            JFormattedTextField amount = new JFormattedTextField(NumberFormat.getNumberInstance());
            amount.setText("");
            amount.setColumns(20);
            amount.setPreferredSize(new Dimension(1000, 28));
            p.add(amount, gbc);

            gbc.gridy++;
            gbc.anchor = GridBagConstraints.LINE_END;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(18, 40, 18, 40);

            JButton addBtn = new JButton("Add Transaction");
            addBtn.setBackground(HEADER_GREEN);
            addBtn.setForeground(Color.WHITE);
            addBtn.setOpaque(true);
            addBtn.setFocusPainted(false);
            addBtn.setBorderPainted(false);
            addBtn.setPreferredSize(new Dimension(1000, 27));
            addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
            addHoverEffect(addBtn);

            addBtn.addActionListener(e -> {
                String date = dateField.getText().trim();
                String description = desc.getText().trim();
                String debitAcc = (String) debit.getSelectedItem();
                String creditAcc = (String) credit.getSelectedItem();
                double amt;
                try {
                    amt = Double.parseDouble(amount.getText().replace(",", ""));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount. Enter numeric value.");
                    return;
                }

                if (date.isEmpty() || description.isEmpty() || debitAcc == null || creditAcc == null) {
                    JOptionPane.showMessageDialog(this, "Please complete all fields.");
                    return;
                }
                if (amt <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero.");
                    return;
                }
                if (debitAcc.equals(creditAcc)) {
                    int ok = JOptionPane.showConfirmDialog(this, "Debit and credit accounts are the same. Continue?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (ok != JOptionPane.YES_OPTION) return;
                }

                transactionsModel.addRow(new Object[]{date, description, debitAcc, creditAcc, moneyFmt.format(amt)});

                // Clear input fields
                desc.setText("");
                amount.setText("");
                dateField.setText("");
                debit.setSelectedIndex(-1);
                credit.setSelectedIndex(-1);

                // General Journal entries
                journalModel.addRow(new Object[]{date, description, debitAcc, moneyFmt.format(amt), ""});
                journalModel.addRow(new Object[]{date, description, creditAcc, "", moneyFmt.format(amt)});

                // Keep account balances updated
                adjustAccountBalance(debitAcc, amt, true);
                adjustAccountBalance(creditAcc, amt, false);
                
                // FIX: Add account to the ledger list if it's new (now using the corrected method)
                addAccountToLedgerList(debitAcc);
                addAccountToLedgerList(creditAcc);
                
                // FIX: Update the balance in the ledger list item
                updateLedgerAccountListItem(debitAcc);
                updateLedgerAccountListItem(creditAcc);

                // Ledger entries (All transactions in one model for dynamic filtering later)
                double runningAfterDebit = getAccountNumericBalance(debitAcc);
                ledgerModel.addRow(new Object[]{date, description + " (Dr: " + debitAcc + ")", moneyFmt.format(amt), "", formatAccountingMoney(runningAfterDebit)});
                
                double runningAfterCredit = getAccountNumericBalance(creditAcc);
                ledgerModel.addRow(new Object[]{date, description + " (Cr: " + creditAcc + ")", "", moneyFmt.format(amt), formatAccountingMoney(runningAfterCredit)});


                updateBalanceSheetTotals();

                JOptionPane.showMessageDialog(this, "Transaction added successfully!");
            });

            p.add(addBtn, gbc);

            return p;
        }

        private JPanel createTransactionsPanel() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(PANEL_BG);

            JTable table = makeStyledTable(transactionsModel);
            JScrollPane sc = new JScrollPane(table);
            sc.setBorder(new EmptyBorder(12, 12, 12, 12));
            p.add(sc, BorderLayout.CENTER);
            return p;
        }

        private JPanel createAccountsPanel() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(PANEL_BG);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            bottom.setOpaque(false);
            JButton add = new JButton("Add Account");
            add.setBackground(HEADER_GREEN);
            add.setForeground(Color.WHITE);
            add.setOpaque(true);
            add.setFocusPainted(false);
            add.setBorderPainted(false);
            add.setPreferredSize(new Dimension(110, 25));
            add.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
            addHoverEffect(add);

            JButton remove = new JButton("Remove Selected Account");
            remove.setBackground(new Color(220, 70, 70));
            remove.setForeground(Color.WHITE);
            remove.setOpaque(true);
            remove.setBorderPainted(false);
            remove.setPreferredSize(new Dimension(170, 25));
            remove.setFont(new Font("Segoe UI", Font.BOLD, 12));
            bottom.add(add);
            bottom.add(remove);
            styleRemoveButton(remove);
            p.add(bottom, BorderLayout.SOUTH);

            JTable table = makeStyledTable(accountsModel);
            JScrollPane sc = new JScrollPane(table);
            sc.setBorder(new EmptyBorder(8, 8, 8, 8));
            p.add(sc, BorderLayout.CENTER);

            // update debit/credit comboboxes in "NEW TRANSACTION PANEL" whenever we edit account names
            accountsModel.addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) { // Name column
                    int row = e.getFirstRow();
                    String newName = (String) accountsModel.getValueAt(row, 0);

                    // Update debit combo
                    DefaultComboBoxModel<String> debitModel = (DefaultComboBoxModel<String>) debit.getModel();
                    for (int i = 0; i < debitModel.getSize(); i++) {
                        String item = debitModel.getElementAt(i);
                        if (item.equals(debitModel.getElementAt(i))) {
                            debitModel.removeElementAt(i);
                            debitModel.insertElementAt(newName, i);
                            break;
                        }
                    }

                    // Update credit combo
                    DefaultComboBoxModel<String> creditModel = (DefaultComboBoxModel<String>) credit.getModel();
                    for (int i = 0; i < creditModel.getSize(); i++) {
                        String item = creditModel.getElementAt(i);
                        if (item.equals(creditModel.getElementAt(i))) {
                            creditModel.removeElementAt(i);
                            creditModel.insertElementAt(newName, i);
                            break;
                        }
                    }
                }
            });

            add.addActionListener(e -> {
                String newAccount = "New Account " + accountsModel.getRowCount();
                String newType = "ASSET";

                // Add new account to the table
                accountsModel.addRow(new Object[]{newAccount, newType, formatAccountingMoney(0.0)});
                int r = accountsModel.getRowCount() - 1;
                table.setRowSelectionInterval(r, r);

                // Add to ledger list
                addAccountToLedgerList(newAccount);

                // Add to debit and credit drop-downs
                if (((DefaultComboBoxModel<String>) debit.getModel()).getIndexOf(newAccount) == -1) {
                    debit.addItem(newAccount);
                }
                if (((DefaultComboBoxModel<String>) credit.getModel()).getIndexOf(newAccount) == -1) {
                    credit.addItem(newAccount);
                }
            });

            remove.addActionListener(e -> {
                int sel = table.getSelectedRow();
                if (sel >= 0) {
                    String accountToRemove = (String) accountsModel.getValueAt(sel, 0);
                    accountsModel.removeRow(sel);
                    updateBalanceSheetTotals();

                    // Remove from Ledger List
                    for (int i = 0; i < ledgerAccountListModel.getSize(); i++) {
                        if (ledgerAccountListModel.getElementAt(i).startsWith(accountToRemove + " ")) {
                            ledgerAccountListModel.remove(i);
                            break;
                        }
                    }

                    // Remove from debit and credit combo boxes
                    debit.removeItem(accountToRemove);
                    credit.removeItem(accountToRemove);

                } else {
                    JOptionPane.showMessageDialog(this, "Select an account row to remove (UI-only).");
                }
            });

            return p;
        }


        private Dimension Dimension(int i, int j) {
            // FIX: The original code had an unimplemented method 'Dimension(int, int)'.
            // Assuming it was meant to be java.awt.Dimension's constructor.
            return new Dimension(i, j);
        }

        private JPanel createBlankJournalPanel() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(PANEL_BG);
            JTable table = makeStyledTable(journalModel);
            p.add(new JScrollPane(table), BorderLayout.CENTER);
            return p;
        }

        private JPanel createGeneralLedgerPanel() {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(PANEL_BG);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            split.setDividerLocation(300);
            split.setResizeWeight(0.3);
            split.setBorder(null);

            // FIX: Use the class-level list model
            JList<String> list = new JList<>(ledgerAccountListModel);
            list.setSelectionBackground(TABLE_SELECTION);
            list.setSelectionForeground(Color.WHITE);
            list.setFont(new Font("SansSerif", Font.PLAIN, 13));
            list.setBorder(new EmptyBorder(8, 8, 8, 8));
            JScrollPane leftScroll = new JScrollPane(list);
            leftScroll.setBorder(new CompoundBorder(new EmptyBorder(6, 6, 6, 6), new LineBorder(new Color(160, 160, 160))));
            split.setLeftComponent(leftScroll);

            JTable table = makeStyledTable(new DefaultTableModel(new String[]{"Date", "Description", "Debit", "Credit", "Running"}, 0));
            JScrollPane rightScroll = new JScrollPane(table);
            split.setRightComponent(rightScroll);
            
            // FIX: Add selection listener to filter the ledger table
            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                    String selected = list.getSelectedValue();
                    // Extract the account name from the list item string (e.g., "Cash (ASSET) — ₱1,000.00")
                    String accountName = selected.substring(0, selected.indexOf(" ("));
                    filterLedger(accountName, table, rightScroll);
                } else if (!e.getValueIsAdjusting() && list.getSelectedValue() == null) {
                    // Clear the table if no account is selected
                    table.setModel(new DefaultTableModel(new String[]{"Date", "Description", "Debit", "Credit", "Running"}, 0));
                }
            });

            p.add(split, BorderLayout.CENTER);
            return p;
        }
        
        // FIX: Method to dynamically filter the Ledger table based on selected account
        private void filterLedger(String accountName, JTable table, JScrollPane scrollPane) {
            DefaultTableModel filteredModel = new DefaultTableModel(new String[]{"Date", "Description", "Debit", "Credit", "Running"}, 0);
            double currentRunningBalance = 0.0;
            
            // Determine the account type and its normal balance (Debit/Credit)
            String type = "";
            for (int i = 0; i < accountsModel.getRowCount(); i++) {
                if (((String) accountsModel.getValueAt(i, 0)).equals(accountName)) {
                    type = (String) accountsModel.getValueAt(i, 1);
                    break;
                }
            }
            boolean isDebitIncrease = "ASSET".equals(type) || "EXPENSE".equals(type);
            
            // Iterate through the General Journal, which holds all transactions
            for (int i = 0; i < journalModel.getRowCount(); i++) {
                String transactionAccount = (String) journalModel.getValueAt(i, 2); 
                if (transactionAccount.equals(accountName)) {
                    
                    String date = (String) journalModel.getValueAt(i, 0);
                    String description = (String) journalModel.getValueAt(i, 1);
                    String debitStr = (String) journalModel.getValueAt(i, 3);
                    String creditStr = (String) journalModel.getValueAt(i, 4);
                    
                    double debit = parseMoney(debitStr);
                    double credit = parseMoney(creditStr);
                    
                    if (debit > 0) {
                        currentRunningBalance += isDebitIncrease ? debit : -debit;
                    } else if (credit > 0) {
                        currentRunningBalance += isDebitIncrease ? -credit : credit;
                    }
                    
                    // Determine the transaction description for this account
                    String drCr = debit > 0 ? "Dr: " : "Cr: ";
                    
                    filteredModel.addRow(new Object[]{
                        date,
                        description,
                        debitStr,
                        creditStr,
                        formatAccountingMoney(currentRunningBalance)
                    });
                }
            }
            
            table.setModel(filteredModel);
            // Re-apply styling and refresh view
            table.setDefaultRenderer(Object.class, new AlternateRowRenderer());
            scrollPane.revalidate();
        }


        private JPanel createBalanceSheetPanel() {
            JPanel p = new JPanel(new GridLayout(1, 2, 12, 12));
            p.setBackground(PANEL_BG);
            p.setBorder(new EmptyBorder(8, 8, 8, 8));

            JPanel left = new JPanel(new BorderLayout());
            left.setBackground(PANEL_BG);
            JLabel lLabel = new JLabel("Assets");
            lLabel.setBorder(new EmptyBorder(6, 6, 6, 6));
            lLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            left.add(lLabel, BorderLayout.NORTH);
            JTable leftTable = makeStyledTable(balanceLeftModel);
            left.add(new JScrollPane(leftTable), BorderLayout.CENTER);

            JPanel right = new JPanel(new BorderLayout());
            right.setBackground(PANEL_BG);
            JLabel rLabel = new JLabel("Liabilities & Equity");
            rLabel.setBorder(new EmptyBorder(6, 6, 6, 6));
            rLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            right.add(rLabel, BorderLayout.NORTH);
            JTable rightTable = makeStyledTable(balanceRightModel);
            right.add(new JScrollPane(rightTable), BorderLayout.CENTER);

            p.add(left);
            p.add(right);
            return p;
        }

        private JLabel makeLabel(String text) {
            JLabel l = new JLabel(text);
            l.setFont(new Font("Segeo UI", Font.PLAIN, 13));
            l.setForeground(new Color(40, 90, 40));
            l.setBorder(new EmptyBorder(6, 6, 4, 6));
            return l;
        }

        private void styleLargeComboBox(JComboBox<String> combo) {
            combo.setPreferredSize(new Dimension(200, 26));
            combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
            combo.setOpaque(true);
            combo.setBackground(Color.WHITE);
            combo.setBorder(new CompoundBorder(new LineBorder(new Color(180, 180, 180)), new EmptyBorder(3, 6, 3, 6)));
        }

        private void applyComboPopupRenderer(JComboBox<String> combo) {
            combo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                                                            int index, boolean isSelected,
                                                            boolean cellHasFocus) {

                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (isSelected) {
                        c.setBackground(new Color(34, 139, 70));
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(235, 247, 237));
                        c.setForeground(Color.BLACK);
                    }

                    return c;
                }
            });
        }

        private JTable makeStyledTable(TableModel model) {
            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(22);
            table.setIntercellSpacing(new Dimension(1, 1));
            table.setGridColor(new Color(210, 210, 210));
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setDefaultRenderer(new HeaderRenderer(table.getTableHeader().getDefaultRenderer()));
            table.setSelectionBackground(TABLE_SELECTION);
            table.setSelectionForeground(Color.WHITE);
            table.setShowHorizontalLines(false);
            table.setShowVerticalLines(false);
            table.setFont(new Font("SansSerif", Font.PLAIN, 13));

            table.setDefaultRenderer(Object.class, new AlternateRowRenderer());
            return table;
        }

        private String[] sampleAccounts() {
            return new String[]{
                    "Cash", "Petty Cash", "Accounts Receivable", "Notes Receivable",
                    "Supplies", "Inventory", "Prepaid Rent", "Prepaid Insurance",
                    "Equipment", "Accumulated Depreciation", "Accounts Payable", "Notes Payable",
                    "Unearned Revenue", "Owner's Capital", "Owner's Drawings", "Retained Earnings",
                    "Sales Revenue", "Service Revenue", "Salaries Expense", "Rent Expense",
                    "Utilities Expense", "Supplies Expense", "Depreciation Expense", "Insurance Expense"
            };
        }

        private class HeaderRenderer implements TableCellRenderer {
            private final TableCellRenderer delegate;

            HeaderRenderer(TableCellRenderer delegate) {
                this.delegate = delegate;
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(TABLE_HEADER_BG);
                c.setForeground(Color.DARK_GRAY);
                c.setFont(new Font("SansSerif", Font.BOLD, 12));
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(new MatteBorder(0, 0, 1, 0, new Color(160, 160, 160)));
                }
                return c;
            }
        }

    private class AlternateRowRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                    boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(TABLE_SELECTION);
                c.setForeground(Color.WHITE);
            } else {
                if (row % 2 == 0) c.setBackground(TABLE_ROW);
                else c.setBackground(TABLE_ALT_ROW);
                c.setForeground(Color.BLACK);
            }

            setBorder(new EmptyBorder(4, 6, 4, 6));

            if (value != null) {
                String text = value.toString();
                if (text.equals("Total Assets") || text.equals("Total Liabilities & Equity")) {
                    c.setFont(new Font("SansSerif", Font.BOLD, 13));
                } else {
                    c.setFont(new Font("SansSerif", Font.PLAIN, 13));
                }
            }
            
            // Ensure that number columns are right-aligned
            if (table.getModel().getColumnName(column).contains("Amount") || 
                table.getModel().getColumnName(column).contains("Debit") ||
                table.getModel().getColumnName(column).contains("Credit") ||
                table.getModel().getColumnName(column).contains("Balance") ||
                table.getModel().getColumnName(column).contains("Running")) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return c;
        }

    }

        // FIX: Renamed and corrected the logic to use DefaultListModel
        private void addAccountToLedgerList(String accountName) {
            // Check if it already exists
            for (int i = 0; i < ledgerAccountListModel.getSize(); i++) {
                // Check only the account name
                if (ledgerAccountListModel.getElementAt(i).startsWith(accountName + " ")) {
                    return; // Already exists
                }
            }

            // Find the type and balance for the initial add to the list
            for (int i = 0; i < accountsModel.getRowCount(); i++) {
                String acct = (String) accountsModel.getValueAt(i, 0);
                if (acct.equals(accountName)) {
                    String type = (String) accountsModel.getValueAt(i, 1);
                    String bal = (String) accountsModel.getValueAt(i, 2);
                    ledgerAccountListModel.addElement(acct + " (" + type + ") — " + bal);
                    return;
                }
            }
        }
        
        // FIX: New method to update the balance shown in the JList item
        private void updateLedgerAccountListItem(String accountName) {
            for (int i = 0; i < accountsModel.getRowCount(); i++) {
                String acct = (String) accountsModel.getValueAt(i, 0);
                if (acct.equals(accountName)) {
                    String type = (String) accountsModel.getValueAt(i, 1);
                    String bal = (String) accountsModel.getValueAt(i, 2);
                    String newItem = acct + " (" + type + ") — " + bal;
                    
                    // Find and replace the existing item in the list model
                    for (int j = 0; j < ledgerAccountListModel.getSize(); j++) {
                        if (ledgerAccountListModel.getElementAt(j).startsWith(accountName + " ")) {
                            ledgerAccountListModel.set(j, newItem);
                            return;
                        }
                    }
                }
            }
        }


        private class GreenTabbedPaneUI extends BasicTabbedPaneUI {
            private final int arc = 6;

            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets.left = 8;
                tabInsets = new Insets(6, 14, 6, 14);
                selectedTabPadInsets = new Insets(2, 2, 2, 2);
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                            int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected) {
                    g2.setColor(TAB_SELECTED);
                } else {
                    g2.setColor(new Color(245, 250, 245));
                }

                g2.fillRoundRect(x + 2, y + 1, w - 4, h - 2, arc, arc);

                g2.setColor(new Color(120, 120, 120, 120));
                g2.drawRoundRect(x + 2, y + 1, w - 4, h - 2, arc, arc);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                g.setColor(new Color(120, 120, 120));
                g.drawLine(0, 33, tabPane.getWidth(), 33);
                g.setColor(PANEL_BG);
                g.fillRect(0, 34, tabPane.getWidth(), tabPane.getHeight() - 34);
            }
            
            


            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                        int x, int y, int w, int h, boolean isSelected) {
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                            int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                    int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g.setColor(isSelected ? Color.WHITE : new Color(40, 40, 40));
                SwingUtilities2.drawStringUnderlineCharAt(tabPane, g, title, -1,
                        textRect.x, textRect.y + metrics.getAscent());
            }
        }

        private static class SwingUtilities2 {
            public static void drawStringUnderlineCharAt(JComponent c, Graphics g, String s, int underlinedIndex, int x, int y) {
                g.drawString(s, x, y);
                if (underlinedIndex >= 0 && underlinedIndex < s.length()) {
                    FontMetrics fm = g.getFontMetrics();
                    int underlineY = y + 1;
                    int underlineStart = x + fm.stringWidth(s.substring(0, underlinedIndex));
                    int underlineEnd = underlineStart + fm.charWidth(s.charAt(underlinedIndex));
                    g.drawLine(underlineStart, underlineY, underlineEnd, underlineY);
                }
            }
        }


        private String deduceAccountType(String accountName) {
            String name = accountName.toLowerCase();
            // Updated logic for better grouping
            if (name.contains("payable") || name.contains("unearned") || name.contains("notes payable") || name.contains("liab")) return "LIABILITY";
            if (name.contains("capital") || name.contains("drawings") || name.contains("retained") || name.contains("equity") || name.contains("owner")) return "EQUITY";
            if (name.contains("revenue") || name.contains("sales") || name.contains("service")) return "REVENUE";
            if (name.contains("expense") || name.contains("rent") || name.contains("utilities") || name.contains("salar") || name.contains("depreciation") || name.contains("insurance")) return "EXPENSE";
            return "ASSET";
        }

        /**
         * Helper method to format a double as a currency string with parentheses for negative values.
         * e.g., 1000.00 -> ₱1,000.00
         * e.g., -1000.00 -> (₱1,000.00)
         */
        private String formatAccountingMoney(double amount) {
            if (amount < 0) {
                return "(" + moneyFmt.format(Math.abs(amount)) + ")";
            } else {
                return moneyFmt.format(amount);
            }
        }

        private void adjustAccountBalance(String accountName, double amount, boolean isDebit) {
            for (int i = 0; i < accountsModel.getRowCount(); i++) {
                String acct = (String) accountsModel.getValueAt(i, 0);
                if (acct.equals(accountName)) {
                    String type = (String) accountsModel.getValueAt(i, 1);
                    double current = parseMoney((String) accountsModel.getValueAt(i, 2));
                    double updated;
                    
                    // Debit increases ASSET, EXPENSE, DRAWINGS
                    // Credit increases LIABILITY, EQUITY, REVENUE
                    boolean debitIncreases = "ASSET".equals(type) || "EXPENSE".equals(type) || accountName.contains("Drawings"); 
                    
                    if (isDebit) {
                        updated = debitIncreases ? (current + amount) : (current - amount);
                    } else { // is Credit
                        updated = debitIncreases ? (current - amount) : (current + amount);
                    }
                    
                    // Use the new accounting format for the balance
                    accountsModel.setValueAt(formatAccountingMoney(updated), i, 2);
                    return;
                }
            }
            // If account not found, add it (using ASSET as default, which is likely not what's wanted, but keeps logic simple)
            // This scenario should be rare if all accounts are pre-loaded or manually added.
            double val = isDebit ? amount : -amount;
            // Use the new accounting format for the balance
            accountsModel.addRow(new Object[]{accountName, "ASSET", formatAccountingMoney(val)});
        }

        private double parseMoney(String moneyString) {
            if (moneyString == null || moneyString.trim().isEmpty()) return 0.0;
            try {
                String cleaned = moneyString.replace("\u20B1", "").replace(",", "").trim();
                boolean isNegative = moneyString.contains("(") && moneyString.contains(")");
                cleaned = cleaned.replace("(", "").replace(")", "");
                
                double value = Double.parseDouble(cleaned);
                
                return isNegative ? -value : value;
            } catch (Exception ex) {
                return 0.0;
            }
        }

        private double getAccountNumericBalance(String accountName) {
            for (int i = 0; i < accountsModel.getRowCount(); i++) {
                String acct = (String) accountsModel.getValueAt(i, 0);
                if (acct.equals(accountName)) {
                    return parseMoney((String) accountsModel.getValueAt(i, 2));
                }
            }
            return 0.0;
        }

        private void updateBalanceSheetTotals() {

            // Remove the totals before calculation
            if (balanceLeftModel.getRowCount() > 0) balanceLeftModel.removeRow(balanceLeftModel.getRowCount() - 1);
            if (balanceRightModel.getRowCount() > 0) balanceRightModel.removeRow(balanceRightModel.getRowCount() - 1);
            
            // Remove all previous account rows to rebuild
            Vector<Vector> assetRows = new Vector<>();
            Vector<Vector> liabEqRows = new Vector<>();

            double totalAssets = 0.0;
            double totalLiabEq = 0.0;

            for (int i = 0; i < accountsModel.getRowCount(); i++) {
                String account = (String) accountsModel.getValueAt(i, 0);
                String type = (String) accountsModel.getValueAt(i, 1);
                // Get the raw numeric balance
                double amount = getAccountNumericBalance(account); 

                // Use the consistent formatting helper
                String formatted = formatAccountingMoney(amount);
                
                // Temporary row data
                Vector<Object> row = new Vector<>();
                row.add(account);
                row.add(formatted);

                // Only Assets and Liabilities/Equity appear on the Balance Sheet. 
                // Revenue/Expense affect Equity (Retained Earnings) but are not listed directly here.
                switch (type) {
                    case "ASSET":
                        assetRows.add(row);
                        totalAssets += amount;
                        break;

                    case "LIABILITY":
                    case "EQUITY":
                        liabEqRows.add(row);
                        totalLiabEq += amount;
                        break;
                }
            }
            
            // Clear and rebuild models
            balanceLeftModel.setRowCount(0);
            for(Vector row : assetRows) balanceLeftModel.addRow(row);
            
            balanceRightModel.setRowCount(0);
            for(Vector row : liabEqRows) balanceRightModel.addRow(row);

            // Use the consistent formatting helper for totals
            String totalA = formatAccountingMoney(totalAssets);
            String totalLE = formatAccountingMoney(totalLiabEq);

            balanceLeftModel.addRow(new Object[]{"Total Assets", totalA});
            balanceRightModel.addRow(new Object[]{"Total Liabilities & Equity", totalLE});
        }

        private void addHoverEffect(JButton btn) {
        btn.setBackground(BTN_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(15, 90, 40), 2));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BTN_GREEN_HOVER); 
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BTN_GREEN); 
            }
        });
    }
    private void styleRemoveButton(JButton btn) {
        btn.setBackground(BTN_RED);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(150, 20, 20), 2));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BTN_RED_HOVER); 
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BTN_RED); 
            }
        });
    }

        public static void main(String[] args) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            SwingUtilities.invokeLater(() -> {
                AccountingSystem ui = new AccountingSystem();
                ui.setVisible(true);
            });
        }
    }
