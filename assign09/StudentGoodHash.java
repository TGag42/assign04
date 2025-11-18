package assign09;

import java.text.DecimalFormat;

/**
 * This class provides a simple representation for a University of Utah student.
 * Object's hashCode method is overridden with a correct hash function for this
 * object, which does a good job of distributing students in a hash table.
 *
 * @author Alex Waldmann
 * @author Tyler Gagliardi
 * @version November 13, 2025
 */
public class StudentGoodHash {

    private int uid;
    private String firstName;
    private String lastName;

    /**
     * Creates a new student with the specified uid, firstName, and lastName.
     */
    public StudentGoodHash(int uid, String firstName, String lastName) {
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
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        StudentGoodHash that = (StudentGoodHash) other;
        return uid == that.uid
                && firstName.equals(that.firstName)
                && lastName.equals(that.lastName);
    }

    /**
     * Returns a hash code for this student that aims to distribute students
     * well across a hash table. It combines all of the fields that participate
     * in equality (uid, firstName, lastName) using a standard 31-based mixing
     * scheme.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + uid;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}
