package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Parser {

	private static Scanner logFile;
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
	private static final String TEST_START="TEST STARTED";
	private static final String TEST_END = "TEST FINISHED";
	private static final String HELP = "-s prints out the time difference between lines containing " + TEST_START +
			" and " + TEST_END + ".\r\n" + 
			"-i <args,…> prints out lines containing all arguments.\r\n" + 
			"-e <args,…> prints out all lines which don't contain any of provided arguments.";
	
	

	public static void main(String[] args) {
		if (args[0].equals("-h")) {
			System.out.print(HELP);
		}
		else {
			try {
				
				for (int argNum=1; argNum<args.length; argNum++) {
					logFile = new Scanner(new File(args[0]));
					switch(args[argNum]) {
					case "-h":
						System.out.print(HELP);
						break;
					case "-s":
						printTimeDifference();
						break;
					case "-i": case "-e":
						if (argNum+1<args.length) {
							List<String> keywords = Arrays.asList(args[argNum+1].split(","));
							if (args[argNum].equals("-i")) {
								printLinesWithAllArgs(keywords);
							}
							else {
								printLinesWithoutAnyArgs(keywords);		
							}
							argNum++;
						}
						else {
							System.out.println("Arguments not provided.");
						}
						break;

					}
				}

			} catch (FileNotFoundException e) {
				System.out.print("File not found\n");
			}
		}
	}

	private static void printLinesWithoutAnyArgs(List<String> keywords) {
		System.out.println("Lines not containing any arguments:");
		while(logFile.hasNextLine()) {
			String thisLine=logFile.nextLine();
			boolean containsAnyKeywords = false;
			for (String keyword: keywords) {
				if (thisLine.contains(keyword)) {
				containsAnyKeywords = true;
				break;
				}
				else {
					containsAnyKeywords = false;
				}
			}
			if (!containsAnyKeywords) {
				System.out.println(thisLine);
			}
		}

	}

	private static void printLinesWithAllArgs(List<String> keywords) {
		System.out.println("Lines containing all arguments:");
		while(logFile.hasNextLine()) {
			String thisLine=logFile.nextLine();
			boolean containsAllKeywords = false;
			for (String keyword: keywords) {
				if (thisLine.contains(keyword)) {
				containsAllKeywords = true;
				}
				else {
					containsAllKeywords = false;
					break;
				}
			}
			if (containsAllKeywords) {
				System.out.println(thisLine);
			}
		}

	}
	

	private static void printTimeDifference() {
		Date testStartTime = null;
		Date testEndTime = null;
		
		while(logFile.hasNextLine()) {
			
			String thisLine=logFile.nextLine();
			if (testStartTime == null) {
				testStartTime = getDateFromLine(thisLine, TEST_START);
			} 
			else {
				testEndTime=getDateFromLine(thisLine, TEST_END);
				if (testEndTime!= null) {
					long testDuration = testEndTime.getTime() - testStartTime.getTime();
					System.out.println("Test duration in ms: " + testDuration);
					break;
				}
			}
		}
	}

	private static Date getDateFromLine(String line, String searchedString) {
		if (line.contains(searchedString)) {
			try {
				Date date = timeFormat.parse(line);
				return date;
			} catch (ParseException e) {
				System.out.println("Wrong date format of the log file. Expected " + timeFormat.toPattern());
			}
			
		}
		return null;
	}

}
