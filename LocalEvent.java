package application;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class LocalEvent {

	private String description;
	private LocalDate date;
	private boolean completed = false;
	private boolean nest = false;
	private int nestedLvl;

	public String getDescription() {
		return description;
	}

	public boolean getCompleted(boolean b) {
		return this.completed;
	}

	public boolean getCompleted() {
		return this.completed;
	}

	public int getNestedLvl() {
		return this.nestedLvl;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void setNest(boolean nest) {
		if (!nest) {
			this.nest = false;
		}
		if (nest) {
			this.nest = true;
		}

	}

	public LocalEvent(LocalDate date, String description) {
		this.setDate(date);
		this.setDescription(description);

	}

	@Override
	public String toString() {
		String baseStr = "At " + this.getDate() + ": " + this.getDescription();
		String nested = "    \u221f" + baseStr;
		if (nest) {
			baseStr = nested;
			this.nestedLvl += 1;
		}
		if (!nest && baseStr.startsWith("    \u221f")) {
			baseStr = baseStr.substring(5);
		}

		if (!completed && baseStr.startsWith(" [\u2713] ")) {
			baseStr = baseStr.substring(5);
		}

		return completed ? " [\u2713] " + baseStr : baseStr;
	}

}