package nz.ac.auckland.avatar.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.avatar.domain.Address;
import nz.ac.auckland.avatar.domain.Avatar;
import nz.ac.auckland.avatar.domain.Bag;
import nz.ac.auckland.avatar.domain.Bag.ItemType;
import nz.ac.auckland.avatar.domain.Category;
import nz.ac.auckland.avatar.domain.Curfew;
import nz.ac.auckland.avatar.domain.Profile;
import nz.ac.auckland.avatar.domain.Profile.Offence;
import nz.ac.auckland.avatar.domain.GeoPosition;
import nz.ac.auckland.avatar.domain.Movement;
import nz.ac.auckland.avatar.services.AvatarMapper;
import nz.ac.auckland.avatar.services.AvatarResource;

@Path("/avatars")
public class AvatarResource {
	private static final Logger _logger = LoggerFactory.getLogger(AvatarResource.class);
	
	private Map<Long, Avatar> _AvatarDB;
	private AtomicLong _idCounter;

	public AvatarResource() {
		reloadDatabase();
	}

	/**
    * 
    */
	@PUT
	public void reloadData() {
		reloadDatabase();
	}

	/**
	 * Adds a new Avatar to the system. The state of the new Avatar is
	 * described by a nz.ac.auckland.Avatar.dto.Avatar object.
	 * 
	 * @param dtoAvatar
	 *            the Avatar data included in the HTTP request body.
	 */
	@POST
	@Consumes("application/xml")
	public Response createAvatar(
			nz.ac.auckland.avatar.dto.Avatar dtoAvatar) {
		_logger.debug("Read Avatar: " + dtoAvatar);
		nz.ac.auckland.avatar.domain.Avatar Avatar = AvatarMapper.toDomainModel(dtoAvatar);
		Avatar.setId(_idCounter.incrementAndGet());
		_AvatarDB.put(Avatar.getId(), Avatar);
		
		_logger.debug("Created Avatar: " + Avatar);
		return Response.created(URI.create("/Avatars/" + Avatar.getId()))
				.build();
	}

	/**
	 * Records a new Movement for a particular Avatar.
	 * 
	 * @param id
	 *            the unique identifier of the Avatar.
	 * @param movement
	 *            the timestamped latitude/longitude position of the Avatar.
	 * 
	 */
	@POST
	@Path("{id}/movements")
	@Consumes("application/xml")
	public void createMovementForAvatar(@PathParam("id") long id,
			Movement movement) {
		Avatar Avatar = findAvatar(id);
		Avatar.addMovement(movement);
	}

	/**
	 * Updates an existing Avatar. The parts of a Avatar that can be updated
	 * are those represented by a nz.ac.auckland.Avatar.dto.Avatar
	 * instance.
	 * 
	 * @param dtoAvatar
	 *            the Avatar data included in the HTTP request body.
	 */
	@PUT
	@Path("{id}")
	@Consumes("application/xml")
	public void updateAvatar(
			nz.ac.auckland.avatar.dto.Avatar dtoAvatar) {
		// Get the full Avatar object from the database.
		Avatar Avatar = findAvatar(dtoAvatar.getId());

		// Update the Avatar object in the database based on the data in
		// shortAvatar.
		Avatar.setFirstname(dtoAvatar.getFirstname());
		Avatar.setUsername(dtoAvatar.getUsername());
		Avatar.setCategory(dtoAvatar.getCategory());
		Avatar.setDateOfBirth(dtoAvatar.getDateOfBirth());
		Avatar.setHomeAddress(dtoAvatar.getHomeAddress());
		Avatar.setCurfew(dtoAvatar.getCurfew());
		Avatar.setBag(dtoAvatar.getBag());

		// Ignore the last known location in dtoAvatar (i.e. the data in the
		// HTTP request header).
	}
	
	/**
	 * Updates the set of a dissassociate Avatars for a given Avatar.
	 * @param id the Avatar whose friends should be updated.
	 * @param friends the new set of friends.
	 */
	@PUT
	@Path("{id}/friends")
	@Consumes("application/xml")
	public void updateFriends(@PathParam("id") long id, Set<nz.ac.auckland.avatar.dto.Avatar> friends) {
		// Get the full Avatar object from the database.
		Avatar Avatar = findAvatar(id);
		
		// Lookup the friend Avatar instances in the database.
		Set<Avatar> friendsInDatabase = new HashSet<Avatar>();
		for(nz.ac.auckland.avatar.dto.Avatar dtoAvatar : friends) {
			Avatar friend = findAvatar(dtoAvatar.getId());
			friendsInDatabase.add(friend);
		}
		
		// Update the Avatar by setting its friends.
		Avatar.updateFriends(friendsInDatabase);
	}
	
	/**
	 * Updates a Avatar's CriminalProfile.
	 * @param id the unique identifier of the Avatar.
	 * @param profile the Avatar's updated criminal profile.
	 */
	@PUT
	@Path("{id}/bag")
	@Consumes("application/xml")
	public void updateBag(@PathParam("id") long id, Bag bag) {
		// Get the full Avatar object from the database.
		Avatar Avatar = findAvatar(id);
		
		// Update the Avatar's bag
		Avatar.setBag(bag);
	}

