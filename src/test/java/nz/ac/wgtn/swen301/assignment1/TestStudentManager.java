package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentManager, to be extended.
 */
public class TestStudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND IN ITS INITIAL STATE BEFORE EACH TEST RUNS
    @BeforeEach
    public  void init () {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    @Test
    public void dummyTest() throws Exception {
        Student student = new StudentManager().fetchStudent("id42");
        // THIS WILL INITIALLY FAIL !!
        assertNotNull(student);
    }

    @Test
    public void testFetchStudent() throws Exception {
        Student student = new StudentManager().fetchStudent("id42");
        System.out.println("Student id: " + student.getId() + ", name: " + student.getName() + ", degree: " + student.getDegree().getName());
        assertNotNull(student);
    }

    @Test
    public void testFetchStudent2() throws Exception {
        boolean noSuchRecordException = false;
        for(int i = 0; i < 10000; i++){
            try {
                Student student = new StudentManager().fetchStudent("id" + i);
                System.out.println("Student id: " + student.getId() + ", name: " + student.getName() + ", degree: " + student.getDegree().getName());
            } catch (Exception e){
                noSuchRecordException = true;
            }
        }
        assertTrue(!noSuchRecordException);
    }

    @Test
    public void testFetchDegree() throws Exception {
        boolean noSuchRecordException = false;
        try {
            Degree degree = new StudentManager().fetchDegree("deg5");
            System.out.println("Degree id:" + degree.getId() + ", name: " + degree.getName());

        } catch(Exception e){
            noSuchRecordException = true;
        }
        assertTrue(!noSuchRecordException);
    }


    @Test
    public void testFetchDegree2() throws Exception {
        boolean exceptionThrown = false;
        for(int i = 0; i < 10; i++){
            try {
                Degree degree = new StudentManager().fetchDegree("deg" + i);
                System.out.println("Degree id:" + degree.getId() + ", name: " + degree.getName());
            } catch(Exception e){
                exceptionThrown = true;
            }
        }
        assertTrue(!exceptionThrown);
    }

    @Test
    public void testRemove() throws Exception {
        boolean noSuchRecordException = false;
        try {
            Student student = new StudentManager().fetchStudent("id42");
            System.out.println("Student id: " + student.getId() + ", name: " + student.getName() + ", degree: " + student.getDegree().getName());
        } catch (Exception e) {
            noSuchRecordException = true;
        }
        assertTrue(!noSuchRecordException);
        try {
            new StudentManager().fetchStudent("id42");
        } catch (Exception e) {
            noSuchRecordException = true;
        }
        assertTrue(noSuchRecordException);
    }

    @Test
    public void testUpdate() throws Exception {
        Student student = new StudentManager().fetchStudent("id42");
        System.out.println("Student id: " + student.getId() + ", name: " + student.getName() + ", degree: " + student.getDegree().getName());
        student.setFirstName("John");
        student.setName("Doe");
        student.setDegree(new StudentManager().fetchDegree("deg5"));
        StudentManager.update(student);
        student = new StudentManager().fetchStudent("id42");
        assertNotNull(student);
        System.out.println("Student id: " + student.getId() + ", name: " + student.getName() + ", degree: " + student.getDegree().getName());
        assertEquals("John", student.getFirstName());
        assertEquals("Doe", student.getName());
        assertEquals("deg5", student.getDegree().getId());
    }

    @Test
    public void testNewStudent() throws Exception {
        Student student1 = StudentManager.newStudent("Doe", "John", new StudentManager().fetchDegree("deg5"));
        Student student2 = new StudentManager().fetchStudent(student1.getId());
        assertNotNull(student1);
        assertNotNull(student2);
        System.out.println("Student1 id: " + student1.getId() + ", name: " + student1.getName() + ", degree: " + student1.getDegree().getName());
        System.out.println("Student2 id: " + student2.getId() + ", name: " + student2.getName() + ", degree: " + student2.getDegree().getName());
        assertEquals("John", student2.getFirstName());
        assertEquals("Doe", student2.getName());
        assertEquals("deg5", student2.getDegree().getId());
    }





    @Test
    public void testPerformance() throws Exception {
        int x = 250; // replace with desired amount of IDs
        String[] studentIds = new String[x];
        String[] degreeIds = new String[x];
        String[] idQueue = new String[2*x]; // twice the size to hold both student and degree ids

        Random rand = new Random();
        for(int i=0; i<x; i++){
            int randomStudentNum = rand.nextInt(10000); // numbers 0-9999
            int randomDegreeNum = rand.nextInt(10); // numbers 0-9

            studentIds[i] = "id" + randomStudentNum;
            degreeIds[i] = "deg" + randomDegreeNum;
        }

        // Merge into idQueue
        for(int i=0; i<x; i++){
            idQueue[i*2] = studentIds[i]; // Even index for studentIds
            idQueue[i*2 + 1] = degreeIds[i]; // Odd index for degreeIds
        }

        long start = System.currentTimeMillis();

        for (String str : idQueue) {
            if (str.startsWith("id")) {
                StudentManager.fetchStudent(str);
            } else {
                StudentManager.fetchDegree(str);

            }
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Time elapsed: " + timeElapsed + "ms");
        System.out.println("Processed " + idQueue.length + " IDs");
    }
}

