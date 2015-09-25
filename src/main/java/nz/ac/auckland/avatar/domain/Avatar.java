package nz.ac.auckland.avatar.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Class to represent a Avatar. A Avatar is described by:
 * - Personal details: lastname, firstname, gender, date-of-birth, home address;
 * - Curfew: any constraints on the Avatar's location;
 * - Criminal profile: criminal history of the Avatar;
 * - Friends: other Avatar who the Avatar is not permitted to be with;
 * - Movements: a timestamped history of where the Avatar has been.
 * 
 * A Avatar is uniquely identified by an id value of type Long.
 * 
 */
@Entity
public class Avatar {
	//@Id
	//generator
	private long _id;
	//@Colummn nullable = false, add many to one etc mapped by
	private String _lastname;
	private String _firstname;
	private Gender _gender;
	private LocalDate _dateOfBirth;
	private Address _homeAddress;
	private Curfew _curfew;
	private Profile _profile;
	private Set<Avatar> _friends;
	private List<Movement> _movements;
	
	
	public Avatar(long id,
			String lastname,
			String firstname,
			Gender gender,
			LocalDate dateOfBirth,
			Address address,
			Curfew curfew) {
		_id = id;
		_lastname = lastname;
		_firstname = firstname;
		_gender = gender;
		_dateOfBirth = dateOfBirth;
		_homeAddress = address;
		_curfew = curfew;
		_friends = new HashSet<Avatar>();
		_movements = new ArrayList<Movement>();
	}
	

	public long getId() {
		return _id;
	}
	
	public void setId(long id) {
		_id = id;
	}
	
	public String getLastname() {
		return _lastname;
	}
	
	public void setLastname(String lastname) {
		_lastname = lastname;
	}
	
	public String getFirstname() {
		return _firstname;
	}
	
	public void setFirstname(String firstname) {
		_firstname = firstname;
	}
	
	public Gender getGender() {
		return _gender;
	}
	
	public void setGender(Gender gender) {
		_gender = gender;
	}
	
	public LocalDate getDateOfBirth() {
		return _dateOfBirth;
	}
	
	public void setDateOfBirth(LocalDate dateOfBirth) {
		_dateOfBirth = dateOfBirth;
	}
	
	public Address getHomeAddress() {
		return _homeAddress;
	}
	
	public void setHomeAddress(Address homeAddress) {
		_homeAddress = homeAddress;
	}
	
	public Curfew getCurfew() {
		return _curfew;
	}
	
	public void setCurfew(Curfew curfew) {
		_curfew = curfew;
	}
	
	public Profile getProfile() {
		return _profile;
	}
	
	public void setCriminalProfile(Profile profile) {
		_profile = profile;
	}
	
	public void addMovement(Movement movement) {
		// Store the new movement.
		_movements.add(movement);
		
		// Ensure that movements are sorted in descending order (i.e. that the
		// most recent movement appears first.
		Collections.sort(_movements, Collections.reverseOrder());
	}
	
	public List<Movement> getMovements() {
		// Returns the Avatar's movements in a read-only collection.
		return Collections.unmodifiableList(_movements);
	}
	
	public Movement getLastKnownPosition() {
		Movement movement = null;
		
		if(!_movements.isEmpty()) {
			movement = _movements.get(0);
		}
		return movement;
	}
	
	public void addFriedn(Avatar parolee) {
		_friends.add(parolee);
	}
	
	public void removeFriend(Avatar parolee) {
		_friends.remove(parolee);
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
		if(_lastname != null) {
			buffer.append(_lastname);
			buffer.append(", ");
		}
		if(_firstname != null) {
			buffer.append(_firstname);
		}
		buffer.append("; ");
		if(_gender != null) {
			buffer.append(_gender);
		}
		buffer.append("; ");
		
		if(_dateOfBirth != null) {
			buffer.append(dOfBFormatter.print(_dateOfBirth));
		}
		buffer.append("\n  ");
		if(_homeAddress != null) {
			buffer.append(_homeAddress);
		}
		
		if(_curfew != null) {
			buffer.append("\n  Curfew from ");
			buffer.append(timeFormatter.print(_curfew.getStartTime()));
			buffer.append(" to ");
			buffer.append(timeFormatter.print(_curfew.getEndTime()));
			buffer.append(" @ ");
			
			if(_homeAddress != null && _homeAddress.equals(_curfew.getConfinementAddress())) {
				buffer.append("home");
			} else {
				buffer.append(_curfew.getConfinementAddress());
			}
		} else {
			buffer.append("No curfew conditions");
		}
		
		buffer.append("\n  ");
		if(_profile != null) {
			buffer.append(_profile);
		} else {
			buffer.append("No criminal profile");
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
				if(friend._lastname != null) {
					buffer.append(friend._lastname);
					buffer.append(", ");
				}
				if(friend._firstname != null) {
					buffer.append(friend._firstname);
				}
				buffer.append(";");
			}
			buffer.deleteCharAt(buffer.length()-1);
		}
		
		if(!_movements.isEmpty()) {
			buffer.append("\n  Last known location: ");
			Movement lastMovement = _movements.get(0);
			buffer.append(lastMovement);
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