	/**
	 * Returns a particular Avatar. The returned Avatar is represented by a
	 * nz.ac.auckland.Avatar.dto.Avatar object.
	 * 
	 * @param id
	 *            the unique identifier of the Avatar.
	 * 
	 */
	@GET
	@Path("{id}")
	@Produces("application/xml")
	public nz.ac.auckland.avatar.dto.Avatar getAvatar(
			@PathParam("id") long id) {
		// Get the full Avatar object from the database.
		Avatar Avatar = findAvatar(id);

		// Convert the full Avatar to a short Avatar.
		nz.ac.auckland.avatar.dto.Avatar dtoAvatar = AvatarMapper.toDto(Avatar);
		
		return dtoAvatar;
	}

	/**
	 * Returns a view of the Avatar database, represented as a List of
	 * nz.ac.auckland.Avatar.dto.Avatar objects.
	 * 
	 */
	@GET
	@Produces("application/xml")
	public List<nz.ac.auckland.avatar.dto.Avatar> getAvatars() {
		List<nz.ac.auckland.avatar.dto.Avatar> Avatars = new ArrayList<nz.ac.auckland.avatar.dto.Avatar>();

		for (Entry<Long, Avatar> entry : _AvatarDB.entrySet()) {
			Avatars.add(AvatarMapper.toDto(entry.getValue()));
		}
		return Avatars;
	}

	/**
	 * Returns movement history for a particular Avatar.
	 * 
	 * @param id
	 *            the unique identifier of the Avatar.
	 * 
	 */
	@GET
	@Path("{id}/movements")
	@Produces("application/xml")
	public List<Movement> getMovements(@PathParam("id") long id) {
		// Get the full Avatar object from the database.
		Avatar Avatar = findAvatar(id);

		// Return the Avatar's movements.
		return Avatar.getMovements();
	}

	/**
	 * Returns the friends associated directly with a particular Avatar.
	 * Each friend is represented as an instance of class
	 * nz.ac.auckland.Avatar.dto.Avatar.
	 * 
	 * @param id
	 *            the unique identifier of the Avatar.
	 */
	@GET
	@Path("{id}/friends")
	@Produces("application/xml")
	public List<nz.ac.auckland.avatar.dto.Avatar> getAvatarFriends(
			@PathParam("id") long id) {
		// Get the full Avatar object from the database.
		Avatar Avatar = findAvatar(id);

		List<nz.ac.auckland.avatar.dto.Avatar> friends = new ArrayList<nz.ac.auckland.avatar.dto.Avatar>();

		for (Avatar friend : Avatar.getFriends()) {
			friends.add(AvatarMapper.toDto(friend));
		}
		return friends;
	}

	/**
	 * Returns the CriminalProfile for a particular Avatar.
	 * @param id the unique identifier of the Avatar.
	 */
	@GET
	@Path("{id}/bag")
	@Produces("application/xml")
	public Bag getAvatarBag(@PathParam("id") long id) {
		// Get the full Avatar object from the database.
		Avatar Avatar = findAvatar(id);
		
		return Avatar.getBag();
	}
	
	
	protected Avatar findAvatar(long id) {
		return _AvatarDB.get(id);
	}

	protected void reloadDatabase() {
		_AvatarDB = new ConcurrentHashMap<Long, Avatar>();
		_idCounter = new AtomicLong();

		// === Initialise Avatar #1
		long id = _idCounter.incrementAndGet();
		Address address = new Address("15", "Bermuda road", "St Johns", "Auckland", "1071");
		Avatar Avatar = new Avatar(id,
				"ellieille", 
				"Oliver", 
				Category.MAGE,
				new LocalDate(1970, 5, 26),
				address,
				new Curfew(address, new LocalTime(20, 00),new LocalTime(06, 30)),
				null);
		_AvatarDB.put(id, Avatar);

		Bag bag = new Bag();
		
		bag.addItem(new Bag.Item(5, 
				"Able to replenish a small amount of mana", 
				ItemType.MANA_POTION));
		Avatar.setBag(bag);
		
		// === Initialise Avatar #2
		id = _idCounter.incrementAndGet();
		address = new Address("22", "Tarawera Terrace", "St Heliers", "Auckland", "1071");
		Avatar = new Avatar(id,
				"lavitasy", 
				"Catherine", 
				Category.MONK,
				new LocalDate(1970, 2, 9),
				address,
				null, new Bag());
		_AvatarDB.put(id, Avatar);
		
		// === Initialise Avatar #3
		id = _idCounter.incrementAndGet();
		address = new Address("67", "Drayton Gardens", "Oraeki", "Auckland", "1071");
		Avatar = new Avatar(id,
				"i_dont_know", 
				"Nasser", 
				Category.BARBARIAN,
				new LocalDate(1980, 10, 19),
				address,
				null,
				null);
		_AvatarDB.put(id, Avatar);
	}
}
