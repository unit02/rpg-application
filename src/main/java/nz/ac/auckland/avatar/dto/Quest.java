package nz.ac.auckland.avatar.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import nz.ac.auckland.avatar.domain.Achievement;
import nz.ac.auckland.avatar.domain.Bag;
import nz.ac.auckland.avatar.domain.Category;
import nz.ac.auckland.parolee.jaxb.LocalDateAdapter;

@XmlRootElement(name="quest")
@XmlAccessorType(XmlAccessType.FIELD)
public class Quest {

		@XmlAttribute(name="id")
		private long _id;
		
		@XmlElement(name="quest-name")
		private String _questName;
		
		@XmlElement(name="quest-description")
		private String _questDescription;
		
		protected Quest() {
		}
		
		/**
		 * Constructs a DTO quest instance.
	     *
		 */
		public Quest(String questName,
				String questDescription
				) throws IllegalArgumentException {
			this(0,questName,questDescription);
		}
		
		/**
		 * Constructs a DTO quest instance. This method should NOT be called by 
		 * Web Service clients. It is intended to be used by the Web Service 
		 * implementation when creating a DTO Avatar from a domain-model Avatar 
		 * object.
		 */
		public Quest(long id,
				String questName,
				String questDescription) {
			_id = id;
			_questName = questName;
			_questDescription = questDescription;
		}
		
		public long getId() {
			return _id;
		}
		
		public void setId(long id) {
			_id = id;
		}
		
		public String getQuestName() {
			return _questName;
		}
		
		public void setQuestName(String questName) {
			_questName = questName;
		}
		
		public String getQuestDescription() {
			return _questDescription;
		}
		
		public void setQuestDescription(String questDescription) {
			_questDescription = questDescription;
		}
		
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
		
			buffer.append("Quest: { [");
			buffer.append(_id);
			buffer.append("]; ");
			if(_questName != null) {
				buffer.append(_questName);
				buffer.append(", ");
			}
			if(_questDescription != null) {
				buffer.append(_questDescription);
			}
			buffer.append(";");
			
			return buffer.toString();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Quest))
	            return false;
	        if (obj == this)
	            return true;

	        Quest quest = (Quest)obj;
	        return _id == quest._id;
		}
		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 31). 
		            append(_id).
		            toHashCode();
		}
	}