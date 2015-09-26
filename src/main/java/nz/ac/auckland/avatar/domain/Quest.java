package nz.ac.auckland.avatar.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Entity
public class Quest {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private long _id;

	private String _questName;
	
	private String _questDescription;
	
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