
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationManager {

    private static final String URL = "jdbc:mysql://localhost:3306/bloodbank_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Mysql@123";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: MySQL Driver not found!");
            e.printStackTrace();
        }
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        if (conn != null) {
            System.out.println("DEBUG: Database connection successful in DonationManager");
        } else {
            System.out.println("ERROR: Database connection is NULL!");
        }
        return conn;
    }

    // === Add Donor ===
    public boolean addDonor(Donor donor) {
        String sql = "INSERT INTO donors (name, age, blood_group, organ_type, contact, location, weight, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'active')";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, donor.getName());
            ps.setInt(2, donor.getAge());
            ps.setString(3, donor.getBloodGroup());
            ps.setString(4, donor.getOrganType());
            ps.setString(5, donor.getContact());
            ps.setString(6, donor.getLocation());
            if (donor.getWeight() != null) ps.setInt(7, donor.getWeight());
            else ps.setNull(7, Types.INTEGER);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding donor: " + e.getMessage());
            return false;
        }
    }

    // === Fetch all donors ===
    public List<Donor> getDonors() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE status IS NULL OR status = 'active' OR status = ''";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
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
                donors.add(donor);
            }
            System.out.println("DEBUG: Fetched " + donors.size() + " donors from database");

        } catch (SQLException e) {
            System.out.println("ERROR: Error fetching donors: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error in getDonors: " + e.getMessage());
            e.printStackTrace();
        }
        return donors;
    }

    // === Update donor ===
    public boolean updateDonor(Donor donor) {
        String sql = "UPDATE donors SET name=?, age=?, blood_group=?, organ_type=?, contact=?, " +
                "location=?, weight=? WHERE donor_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, donor.getName());
            ps.setInt(2, donor.getAge());
            ps.setString(3, donor.getBloodGroup());
            ps.setString(4, donor.getOrganType());
            ps.setString(5, donor.getContact());
            ps.setString(6, donor.getLocation());
            if (donor.getWeight() != null) ps.setInt(7, donor.getWeight());
            else ps.setNull(7, Types.INTEGER);
            ps.setInt(8, donor.getDonorId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating donor: " + e.getMessage());
            return false;
        }
    }

    // === Mark donor inactive ===
    public boolean markDonorInactive(String name, String contact) {
        String sql = "UPDATE donors SET status='inactive' WHERE name=? AND contact=? AND status='active'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contact);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deactivating donor: " + e.getMessage());
            return false;
        }
    }

    // === Delete donor by ID ===
    public boolean deleteDonor(int donorId) {
        String sql = "DELETE FROM donors WHERE donor_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, donorId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting donor: " + e.getMessage());
            return false;
        }
    }

    // === Search donors by blood or organ ===
    public List<Donor> searchDonors(String keyword) {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE blood_group LIKE ? OR organ_type LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

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
                donors.add(donor);
            }

        } catch (SQLException e) {
            System.out.println("Error searching donors: " + e.getMessage());
        }
        return donors;
    }

    // === Eligibility checks ===
    public boolean isEligible(int age, Integer weight, boolean hadOperation, boolean onMedication, String type) {
        if (age < 18) return false;
        if ("Blood".equalsIgnoreCase(type) && (weight == null || weight < 50)) return false;
        if (hadOperation) return false;
        if (onMedication) return false;
        return true;
    }

    public String getIneligibilityReason(int age, Integer weight, boolean hadOperation, boolean onMedication, String type) {
        if (age < 18) return "Age must be 18 or above.";
        if ("Blood".equalsIgnoreCase(type) && (weight == null || weight < 50)) return "Minimum weight for blood donation is 50 kg.";
        if (hadOperation) return "Donor had a recent surgery.";
        if (onMedication) return "Donor is currently on medication.";
        return "Unspecified reason.";
    }

    // === Fetch all recipients ===
    public List<Recipient> getRecipients() {
        List<Recipient> recipients = new ArrayList<>();
        String sql = "SELECT * FROM recipients";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Recipient recipient = new Recipient(
                        rs.getString("recipient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("required_blood_group"),
                        rs.getString("required_organ"),
                        rs.getString("contact"),
                        rs.getString("location")
                );
                recipients.add(recipient);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching recipients: " + e.getMessage());
        }
        return recipients;
    }

    // === Get total donors count ===
    public int getTotalDonors() {
        // First try with status filter, if that fails, try without
        String sql1 = "SELECT COUNT(*) as total FROM donors WHERE status IS NULL OR status = 'active' OR status = ''";
        String sql2 = "SELECT COUNT(*) as total FROM donors";
        
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.out.println("ERROR: Connection is NULL in getTotalDonors!");
                return 0;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql1)) {
                if (rs.next()) {
                    int count = rs.getInt("total");
                    System.out.println("DEBUG: Total Donors (with status filter) = " + count);
                    return count;
                }
            } catch (SQLException e) {
                // If status column doesn't exist, try without it
                System.out.println("WARNING: Status filter failed, trying without status: " + e.getMessage());
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql2)) {
                    if (rs.next()) {
                        int count = rs.getInt("total");
                        System.out.println("DEBUG: Total Donors (all) = " + count);
                        return count;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error fetching donors count: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error in getTotalDonors: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // === Get total recipients count ===
    public int getTotalRecipients() {
        String sql = "SELECT COUNT(*) as total FROM recipients";
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.out.println("ERROR: Connection is NULL in getTotalRecipients!");
                return 0;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    int count = rs.getInt("total");
                    System.out.println("DEBUG: Total Recipients = " + count);
                    return count;
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error fetching recipients count: " + e.getMessage());
            System.out.println("ERROR: SQL: " + sql);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error in getTotalRecipients: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // === Get total organs count (donors with organ_type) ===
    public int getTotalOrgans() {
        // Try with status filter first, then without
        String sql1 = "SELECT COUNT(*) as total FROM donors WHERE organ_type IS NOT NULL AND organ_type != '' AND organ_type != 'null' AND (status IS NULL OR status = 'active' OR status = '')";
        String sql2 = "SELECT COUNT(*) as total FROM donors WHERE organ_type IS NOT NULL AND organ_type != '' AND organ_type != 'null'";
        
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.out.println("ERROR: Connection is NULL in getTotalOrgans!");
                return 0;
            }
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql1)) {
                if (rs.next()) {
                    int count = rs.getInt("total");
                    System.out.println("DEBUG: Total Organs (with status filter) = " + count);
                    return count;
                }
            } catch (SQLException e) {
                // If status column doesn't exist, try without it
                System.out.println("WARNING: Status filter failed in getTotalOrgans, trying without: " + e.getMessage());
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql2)) {
                    if (rs.next()) {
                        int count = rs.getInt("total");
                        System.out.println("DEBUG: Total Organs (all) = " + count);
                        return count;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error fetching organs count: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error in getTotalOrgans: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // === Get count of recipients without matching donors ===
    public int getUnmatchedRecipientsCount() {
        int unmatchedCount = 0;
        String sql = "SELECT * FROM recipients";
        
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.out.println("ERROR: Connection is NULL in getUnmatchedRecipientsCount!");
                return 0;
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String requiredBloodGroup = rs.getString("required_blood_group");
                String requiredOrgan = rs.getString("required_organ");
                boolean hasMatch = false;
                
                // Check for blood match
                if (requiredBloodGroup != null && !requiredBloodGroup.trim().isEmpty()) {
                    String bloodCheckSql = "SELECT COUNT(*) as count FROM donors WHERE blood_group = ? AND (status IS NULL OR status = 'active')";
                    try (PreparedStatement ps = conn.prepareStatement(bloodCheckSql)) {
                        ps.setString(1, requiredBloodGroup.trim());
                        ResultSet bloodRs = ps.executeQuery();
                        if (bloodRs.next() && bloodRs.getInt("count") > 0) {
                            hasMatch = true;
                        }
                    }
                }
                
                // Check for organ match - if recipient needs organ, check if it exists in inventory
                if (!hasMatch && requiredOrgan != null && !requiredOrgan.trim().isEmpty()) {
                    String organCheckSql = "SELECT COUNT(*) as count FROM donors WHERE organ_type = ? AND (status IS NULL OR status = 'active')";
                    try (PreparedStatement ps = conn.prepareStatement(organCheckSql)) {
                        ps.setString(1, requiredOrgan.trim());
                        ResultSet organRs = ps.executeQuery();
                        if (organRs.next() && organRs.getInt("count") > 0) {
                            // Organ exists in inventory, now check blood compatibility if required
                            if (requiredBloodGroup != null && !requiredBloodGroup.trim().isEmpty()) {
                                String compatibleSql = "SELECT COUNT(*) as count FROM donors WHERE organ_type = ? AND blood_group = ? AND (status IS NULL OR status = 'active')";
                                try (PreparedStatement ps2 = conn.prepareStatement(compatibleSql)) {
                                    ps2.setString(1, requiredOrgan.trim());
                                    ps2.setString(2, requiredBloodGroup.trim());
                                    ResultSet compatRs = ps2.executeQuery();
                                    if (compatRs.next() && compatRs.getInt("count") > 0) {
                                        hasMatch = true;
                                    }
                                }
                            } else {
                                hasMatch = true; // Organ exists and no blood requirement
                            }
                        }
                    }
                }
                
                if (!hasMatch) {
                    unmatchedCount++;
                }
            }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching unmatched recipients count: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error in getUnmatchedRecipientsCount: " + e.getMessage());
            e.printStackTrace();
        }
        
        return unmatchedCount;
    }
}
