import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.*;
import java.awt.*;
import java.text.NumberFormat;

public class AccountingSystem extends JFrame {

    private static final Color HEADER_GREEN = new Color(21, 120, 55);           
    private static final Color TAB_SELECTED = new Color(34, 139, 70);          
    private static final Color TAB_UNSELECTED = new Color(225, 238, 230);      
    private static final Color PANEL_BG = new Color(235, 247, 237);            
    private static final Color TABLE_ALT_ROW = new Color(244, 252, 244);
    private static final Color TABLE_ROW = new Color(255, 255, 255);
    private static final Color TABLE_HEADER_BG = new Color(194, 225, 200);
    private static final Color TABLE_SELECTION = new Color(34, 139, 70);       

    public AccountingSystem() {
        
        super("Accounting System");

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
        tabs.setFont(new Font("Segio UI", Font.PLAIN, 13));

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
        heading.setFont(new Font("SansSerif", Font.BOLD, 23));
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
        JTextField dateField = new JTextField("2025-11-26");
        dateField.setPreferredSize(new Dimension(1000, 30));
        p.add(dateField, gbc);

        gbc.gridy++;
        p.add(makeLabel("Description"), gbc);
        gbc.gridy++;
        JTextField desc = new JTextField();
        desc.setPreferredSize(new Dimension(1000, 30));
        p.add(desc, gbc);

        gbc.gridy++;
        p.add(makeLabel("Debit Account"), gbc);
        gbc.gridy++;
        JComboBox<String> debit = new JComboBox<>(sampleAccounts());
        styleLargeComboBox(debit);
        applyComboPopupRenderer(debit); 
        debit.setPreferredSize(new Dimension(1000, 30));
        p.add(debit, gbc);

        gbc.gridy++;
        p.add(makeLabel("Credit Account"), gbc);
        gbc.gridy++;
        JComboBox<String> credit = new JComboBox<>(sampleAccounts());
        styleLargeComboBox(credit);
        applyComboPopupRenderer(credit); 
        credit.setPreferredSize(new Dimension(1000, 30));
        p.add(credit, gbc);

        gbc.gridy++;
        p.add(makeLabel("Amount"), gbc);
        gbc.gridy++;
        JFormattedTextField amount = new JFormattedTextField(NumberFormat.getNumberInstance());
        amount.setText("0");
        amount.setColumns(20);
        amount.setPreferredSize(new Dimension(1000, 30));
        p.add(amount, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(18, 40, 18, 40);
        
        JButton addBtn = new JButton("Add Transaction");
        addBtn.setBackground(new Color(0, 128, 0));
        amount.setPreferredSize(new Dimension(1000, 30));
        addBtn.setForeground(Color.BLACK);
        addBtn.setFocusPainted(false);
        p.add(addBtn, gbc);

        return p;
    }

    private JPanel createTransactionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_BG);

        String[] cols = {"Date", "Description", "Debit", "Credit", "Amount"};
        JTable table = makeStyledTable(new DefaultTableModel(cols, 0));
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
        add.setBackground(new Color(0, 128, 0));
        add.setForeground(Color.BLACK);
        JButton remove = new JButton("Remove Selected Account");
        remove.setBackground(new Color(220, 70, 70));
        remove.setForeground(Color.BLACK);
        bottom.add(add);
        bottom.add(remove);
        p.add(bottom, BorderLayout.SOUTH);

        String[] cols = {"Account", "Type", "Balance"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (String s : sampleAccounts()) {
            model.addRow(new Object[]{s, "ASSET", "\u20B1" + "0.00"});
        }
        JTable table = makeStyledTable(model);
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(new EmptyBorder(8, 8, 8, 8));
        p.add(sc, BorderLayout.CENTER);

        add.addActionListener(e -> {
            model.addRow(new Object[]{"New Account", "ASSET", "\u20B1" + "0.00"});
            int r = model.getRowCount() - 1;
            table.setRowSelectionInterval(r, r);
        });
        remove.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                model.removeRow(sel);
            } else {
                JOptionPane.showMessageDialog(this, "Select an account row to remove (UI-only).");
            }
        });

        return p;
    }

    private JPanel createBlankJournalPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_BG);
        String[] cols = {"Date", "Description", "Account", "Debit", "Credit"};
        JTable table = makeStyledTable(new DefaultTableModel(cols, 0));
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

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String s : sampleAccounts()) {
            listModel.addElement(s + " (ASSET) — \u20B1" + "0.00");
        }
        JList<String> list = new JList<>(listModel);
        list.setSelectionBackground(TABLE_SELECTION);
        list.setSelectionForeground(Color.WHITE);
        list.setFont(new Font("SansSerif", Font.PLAIN, 13));
        list.setBorder(new EmptyBorder(8,8,8,8));
        JScrollPane leftScroll = new JScrollPane(list);
        leftScroll.setBorder(new CompoundBorder(new EmptyBorder(6,6,6,6), new LineBorder(new Color(160,160,160))));
        split.setLeftComponent(leftScroll);

        String[] cols = {"Date", "Description", "Debit", "Credit", "Running"};
        JTable table = makeStyledTable(new DefaultTableModel(cols, 0));
        split.setRightComponent(new JScrollPane(table));

        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel createBalanceSheetPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 12));
        p.setBackground(PANEL_BG);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(PANEL_BG);
        JLabel lLabel = new JLabel("Assets");
        lLabel.setBorder(new EmptyBorder(6,6,6,6));
        lLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        left.add(lLabel, BorderLayout.NORTH);
        String[] cols = {"Asset", "Amount"};
        DefaultTableModel leftModel = new DefaultTableModel(cols, 0);
        leftModel.addRow(new Object[]{"Total Assets", "\u20B1" + "0.00"});
        JTable leftTable = makeStyledTable(leftModel);
        left.add(new JScrollPane(leftTable), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(PANEL_BG);
        JLabel rLabel = new JLabel("Liabilities & Equity");
        rLabel.setBorder(new EmptyBorder(6,6,6,6));
        rLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        right.add(rLabel, BorderLayout.NORTH);
        DefaultTableModel rightModel = new DefaultTableModel(cols, 0);
        rightModel.addRow(new Object[]{"Total Liabilities & Equity", "\u20B1" + "0.00"});
        JTable rightTable = makeStyledTable(rightModel);
        right.add(new JScrollPane(rightTable), BorderLayout.CENTER);

        p.add(left);
        p.add(right);
        return p;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
            return c;
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            AccountingSystem ui = new AccountingSystem();
            ui.setVisible(true);
        });
    }
}

