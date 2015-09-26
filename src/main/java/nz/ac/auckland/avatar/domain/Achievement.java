package nz.ac.auckland.avatar.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/*
 * Achievements act as trophies, and 
 * are used to display the avatar's progress
 * through the game
 */
@Entity
public class Achievement implements Comparable<Achievement> {
	@Id
	private long _id;

	private String _achievementName;

	private DateTime _timeRecieved;

	protected Achievement() {
		// Required by JAXB for unmarshalling purposes.
	}

	public Achievement(long id, DateTime time, String name) {
		_id = id;
		_timeRecieved = time;
		_achievementName = name;
	}

	public long getId() {
		return _id;
	}

	public void setId(long id) {
		_id = id;
	}

	public DateTime getTimeAchieved() {
		return _timeRecieved;
	}

	public void setTimeAchieved(DateTime time) {
		_timeRecieved = time;
	}

	public String getAchievementName() {
		return _achievementName;
	}

	public void setAchievementName(String name) {
		_achievementName = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Achievement))
			return false;
		if (obj == this)
			return true;

		Achievement ach = (Achievement) obj;
		return new EqualsBuilder().
				append(_id, ach._id).
				append(_achievementName, ach._achievementName).
				append(_timeRecieved, ach._timeRecieved).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
				append(_id).
				append(_achievementName).
				append(_timeRecieved).
				toHashCode();
	}

	@Override
	public int compareTo(Achievement achievement) {
		return _timeRecieved.compareTo(achievement._timeRecieved);
	}

	@Override
	public String toString() {
		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");

		StringBuffer buffer = new StringBuffer();
		buffer.append("Achievement: { [");
		buffer.append(_id);
		buffer.append("]; ");

		if(_achievementName != null) {
			buffer.append(_achievementName);
			buffer.append(", ");
		}
		if(_timeRecieved != null) {
			buffer.append(timeFormatter.print(_timeRecieved));
			buffer.append( " on " );
			buffer.append(dateFormatter.print(_timeRecieved));		
		}


		return buffer.toString();
	}

}