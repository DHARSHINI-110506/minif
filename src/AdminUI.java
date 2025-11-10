
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.LinkedHashMap;

public class AdminUI extends JFrame {

    private DonationManager manager = new DonationManager();

    private BloodBank bloodBank = new BloodBank();

    public AdminUI() {
        setTitle("Admin Panel - Blood & Organ Donation System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        System.out.println("DEBUG UI: AdminUI constructor called, about to initialize UI");
        try {
            initUI();
            System.out.println("DEBUG UI: AdminUI initialization complete");
        } catch (Exception e) {
            System.out.println("DEBUG UI: Error during AdminUI initialization: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error initializing admin panel: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        // Main panel with red gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(200, 0, 0),
                        0, getHeight(), new Color(255, 100, 100));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center content panel with summary cards - professional layout
        JPanel statsPanel = new JPanel(new GridLayout(3, 3, 25, 25));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Create summary cards - fetch directly from DB using COUNT queries
        System.out.println("\n========================================");
        System.out.println("=== FETCHING DASHBOARD STATS ===");
        System.out.println("========================================\n");
        
        int totalDonors = 0;
        int totalRecipients = 0;
        int bloodUnits = 0;
        int totalOrgans = 0;
        int lowStock = 0;
        int unmatchedCount = 0;
        boolean dbConnectionIssue = false;
        
        try {
            totalDonors = manager.getTotalDonors();
            totalRecipients = manager.getTotalRecipients();
            bloodUnits = bloodBank.totalUnits();
            totalOrgans = manager.getTotalOrgans();
            lowStock = bloodBank.lowStockCount(5);
            unmatchedCount = manager.getUnmatchedRecipientsCount();
        } catch (Exception e) {
            System.out.println("ERROR: Database connection issue while fetching stats: " + e.getMessage());
            e.printStackTrace();
            dbConnectionIssue = true;
        }
        
        System.out.println("\n========================================");
        System.out.println("FINAL DASHBOARD STATS:");
        System.out.println("  Donors: " + totalDonors);
        System.out.println("  Recipients: " + totalRecipients);
        System.out.println("  Blood Units: " + bloodUnits);
        System.out.println("  Organs: " + totalOrgans);
        System.out.println("========================================\n");
        
        // Show message if all are zero (likely no data or connection issue)
        if (dbConnectionIssue || (totalDonors == 0 && totalRecipients == 0 && bloodUnits == 0 && totalOrgans == 0)) {
            System.out.println("⚠️ WARNING: Database connection issue or all counts are ZERO!");
            System.out.println("   Please check:");
            System.out.println("   1. Database connection is working");
            System.out.println("   2. MySQL server is running");
            System.out.println("   3. Database credentials in DBConnection.java are correct");
            System.out.println("   4. Tables have data");
            System.out.println("   5. Check console for error messages above");
        }
        
        // Convert to strings explicitly
        String donorsStr = String.valueOf(totalDonors);
        String recipientsStr = String.valueOf(totalRecipients);
        String bloodStr = String.valueOf(bloodUnits);
        String organsStr = String.valueOf(totalOrgans);
        
        // Show warning dialog if database connection failed
        if (dbConnectionIssue) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                    "⚠️ Database Connection Warning\n\n" +
                    "Unable to connect to the database. The admin panel will open with default values (0).\n\n" +
                    "Please check:\n" +
                    "• MySQL server is running\n" +
                    "• Database credentials in DBConnection.java\n" +
                    "• Database 'bloodbank_db' exists\n\n" +
                    "You can still use the admin panel, but data operations may fail.",
                    "Database Connection Issue",
                    JOptionPane.WARNING_MESSAGE);
            });
        }
        
        System.out.println("DEBUG UI: About to create cards with values:");
        System.out.println("  Donors: " + donorsStr);
        System.out.println("  Recipients: " + recipientsStr);
        System.out.println("  Blood: " + bloodStr);
        System.out.println("  Organs: " + organsStr);
        
        // Create cards with explicit string values
        JPanel card1 = createSummaryCard("Total Donors", donorsStr, "🧍");
        JPanel card2 = createSummaryCard("Total Recipients", recipientsStr, "❤️");
        JPanel card3 = createSummaryCard("Blood Units Available", bloodStr, "🩸");
        JPanel card4 = createSummaryCard("Total Organs in Inventory", organsStr, "🫀");
        
        statsPanel.add(card1);
        statsPanel.add(card2);
        statsPanel.add(card3);
        statsPanel.add(card4);
        
        System.out.println("DEBUG UI: All cards created and added to statsPanel");
        
        // Low stock card
        String lowStockText = lowStock > 0 ? String.valueOf(lowStock) + " types" : "None";
        statsPanel.add(createSummaryCard("Low Stock Blood Types", lowStockText, "⚠️"));
        
        // Unmatched recipients card
        String unmatchedText = unmatchedCount > 0 ? String.valueOf(unmatchedCount) + " recipients" : "None";
        statsPanel.add(createSummaryCardWithAlert("Unmatched Recipients", unmatchedText, "🔴", unmatchedCount > 0));
        
        // Action buttons card
        JPanel actionCard = createActionCard();
        statsPanel.add(actionCard);
        
        // Empty space for alignment
        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        statsPanel.add(emptyPanel);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 LifeConnect | Saving Lives Through Unity", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footer.setForeground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        
        // Force layout and repaint - do this on EDT
        SwingUtilities.invokeLater(() -> {
            validate();
            repaint();
            System.out.println("DEBUG UI: Window validated and repainted on EDT");
        });
        
        // Also validate immediately
        pack();
        validate();
        
        System.out.println("DEBUG UI: Main panel set as content pane");
        System.out.println("DEBUG UI: Window size: " + getSize());
        System.out.println("DEBUG UI: Window should now display with correct values");
    }

    private JPanel createSummaryCard(String title, String value, String icon) {
        return createSummaryCardWithAlert(title, value, icon, false);
    }
    
    private JPanel createSummaryCardWithAlert(String title, String value, String icon, boolean isAlert) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(isAlert ? new Color(255, 240, 240) : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isAlert ? new Color(220, 0, 0) : new Color(200, 0, 0), 3, true),
                BorderFactory.createEmptyBorder(30, 25, 30, 25)
        ));

        // Removed icon section to prevent box symbols from hiding data
        
        // Value with better styling - ensure value is not null or empty
        String displayValue = (value == null || value.isEmpty()) ? "0" : value;
        System.out.println("DEBUG UI: Creating card - Title: '" + title + "', Value: '" + displayValue + "'");
        
        JLabel valueLabel = new JLabel(displayValue, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(isAlert ? new Color(220, 0, 0) : new Color(200, 0, 0));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        valueLabel.setOpaque(false);
        
        // Verify the label text is set correctly
        System.out.println("DEBUG UI: JLabel text set to: '" + valueLabel.getText() + "'");
        
        card.add(valueLabel, BorderLayout.CENTER);

        // Title with better styling
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>" + title + "</div></html>", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(80, 80, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        card.add(titleLabel, BorderLayout.SOUTH);

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(isAlert ? new Color(255, 230, 230) : new Color(255, 245, 245));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(isAlert ? new Color(255, 240, 240) : Color.WHITE);
            }
        });

        return card;
    }

    private JPanel createActionCard() {
        JPanel card = new JPanel(new GridLayout(4, 2, 12, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 0, 0), 3, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JButton viewBtn = createRedCardButton("View Donors");
        JButton viewOrganBtn = createRedCardButton("View Organ Bank");
        JButton deleteBtn = createRedCardButton("Delete Donor");
        JButton addBloodBtn = createRedCardButton("Add Blood Units");
        JButton viewBloodBtn = createRedCardButton("View Blood Units");
        JButton addOrganBtn = createRedCardButton("Add Organ");
        JButton refreshBtn = createRedCardButton("Refresh");
        JButton logoutBtn = createRedCardButton("Logout");

        card.add(viewBtn);
        card.add(viewOrganBtn);
        card.add(deleteBtn);
        card.add(addBloodBtn);
        card.add(viewBloodBtn);
        card.add(addOrganBtn);
        card.add(refreshBtn);
        card.add(logoutBtn);

        // Button actions
        viewBtn.addActionListener(e -> viewAllDonors());
        viewOrganBtn.addActionListener(e -> viewOrganBank());
        deleteBtn.addActionListener(e -> deleteDonor());
        addBloodBtn.addActionListener(e -> addBloodUnits());
        viewBloodBtn.addActionListener(e -> viewBloodUnits());
        addOrganBtn.addActionListener(e -> addOrgan());
        refreshBtn.addActionListener(e -> refreshDashboard());
        logoutBtn.addActionListener(e -> {
            dispose();
            new MainUI().setVisible(true);
        });

        return card;
    }

    private void viewAllDonors() {
        List<Donor> donors = manager.getDonors();
        if (donors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No donors found.");
            return;
        }
        
        JFrame frame = new JFrame("All Donors");
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(this);
        
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Age", "Blood Group", "Organ Type", "Contact", "Location", "Weight"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Donor d : donors) {
            model.addRow(new Object[]{
                    d.getDonorId(), d.getName(), d.getAge(),
                    d.getBloodGroup() != null ? d.getBloodGroup() : "-",
                    d.getOrganType() != null ? d.getOrganType() : "-",
                    d.getContact(), d.getLocation(),
                    d.getWeight() != null ? d.getWeight() : "-"
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(255, 200, 200));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setForeground(new Color(150, 0, 0));
        table.setGridColor(new Color(230, 150, 150));
        table.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private void viewBloodUnits() {
        // Check database connection first
        try {
            Connection testConn = DBConnection.getConnection();
            if (testConn == null) {
                JOptionPane.showMessageDialog(this,
                    "❌ Database Connection Error\n\n" +
                    "Cannot connect to the database. Please check:\n" +
                    "• MySQL server is running\n" +
                    "• Database credentials in DBConnection.java are correct\n" +
                    "• Database 'bloodbank_db' exists",
                    "Database Connection Required",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            testConn.close();
        } catch (Exception dbEx) {
            JOptionPane.showMessageDialog(this,
                "❌ Database Connection Error\n\n" +
                "Cannot connect to the database. Please check:\n" +
                "• MySQL server is running\n" +
                "• Database credentials in DBConnection.java are correct\n" +
                "• Database 'bloodbank_db' exists",
                "Database Connection Required",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Fetch blood inventory from database
        Map<String, Integer> bloodInventory = new LinkedHashMap<>();
        String sql = "SELECT blood_type, units FROM blood_inventory ORDER BY blood_type";
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "❌ Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    String bloodType = rs.getString("blood_type");
                    int units = rs.getInt("units");
                    bloodInventory.put(bloodType, units);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error fetching blood inventory: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "❌ Error fetching blood inventory: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Display results
        if (bloodInventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No blood units found in inventory.\n\nUse 'Add Blood Units' to add blood to the inventory.", 
                "Empty Inventory", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create and display table
        JFrame frame = new JFrame("Blood Inventory");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(this);
        
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Blood Type", "Units Available"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        int totalUnits = 0;
        for (Map.Entry<String, Integer> entry : bloodInventory.entrySet()) {
            String bloodType = entry.getKey();
            int units = entry.getValue();
            totalUnits += units;
            model.addRow(new Object[]{bloodType, units});
        }
        
        // Add total row
        model.addRow(new Object[]{"TOTAL", totalUnits});
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        
        // Style the header
        table.getTableHeader().setBackground(new Color(200, 0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setForeground(Color.WHITE);
        
        // Style the table
        table.setGridColor(new Color(230, 150, 150));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 200, 200));
        
        // Highlight low stock items (less than 5 units)
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Don't highlight the total row
                if (row < table.getRowCount() - 1 && column == 1) {
                    try {
                        int units = Integer.parseInt(value.toString());
                        if (units < 5) {
                            c.setBackground(new Color(255, 240, 240)); // Light red for low stock
                            c.setForeground(new Color(200, 0, 0));
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } else if (row == table.getRowCount() - 1) {
                    // Highlight total row
                    c.setBackground(new Color(200, 0, 0));
                    c.setForeground(Color.WHITE);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 15));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                
                if (isSelected) {
                    c.setBackground(new Color(255, 200, 200));
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
            "🩸 Blood Inventory - Total: " + totalUnits + " units",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(200, 0, 0)
        ));
        
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private void viewOrganBank() {
        // Check database connection first
        try {
            Connection testConn = DBConnection.getConnection();
            if (testConn == null) {
                JOptionPane.showMessageDialog(this,
                    "❌ Database Connection Error\n\n" +
                    "Cannot connect to the database. Please check:\n" +
                    "• MySQL server is running\n" +
                    "• Database credentials in DBConnection.java are correct\n" +
                    "• Database 'bloodbank_db' exists",
                    "Database Connection Required",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            testConn.close();
        } catch (Exception dbEx) {
            JOptionPane.showMessageDialog(this,
                "❌ Database Connection Error\n\n" +
                "Cannot connect to the database. Please check:\n" +
                "• MySQL server is running\n" +
                "• Database credentials in DBConnection.java are correct\n" +
                "• Database 'bloodbank_db' exists",
                "Database Connection Required",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Fetch organ inventory from database
        List<Donor> organDonors = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE organ_type IS NOT NULL AND organ_type != '' AND organ_type != 'null' " +
                     "AND (status IS NULL OR status = 'active' OR status = '') ORDER BY organ_type, blood_group";
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "❌ Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    Donor donor = new Donor(
                        rs.getInt("donor_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("blood_group"),
                        rs.getString("organ_type"),
                        rs.getString("contact"),
                        rs.getString("location"),
                        rs.getObject("weight") != null ? rs.getInt("weight") : null
                    );
                    organDonors.add(donor);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error fetching organ inventory: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "❌ Error fetching organ inventory: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Display results
        if (organDonors.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No organs found in inventory.\n\nUse 'Add Organ' to add organs to the inventory.", 
                "Empty Organ Bank", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create and display table
        JFrame frame = new JFrame("Organ Bank Inventory");
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(this);
        
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Organ Type", "Blood Group", "Donor Name", "Contact", "Location", "Age", "Weight"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Count organs by type
        Map<String, Integer> organCounts = new LinkedHashMap<>();
        for (Donor donor : organDonors) {
            String organType = donor.getOrganType();
            organCounts.put(organType, organCounts.getOrDefault(organType, 0) + 1);
            
            model.addRow(new Object[]{
                organType != null ? organType : "-",
                donor.getBloodGroup() != null ? donor.getBloodGroup() : "-",
                donor.getName(),
                donor.getContact(),
                donor.getLocation(),
                donor.getAge(),
                donor.getWeight() != null ? donor.getWeight() + " kg" : "-"
            });
        }
        
        // Add summary row
        StringBuilder summary = new StringBuilder("Total: ");
        for (Map.Entry<String, Integer> entry : organCounts.entrySet()) {
            summary.append(entry.getKey()).append(" (").append(entry.getValue()).append("), ");
        }
        String summaryText = summary.toString();
        if (summaryText.endsWith(", ")) {
            summaryText = summaryText.substring(0, summaryText.length() - 2);
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        
        // Style the header
        table.getTableHeader().setBackground(new Color(200, 0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setForeground(Color.WHITE);
        
        // Style the table
        table.setGridColor(new Color(230, 150, 150));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 200, 200));
        
        // Auto-resize columns
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 0, 0), 2),
            "🫀 Organ Bank Inventory - " + summaryText + " | Total Organs: " + organDonors.size(),
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(200, 0, 0)
        ));
        
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private void refreshDashboard() {
        System.out.println("=== REFRESHING DASHBOARD ===");
        // Close current window
        dispose();
        // Create new window with fresh data
        SwingUtilities.invokeLater(() -> {
            AdminUI newAdminUI = new AdminUI();
            newAdminUI.setVisible(true);
            System.out.println("DEBUG: New AdminUI window created after refresh");
        });
    }

    private JButton createRedCardButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                if (getModel().isPressed()) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(180, 0, 0),
                            0, getHeight(), new Color(220, 50, 50));
                    g2.setPaint(gp);
                } else if (getModel().isRollover()) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(230, 0, 0),
                            0, getHeight(), new Color(255, 80, 80));
                    g2.setPaint(gp);
            } else {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(200, 0, 0),
                            0, getHeight(), new Color(240, 60, 60));
                    g2.setPaint(gp);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                g.setColor(new Color(150, 0, 0));
                ((Graphics2D) g).setStroke(new BasicStroke(2));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }



    private void updateDonor() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Donor ID to update:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        
        try {
            int id = Integer.parseInt(idStr.trim());
            List<Donor> donors = manager.getDonors();
            Donor donorToUpdate = null;
            for (Donor d : donors) {
                if (d.getDonorId() == id) {
                    donorToUpdate = d;
                    break;
                }
            }
            
            if (donorToUpdate == null) {
                JOptionPane.showMessageDialog(this, "❌ Donor with ID " + id + " not found.");
            return;
        }

            String name = JOptionPane.showInputDialog(this, "Enter Name:", donorToUpdate.getName());
            if (name == null || name.trim().isEmpty()) return;
            
            int age = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Age:", String.valueOf(donorToUpdate.getAge())));
            String bloodGroup = JOptionPane.showInputDialog(this, "Enter Blood Group:", donorToUpdate.getBloodGroup());
            if (bloodGroup == null || bloodGroup.trim().isEmpty()) return;
            
            String organType = JOptionPane.showInputDialog(this, "Enter Organ Type:", donorToUpdate.getOrganType());
            if (organType == null || organType.trim().isEmpty()) return;
            
            String contact = JOptionPane.showInputDialog(this, "Enter Contact:", donorToUpdate.getContact());
            if (contact == null || contact.trim().isEmpty()) return;
            
            String location = JOptionPane.showInputDialog(this, "Enter Location:", donorToUpdate.getLocation());
            if (location == null || location.trim().isEmpty()) return;
            
            String weightStr = JOptionPane.showInputDialog(this, "Enter Weight (kg) - Required:", 
                    donorToUpdate.getWeight() != null ? String.valueOf(donorToUpdate.getWeight()) : "");
            if (weightStr == null || weightStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Weight is required. Update cancelled.");
                return;
            }
            
            int weight = Integer.parseInt(weightStr.trim());
            if (weight < 50) {
                JOptionPane.showMessageDialog(this, "❌ Weight must be at least 50 kg. Update cancelled.", "Invalid Weight", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Donor donor = new Donor(id, name.trim(), age, bloodGroup.trim(), organType.trim(), contact.trim(), location.trim(), weight);
            boolean success = manager.updateDonor(donor);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Donor updated successfully.");
                refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to update donor.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Invalid numeric input. Please enter valid numbers for ID, age and weight.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Invalid input: " + ex.getMessage());
        }
    }

    private void deleteDonor() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Donor ID to delete:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        
        try {
            int id = Integer.parseInt(idStr.trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete donor ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = manager.deleteDonor(id);
            if (success) {
                    JOptionPane.showMessageDialog(this, "✅ Donor deleted successfully.");
                    refreshDashboard();
            } else {
                    JOptionPane.showMessageDialog(this, "❌ Failed to delete donor.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Invalid ID format.");
        }
    }

    private void addBloodUnits() {
        JTextField bloodTypeField = new JTextField();
        JTextField unitsField = new JTextField();
        
        Object[] message = {
            "Blood Type (e.g., A+, B-, O+):", bloodTypeField,
            "Number of Units:", unitsField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Blood Units", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;
        
        try {
            String bloodType = bloodTypeField.getText().trim().toUpperCase();
            String unitsStr = unitsField.getText().trim();
            
            if (bloodType.isEmpty() || unitsStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int units = Integer.parseInt(unitsStr);
            if (units <= 0) {
                JOptionPane.showMessageDialog(this, "❌ Number of units must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate blood type
            String[] validTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            boolean isValid = false;
            for (String type : validTypes) {
                if (type.equals(bloodType)) {
                    isValid = true;
                    break;
                }
            }
            
            if (!isValid) {
                JOptionPane.showMessageDialog(this, "❌ Invalid blood type. Valid types: A+, A-, B+, B-, AB+, AB-, O+, O-", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check database connection before attempting to add
            try {
                Connection testConn = DBConnection.getConnection();
                if (testConn == null) {
                    throw new SQLException("Database connection is null");
                }
                testConn.close();
            } catch (Exception dbEx) {
                JOptionPane.showMessageDialog(this,
                    "❌ Database Connection Error\n\n" +
                    "Cannot connect to the database. Please check:\n" +
                    "• MySQL server is running\n" +
                    "• Database credentials in DBConnection.java are correct\n" +
                    "• Database 'bloodbank_db' exists\n\n" +
                    "Cannot add blood units without database connection.",
                    "Database Connection Required",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            bloodBank.addBlood(bloodType, units);
            JOptionPane.showMessageDialog(this, "✅ Successfully added " + units + " units of " + bloodType + " to blood bank.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshDashboard();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Invalid number format for units.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            // Handle database connection errors from addBlood
            String errorMsg = ex.getMessage();
            if (errorMsg != null && errorMsg.contains("Database connection")) {
                JOptionPane.showMessageDialog(this,
                    "❌ Database Connection Error\n\n" +
                    "Cannot connect to the database. Please check:\n" +
                    "• MySQL server is running\n" +
                    "• Database credentials in DBConnection.java are correct\n" +
                    "• Database 'bloodbank_db' exists",
                    "Database Connection Required",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error adding blood units: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error adding blood units: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addOrgan() {
        JTextField organTypeField = new JTextField();
        JTextField bloodGroupField = new JTextField();
        JTextField donorNameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField locationField = new JTextField();
        
        Object[] message = {
            "Organ Type (e.g., Kidney, Heart, Liver):", organTypeField,
            "Blood Group (e.g., A+, B-, O+):", bloodGroupField,
            "Donor Name:", donorNameField,
            "Contact Number:", contactField,
            "Location:", locationField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Organ to Blood Bank", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;
        
        try {
            String organType = organTypeField.getText().trim();
            String bloodGroup = bloodGroupField.getText().trim().toUpperCase();
            String donorName = donorNameField.getText().trim();
            String contact = contactField.getText().trim();
            String location = locationField.getText().trim();
            
            if (organType.isEmpty() || bloodGroup.isEmpty() || donorName.isEmpty() || contact.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate blood type
            String[] validTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
            boolean isValid = false;
            for (String type : validTypes) {
                if (type.equals(bloodGroup)) {
                    isValid = true;
                    break;
                }
            }
            
            if (!isValid) {
                JOptionPane.showMessageDialog(this, "❌ Invalid blood type. Valid types: A+, A-, B+, B-, AB+, AB-, O+, O-", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check database connection before attempting to add
            try {
                Connection testConn = DBConnection.getConnection();
                if (testConn == null) {
                    throw new SQLException("Database connection is null");
                }
                testConn.close();
            } catch (Exception dbEx) {
                JOptionPane.showMessageDialog(this,
                    "❌ Database Connection Error\n\n" +
                    "Cannot connect to the database. Please check:\n" +
                    "• MySQL server is running\n" +
                    "• Database credentials in DBConnection.java are correct\n" +
                    "• Database 'bloodbank_db' exists\n\n" +
                    "Cannot add organ without database connection.",
                    "Database Connection Required",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create a donor entry with organ information
            // Using minimum age (20) and weight (50) for organ inventory entry
            Donor organDonor = new Donor(donorName, 20, bloodGroup, organType, contact, location, 50);
            boolean success = manager.addDonor(organDonor);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Successfully added " + organType + " (Blood Group: " + bloodGroup + ") to blood bank inventory.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Failed to add organ to blood bank.\n\n" +
                    "This could be due to:\n" +
                    "• Database connection issue\n" +
                    "• Invalid data format\n" +
                    "• Database constraint violation\n\n" +
                    "Please check the console for details.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            String errorMsg = ex.getMessage();
            if (errorMsg != null && errorMsg.contains("Database connection")) {
                JOptionPane.showMessageDialog(this,
                    "❌ Database Connection Error\n\n" +
                    "Cannot connect to the database. Please check:\n" +
                    "• MySQL server is running\n" +
                    "• Database credentials in DBConnection.java are correct\n" +
                    "• Database 'bloodbank_db' exists",
                    "Database Connection Required",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error adding organ: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminUI().setVisible(true);
        });
    }
}