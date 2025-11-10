
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainUI extends JFrame {

    private DonationManager manager = new DonationManager();
    private OrganBank organBank = new OrganBank();
    private AdminManager adminManager = new AdminManager();

    public MainUI() {
        setTitle("🩸 LifeConnect - Blood & Organ Donation System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background panel with gradient
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(150, 0, 0),
                        0, h, new Color(255, 150, 150));
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
            }
        };
        background.setLayout(new BorderLayout());
        add(background);

        // Header
        JLabel header = new JLabel("LIFECONNECT | Blood & Organ Donation System", JLabel.CENTER);
        header.setFont(new Font("Segoe UI Black", Font.BOLD, 28));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        background.add(header, BorderLayout.NORTH);

        // Center buttons
        JPanel center = new JPanel(new GridLayout(2, 2, 25, 25));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(60, 200, 60, 200));

        String[] btnText = {
                "🧍 Donor Section", "❤️ Recipient Section",
                "🔐 Admin Login", "🚪 Exit"
        };
        JButton[] buttons = new JButton[btnText.length];
        for (int i = 0; i < btnText.length; i++) {
            buttons[i] = createRoundedButton(btnText[i]);
            center.add(buttons[i]);
        }
        background.add(center, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 LifeConnect | Saving Lives Through Unity ❤️", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        footer.setForeground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        background.add(footer, BorderLayout.SOUTH);

        // Button actions
        buttons[0].addActionListener(e -> openDonorSection());
        buttons[1].addActionListener(e -> openRecipientSection());
        buttons[2].addActionListener(e -> adminLoginUI());
        buttons[3].addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JButton createRoundedButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) g.setColor(new Color(230, 0, 0));
                else if (getModel().isRollover()) g.setColor(new Color(255, 100, 100));
                else g.setColor(new Color(255, 255, 255, 230));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                g.setColor(new Color(180, 0, 0));
                ((Graphics2D) g).setStroke(new BasicStroke(2));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
            }
        };
        b.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        b.setForeground(new Color(120, 0, 0));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // === Donor Section ===
    private void openDonorSection() {
        JFrame donorFrame = new JFrame("🧍 Donor Section");
        donorFrame.setSize(500, 600);
        donorFrame.setLocationRelativeTo(this);
        donorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with simple background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 240, 240),
                        0, getHeight(), new Color(255, 220, 220));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);

        // Header
        JLabel headerLabel = new JLabel("Donor Section", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(new Color(120, 0, 0));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Center cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        cardsPanel.add(createRedCardButton("➕ Register Donor", e -> addDonorUI()));
        cardsPanel.add(createRedCardButton("👁 View Donors", e -> showDonorsUI()));
        cardsPanel.add(createRedCardButton("🔍 Search Donor by Blood", e -> searchDonorUI()));
        cardsPanel.add(createRedCardButton("🗑️ Delete My Donor Record", e -> deleteSelfDonorUI()));
        cardsPanel.add(createRedCardButton("🔙 Back", e -> donorFrame.dispose()));

        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 LifeConnect | Saving Lives Through Unity", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(120, 0, 0));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        donorFrame.setContentPane(mainPanel);
        donorFrame.setVisible(true);
    }

    private JButton createRedCardButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) g.setColor(new Color(230, 0, 0));
                else if (getModel().isRollover()) g.setColor(new Color(255, 100, 100));
                else g.setColor(new Color(255, 255, 255, 230));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                g.setColor(new Color(180, 0, 0));
                ((Graphics2D) g).setStroke(new BasicStroke(2));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
            }
        };
        btn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        btn.setForeground(new Color(120, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(400, 60));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    // === Recipient Section ===
    private void openRecipientSection() {
        JFrame recipientFrame = new JFrame("❤️ Recipient Section");
        recipientFrame.setSize(500, 500);
        recipientFrame.setLocationRelativeTo(this);
        recipientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with simple background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 240, 240),
                        0, getHeight(), new Color(255, 220, 220));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);

        // Header
        JLabel headerLabel = new JLabel("Recipient Section", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(new Color(120, 0, 0));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Center cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        cardsPanel.add(createRedCardButton("👁 View Recipients", e -> viewRecipientsUI()));
        cardsPanel.add(createRedCardButton("🔍 Search Recipient by Blood", e -> searchRecipientUI()));
        cardsPanel.add(createRedCardButton("🔗 Find Recipient Match", e -> findRecipientMatchUI()));
        cardsPanel.add(createRedCardButton("🔙 Back", e -> recipientFrame.dispose()));

        mainPanel.add(cardsPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 LifeConnect | Saving Lives Through Unity", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setForeground(new Color(120, 0, 0));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        recipientFrame.setContentPane(mainPanel);
        recipientFrame.setVisible(true);
    }

    private void addDonorUI() {
        // First ask user to choose between Blood or Organ donation
        String[] options = {"Blood", "Organ"};
        String donationType = (String) JOptionPane.showInputDialog(
            this,
            "Select donation type:",
            "Register Donor",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (donationType == null) {
            return; // User cancelled
        }
        
        // Call appropriate UI based on selection
        if (donationType.equals("Blood")) {
            organBank.addBloodDonorUI();
        } else {
            organBank.addOrganDonorUI();
        }
    }

    private void showDonorsUI() {
        List<Donor> donors = manager.getDonors();
        showDonorsModalUI("All Donors", donors);
    }
    
    private void showDonorsModalUI(String title, List<Donor> donors) {
        JFrame modalFrame = new JFrame(title);
        modalFrame.setSize(900, 500);
        modalFrame.setLocationRelativeTo(this);
        modalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
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
        
        // Center modal panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(150, 0, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        centerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Age", "Blood", "Organ", "Contact", "Location", "Weight"}, 0) {
            @Override
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
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0), 2));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(new Color(200, 0, 0));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        closeBtn.addActionListener(e -> modalFrame.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeBtn);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer
        JLabel footer = new JLabel("© 2025 LifeConnect | Saving Lives Through Unity", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footer.setForeground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);
        
        modalFrame.setContentPane(mainPanel);
        modalFrame.setVisible(true);
    }
    

    private void viewRecipientsUI() {
        List<Donor> recipients = manager.getDonors(); // Assuming all organ donors
        showDonorsModalUI("All Recipients", recipients);
    }

    private void searchDonorUI() {
        String bg = JOptionPane.showInputDialog(this, "Enter Blood Group to Search:");
        if (bg == null || bg.trim().isEmpty()) return;
        List<Donor> donors = manager.getDonors().stream()
                .filter(d -> d.getBloodGroup() != null && d.getBloodGroup().equalsIgnoreCase(bg))
                .toList();
        showTableUI("Search Results - Donors", donors,
                new String[]{"ID", "Name", "Age", "Blood", "Organ", "Contact", "Location", "Weight"});
    }

    private void searchRecipientUI() {
        String bg = JOptionPane.showInputDialog(this, "Enter Blood Group to Search:");
        if (bg == null || bg.trim().isEmpty()) return;
        List<Donor> recipients = manager.getDonors().stream()
                .filter(d -> d.getBloodGroup() != null && d.getBloodGroup().equalsIgnoreCase(bg))
                .toList();
        showTableUI("Search Results - Recipients", recipients,
                new String[]{"ID", "Name", "Age", "Blood", "Organ", "Contact", "Location", "Weight"});
    }

    private void deleteSelfDonorUI() {
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        Object[] msg = {"Enter your Name:", nameField, "Enter your Contact Number:", contactField};
        int opt = JOptionPane.showConfirmDialog(this, msg, "Delete My Donor Record", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String nm = nameField.getText().trim();
            String ct = contactField.getText().trim();
            if (nm.isEmpty() || ct.isEmpty()) {
                warn("Please provide both name and contact.");
                return;
            }
            boolean ok = manager.markDonorInactive(nm, ct);
            if (ok) info("🩸 Your donor record has been marked INACTIVE. Thank you for your contribution.");
            else warn("⚠ No matching active donor found for the provided details.");
        }
    }

    private void showTableUI(String title, List<Donor> data, String[] cols) {
        showDonorsModalUI(title, data);
    }

    private void info(String m) { JOptionPane.showMessageDialog(this, m, "Info", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }

    private void findRecipientMatchUI() {
        // Ask user to choose between Blood or Organ matching
        String[] options = {"Blood", "Organ"};
        String matchType = (String) JOptionPane.showInputDialog(
            this,
            "Select match type:",
            "Find Recipient Match",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (matchType == null) {
            return; // User cancelled
        }
        
        if (matchType.equals("Blood")) {
            // Find recipients matching blood group
            String bloodGroup = JOptionPane.showInputDialog(this, "Enter Blood Group to match:");
            if (bloodGroup == null || bloodGroup.trim().isEmpty()) return;
            
            List<Recipient> recipients = manager.getRecipients();
            List<Recipient> matched = recipients.stream()
                .filter(r -> r.getRequiredBloodGroup() != null && 
                            r.getRequiredBloodGroup().equalsIgnoreCase(bloodGroup.trim()))
                .toList();
            
            if (matched.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No recipients found matching blood group: " + bloodGroup,
                    "No Matches",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                showRecipientsModalUI("Recipients Matching Blood Group: " + bloodGroup, matched);
            }
        } else {
            // Find recipients matching organ
            String organ = JOptionPane.showInputDialog(this, "Enter Organ Type to match:");
            if (organ == null || organ.trim().isEmpty()) return;
            
            List<Recipient> recipients = manager.getRecipients();
            List<Recipient> matched = recipients.stream()
                .filter(r -> r.getRequiredOrgan() != null && 
                            r.getRequiredOrgan().equalsIgnoreCase(organ.trim()))
                .toList();
            
            if (matched.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No recipients found matching organ: " + organ,
                    "No Matches",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                showRecipientsModalUI("Recipients Matching Organ: " + organ, matched);
            }
        }
    }
    
    private void showRecipientsModalUI(String title, List<Recipient> recipients) {
        JFrame modalFrame = new JFrame(title);
        modalFrame.setSize(900, 500);
        modalFrame.setLocationRelativeTo(this);
        modalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
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
        
        // Center modal panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(150, 0, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        centerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Name", "Age", "Required Blood", "Required Organ", "Contact", "Location"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Recipient r : recipients) {
            model.addRow(new Object[]{
                    r.getRecipientId(), r.getName(), r.getAge(),
                    r.getRequiredBloodGroup() != null ? r.getRequiredBloodGroup() : "-",
                    r.getRequiredOrgan() != null ? r.getRequiredOrgan() : "-",
                    r.getContact(), r.getLocation()
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
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 0, 0), 2));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(new Color(200, 0, 0));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        closeBtn.addActionListener(e -> modalFrame.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeBtn);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer
        JLabel footer = new JLabel("© 2025 LifeConnect | Saving Lives Through Unity", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footer.setForeground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);
        
        modalFrame.setContentPane(mainPanel);
        modalFrame.setVisible(true);
    }

    private void adminLoginUI() {
        // Username and password fields
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        
        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField
        };
        
        int option = JOptionPane.showConfirmDialog(
            this,
            fields,
            "Admin Login",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            
            // Check if fields are empty
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "❌ Please enter both username and password!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            System.out.println("DEBUG: Attempting login with username: '" + username + "'");
            
            // Try database authentication first
            boolean loginSuccess = false;
            try {
                Admin admin = new Admin("", username, password);
                loginSuccess = admin.login();
                System.out.println("DEBUG: Database login result: " + loginSuccess);
            } catch (Exception e) {
                System.out.println("DEBUG: Database login exception: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Fallback to hardcoded credentials if database fails or no admins table
            if (!loginSuccess) {
                if (username.equals("Admin") && password.equals("Admin123")) {
                    loginSuccess = true;
                    System.out.println("DEBUG: Using hardcoded credentials - login successful");
                }
            }
            
            if (loginSuccess) {
                try {
                    System.out.println("DEBUG: Creating AdminUI window...");
                    // Create and show AdminUI window
                    AdminUI adminUI = new AdminUI();
                    adminUI.setVisible(true);
                    adminUI.toFront();
                    adminUI.requestFocus();
                    System.out.println("DEBUG: AdminUI window created and set visible");
                } catch (Exception e) {
                    System.out.println("DEBUG: Error creating AdminUI: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                        this,
                        "❌ Error opening admin panel: " + e.getMessage() + "\n\nCheck console for details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "❌ Invalid username or password!\n\nTry:\nUsername: Admin\nPassword: Admin123",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}

