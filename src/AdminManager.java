
import java.util.Scanner;
import java.util.UUID;

public class AdminManager {
    private Scanner sc = new Scanner(System.in);

    public Admin adminLoginConsole() {
        System.out.print("Enter admin username: ");
        String username = sc.nextLine();
        System.out.print("Enter admin password: ");
        String password = sc.nextLine();

        Admin admin = new Admin(UUID.randomUUID().toString(), username, password);
        if (admin.login()) {
            System.out.println("✅ Admin login successful!");
            return admin;
        } else {
            System.out.println("❌ Invalid admin credentials.");
            return null;
        }
    }
}





