import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import net.didion.jwnl.JWNLException;

/**
 * Main chatbot function. Run file to start.
 */
public class Chatbot {
	Response Root = new Response("Please type your question below:\n", new String[0]);
	
	ArrayList<Response> saved_nodes = new ArrayList<Response>();
	ArrayList<String> saved_nodes_id = new ArrayList<String>();
	ArrayList<Flow> flows = new ArrayList<Flow>();
	ArrayList<String> flowID = new ArrayList<String>();

	GUI gui;
	Dict spellCheck;

	private boolean foundPotentialPath, flowComplete;
	private int flowMarker;
	private ArrayList<String> flowInputs = new ArrayList<String>();
	private Response current_node, potentialPath;

	public Chatbot() {
		current_node = Root;
		initializeFlows();
		initializeResponses();
		try {
			spellCheck = new Dict();
		} catch (JWNLException | InterruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary files not found");
			JOptionPane.showMessageDialog(null, "Dictionary files not found", "Load Error", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		}
		
		gui = new GUI(this);
		gui.sendMessage("Hello, welcome to VR/AR support. Please type your question below:\n");
		
		potentialPath = Root;
		foundPotentialPath = false;
		flowMarker = -1;
		flowComplete = false;
	}
	/**
	 * Resets Chatbot to initial state
	 */
	public void reset() {
		current_node = Root;

		potentialPath = Root;
		foundPotentialPath = false;
		flowMarker = -1;
		flowComplete = false;

		gui.sendMessage("Program has been reset. Please type your question below:\n");
	}
	
	/**
	 * Takes user input from GUI and processes it
	 * @param userInput
	 */
	public void processInput(String userInput){	
		// If current node has a Flow, execute it
		if(current_node.hasFlow() && !flowComplete) processFlow(userInput);
		else if (flowComplete) {
			flowComplete = false;			
			if (current_node.getFlow().title.equals("Review")) {
				gui.userinput.setEditable(false);
				gui.userinput.setText("Agent saving review...");
				current_node.getFlow().saveInputs(flowInputs);
				gui.sendMessage("Review saved successfully. Thank you for your feedback!");
			}		

			if (selectPath(current_node, userInput, 0) == null) { // No more paths, back to Root
				current_node = Root;
				gui.sendMessage("Thanks for using our support bot. The end of this branch has been reached. Returning to top.");
				gui.sendMessage(current_node.getMessage());	
			} else {
				current_node = current_node.getChildren().get(0); // Proceed to first child after Flow end (If there is one)
				gui.sendMessage(current_node.getMessage());	
			}
			System.out.println("Flow inputs: ");
			for (String x: flowInputs) {
				System.out.print(x + "|");
			}
		} else {			
			// Remembers starting node to check for loop
			Response starting_node = current_node;

			try { // Spelling check
				userInput = spellCheck.extendSentence(userInput);
				System.out.println(userInput);
			} catch (FileNotFoundException | JWNLException | InterruptedException e) {
				e.printStackTrace();
			}

			if (potentialPath != Root) { // If previous input resulted in potentialPath, check if user says yes or no
				if (userInput.toLowerCase().contains("yes")) {
					gui.sendMessage("Thanks for letting me know. Changing branch...");
					current_node = potentialPath;
				} else {
					gui.sendMessage("Thanks for letting me know. Can you please try me again with different words?");
				}
				potentialPath = Root;
			} else { // Sends corrected spelling sentence to selectPath()
				current_node = selectPath(current_node, userInput, 0);
				// If program cannot find match in children of current node
				if (starting_node == current_node) {
					// Checks Root node for matches and suggests to user
					potentialPath = selectPath(Root, userInput, 0);
					if (potentialPath != Root) {
						gui.sendMessage("I'm sorry, I don't recognise your input for the current branch. Did you want information on \"" + potentialPath.getTitle() + "\" instead?\n");
						foundPotentialPath = true;
					} else { // If cannot find match in Root node, asks user to try again
						gui.sendMessage("I'm sorry, I don't recognise your input for the current branch. Can you try me again with different words?");
					}
				}
			}
			if (!foundPotentialPath) { // print current node message
				gui.sendMessage(current_node.getMessage() + "\n");
			}
			foundPotentialPath = false;

			if (current_node.isGhost()) { // If current node is a ghost node, do not update current node.
				current_node = starting_node;
			} else if (current_node.hasFlow()) {
				processFlow(userInput);
			} else if (selectPath(current_node, userInput, 0) == null) { // No more paths, back to Root
				current_node = Root;
				gui.sendMessage("Thanks for using our support bot. The end of this branch has been reached. Returning to top.");
				gui.sendMessage(current_node.getMessage());
			}
		}
	}
	/**
	 * Sub-method for processInput. Handles processing of Flows when encountered.
	 * @param userInput User input from GUI
	 */
	private void processFlow(String userInput) {
		Flow currentFlow = current_node.getFlow();
		if (flowMarker == -1) { // Sends initial sentence
			gui.sendMessage(currentFlow.responses.get(0) + "\n");
			flowMarker++;
		} else if (!currentFlow.matches(flowMarker, userInput)) { // Checks if userInput matches pattern				
			gui.sendMessage("Hmm. The input you have entered doesn't seem to be right. Please try again.\n");
		} else {
			flowMarker++;
			gui.sendMessage(currentFlow.responses.get(flowMarker) + "\n");
			flowInputs.add(userInput);
		} 
		
		if (flowMarker >= currentFlow.pattern_format.size()) { // Checks if end of flow has been reached
			flowMarker = -1;
			flowComplete = true;
			processInput(null);
		}
	}
	/**
	 * Sub-method for processInput. Handles branch selection of chatbot.
	 * Takes current node and user input and checks all children nodes of current node for matches
	 * @return Matching response node
	 */
	Response selectPath(Response current_node, String userInput, int depth) {
		Response starting_node = current_node; // Prevents infinite loop - Part 1

		if (!current_node.getChildren().isEmpty()) {
			ArrayList<Response> subResponse = current_node.getChildren();
			int maxMatchSubResponse = 0;
			
			if (depth == 0) { // If current iteration is checking direct children (Search all nodes regardless of cutoff)
				for(int i=0; i<subResponse.size(); i++){
					int checkNum = subResponse.get(i).check(userInput);
					if(maxMatchSubResponse < checkNum) {
						maxMatchSubResponse = checkNum;
						current_node = subResponse.get(i);
					}
				}
			} else { // If current iteration is checking children of children (Search only nodes that doesn't have cutoff)
				for(int i=0; i<subResponse.size(); i++){
					if (!subResponse.get(i).isCutoff()) {
						int checkNum = subResponse.get(i).check(userInput);
						if(maxMatchSubResponse < checkNum) {
							maxMatchSubResponse = checkNum;
							current_node = subResponse.get(i);
						}
					}					
				}
			}
			if (starting_node != current_node) { // Prevents infinite loop - Part 2 - Checks if current_node has changed before recursive call
				current_node = selectPath(current_node, userInput, depth+1);
			}
		} else if (depth == 0) { // Return null if end of tree			
			return null;			
		}
		return current_node;
	}
	
	/**
	 * Reads text file and initializes Flow objects to later be used in creating Response objects
	 */
	private void initializeFlows() {
		InputStream path = getClass().getClassLoader().getResourceAsStream("Resources/Flows.txt");
		if (path == null) {
			System.out.println("Flows file not found");
			JOptionPane.showMessageDialog(null, "Flows file not found", "Load Error", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		}
		BufferedReader read = new BufferedReader(new InputStreamReader(path));
		String title = "", lastline;
		
		try {
			lastline = read.readLine();
			while (true) {
				ArrayList<String> responses = new ArrayList<String>();
				ArrayList<String> patterns = new ArrayList<String>();
				ArrayList<String> responseID = new ArrayList<String>();
				boolean flipflop = true;
				do {
					if (lastline.contains("~~~")) {
						title = lastline.split("~~~")[1];
					} else {
						if (flipflop) {
							String[] temp = lastline.trim().split("\\|");
							responseID.add(temp[0]);
							responses.add(temp[1]);
							//System.out.println("R: " + lastline);
							flipflop = false;
						}
						else {
							patterns.add(lastline.trim());
							//System.out.println("P: " + lastline);
							flipflop = true;
						}
					}
					lastline = read.readLine();
				} while (lastline != null && !lastline.contains("~~~"));

				flows.add(new Flow(responseID, responses, patterns, title));
				flowID.add(title);
				if (lastline == null) break;
			}
			
			read.close();
		} catch (IOException e) {
			System.out.println("Fatal Error - IO Exception");
			JOptionPane.showMessageDialog(null, "IO Exception", "Fatal Error", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Load Error - Flows File Format Inconsistency");
			JOptionPane.showMessageDialog(null, "Flows File Format Inconsistency", "Load Error", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * Reads text file and creates Response objects for use with Chatbot
	 */
	void initializeResponses() {
		System.out.println(getClass().getClassLoader().getResource("Dictionaries").getFile());
		int lineError = 0;	
		String whole = null;	
		try {// Main method. Recursively calls down to implement parent-child tree
			InputStream path = getClass().getClassLoader().getResourceAsStream("Resources/Responses.txt");
			if (path == null) {
				System.out.println("Responses file not found");
				JOptionPane.showMessageDialog(null, "Responses file not found", "Load Error", JOptionPane.INFORMATION_MESSAGE);
				System.exit(1);
			}
            BufferedReader read = new BufferedReader(new InputStreamReader(path));
            
			while(true) {
                whole = read.readLine();
				lineError++;
                if (whole == null) break;
                String[] parts = whole.split("\\|");
				initializeResponses(parts[0], parts[1].toLowerCase().split(","), parts[2], Root);
			}
			read.close();
		} catch (IOException e) {
			System.out.println("Fatal Error - IO Exception");
			JOptionPane.showMessageDialog(null, "IO Exception", "Fatal Error", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Load Error - Response File Format Inconsistency");
			JOptionPane.showMessageDialog(null, "Response File Format Inconsistency: Line " + lineError + "\n" + whole.substring(0, 10), "Load Error", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}	
	}
	// Recursive call method
	private void initializeResponses(String metadata, String[] triggers, String message, Response current_parent) {
		if (metadata.charAt(0) == '\t') {
			initializeResponses(metadata.substring(1), triggers, message, current_parent.getChildren().get(current_parent.getChildren().size() - 1));
		} else {
			// If is connector node, add previously saved reference as child, else create new node.
			if (metadata.charAt(metadata.length() - 1) == '>') {
				current_parent.addChild(saved_nodes.get(saved_nodes_id.indexOf(message)));				
			} else {
				Response newNode = new Response(message, triggers);

				// Gets index of modifier character
				char[] modifierChars = {'!', '#', '&'};
				int modIndex = 0;
				for (char x: modifierChars) {
					int index = metadata.indexOf(x);
					if (index != -1) {
						modIndex = metadata.indexOf(x);
						break;
					}
				}
				// Checks to see if node should be saved for future reference + adds title to node if present
				if (metadata.length() > 1) {
					String saveReference = metadata.substring(0, modIndex);					
					if (saveReference.length() != 0) {
						saved_nodes.add(newNode);
						saved_nodes_id.add(saveReference);
					}

					String saveTitle = metadata.substring(modIndex + 1, metadata.contains("~") ? metadata.indexOf("~") : metadata.length());
					if (saveTitle.length() != -1) {
						newNode.setTitle(saveTitle);
					}

					if (metadata.contains("~")) {
						String saveFlow = metadata.substring(metadata.indexOf("~") + 1);
						newNode.assignFlow(flows.get(flowID.indexOf(saveFlow)));
					}
					
				}

				// Sets response object flags
				if (metadata.contains("#")) newNode.setCutoff();
				if (metadata.contains("&")) newNode.setGhost();
				
				current_parent.addChild(newNode);
			}
		}
	}
}
