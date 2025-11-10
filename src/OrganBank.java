
import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class OrganBank {

    private DonationManager manager = new DonationManager();

    private static final List<String> BLOOD_TYPES = Arrays.asList(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    );

    public void addBloodDonorUI() {
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField bloodField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField locationField = new JTextField();

        Object[] msg = {
                "Name:", nameField,
                "Age:", ageField,
                "Blood Group:", bloodField,
                "Weight (kg):", weightField,
                "Contact:", contactField,
                "Location:", locationField
        };

        int option = JOptionPane.showConfirmDialog(null, msg, "Register Blood Donor", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String blood = bloodField.getText().trim().toUpperCase();
            int weight = Integer.parseInt(weightField.getText().trim());
            String contact = contactField.getText().trim();
            String location = locationField.getText().trim();

            // Validate blood group
            if (!BLOOD_TYPES.contains(blood)) {
                JOptionPane.showMessageDialog(null, "❌ Invalid blood group. Allowed: " + BLOOD_TYPES, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Eligibility check: age >= 20 AND weight >= 50
            if (age < 20) {
                JOptionPane.showMessageDialog(null, "❌ Registration failed! Age must be at least 20 years to register as a donor.", "Ineligible Donor", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (weight < 50) {
                JOptionPane.showMessageDialog(null, "❌ Registration failed! Weight must be at least 50 kg to register as a donor.", "Ineligible Donor", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Additional eligibility check
            boolean eligible = manager.isEligible(age, weight, false, false, "Blood");
            if (!eligible) {
                String reason = manager.getIneligibilityReason(age, weight, false, false, "Blood");
                JOptionPane.showMessageDialog(null, "❌ You cannot donate: " + reason, "Ineligible Donor", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Donor donor = new Donor(name, age, blood, null, contact, location, weight);
            boolean added = manager.addDonor(donor);

            if (added) JOptionPane.showMessageDialog(null, "✅ Blood donor registered successfully!");
            else JOptionPane.showMessageDialog(null, "⚠ Could not register donor. Check database connection.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Invalid numeric input.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addOrganDonorUI() {
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField bloodField = new JTextField();
        JTextField organField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField locationField = new JTextField();

        Object[] msg = {
                "Name:", nameField,
                "Age:", ageField,
                "Blood Group:", bloodField,
                "Organ Type:", organField,
                "Weight (kg):", weightField,
                "Contact:", contactField,
                "Location:", locationField
        };

        int option = JOptionPane.showConfirmDialog(null, msg, "Register Organ Donor", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String blood = bloodField.getText().trim().toUpperCase();
            String organ = organField.getText().trim();
            String weightStr = weightField.getText().trim();
            String contact = contactField.getText().trim();
            String location = locationField.getText().trim();

            // Validate weight - required and must be >= 50
            if (weightStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "❌ Weight is required. Please enter your weight.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int weight = Integer.parseInt(weightStr);
            
            // Eligibility check: age >= 20 AND weight >= 50
            if (age < 20) {
                JOptionPane.showMessageDialog(null, "❌ Registration failed! Age must be at least 20 years to register as a donor.", "Ineligible Donor", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (weight < 50) {
                JOptionPane.showMessageDialog(null, "❌ Registration failed! Weight must be at least 50 kg to register as a donor.", "Ineligible Donor", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Blood group validation
            if (!BLOOD_TYPES.contains(blood)) {
                JOptionPane.showMessageDialog(null, "❌ Invalid blood group. Allowed: " + BLOOD_TYPES, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int chronic = JOptionPane.showConfirmDialog(null, "Do you have any chronic diseases?", "Health Check", JOptionPane.YES_NO_OPTION);
            int op = JOptionPane.showConfirmDialog(null, "Have you undergone major surgery recently?", "Health Check", JOptionPane.YES_NO_OPTION);

            boolean eligible = chronic == JOptionPane.NO_OPTION && op == JOptionPane.NO_OPTION;
            if (!eligible) {
                JOptionPane.showMessageDialog(null, "❌ You are not eligible to donate organs currently.", "Ineligible Donor", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Donor donor = new Donor(name, age, blood, organ, contact, location, weight);
            boolean added = manager.addDonor(donor);
            if (added) JOptionPane.showMessageDialog(null, "✅ Organ donor registered successfully!");
            else JOptionPane.showMessageDialog(null, "⚠ Could not register donor. Check database connection.");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "❌ Invalid numeric input. Please enter valid numbers for age and weight.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


