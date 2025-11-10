import java.sql.*;

public class TestDashboard {
    public static void main(String[] args) {
        System.out.println("=== TESTING DASHBOARD COUNTS ===\n");
        
        // Test DonationManager counts
        DonationManager manager = new DonationManager();
        
        System.out.println("1. Testing Total Donors...");
        int donors = manager.getTotalDonors();
        System.out.println("   Result: " + donors + "\n");
        
        System.out.println("2. Testing Total Recipients...");
        int recipients = manager.getTotalRecipients();
        System.out.println("   Result: " + recipients + "\n");
        
        System.out.println("3. Testing Total Organs...");
        int organs = manager.getTotalOrgans();
        System.out.println("   Result: " + organs + "\n");
        
        // Test BloodBank counts
        BloodBank bloodBank = new BloodBank();
        
        System.out.println("4. Testing Blood Units...");
        int bloodUnits = bloodBank.totalUnits();
        System.out.println("   Result: " + bloodUnits + "\n");
        
        System.out.println("5. Testing Low Stock...");
        int lowStock = bloodBank.lowStockCount(5);
        System.out.println("   Result: " + lowStock + "\n");
        
        // Direct database query test
        System.out.println("6. Direct Database Query Test...");
        testDirectQueries();
        
        System.out.println("\n=== TEST COMPLETE ===");
    }
    
    private static void testDirectQueries() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("   ERROR: Cannot connect to database!");
                return;
            }
            
            // Test donors count
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM donors");
            if (rs.next()) {
                System.out.println("   Direct Donors Count: " + rs.getInt("count"));
            }
            
            // Test recipients count
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM recipients");
            if (rs.next()) {
                System.out.println("   Direct Recipients Count: " + rs.getInt("count"));
            }
            
            // Test blood inventory
            rs = stmt.executeQuery("SELECT COALESCE(SUM(units), 0) as total FROM blood_inventory");
            if (rs.next()) {
                System.out.println("   Direct Blood Units: " + rs.getInt("total"));
            }
            
            // Test organs count
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM donors WHERE organ_type IS NOT NULL AND organ_type != ''");
            if (rs.next()) {
                System.out.println("   Direct Organs Count: " + rs.getInt("count"));
            }
            
            conn.close();
        } catch (Exception e) {
            System.out.println("   ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

