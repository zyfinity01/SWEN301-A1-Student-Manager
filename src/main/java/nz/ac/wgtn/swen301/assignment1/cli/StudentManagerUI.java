package nz.ac.wgtn.swen301.assignment1.cli;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class StudentManagerUI {

    // THE FOLLOWING METHOD MUST BE IMPLEMENTED
    /**
     * Executable: the user will provide argument(s) and print details to the console as described in the assignment brief,
     * E.g. a user could invoke this by running "java -cp <someclasspath> <arguments></arguments>"
     * @param arg
     */
    public static void main (String[] arg) {
        // Define options
        Options options = new Options();

        Option fetchOne = Option.builder("fetchone")
                .argName("studentId")
                .hasArg()
                .desc("Fetch the respective student record with provided id")
                .build();

        Option fetchAll = Option.builder("fetchall")
                .desc("Fetch all student records")
                .build();

        Option export = Option.builder("export")
                .desc("Fetch all student records, and write them to a file")
                .build();

        Option file = Option.builder("f")
                .argName("file")
                .hasArg()
                .desc("The file to write to")
                .build();

        options.addOption(fetchOne);
        options.addOption(fetchAll);
        options.addOption(export);
        options.addOption(file);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, arg);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        if (cmd.hasOption("fetchone")) {
            String studentId = cmd.getOptionValue("fetchone");
            try {
                Student student = StudentManager.fetchStudent(studentId);
                System.out.println("Student ID: " + student.getId() + "First Name: " + student.getFirstName() + "Last Name: " + student.getName() + "Degree: " + student.getDegree().getName());
            } catch (NoSuchRecordException e){
            }
            // Fetch and print student record with id = studentId
        } else if (cmd.hasOption("fetchall")) {
            try {
                ArrayList<String> studentIdsList = new ArrayList<>(StudentManager.fetchAllStudentIds());

                Collections.sort(studentIdsList, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        Integer id1 = Integer.parseInt(o1.substring(2));
                        Integer id2 = Integer.parseInt(o2.substring(2));
                        return id1.compareTo(id2);
                    }
                });
                for(String id : studentIdsList) {
                    Student student = StudentManager.fetchStudent(id);
                    System.out.println("Student ID: " + student.getId() + "First Name: " + student.getFirstName() + "Last Name: " + student.getName() + "Degree: " + student.getDegree().getName());
                }
            } catch (NoSuchRecordException e){
            }
            // Fetch and print all student records
        } else if (cmd.hasOption("export")) {
            if (cmd.hasOption("f")) {
                String fileName = cmd.getOptionValue("f");
                // Fetch all student records and write them to file with name = fileName
            } else {
                System.out.println("The -export option requires the -f <file> option");
                formatter.printHelp("utility-name", options);
                System.exit(1);
            }
        }
    }



}


