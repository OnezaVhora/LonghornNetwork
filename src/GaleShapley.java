import java.util.*;

/**
 * GaleShapley class implements the Gale-Shapley algorithm for roommate assignment.
 * It takes a list of UniversityStudent objects and assigns them roommates based on their preferences.
 */
public class GaleShapley {
    public static void assignRoommates(List<UniversityStudent> students) {
        //map to hold final pairings, each student is assigned to a roommate
        Map<UniversityStudent, UniversityStudent> roommatepairs = new HashMap<>();
        //map to track which proposal each student has made
        Map<UniversityStudent, Integer> nextPropsalIndex = new HashMap<>();
        //map to quickly look up student by name
        Map<String, UniversityStudent> nameToStudent = new HashMap<>();

        for (UniversityStudent s : students) {
            nameToStudent.put(s.name(), s);
            nextPropsalIndex.put(s, 0); //initialize to 0, no proposals made yet
        }

         //queue for students who are free and need to propose
         Queue<UniversityStudent> freeStudents = new LinkedList<>(students);
         for (UniversityStudent s: students) {
             if(!s.roommatePreferences.isEmpty()){
                 freeStudents.offer(s); //add to queue if they have preferences
             }
         }
 
         while(!freeStudents.isEmpty()){
             UniversityStudent s = freeStudents.poll(); //get the next free student
             //skip if s is alr paired
             if(s.getRoommate() != null){
                 continue;
             }//regular gale-shapley algorithm: checks to see if there is one roomate that is preferred over the other, no such case will be tested
             
             int index = nextPropsalIndex.get(s); //get the index of the next proposal
             if(index >= s.roommatePreferences.size()){
                 continue; //s doesn't have any more preferences to propose to
             }
 
             String preferredName = s.roommatePreferences.get(index); //get the name of the preferred student
             nextPropsalIndex.put(s, index + 1); //increment the index for the next proposal
             UniversityStudent t = nameToStudent.get(preferredName); 
             if (t == null) {
                 //preffered student not found, try next option
                 if(nextPropsalIndex.get(s) < s.roommatePreferences.size()){
                     freeStudents.offer(s); //add back to queue if they have more preferences
                 }
                 continue; //skip to the next student
             }
             if(!t.roommatePreferences.contains(s.name())){
                 if(nextPropsalIndex.get(s) < s.roommatePreferences.size()){
                     freeStudents.offer(s); //add back to queue if they have more preferences
                 }
                 continue; //skip to the next student    
             }
             
             //if t is free, pair them up
             if(t.getRoommate() == null){
                 roommatepairs.put(s, t); //pair them up
                 roommatepairs.put(t, s); //pair them up in the opposite direction as well
                 s.setRoommate(t); //set the roommate for s
                 t.setRoommate(s); //set the roommate for t
             }
             else{
                 UniversityStudent currentRoommate = t.getRoommate();
                 int currentIndex = t.roommatePreferences.indexOf(currentRoommate.name());
                 int newIndex = t.roommatePreferences.indexOf(s.name());
                 if(newIndex < currentIndex){
                     //t prefers s over their current roommate, so swap them
                     roommatepairs.put(t, s);
                     roommatepairs.put(s, t);
                     roommatepairs.remove(currentRoommate); // Remove current partner from the pairs
                     freeStudents.offer(currentRoommate); // Add current partner back to the queue
                     currentRoommate.setRoommate(null); // Set current partner's roommate to null
                     s.setRoommate(t); // Set s's roommate to t
                     t.setRoommate(s); // Set t's roommate to s
                 }
                 else{
                     //t prefers their current roommate, so reject s
                     if(nextPropsalIndex.get(s) < s.roommatePreferences.size()){
                         freeStudents.offer(s); //add back to queue if they have more preferences
                     }
                 }
             }
         }
 
         // Print the final pairings
         System.out.println("\nRoommate Pairings (Gale-Shapley):");
         Set<UniversityStudent> printed = new HashSet<>();
         for (UniversityStudent s: roommatepairs.keySet()) {
             UniversityStudent roommate = roommatepairs.get(s);
             if (!printed.contains(s) && !printed.contains(roommate)) {
                 System.out.println(s.name() + " paired with " + roommate.name());
                 printed.add(s);
                 printed.add(roommate);
             }
         }
     }
 }
 