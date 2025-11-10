
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BloodBank {
    public void addBlood(String type, int units) {
        String check = "SELECT units FROM blood_inventory WHERE blood_type LIKE ?";
        String insert = "INSERT INTO blood_inventory (blood_type, units) VALUES (?, ?)";
        String update = "UPDATE blood_inventory SET units=? WHERE blood_type LIKE ?";
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("ERROR: BloodBank - Connection is NULL in addBlood!");
                throw new SQLException("Database connection is null. Please check your database connection.");
            }
            try (PreparedStatement psCheck = conn.prepareStatement(check)) {
                psCheck.setString(1, type);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next()) {
                    int existing = rs.getInt("units");
                    try (PreparedStatement psUpdate = conn.prepareStatement(update)) {
                        psUpdate.setInt(1, existing + units);
                        psUpdate.setString(2, type);
                        psUpdate.executeUpdate();
                        System.out.println("✅ Blood inventory updated.");
                    }
                } else {
                    try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                        psInsert.setString(1, type);
                        psInsert.setInt(2, units);
                        psInsert.executeUpdate();
                        System.out.println("✅ Blood added to inventory.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error in addBlood: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add blood units: " + e.getMessage(), e);
        }
    }

    public void displayInventory() {
        String sql = "SELECT * FROM blood_inventory";
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("ERROR: BloodBank - Connection is NULL in displayInventory!");
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                System.out.println("\n🩸 Blood Inventory:");
                while (rs.next()) {
                    System.out.println(rs.getString("blood_type") + " : " + rs.getInt("units") + " units");
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error in displayInventory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Fetch total units across all blood types
    public int totalUnits() {
        String sql = "SELECT COALESCE(SUM(units), 0) as total FROM blood_inventory";
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("ERROR: BloodBank - Connection is NULL!");
                return 0;
            }
            System.out.println("DEBUG: BloodBank - Connection established");
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("total");
                    System.out.println("DEBUG: Blood Units Available = " + count);
                    return count;
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error fetching blood units: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error in totalUnits: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // number of blood types with units < threshold
    public int lowStockCount(int threshold) {
        String sql = "SELECT COUNT(*) as lowcount FROM blood_inventory WHERE units < ?";
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                System.out.println("ERROR: BloodBank - Connection is NULL in lowStockCount!");
                return 0;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, threshold);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("lowcount");
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: Error in lowStockCount: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error in lowStockCount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
