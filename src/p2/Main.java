package p2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Main Class
 * 
 * @author Jordi Linares
 *
 */
public class Main {

	/**
	 * Main Code
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		//Graph
		PRGraphImpl myGraph = new PRGraphImpl();
		//Line counter
		int countLine = 0;
		//Exception detected
		boolean exception = false;

		// Checking if the args are correct
		if (args.length != 1) {
			System.out.println("Invalid command syntax: command txtfile");
			System.exit(0);
		}
		BufferedReader in = null;

		// Checking if file is okay
		try {
			in = new BufferedReader(new FileReader(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("Unknown file error");
			System.exit(0);
		}

		String line = "";
		StringTokenizer stringTokenizer;

		// Reading the file
		try {
			while ((line = in.readLine()) != null) {
				line = readLine(line);
				countLine++;
				
				//Ignore blank lines
				if (line.length() == 0)
					continue;
				
				//String tokenizer
				stringTokenizer = new StringTokenizer(line);
				
				//Token counter
				int countTokens = stringTokenizer.countTokens();

				//Possible cases
				switch (stringTokenizer.nextToken()) {
				case "PROCESS": {
					if (countTokens != 2)
						throw new IOException();

					String process = stringTokenizer.nextToken();
					myGraph.addProcess(process);

					break;
				}
				case "RESOURCE": {
					if (countTokens != 2)
						throw new IOException();

					String resource = stringTokenizer.nextToken();
					myGraph.addResource(resource);

					break;
				}
				case "OPEN": {
					if (countTokens != 3)
						throw new IOException();

					String process = stringTokenizer.nextToken();
					String resource = stringTokenizer.nextToken();

					try {
						myGraph.open(process, resource);
					} catch (DeadlockException e) {
						System.out.println("DeadlockException detected at line " + countLine);

						e.printStackTrace();

						exception = true;
					}

					break;
				}
				case "CLOSE": {
					if (countTokens != 3)
						throw new IOException();

					String process = stringTokenizer.nextToken();
					String resource = stringTokenizer.nextToken();
					myGraph.close(process, resource);
					break;
				}
				default:
					throw new IOException();
				}
			}
		} catch (IOException e) {
			exception = true;
			System.out.println("Unexpected value at line " + countLine);
			System.exit(0);
		}

		//Printing the Graph if there's no exceptions
		if (!exception) {
			System.out.println("\nGraph toString: \n\n" + myGraph.toString());
			System.out.println("Simulation ended successfully!");

		}
	}

	// Reads a line and removes comments
	private static String readLine(String line) {
		int pos = line.indexOf('#');
		if (pos != -1)
			line = line.substring(0, pos);
		return line;

	}
}
