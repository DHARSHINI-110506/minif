import java.sql.*;

// Quick test to verify database connection and data
public class QuickDBTest {
    public static void main(String[] args) {
        System.out.println("=== QUICK DATABASE TEST ===\n");
        
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("❌ FAILED: Cannot connect to database!");
                System.out.println("   Check:");
                System.out.println("   1. MySQL is running");
                System.out.println("   2. Database 'bloodbank_db' exists");
                System.out.println("   3. Username: root, Password: Mysql@123");
                return;
            }
            System.out.println("✅ Database connection successful!\n");
            
            Statement stmt = conn.createStatement();
            
            // Test donors table
            System.out.println("1. Testing DONORS table:");
            try {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM donors");
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("   Total rows in donors table: " + count);
                    
                    if (count > 0) {
                        // Show sample data
                        rs = stmt.executeQuery("SELECT * FROM donors LIMIT 3");
                        System.out.println("   Sample data:");
                        while (rs.next()) {
                            System.out.println("     - ID: " + rs.getInt("donor_id") + 
                                             ", Name: " + rs.getString("name") +
                                             ", Blood: " + rs.getString("blood_group") +
                                             ", Organ: " + rs.getString("organ_type"));
                        }
                    } else {
                        System.out.println("   ⚠️ Table is EMPTY - no donors found!");
                    }
                }
            } catch (SQLException e) {
                System.out.println("   ❌ ERROR: " + e.getMessage());
            }
            
            // Test recipients table
            System.out.println("\n2. Testing RECIPIENTS table:");
            try {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM recipients");
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("   Total rows in recipients table: " + count);
                    if (count == 0) {
                        System.out.println("   ⚠️ Table is EMPTY - no recipients found!");
                    }
                }
            } catch (SQLException e) {
                System.out.println("   ❌ ERROR: " + e.getMessage());
            }
            
            // Test blood_inventory table
            System.out.println("\n3. Testing BLOOD_INVENTORY table:");
            try {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM blood_inventory");
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("   Total rows in blood_inventory table: " + count);
                    
                    if (count > 0) {
                        rs = stmt.executeQuery("SELECT COALESCE(SUM(units), 0) as total FROM blood_inventory");
                        if (rs.next()) {
                            System.out.println("   Total blood units: " + rs.getInt("total"));
                        }
                    } else {
                        System.out.println("   ⚠️ Table is EMPTY - no blood inventory found!");
                    }
                }
            } catch (SQLException e) {
                System.out.println("   ❌ ERROR: " + e.getMessage());
            }
            
            // Check table structure
            System.out.println("\n4. Checking table structures:");
            try {
                ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM donors");
                System.out.println("   Donors table columns:");
                boolean hasStatus = false;
                while (rs.next()) {
                    String colName = rs.getString("Field");
                    System.out.println("     - " + colName);
                    if (colName.equals("status")) hasStatus = true;
                }
                if (!hasStatus) {
                    System.out.println("   ⚠️ 'status' column does NOT exist in donors table!");
                }
            } catch (SQLException e) {
                System.out.println("   ❌ ERROR: " + e.getMessage());
            }
            
            conn.close();
            System.out.println("\n=== TEST COMPLETE ===");
            
        } catch (Exception e) {
            System.out.println("❌ FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

