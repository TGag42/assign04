package assign09;

import java.text.DecimalFormat;

/**
 * This class provides a simple representation for a University of Utah student.
 * Object's hashCode method is overridden with a correct hash function for this
 * object, which does a reasonable (medium-quality) job of distributing students
 * in a hash table.
 */
public class StudentMediumHash {

    private int uid;
    private String firstName;
    private String lastName;

    /**
     * Creates a new student with the specified uid, firstName, and lastName.
     */
    public StudentMediumHash(int uid, String firstName, String lastName) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getUid() {
        return this.uid;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String toString() {
        DecimalFormat formatter = new DecimalFormat("0000000");
        return firstName + " " + lastName + " (u" + formatter.format(uid) + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        StudentMediumHash that = (StudentMediumHash) other;
        return uid == that.uid &&
               firstName.equals(that.firstName) &&
               lastName.equals(that.lastName);
    }

    /**
     * Returns a hash code for this student that does a better job of distributing
     * students in a hash table than StudentBadHash, but is still fairly simple.
     * <p>
     * This implementation uses only the uid field. Since UIDs are well-spread
     * integers, this generally gives a decent distribution when combined with
     * the table's modulus operation.
     */
    @Override
    public int hashCode() {
        return uid;
    }
}
