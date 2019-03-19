import org.apache.commons.lang3.StringUtils;

public class TrackerAnalyzeNotes {

	private Note notes[];
	private int nbNotes;
	
	public TrackerAnalyzeNotes() {
		notes = new Note[30];
		nbNotes = 0;
	}
	
	public void readNote(String note) {
		String temp = "";
		int timer = 0, pty = 0;;
		double spellBook = 0, ionianBoots = 0, cosmicInsight = 0;
		if (nbNotes < notes.length) {
			for (String d : note.split(" ")) {
				if (StringUtils.isNumeric(d)) {
					pty = Integer.parseInt(d);
				}
				
				if (d.equalsIgnoreCase("clear")) {
					nbNotes = 0;
					break;
				} 
				else if (d.contains("delete")) {
					try {
						d = d.replace("delete", "");
						if (nbNotes != 0)
							delete(Integer.parseInt(d) - 1);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
				
				if (d.equalsIgnoreCase("inhib")) {
					temp += "Allied Inhibitor: ";
					timer = 300;
				}
				else if (d.equalsIgnoreCase("einhib")) {
					temp += "Enemy Inhibitor: ";
					timer = 300;
				}
				else if (d.equalsIgnoreCase("raptors")) {
					temp += "Raptors: ";
					timer = 150;
				}
				else if (d.equalsIgnoreCase("wolves")) {
					temp += "Wolves: ";
					timer = 150;
				}
				else if (d.equalsIgnoreCase("krugs")) {
					temp += "Krugs: ";
					timer = 150;
				}
				else if (d.equalsIgnoreCase("gromp")) {
					temp += "Gromp: ";
					timer = 150;
				}
				else if (d.equalsIgnoreCase("Top"))
					temp += "Top ";
				else if (d.equalsIgnoreCase("Jungle") || d.equalsIgnoreCase("jg"))
					temp += "Jungle ";
				else if (d.equalsIgnoreCase("Mid"))
					temp += "Mid ";
				else if (d.equalsIgnoreCase("ADC"))
					temp += "ADC ";
				else if (d.equalsIgnoreCase("Support") || d.equalsIgnoreCase("Supp"))
					temp += "Support ";
				
				if (temp.contains("Top") || temp.contains("Jungle") || temp.contains("Mid") || temp.contains("ADC") || temp.contains("Support")) {
					if (d.equalsIgnoreCase("f")) {
						temp += "Flash: ";
						timer = 300;
					}
					else if (d.equalsIgnoreCase("h")) {
						temp += "Heal: ";
						timer = 240;
					}
					else if (d.equalsIgnoreCase("b")) {
						temp += "Barrier: ";
						timer = 180;
					}
					else if (d.equalsIgnoreCase("c")) {
						temp += "Cleanse: ";
						timer = 210;
					}
					else if (d.equalsIgnoreCase("e")) {
						temp += "Exhaust: ";
						timer = 210;
					}
					else if (d.equalsIgnoreCase("i")) {
						temp += "Ignite: ";
						timer = 210;
					}
					else if (d.equalsIgnoreCase("t")) {
						temp += "Teleport: ";
						timer = 300;
					}
					else if (d.equalsIgnoreCase("sb")) {
						spellBook = 0.15;
					}
					else if (d.equalsIgnoreCase("luc")) {
						ionianBoots = 0.10;
					}
					else if (d.equalsIgnoreCase("cos")) {
						cosmicInsight = 0.05;
					}
				}
			}
			timer *= 1 - spellBook - ionianBoots - cosmicInsight;
			if (temp.length() > 0 && timer > 0) {
				Note tempNote = new Note(temp, timer, pty);
				notes[nbNotes++] = tempNote;
				Main.sendNote(tempNote);
			}
		}
		else {
			return;
		}
	}
	
	public String getNotes() {
		if (nbNotes == 0) {
			return "Use /n to write a new note.";
		}
		String temp = "";
		for (int i = 0; i < nbNotes; i++) {
			temp += "" + notes[i].getNote() + notes[i].getTime() + " - " + (notes[i].getTime() / 60) + "m" + (notes[i].getTime() % 60) + "s" + "\n";
			if (i > 3)
				temp += "\n";
		}
		return temp;
	}
	
	public void minusOne() {
		for (int i = 0; i < nbNotes; i++) {
			notes[i].setTime(notes[i].getTime() - 1);
			if (notes[i].getTime() <= 0) {
				delete(i);
				i--;
			}
		}
	}
	
	public void addNote(Note note) {
		if (nbNotes < notes.length)
			notes[nbNotes++] = new Note(note);
	}
	
	public void delete(int index) {
		for (int i = index; i < nbNotes - 1; i++) {
			notes[i] = notes[i + 1];
		}
		nbNotes--;
	}
	
	public void sortArray() {
	    Note tmp;
	    if (nbNotes > 1) {
		    for (int i = 0; i < nbNotes; i++) {
		        for (int j = 1; j < (nbNotes - i); j++) {
		            if (notes[j - 1].getPriority() < notes[j].getPriority() || (notes[j - 1].getPriority() == notes[j].getPriority()
		            	&& notes[j - 1].getTime() > notes[j].getTime())) {
		                tmp = notes[j - 1];
		                notes[j - 1] = notes[j];
		                notes[j] = tmp;
		            }
		        }
		    }
	    }
	}

	public void clearNotes() {
		nbNotes = 0;
	}

}
