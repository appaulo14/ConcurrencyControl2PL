import java.util.ArrayList;

/**
 * Interacts with {@ConcurrencyControlManagar} to
 * perform transactions on the class rosters
 * 
 * @author Pawl
 * 
 */
public class TransactionObject {

    ConcurrencyControlManager myCCM = new ConcurrencyControlManager();

    /**
     * Insert the specified student into the specified class roster.
     * 
     * @param classRosterId
     *            the id of the class roster to insert into
     * @param studentId
     *            the id of the student to insert into the class roster
     */
    public void insert(int classRosterId, int studentId) {
        myCCM.requestWriteLock(classRosterId);
        ConcurrencyControl.rosterList.get(classRosterId).insert(studentId);
        myCCM.releaseWriteLock(classRosterId);
    }

    /**
     * Delete the specified student from the specified class roster.
     * 
     * @param classRosterId
     *            the id of the class roster to delete from
     * @param id
     *            the id of the student to delete from the class roster
     */
    public void delete(int classRosterId, int id) {
        myCCM.requestWriteLock(classRosterId);
        ConcurrencyControl.rosterList.get(classRosterId).delete(id);
        myCCM.releaseWriteLock(classRosterId);
    }

    /**
     * Get a list of the rosters for the classes in which the specified student
     * is currently enrolled
     * 
     * @param studentId
     *            the id of the student whose enrolled course will be returned
     * @return the list of class rosters in which the student is currently
     *         enrolled
     */
    public ArrayList<ClassRoster> enrolled_courses(int studentId) {
        myCCM.requestReadLockForAll();
        ArrayList<ClassRoster> enrolledCoursesList = new ArrayList<ClassRoster>();
        for (ClassRoster cr : ConcurrencyControl.rosterList) {
            if (cr.find(studentId)) {
                enrolledCoursesList.add(cr);
            }
        }
        myCCM.releaseReadLockForAll();
        return enrolledCoursesList;
    }

    /**
     * @return the total student enrollment for all of the class rosters
     */
    public int total_enrollment() {
        int totalEnrollmentCount = 0;
        myCCM.requestReadLockForAll();
        for (ClassRoster cr : ConcurrencyControl.rosterList) {
            totalEnrollmentCount += cr.count();
        }
        myCCM.releaseReadLockForAll();
        return totalEnrollmentCount;
    }

    /**
     * Transfer a student from one class roster to another
     * 
     * @param classRosterId1
     *            the id of the class roster to transfer from
     * @param classRosterId2
     *            the id of the class roster to transfer to
     * @param studentId
     *            the id of the student who will get transfered
     */
    public void transfer(int classRosterId1, int classRosterId2, int studentId) {
        if (classRosterId1 < classRosterId2) {
            myCCM.requestWriteLock(classRosterId1);
            myCCM.requestWriteLock(classRosterId2);
        }
        else {
            myCCM.requestWriteLock(classRosterId2);
            myCCM.requestWriteLock(classRosterId1);
        }
        ClassRoster cr1 = ConcurrencyControl.rosterList.get(classRosterId1);
        ClassRoster cr2 = ConcurrencyControl.rosterList.get(classRosterId2);
        cr1.delete(studentId);
        cr2.insert(studentId);
        myCCM.releaseWriteLock(classRosterId1);
        myCCM.releaseWriteLock(classRosterId2);
    }
}
