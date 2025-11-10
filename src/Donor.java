
public class Donor {

    private int donorId;
    private String name;
    private int age;
    private String bloodGroup;
    private String organType;
    private String contact;
    private String location;
    private Integer weight;   // optional

    // === Constructor without weight ===
    public Donor(String name, int age, String bloodGroup, String organType, String contact, String location) {
        this(name, age, bloodGroup, organType, contact, location, null);
    }

    // === Constructor with weight ===
    public Donor(String name, int age, String bloodGroup, String organType, String contact,
                 String location, Integer weight) {
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.organType = organType;
        this.contact = contact;
        this.location = location;
        this.weight = weight;
    }

    // === Constructor for fetching from DB ===
    public Donor(int donorId, String name, int age, String bloodGroup, String organType, String contact,
                 String location, Integer weight) {
        this(name, age, bloodGroup, organType, contact, location, weight);
        this.donorId = donorId;
    }

    // === Getters ===
    public int getDonorId() { return donorId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getBloodGroup() { return bloodGroup; }
    public String getOrganType() { return organType; }
    public String getContact() { return contact; }
    public String getLocation() { return location; }
    public Integer getWeight() { return weight; }

    // === Setters ===
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setOrganType(String organType) { this.organType = organType; }
    public void setContact(String contact) { this.contact = contact; }
    public void setLocation(String location) { this.location = location; }
    public void setWeight(Integer weight) { this.weight = weight; }

    @Override
    public String toString() {
        return "ID: " + donorId + " | Name: " + name + " | Age: " + age +
                " | Blood: " + bloodGroup + " | Organ: " + organType +
                " | Contact: " + contact + " | Location: " + location +
                " | Weight: " + (weight != null ? weight : "-");
    }
}

