package nz.ac.auckland.parolee.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

import nz.ac.auckland.parolee.domain.Address;
import nz.ac.auckland.parolee.domain.CriminalProfile;
import nz.ac.auckland.parolee.domain.Curfew;
import nz.ac.auckland.parolee.domain.Gender;
import nz.ac.auckland.parolee.domain.GeoPosition;
import nz.ac.auckland.parolee.domain.Movement;
import nz.ac.auckland.parolee.domain.Parolee;
import nz.ac.auckland.parolee.domain.CriminalProfile.Offence;

/**
 * Web service resource implementation for the Parolee application. An instance
 * of this class handles all HTTP requests for the Parolee Web service.
 * 
 * @author Ian Warren
 *
 */
@Path("/parolees")
public class ParoleeResource {
	private static final Logger _logger = LoggerFactory.getLogger(ParoleeResource.class);
	
	private Map<Long, Parolee> _paroleeDB;
	private AtomicLong _idCounter;

	public ParoleeResource() {
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
	 * Adds a new Parolee to the system. The state of the new Parolee is
	 * described by a nz.ac.auckland.parolee.dto.Parolee object.
	 * 
	 * @param dtoParolee
	 *            the Parolee data included in the HTTP request body.
	 */
	@POST
	@Consumes("application/xml")
	public Response createParolee(
			nz.ac.auckland.parolee.dto.Parolee dtoParolee) {
		_logger.debug("Read parolee: " + dtoParolee);
		Parolee parolee = ParoleeMapper.toDomainModel(dtoParolee);
		parolee.setId(_idCounter.incrementAndGet());
		_paroleeDB.put(parolee.getId(), parolee);

		
		_logger.debug("Created parolee: " + parolee);
		return Response.created(URI.create("/parolees/" + parolee.getId()))
				.build();
	}

	/**
	 * Records a new Movement for a particular Parolee.
	 * 
	 * @param id
	 *            the unique identifier of the Parolee.
	 * @param movement
	 *            the timestamped latitude/longitude position of the Parolee.
	 * 
	 */
	@POST
	@Path("{id}/movements")
	@Consumes("application/xml")
	public void createMovementForParolee(@PathParam("id") long id,
			Movement movement) {
		Parolee parolee = findParolee(id);
		parolee.addMovement(movement);
	}

	/**
	 * Updates an existing Parolee. The parts of a Parolee that can be updated
	 * are those represented by a nz.ac.auckland.parolee.dto.Parolee
	 * instance.
	 * 
	 * @param dtoParolee
	 *            the Parolee data included in the HTTP request body.
	 */
	@PUT
	@Path("{id}")
	@Consumes("application/xml")
	public void updateParolee(
			nz.ac.auckland.parolee.dto.Parolee dtoParolee) {
		// Get the full Parolee object from the database.
		Parolee parolee = findParolee(dtoParolee.getId());

		// Update the Parolee object in the database based on the data in
		// shortParolee.
		parolee.setFirstname(dtoParolee.getFirstname());
		parolee.setLastname(dtoParolee.getLastname());
		parolee.setGender(dtoParolee.getGender());
		parolee.setDateOfBirth(dtoParolee.getDateOfBirth());
		parolee.setHomeAddress(dtoParolee.getHomeAddress());
		parolee.setCurfew(dtoParolee.getCurfew());

		// Ignore the last known location in dtoParolee (i.e. the data in the
		// HTTP request header).
	}
	
	/**
	 * Updates the set of a dissassociate Parolees for a given Parolee.
	 * @param id the Parolee whose dissassociates should be updated.
	 * @param dissassociates the new set of dissassociates.
	 */
	@PUT
	@Path("{id}/dissassociates")
	@Consumes("application/xml")
	public void updateDissassociates(@PathParam("id") long id, Set<nz.ac.auckland.parolee.dto.Parolee> dissassociates) {
		// Get the full Parolee object from the database.
		Parolee parolee = findParolee(id);
		
		// Lookup the dissassociate Parolee instances in the database.
		Set<Parolee> dissassociatesInDatabase = new HashSet<Parolee>();
		for(nz.ac.auckland.parolee.dto.Parolee dtoParolee : dissassociates) {
			Parolee dissassociate = findParolee(dtoParolee.getId());
			dissassociatesInDatabase.add(dissassociate);
		}
		
		// Update the Parolee by setting its dissassociates.
		parolee.updateDissassociates(dissassociatesInDatabase);
	}
	
	/**
	 * Updates a Parolee's CriminalProfile.
	 * @param id the unique identifier of the Parolee.
	 * @param profile the Parolee's updated criminal profile.
	 */
	@PUT
	@Path("{id}/criminal-profile")
	@Consumes("application/xml")
	public void updateCriminalProfile(@PathParam("id") long id, CriminalProfile profile) {
		// Get the full Parolee object from the database.
		Parolee parolee = findParolee(id);
		
		// Update the Parolee's criminal profile.
		parolee.setCriminalProfile(profile);
	}

	/**
	 * Returns a particular Parolee. The returned Parolee is represented by a
	 * nz.ac.auckland.parolee.dto.Parolee object.
	 * 
	 * @param id
	 *            the unique identifier of the Parolee.
	 * 
	 */
	@GET
	@Path("{id}")
	@Produces("application/xml")
	public nz.ac.auckland.parolee.dto.Parolee getParolee(
			@PathParam("id") long id) {
		// Get the full Parolee object from the database.
		Parolee parolee = findParolee(id);

		// Convert the full Parolee to a short Parolee.
		nz.ac.auckland.parolee.dto.Parolee dtoParolee = ParoleeMapper.toDto(parolee);
		
		return dtoParolee;
	}

	/**
	 * Returns a view of the Parolee database, represented as a List of
	 * nz.ac.auckland.parolee.dto.Parolee objects.
	 * 
	 */
	@GET
	@Produces("application/xml")
	public List<nz.ac.auckland.parolee.dto.Parolee> getParolees() {
		List<nz.ac.auckland.parolee.dto.Parolee> parolees = new ArrayList<nz.ac.auckland.parolee.dto.Parolee>();

		for (Entry<Long, Parolee> entry : _paroleeDB.entrySet()) {
			parolees.add(ParoleeMapper.toDto(entry.getValue()));
		}
		return parolees;
	}

	/**
	 * Returns movement history for a particular Parolee.
	 * 
	 * @param id
	 *            the unique identifier of the Parolee.
	 * 
	 */
	@GET
	@Path("{id}/movements")
	@Produces("application/xml")
	public List<Movement> getMovements(@PathParam("id") long id) {
		// Get the full Parolee object from the database.
		Parolee parolee = findParolee(id);

		// Return the Parolee's movements.
		return parolee.getMovements();
	}

	/**
	 * Returns the dissassociates associated directly with a particular Parolee.
	 * Each dissassociate is represented as an instance of class
	 * nz.ac.auckland.parolee.dto.Parolee.
	 * 
	 * @param id
	 *            the unique identifier of the Parolee.
	 */
	@GET
	@Path("{id}/dissassociates")
	@Produces("application/xml")
	public List<nz.ac.auckland.parolee.dto.Parolee> getParoleeDissassociates(
			@PathParam("id") long id) {
		// Get the full Parolee object from the database.
		Parolee parolee = findParolee(id);

		List<nz.ac.auckland.parolee.dto.Parolee> dissassociates = new ArrayList<nz.ac.auckland.parolee.dto.Parolee>();

		for (Parolee dissassociate : parolee.getDissassociates()) {
			dissassociates.add(ParoleeMapper.toDto(dissassociate));
		}
		return dissassociates;
	}

	/**
	 * Returns the CriminalProfile for a particular parolee.
	 * @param id the unique identifier of the Parolee.
	 */
	@GET
	@Path("{id}/criminal-profile")
	@Produces("application/xml")
	public CriminalProfile getCriminalProfile(@PathParam("id") long id) {
		// Get the full Parolee object from the database.
		Parolee parolee = findParolee(id);
		
		return parolee.getCriminalProfile();
	}
	
	
	protected Parolee findParolee(long id) {
		return _paroleeDB.get(id);
	}

	protected void reloadDatabase() {
		_paroleeDB = new ConcurrentHashMap<Long, Parolee>();
		_idCounter = new AtomicLong();

		// === Initialise Parolee #1
		long id = _idCounter.incrementAndGet();
		Address address = new Address("15", "Bermuda road", "St Johns", "Auckland", "1071");
		Parolee parolee = new Parolee(id,
				"Sinnen", 
				"Oliver", 
				Gender.MALE,
				new LocalDate(1970, 5, 26),
				address,
				new Curfew(address, new LocalTime(20, 00),new LocalTime(06, 30)));
		_paroleeDB.put(id, parolee);

		CriminalProfile profile = new CriminalProfile();
		profile.addConviction(new CriminalProfile.Conviction(new LocalDate(
				1994, 1, 19), "Crime of passion", Offence.MURDER,
				Offence.POSSESION_OF_OFFENSIVE_WEAPON));
		parolee.setCriminalProfile(profile);

		DateTime now = new DateTime();
		DateTime earlierToday = now.minusHours(1);
		DateTime yesterday = now.minusDays(1);
		GeoPosition position = new GeoPosition(-36.852617, 174.769525);

		parolee.addMovement(new Movement(yesterday, position));
		parolee.addMovement(new Movement(earlierToday, position));
		parolee.addMovement(new Movement(now, position));
		
		// === Initialise Parolee #2
		id = _idCounter.incrementAndGet();
		address = new Address("22", "Tarawera Terrace", "St Heliers", "Auckland", "1071");
		parolee = new Parolee(id,
				"Watson", 
				"Catherine", 
				Gender.FEMALE,
				new LocalDate(1970, 2, 9),
				address,
				null);
		_paroleeDB.put(id, parolee);
		
		// === Initialise Parolee #3
		id = _idCounter.incrementAndGet();
		address = new Address("67", "Drayton Gardens", "Oraeki", "Auckland", "1071");
		parolee = new Parolee(id,
				"Giacaman", 
				"Nasser", 
				Gender.MALE,
				new LocalDate(1980, 10, 19),
				address,
				null);
		_paroleeDB.put(id, parolee);
	}
}
