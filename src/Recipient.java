
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Recipient {
    private String recipientId, name, requiredBloodGroup, requiredOrgan, contact, location;
    private int age;

    public Recipient(String recipientId, String name, int age, String requiredBloodGroup, String requiredOrgan, String contact, String location) {
        this.recipientId = recipientId;
        this.name = name;
        this.age = age;
        this.requiredBloodGroup = requiredBloodGroup;
        this.requiredOrgan = requiredOrgan;
        this.contact = contact;
        this.location = location;
    }

    public void saveToDB() {
        String sql = "INSERT INTO recipients (recipient_id, name, age, required_blood_group, required_organ, contact, location) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, recipientId);
            ps.setString(2, name);
            ps.setInt(3, age);
            ps.setString(4, requiredBloodGroup);
            ps.setString(5, requiredOrgan);
            ps.setString(6, contact);
            ps.setString(7, location);
            ps.executeUpdate();
            System.out.println("✅ Recipient saved to database successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Failed to save recipient to DB.");
            e.printStackTrace();
        }
    }

    // Getter methods
    public String getRecipientId() { return recipientId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getRequiredBloodGroup() { return requiredBloodGroup; }
    public String getRequiredOrgan() { return requiredOrgan; }
    public String getContact() { return contact; }
    public String getLocation() { return location; }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Age: %d | Blood: %s | Organ: %s | Contact: %s | Location: %s",
                recipientId, name, age, requiredBloodGroup, requiredOrgan, contact, location);
    }
}


