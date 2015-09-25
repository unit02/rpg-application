package nz.ac.auckland.avatar.services;

import nz.ac.auckland.avatar.domain.Avatar;

/**
 * Helper class to convert between domain-model and DTO objects representing
 * Parolees.
 * 
 * @author Ian Warren
 *
 */
public class AvatarMapper {

	static Avatar toDomainModel(nz.ac.auckland.avatar.dto.Avatar dtoavatar) {
		Avatar fullavatar = new Avatar(dtoavatar.getId(),
				dtoavatar.getLastname(),
				dtoavatar.getFirstname(),
				dtoavatar.getGender(),
				dtoavatar.getDateOfBirth(),
				dtoavatar.getHomeAddress(),
				dtoavatar.getCurfew());
		return fullavatar;
	}
	
	static nz.ac.auckland.avatar.dto.Avatar toDto(Avatar avatar) {
		nz.ac.auckland.avatar.dto.Avatar dtoavatar = 
				new nz.ac.auckland.avatar.dto.Avatar(
						avatar.getId(),
						avatar.getLastname(),
						avatar.getFirstname(),
						avatar.getGender(),
						avatar.getDateOfBirth(),
						avatar.getHomeAddress(),
						avatar.getCurfew(),
						avatar.getLastKnownPosition());
		return dtoavatar;
		
	}
}
