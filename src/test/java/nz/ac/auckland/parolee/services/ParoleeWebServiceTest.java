package nz.ac.auckland.parolee.services;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

import nz.ac.auckland.parolee.domain.Address;
import nz.ac.auckland.parolee.domain.CriminalProfile;
import nz.ac.auckland.parolee.domain.CriminalProfile.Offence;
import nz.ac.auckland.parolee.domain.Curfew;
import nz.ac.auckland.parolee.domain.Gender;
import nz.ac.auckland.parolee.domain.GeoPosition;
import nz.ac.auckland.parolee.domain.Movement;
import nz.ac.auckland.parolee.dto.Parolee;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ParoleeWebServiceTest {
	private static final String WEB_SERVICE_URI = "http://localhost:10000/services/parolees";

	private static Client _client;

	/**
	 * One-time setup method that creates a Web service client.
	 */
	@BeforeClass
	public static void setUpClient() {
		_client = ClientBuilder.newClient();
	}

	/**
	 * Runs before each unit test restore Web service database. This ensures
	 * that each test is independent; each test runs on a Web service that has
	 * been initialised with a common set of Parolees.
	 */
	@Before
	public void reloadServerData() {
		Response response = _client
				.target(WEB_SERVICE_URI).request()
				.put(null);
		response.close();

		// Pause briefly before running any tests. Test addParoleeMovement(),
		// for example, involves creating a timestamped value (a movement) and
		// having the Web service compare it with data just generated with 
		// timestamps. Joda's Datetime class has only millisecond precision, 
		// so pause so that test-generated timestamps are actually later than 
		// timestamped values held by the Web service.
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * One-time finalisation method that destroys the Web service client.
	 */
	@AfterClass
	public static void destroyClient() {
		_client.close();
	}

	/**
	 * Tests that the Web service can create a new Parolee.
	 */
	@Test
	public void addParolee() {
		Address homeAddress = new Address("34", "Appleby Road", "Remuera",
				"Auckland", "1070");
		Parolee zoran = new Parolee("Salcic", "Zoran", Gender.MALE,
				new LocalDate(1958, 5, 17), homeAddress, null);

		Response response = _client
				.target(WEB_SERVICE_URI).request()
				.post(Entity.xml(zoran));
		if (response.getStatus() != 201) {
			fail("Failed to create new Parolee");
		}

		String location = response.getLocation().toString();
		response.close();

		// Query the Web service for the new Parolee.
		Parolee zoranFromService = _client.target(location).request()
				.accept("application/xml").get(Parolee.class);

		// The original local Parolee object (zoran) should have a value equal
		// to that of the Parolee object representing Zoran that is later
		// queried from the Web service. The only exception is the value
		// returned by getId(), because the Web service assigns this when it
		// creates a Parolee.
		assertEquals(zoran.getLastname(), zoranFromService.getLastname());
		assertEquals(zoran.getFirstname(), zoranFromService.getFirstname());
		assertEquals(zoran.getGender(), zoranFromService.getGender());
		assertEquals(zoran.getDateOfBirth(), zoranFromService.getDateOfBirth());
		assertEquals(zoran.getHomeAddress(), zoranFromService.getHomeAddress());
		assertEquals(zoran.getCurfew(), zoranFromService.getCurfew());
		assertEquals(zoran.getLastKnownPosition(),
				zoranFromService.getLastKnownPosition());
	}

	/**
	 * Tests that the Web serves can process requests to record new Parolee
	 * movements.
	 */
	@Test
	public void addParoleeMovement() {
		Movement newLocation = new Movement(new DateTime(), new GeoPosition(
				-36.848238, 174.762212));

		Response response = _client
				.target(WEB_SERVICE_URI + "/1/movements")
				.request().post(Entity.xml(newLocation));
		if (response.getStatus() != 204) {
			fail("Failed to create new Movement");
		}
		response.close();

		// Query the Web service for the Parolee whose location has been
		// updated.
		Parolee oliver = _client
				.target(WEB_SERVICE_URI + "/1").request()
				.accept("application/xml").get(Parolee.class);
		assertEquals(newLocation, oliver.getLastKnownPosition());
	}

	/**
	 * Tests that the Web service can process Parolee update requests.
	 */
	@Test
	public void updateParolee() {
		final String targetUri = WEB_SERVICE_URI + "/2";

		// Query a Parolee (Catherine) from the Web service.
		Parolee catherine = _client.target(targetUri).request()
				.accept("application/xml").get(Parolee.class);

		Address originalAddress = catherine.getHomeAddress();
		assertNull(catherine.getCurfew());

		// Update soem of Catherine's details.
		Address newAddress = new Address("40", "Clifton Road", "Herne Bay",
				"Auckland", "1022");
		catherine.setHomeAddress(newAddress);
		Curfew newCurfew = new Curfew(newAddress, new LocalTime(21, 00),
				new LocalTime(7, 00));
		catherine.setCurfew(newCurfew);

		Response response = _client.target(targetUri).request()
				.put(Entity.xml(catherine));
		if (response.getStatus() != 204)
			fail("Failed to update Parolee");
		response.close();

		// Requery Parolee from the Web service.
		Parolee updatedCatherine = _client.target(targetUri).request()
				.accept("application/xml").get(Parolee.class);

		// Parolee's home address should have changed.
		assertFalse(originalAddress.equals(updatedCatherine.getHomeAddress()));
		assertEquals(newAddress, updatedCatherine.getHomeAddress());

		// Curfew should now be set.
		assertEquals(newCurfew, updatedCatherine.getCurfew());
	}

	/**
	 * Tests that the Web service can add dissassociates to a Parolee.
	 */
	@Test
	public void updateDissassociates() {
		// Query Parolee Catherine from the Web service.
		Parolee catherine = _client
				.target(WEB_SERVICE_URI + "/2").request()
				.accept("application/xml").get(Parolee.class);

		// Query a second Parolee, Nasser.
		Parolee nasser = _client
				.target(WEB_SERVICE_URI + "/3").request()
				.accept("application/xml").get(Parolee.class);

		// Query Catherines's dissassociates.
		Set<Parolee> dissassociates = _client
				.target(WEB_SERVICE_URI + "/1/dissassociates")
				.request().accept("application/xml")
				.get(new GenericType<Set<Parolee>>() {
				});

		// Catherine should not yet have any recorded dissassociates.
		assertTrue(dissassociates.isEmpty());

		// Request that Nasser is added as a dissassociate to Catherine.
		dissassociates.add(nasser);
		GenericEntity<Set<Parolee>> entity = new GenericEntity<Set<Parolee>>(
				dissassociates) {
		};
		Response response = _client
				.target(WEB_SERVICE_URI + "/1/dissassociates")
				.request().put(Entity.xml(entity));
		if (response.getStatus() != 204)
			fail("Failed to update Parolee");
		response.close();

		// Requery Catherine's dissassociates.
		Set<Parolee> updatedDissassociates = _client
				.target(WEB_SERVICE_URI + "/1/dissassociates")
				.request().accept("application/xml")
				.get(new GenericType<Set<Parolee>>() {
				});
		// The Set of Parolees returned in response to the request for
		// Catherine's dissassociates should contain one object with the same
		// state (value) as the Parolee instance representing Nasser.
		assertTrue(updatedDissassociates.contains(nasser));
		assertEquals(1, updatedDissassociates.size());
	}

	@Test
	public void updateCriminalProfile() {
		final String targetUri = WEB_SERVICE_URI + "/1/criminal-profile";

		CriminalProfile profileForOliver = _client.target(targetUri).request()
				.accept("application/xml").get(CriminalProfile.class);

		// Amend the criminal profile.
		profileForOliver.addConviction(new CriminalProfile.Conviction(
				new LocalDate(), "Shoplifting", Offence.THEFT));

		// Send a Web service request to update the profile.
		Response response = _client.target(targetUri).request()
				.put(Entity.xml(profileForOliver));
		if (response.getStatus() != 204)
			fail("Failed to update CriminalProfile");
		response.close();

		// Requery Oliver's criminal profile.
		CriminalProfile reQueriedProfile = _client.target(targetUri).request()
				.accept("application/xml").get(CriminalProfile.class);

		// The locally updated copy of Oliver's CriminalProfile should have
		// the same value as the updated profile obtained from the Web service.
		assertEquals(profileForOliver, reQueriedProfile);
	}

	/**
	 * Tests that the Web service can handle requests to query a particular
	 * Parolee.
	 */
	@Test
	public void queryParolee() {
		Parolee parolee = _client
				.target(WEB_SERVICE_URI + "/1").request()
				.accept("application/xml").get(Parolee.class);

		assertEquals(1, parolee.getId());
		assertEquals("Sinnen", parolee.getLastname());
	}

	/**
	 * Tests that the Web service processes requests for all Parolees.
	 */
	@Test
	public void queryAllParolees() {
		List<Parolee> parolees = _client
				.target(WEB_SERVICE_URI).request()
				.accept("application/xml")
				.get(new GenericType<List<Parolee>>() {
				});

		assertEquals(3, parolees.size());
	}

	/**
	 * Tests that the Web service can process requests for a particular
	 * Parolee's movements.
	 */
	@Test
	public void queryParoleeMovements() {
		List<Movement> movementsForOliver = _client
				.target(WEB_SERVICE_URI + "/1/movements")
				.request().accept("application/xml")
				.get(new GenericType<List<Movement>>() {
				});

		// Oliver has 3 recorded movements.
		assertEquals(3, movementsForOliver.size());
	}
}
