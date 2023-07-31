package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A student manager providing basic CRUD operations for instances of Student, and a read operation for instances of Degree.
 * @author jens dietrich
 */
public class StudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND THE APPLICATION CAN CONNECT TO IT WITH JDBC
    static {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    // Database connection parameters
    private static final String DATABASE_URL = "jdbc:derby:memory:studentdb";
    private static Connection conn;
    static HashMap<String, Student> students = new HashMap();
    static HashMap<String, Degree> degrees = new HashMap();

    static {
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // THE FOLLOWING METHODS MUST BE IMPLEMENTED :

    /**
     * Return a student instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student fetchStudent(String id) throws NoSuchRecordException {
        if(students.containsKey(id)){
            return students.get(id);
        }
        String sql = "SELECT * FROM students WHERE ID = '" + id + "'";
        try {
            Statement stmt = conn.createStatement();
            // Use stmt to execute a query

            try (ResultSet rs = stmt.executeQuery(sql)) {
                String sID = null;
                String sName = null;
                String sFirstName = null;
                Degree sDegree = null;
                while (rs.next()) {
                    sID = rs.getString("id");
                    sName = rs.getString("name");
                    sFirstName = rs.getString("first_name");
                    String degreeID = rs.getString("degree");
                    sDegree = fetchDegree(degreeID);
                    Student student = new Student(sID, sName, sFirstName, sDegree);
                    students.put(sID, student);
                    return student;
                }
            }
        } catch (SQLException e) {
            // handle exception
            throw new NoSuchRecordException();
        }
        return null;
    }

    /**
     * Return a degree instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchDegree (followed by optional numbers if multiple tests are used)
     */
    public static Degree fetchDegree(String id) throws NoSuchRecordException {
        if(degrees.containsKey(id)){
            return degrees.get(id);
        }
        String sql = "SELECT * FROM degrees WHERE ID ='"  + id + "'";
        try {
            Statement stmt = conn.createStatement();
            // Use stmt to execute a query

            try (ResultSet rs = stmt.executeQuery(sql)) {
                String dID = null;
                String dName = null;
                while (rs.next()) {
                    dID = rs.getString("id");
                    dName = rs.getString("name");
                    Degree degree = new Degree(dID, dName);
                    degrees.put(dID, degree);
                    return degree;
                }
            }
        } catch (SQLException e) {
            // handle exception
            throw new NoSuchRecordException();
        }
        return null;
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testRemove
     */
    public static void remove(Student student) throws NoSuchRecordException {
        String sql = "DELETE FROM students WHERE ID = '" + student.getId() + "'";
        try {
            Statement stmt = conn.createStatement();
            int affectedRows = stmt.executeUpdate(sql);
            if (affectedRows == 0) {
                throw new NoSuchRecordException();
            }
            students.remove(student.getId());
        } catch (SQLException e) {
            // handle exception
            e.printStackTrace();
        }
    }


    /**
     * Update (synchronize) a student instance with the database.
     * The id will not be changed, but the values for first names or degree in the database might be changed by this operation.
     * After executing this command, the attribute values of the object and the respective database value are consistent.
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testUpdate (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws NoSuchRecordException {
        String sql = "UPDATE students SET first_name = " + "'" + student.getFirstName() +
                "', name = '" + student.getName() +
                "', degree = '" + student.getDegree().getId() +
                "' WHERE ID = '" + student.getId() + "'";
        try {
            Statement stmt = conn.createStatement();
            int affectedRows = stmt.executeUpdate(sql);

            if (affectedRows == 0) {
                throw new NoSuchRecordException();
            }

            // Update the student in the local list as well
            students.put(student.getId(), student);

        } catch (SQLException e) {
            // handle exception
            e.printStackTrace();
        }




    }


    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not being used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name
     * @param firstName
     * @param degree
     * @return a freshly created student instance
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testNewStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student newStudent(String name,String firstName,Degree degree) {
        List<Integer> allStudentIds = fetchAllStudentIds().stream()
                .map(s -> Integer.parseInt(s.replace("id", "")))
                .collect(Collectors.toList());
        String newId = "id" + (Collections.max(allStudentIds) + 1);
        String sql = "INSERT INTO students (id, name, first_name, degree) VALUES ('" + newId + "', '" + name + "', '" + firstName + "', '" + degree.getId() + "')";

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);

            // Create new student instance
            Student newStudent = new Student(newId, name, firstName, degree);

            // Update the student in the local list as well
            students.put(newStudent.getId(), newStudent);

            return newStudent;
        } catch (SQLException e) {
            // handle exception
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all student ids currently being used in the database.
     * @return
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> fetchAllStudentIds() {
        String sql = "SELECT id FROM students";
        HashSet<String> ids = new HashSet<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ids.add(rs.getString("id"));
            }
        } catch (SQLException e) {
            // handle exception
            e.printStackTrace();
        }
        return ids;
    }



}
