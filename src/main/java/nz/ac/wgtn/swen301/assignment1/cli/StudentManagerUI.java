package nz.ac.wgtn.swen301.assignment1.cli;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
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
                Student student = StudentManager.fetchStudent("id" + studentId);
                System.out.println("Student ID: " + student.getId() + " First Name: " + student.getFirstName() + " Last Name: " + student.getName() + " Degree: " + student.getDegree().getName());
            } catch (NoSuchRecordException e){
                System.out.println("No student with id " + studentId + " found");
            }
            // Fetch and print student record with id = studentId
        } else if (cmd.hasOption("fetchall")) {
            try {
                ArrayList<String> studentIdsList = new ArrayList<>(StudentManager.fetchAllStudentIds());

                for(String id : studentIdsList) {
                    Student student = StudentManager.fetchStudent(id);
                    System.out.println("Student ID: " + student.getId() + " First Name: " + student.getFirstName() + " Last Name: " + student.getName() + " Degree: " + student.getDegree().getName());
                }
            } catch (NoSuchRecordException e){
                System.out.println("No such records found");

            }
            // Fetch and print all student records
        } else if (cmd.hasOption("export")) {
            if (cmd.hasOption("f")) {
                String fileName = cmd.getOptionValue("f") + ".csv";
                try {
                    ArrayList<String> studentIdsList = new ArrayList<>(StudentManager.fetchAllStudentIds());
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                        writer.write("id,name,firstname,degree");
                        for (String studentId : studentIdsList) {
                            Student student = StudentManager.fetchStudent(studentId);
                            writer.write(student.getId() + "," + student.getName() + "," + student.getFirstName() + "," + student.getDegree().getName());
                            writer.newLine();
                        }
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchRecordException e) {
                    System.out.println("No such records found");
                }
            } else {
                System.out.println("The -export option requires the -f <file> option");
                formatter.printHelp("utility-name", options);
                System.exit(1);
            }
        }
    }



}


