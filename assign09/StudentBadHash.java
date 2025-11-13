package assign09;

import java.text.DecimalFormat;

/**
 * This class provides a simple representation for a University of Utah student.
 * Object's hashCode method is overridden with a correct hash function for this
 * object, but one that does a poor job of distributing students in a hash
 * table.
 */
public class StudentBadHash {

    private int uid;
    private String firstName;
    private String lastName;

    /**
     * Creates a new student with the specified uid, firstName, and lastName.
     *
     * @param uid
     * @param firstName
     * @param lastName
     */
    public StudentBadHash(int uid, String firstName, String lastName) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * @return the UID for this student object
     */
    public int getUid() {
        return this.uid;
    }

    /**
     * @return the first name for this student object
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * @return the last name for this student object
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * @return a textual representation of this student
     */
    public String toString() {
        DecimalFormat formatter = new DecimalFormat("0000000");
        return firstName + " " + lastName + " (u" + formatter.format(uid) + ")";
    }

    /**
     * Two StudentBadHash objects are considered equal if they have the same
     * UID, first name, and last name.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        StudentBadHash that = (StudentBadHash) other;
        return uid == that.uid &&
               firstName.equals(that.firstName) &&
               lastName.equals(that.lastName);
    }

    /**
     * Returns a hash code for this student that satisfies the equals / hashCode
     * contract but does a very poor job of distributing students in a hash table.
     * <p>
     * This implementation returns the same hash code for every student, which
     * forces all students into the same bucket and causes many collisions.
     */
    @Override
    public int hashCode() {
        return 1;  // extremely bad distribution, but still a correct hash function
    }
}
