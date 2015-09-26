package nz.ac.auckland.avatar.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Class to represent a Avatar. A Avatar is described by:
 * - Personal details: username, firstname, gender, date-of-birth, home address;
 * - Curfew: any constraints on the Avatar's location;
 * - Bag: Holds the info regarding which items the avatar has;
 * - Friends: other Avatar who the Avatar is not permitted to be with;
 * - Achievements: a timestamped history of where the Avatar has been.
 * 
 * A Avatar is uniquely identified by an id value of type Long.
 * 
 */
@Entity
public class Avatar {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private long _id;
	//@Colummn nullable = false, add many to one etc mapped by
	private String _username;
	private Category _category;
	private LocalDate _dateOfBirth;
	private Bag _bag;
	private Set<Avatar> _friends;
	private List<Achievement> _achievements;
	
	private Quest _currentQuest;
	
	
	public Avatar(long id,
			String username,
			Category category,
			LocalDate dateOfBirth,
			Bag bag) {
		_id = id;
		_username = username;
		_category = category;
		_dateOfBirth = dateOfBirth;
		_bag = bag;
		_friends = new HashSet<Avatar>();
		_achievements = new ArrayList<Achievement>();
	}
	
	public Avatar(long id,
			String username,
			Category category,
			LocalDate dateOfBirth,
			Bag bag,
			List<Achievement> achievement) {
		_id = id;
		_username = username;
		_category = category;
		_dateOfBirth = dateOfBirth;
		_bag = bag;
		_achievements = achievement;
	}
	

	public long getId() {
		return _id;
	}
	
	public void setId(long id) {
		_id = id;
	}
	
	public String getUsername() {
		return _username;
	}
	
	public void setUsername(String username) {
		_username = username;
	}
	
	public Category getCategory() {
		return _category;
	}
	
	public void setCategory(Category gender) {
		_category = gender;
	}
	
	public LocalDate getDateOfBirth() {
		return _dateOfBirth;
	}
	
	public void setDateOfBirth(LocalDate dateOfBirth) {
		_dateOfBirth = dateOfBirth;
	}
	
	public void setCurrentQuest(Quest quest){
		_currentQuest = quest;
	}
	
	public Quest getCurrentQuest() {
		return _currentQuest;
	}
	
	public Bag getBag() {
		return _bag;
	}
	
	public void setBag(Bag bag) {
		_bag = bag;
	}
	
	
	public void addAchievement(Achievement achievement) {
		// Store the new achievement.
		_achievements.add(achievement);
		
		// Ensure that achievements are sorted in descending order (i.e. that the
		// most recent achievement appears first.
		Collections.sort(_achievements, Collections.reverseOrder());
	}
	
	public List<Achievement> getAchievements() {
		// Returns the Avatar's achievements in a read-only collection.
		return Collections.unmodifiableList(_achievements);
	}
	
	public Achievement getLatestAchievement(){
		if (_achievements.isEmpty()){
			return _achievements.get(0);
		}
		return null;
	}
	public void addFriend(Avatar avatar) {
		_friends.add(avatar);
	}
	
	public void removeFriend(Avatar avatar) {
		_friends.remove(avatar);
	}
	
	public Set<Avatar> getFriends() {
		return Collections.unmodifiableSet(_friends);
	}
	
	public void updateFriends(Set<Avatar> friends) {
		_friends = friends;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		DateTimeFormatter dOfBFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm");
		
		buffer.append("Avatar: { [");
		buffer.append(_id);
		buffer.append("]; ");
		if(_username != null) {
			buffer.append(_username);
			buffer.append(", ");
		}

		buffer.append("; ");
		if(_category != null) {
			buffer.append(_category);
			}
		buffer.append("; ");
		
		if(_dateOfBirth != null) {
			buffer.append(dOfBFormatter.print(_dateOfBirth));
		}
		
		buffer.append("\n  ");
		if(_bag != null) {
			buffer.append(_bag);
		} else {
			buffer.append("Not holding any items at present");
		}
		
		buffer.append("\n");
		buffer.append("  Friends: ");
		if(_friends.isEmpty()) {
			buffer.append("none");
		} else {
			for(Avatar friend : _friends) {
				buffer.append("[");
				buffer.append(friend._id);
				buffer.append("]");
				buffer.append(" ");
				if(friend._username != null) {
					buffer.append(friend._username);
					buffer.append(", ");
				}
				buffer.append(";");
			}
			buffer.deleteCharAt(buffer.length()-1);
		}
		
		if(!_achievements.isEmpty()) {
			for (Achievement ach : _achievements){
				buffer.append(ach.getAchievementName());
			}
			buffer.append("\n  Last known location: ");
			Achievement lastAchievement = _achievements.get(0);
			buffer.append(lastAchievement);
		}
		
		buffer.append(" }");
		
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Avatar))
            return false;
        if (obj == this)
            return true;

        Avatar other = (Avatar)obj;
        return _id == other._id;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            toHashCode();
	}
}


