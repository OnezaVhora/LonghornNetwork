import java.util.*;

/**
 * UniversityStudent class extends the Student class and represents a university student.
 * It contains additional attributes and methods specific to university students.
 */
public class UniversityStudent extends Student {
    // TODO: Constructor and additional methods to be implemented

    private UniversityStudent roommate; 
    private List<String> friendRequests = new ArrayList<>();
    private List<String> chatHistory = new ArrayList<>();



    /**
     * Constructor for UniversityStudent.
     * Initializes the student with the given parameters and creates defensive copies of the lists.     
     * @param name
     * @param age
     * @param gender
     * @param year
     * @param major
     * @param gpa
     * @param roommatePreferences
     * @param previousInternships
     */
    public UniversityStudent(String name, int age, String gender, int year, String major, double gpa, List<String> roommatePreferences, List<String> previousInternships){

        this.name = name;
        this.age = age;
        this.gender = gender;
        this.year = year;
        this.major = major;
        this.gpa = gpa;
        this.roommatePreferences = roommatePreferences;
        this.previousInternships = previousInternships;
        this.roommate = null; //intialize to null, no roommate assigned yet
    }


 //Setter for roommate - used in Gale-Shapley algorithm
 public void setRoommate(UniversityStudent roommate) {
    this.roommate = roommate;
}

//getter for roomate
public UniversityStudent getRoommate() {
    return roommate;
}

//getter for name
public String name() {
    return name;
}

public String getName() {
    return name;
}

//getter for internships
public List<String> PreviousInternships() {
    return previousInternships;
}

public List<String> getFriendRequests() {
    return friendRequests;
}

public List<String> getChatHistory() {
    return chatHistory;
}

public void addFriendRequest(String requester) {
    friendRequests.add(requester);
}

public void addChatMessage(String sender, String message) {
    // Update implementation to handle sender and message
    chatHistory.add(sender + ": " + message);
}

//create universitystudent from map of key-value pairs
public static UniversityStudent fromMap(Map<String, String> data){
    String name = data.get("Name");
    String ageStr = data.get("Age");
    String gender = data.get("Gender");
    String yearStr = data.get("Year");
    String major = data.get("Major");
    String gpaStr = data.get("GPA");
    String roommatePreferencesStr = data.get("RoommatePreferences");
    String previousInternshipsStr = data.get("PreviousInternships");

    //check for missing values
    if(name == null || ageStr == null || gender == null || yearStr == null || major == null || gpaStr == null || roommatePreferencesStr == null || previousInternshipsStr == null){
        throw new IllegalArgumentException("Missing required field in student entry.");
    }

    int age;
    int year;
    double gpa;
    try{
        age = Integer.parseInt(ageStr);
    } catch (NumberFormatException e) {
        throw new NumberFormatException("Invalid number format for age: '" + ageStr + "' in student entry for " + name + ".");
    }

    try{
        year = Integer.parseInt(yearStr);
    } catch (NumberFormatException e) {
        throw new NumberFormatException("Invalid number format for year: '" + yearStr + "' in student entry for " + name + ".");
    }

    try{
        gpa = Double.parseDouble(gpaStr);
    } catch (NumberFormatException e) {
        throw new NumberFormatException("Invalid number format for GPA: '" + gpaStr + "' in student entry for " + name + ".");
    }

    //Parse comma separated fields, if the value is none, treat as empty list
    List<String> roommatePreferences = new ArrayList<>();
    for(String pref: roommatePreferencesStr.split(",")){
        pref = pref.trim(); //remove leading and trailing whitespace
        if(!pref.isEmpty()){
            roommatePreferences.add(pref);
        }
    }

    List<String> previousInternships = new ArrayList<>();
    if(!previousInternshipsStr.equalsIgnoreCase("none")){
        for(String intern: previousInternshipsStr.split(",")){
            intern = intern.trim(); //remove leading and trailing whitespace
            if(!intern.isEmpty()){
                previousInternships.add(intern);
            }
        }
    }

    return new UniversityStudent(name, age, gender, year, major, gpa, roommatePreferences, previousInternships);    
}

@Override
public int calculateConnectionStrength(Student other) {
    int strength = 0;

    if(other instanceof UniversityStudent){
        UniversityStudent o = (UniversityStudent) other;
        //if o is the assigned roommate add +4 bonus
        if(this.roommate != null && this.roommate.equals(o)){
            strength += 4;
        }
        //+3 for each shared internship
        for(String internship: this.previousInternships){
            if(o.previousInternships.contains(internship)){
                strength += 3;
            }
        }
        //+2 if same major
        if(this.major.equals(o.major)){
            strength += 2;
        }
        //+1 if same age
        if(this.age == o.age){
            strength += 1;
        }
    }
    return strength;
}

@Override
public String toString() {
    return "UniversityStudent{" +
            "name='" + name + '\'' +
            ", age=" + age + 
            ", gender='" +  gender + '\'' +
            ", year=" + year +
            ", major='" + major + '\'' +
            ", gpa=" + gpa +
            ", roommatePreferences=" + roommatePreferences +
            ", previousInternships=" + previousInternships +
            "}";
}

Iterable<String> previousInternships() {
    throw new UnsupportedOperationException("Not supported yet.");
}

}