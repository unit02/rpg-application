package nz.ac.auckland.avatar.services;

import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import nz.ac.auckland.avatar.domain.Achievement;
import nz.ac.auckland.avatar.domain.Avatar;
import nz.ac.auckland.avatar.domain.Bag;
import nz.ac.auckland.avatar.domain.Category;

/**
 * Helper class to convert between domain-model and DTO objects representing
 * Parolees.
 * 
 * @author Ian Warren
 *
 */
public class AvatarMapper {

	static Avatar toDomainModel(nz.ac.auckland.avatar.dto.Avatar dtoavatar) {
		Avatar fullavatar = new Avatar(
				dtoavatar.getId(),
				dtoavatar.getUsername(),
				dtoavatar.getCategory(),
				dtoavatar.getDateOfBirth(),
				dtoavatar.getBag(),
				dtoavatar.getAchievements());
		return fullavatar;
	}
	
	static nz.ac.auckland.avatar.dto.Avatar toDto(Avatar avatar) {
		nz.ac.auckland.avatar.dto.Avatar dtoavatar = 
				new nz.ac.auckland.avatar.dto.Avatar(
						avatar.getId(),
						avatar.getUsername(),
						avatar.getCategory(),
						avatar.getDateOfBirth(),
						avatar.getBag(), 
						avatar.getAchievements());
		return dtoavatar;
		
	}
}
