import java.util.ArrayList;

public class Response {
	private String title;
	private String message;
	private String[] triggers;
	private ArrayList<Response> children = new ArrayList<Response>();
	private Flow flow = null;
	private boolean cutoff = false;
	private boolean ghost = false;
	
	public Response(String message, String[] triggers) {
		this.message = message;
		this.triggers = triggers;
	}
	
	/**
	 * Checks the user input against an array of key-words assigned to the response
	 * @param user_input String input from the user.
	 * @return Number of matching triggers
	 */
	public int check(String user_input) {
		int matches = 0;
		for (String current: this.triggers) {
			if ((" " + user_input + " ").toLowerCase().contains(current)) {
				matches++;
			}
		}
		return matches;
	}

	// Get-set methods
	public void addChild(Response child) {
		this.children.add(child);
	}	
	public ArrayList<Response> getChildren() {
		return this.children;
	}

	public void assignFlow(Flow flow) {
		this.flow = flow;
	}
	public Flow getFlow() {
		return this.flow;
	}
	public boolean hasFlow() {
		return this.flow != null;
	}
	
	public String getMessage() {
		return this.message;
	}	
	public String[] getTriggers() {
		return this.triggers;
	}

	public boolean isCutoff() {
		return this.cutoff;
	}
	public void setCutoff() {
		this.cutoff = true;
	}

	public boolean isGhost() {
		return this.ghost;
	}
	public void setGhost() {
		this.ghost = true;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
}
