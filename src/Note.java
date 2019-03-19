import java.io.Serializable;

public class Note implements Serializable {
	
	private static final long serialVersionUID = 8581703854704855954L;
	private String note;
	private int time;
	private int priority;
	
	public Note() {
		note = "";
		time = 0;
		priority = 0;
	}
	
	public Note(String note, int time, int priority) {
		this.note = note;
		this.time = time;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Note(Note n) {
		note = n.note;
		time = n.time;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

}
