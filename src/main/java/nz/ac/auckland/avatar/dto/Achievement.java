package nz.ac.auckland.avatar.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


@XmlRootElement(name="achievement")
@XmlAccessorType(XmlAccessType.FIELD)
public class Achievement {

		@XmlAttribute(name="id")
		private long _id;
		
		@XmlElement(name="achievement-name")
		private String _achievementName;
		
		@XmlElement(name="time-recieved")
		private DateTime _timeRecieved;
		
		@XmlElement(name="quest-id")
		private long _questId;
		
		protected Achievement() {
		}
		
		/**
		 * Constructs a DTO quest instance.
	     *
		 */
		public Achievement(DateTime time,
				String achievementName
				) throws IllegalArgumentException {
			this(0,time,achievementName);
		}
		
		/**
		 * Constructs a DTO quest instance. This method should NOT be called by 
		 * Web Service clients. It is intended to be used by the Web Service 
		 * implementation when creating a DTO Avatar from a domain-model Avatar 
		 * object.
		 */
		public Achievement(long id,
				DateTime time,
				String achievementName) {
			_id = id;
			_timeRecieved = time;
			_achievementName = achievementName;
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

	        Achievement achievement = (Achievement)obj;
	        return _id == achievement._id;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 31). 
		            append(_id).
		            toHashCode();
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