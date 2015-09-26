package nz.ac.auckland.avatar.services;

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

import nz.ac.auckland.avatar.domain.Address;
import nz.ac.auckland.avatar.domain.Bag;
import nz.ac.auckland.avatar.domain.Curfew;
import nz.ac.auckland.avatar.domain.Profile;
import nz.ac.auckland.avatar.domain.Profile.Offence;
import nz.ac.auckland.avatar.domain.Category;
import nz.ac.auckland.avatar.domain.GeoPosition;
import nz.ac.auckland.avatar.domain.Movement;
import nz.ac.auckland.avatar.dto.Avatar;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AvatarWebServiceTest {
	private static final String WEB_SERVICE_URI = "http://localhost:8080/services/avatars";

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
	 * been initialised with a common set of Avatars.
	 */
	@Before
	public void reloadServerData() {
		Response response = _client
				.target(WEB_SERVICE_URI).request()
				.put(null);
		response.close();

		// Pause briefly before running any tests. Test addAvatarMovement(),
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
	 * Tests that the Web service can create a new Avatar.
	 */
	@Test
	public void addAvatar() {
		Address homeAddress = new Address("34", "Appleby Road", "Remuera",
				"Auckland", "1070");
		Avatar zoran = new Avatar("xXxmobixXx", "Zoran", Category.ROUGE,
				new LocalDate(1958, 5, 17), homeAddress, null, new Bag());

		Response response = _client
				.target(WEB_SERVICE_URI).request()
				.post(Entity.xml(zoran));
		if (response.getStatus() != 201) {
			fail("Failed to create new Avatar");
		}

		String location = response.getLocation().toString();
		response.close();

		// Query the Web service for the new Avatar.
		Avatar zoranFromService = _client.target(location).request()
				.accept("application/xml").get(Avatar.class);

		// The original local Avatar object (zoran) should have a value equal
		// to that of the Avatar object representing Zoran that is later
		// queried from the Web service. The only exception is the value
		// returned by getId(), because the Web service assigns this when it
		// creates a Avatar.
		assertEquals(zoran.getUsername(), zoranFromService.getUsername());
		assertEquals(zoran.getFirstname(), zoranFromService.getFirstname());
		assertEquals(zoran.getCategory(), zoranFromService.getCategory());
		assertEquals(zoran.getDateOfBirth(), zoranFromService.getDateOfBirth());
		assertEquals(zoran.getHomeAddress(), zoranFromService.getHomeAddress());
		assertEquals(zoran.getCurfew(), zoranFromService.getCurfew());
		assertEquals(zoran.getLastKnownPosition(),
				zoranFromService.getLastKnownPosition());
		assertEquals(zoran.getBag(), zoranFromService.getBag());

	}

//	/**
//	 * Tests that the Web serves can process requests to record new Avatar
//	 * movements.
//	 */
//	@Test
//	public void addAvatarMovement() {
//		Movement newLocation = new Movement(new DateTime(), new GeoPosition(
//				-36.848238, 174.762212));
//
//		Response response = _client
//				.target(WEB_SERVICE_URI + "/1/movements")
//				.request().post(Entity.xml(newLocation));
//		if (response.getStatus() != 204) {
//			fail("Failed to create new Movement");
//		}
//		response.close();
//
//		// Query the Web service for the Avatar whose location has been
//		// updated.
//		Avatar oliver = _client
//				.target(WEB_SERVICE_URI + "/1").request()
//				.accept("application/xml").get(Avatar.class);
//		assertEquals(newLocation, oliver.getLastKnownPosition());
//	}

	/**
	 * Tests that the Web service can process Avatar update requests.
	 */
//	@Test
//	public void updateAvatar() {
//		final String targetUri = WEB_SERVICE_URI + "/2";
//
//		// Query a Avatar (Catherine) from the Web service.
//		Avatar catherine = _client.target(targetUri).request()
//				.accept("application/xml").get(Avatar.class);
//
//		Address originalAddress = catherine.getHomeAddress();
//		assertNull(catherine.getCurfew());
//
//		// Update soem of Catherine's details.
//		Address newAddress = new Address("40", "Clifton Road", "Herne Bay",
//				"Auckland", "1022");
//		catherine.setHomeAddress(newAddress);
//		Curfew newCurfew = new Curfew(newAddress, new LocalTime(21, 00),
//				new LocalTime(7, 00));
//		catherine.setCurfew(newCurfew);
//
//		Response response = _client.target(targetUri).request()
//				.put(Entity.xml(catherine));
//		if (response.getStatus() != 204)
//			fail("Failed to update Avatar");
//		response.close();
//
//		// Requery Avatar from the Web service.
//		Avatar updatedCatherine = _client.target(targetUri).request()
//				.accept("application/xml").get(Avatar.class);
//
//		// Avatar's home address should have changed.
//		assertFalse(originalAddress.equals(updatedCatherine.getHomeAddress()));
//		assertEquals(newAddress, updatedCatherine.getHomeAddress());
//
//		// Curfew should now be set.
//		assertEquals(newCurfew, updatedCatherine.getCurfew());
//	}
//
//	/**
//	 * Tests that the Web service can add friends to a Avatar.
//	 */
//	@Test
//	public void updateFriends() {
//		// Query Avatar Catherine from the Web service.
//		Avatar catherine = _client
//				.target(WEB_SERVICE_URI + "/2").request()
//				.accept("application/xml").get(Avatar.class);
//
//		// Query a second Avatar, Nasser.
//		Avatar nasser = _client
//				.target(WEB_SERVICE_URI + "/3").request()
//				.accept("application/xml").get(Avatar.class);
//
//		// Query Catherines's friends.
//		Set<Avatar> friends = _client
//				.target(WEB_SERVICE_URI + "/1/friends")
//				.request().accept("application/xml")
//				.get(new GenericType<Set<Avatar>>() {
//				});
//
//		// Catherine should not yet have any recorded friends.
//		assertTrue(friends.isEmpty());
//
//		// Request that Nasser is added as a friend to Catherine.
//		friends.add(nasser);
//		GenericEntity<Set<Avatar>> entity = new GenericEntity<Set<Avatar>>(
//				friends) {
//		};
//		Response response = _client
//				.target(WEB_SERVICE_URI + "/1/friends")
//				.request().put(Entity.xml(entity));
//		if (response.getStatus() != 204)
//			fail("Failed to update Avatar");
//		response.close();
//
//		// Requery Catherine's friends.
//		Set<Avatar> updatedFriends = _client
//				.target(WEB_SERVICE_URI + "/1/friends")
//				.request().accept("application/xml")
//				.get(new GenericType<Set<Avatar>>() {
//				});
//		// The Set of Avatars returned in response to the request for
//		// Catherine's friends should contain one object with the same
//		// state (value) as the Avatar instance representing Nasser.
//		assertTrue(updatedFriends.contains(nasser));
//		assertEquals(1, updatedFriends.size());
//	}
//
////	@Test
////	public void updateCriminalProfile() {
////		final String targetUri = WEB_SERVICE_URI + "/1/profile";
////
////		Profile profileForOliver = _client.target(targetUri).request()
////				.accept("application/xml").get(Profile.class);
////
////		// Amend the criminal profile.
////		profileForOliver.addConviction(new Profile.Conviction(
////				new LocalDate(), "Shoplifting", Offence.THEFT));
////
////		// Send a Web service request to update the profile.
////		Response response = _client.target(targetUri).request()
////				.put(Entity.xml(profileForOliver));
////		if (response.getStatus() != 204)
////			fail("Failed to update Profile");
////		response.close();
////
////		// Requery Oliver's criminal profile.
////		Profile reQueriedProfile = _client.target(targetUri).request()
////				.accept("application/xml").get(Profile.class);
////
////		// The locally updated copy of Oliver's CriminalProfile should have
////		// the same value as the updated profile obtained from the Web service.
////		assertEquals(profileForOliver, reQueriedProfile);
////	}
//
//	/**
//	 * Tests that the Web service can handle requests to query a particular
//	 * Avatar.
//	 */
//	@Test
//	public void queryAvatar() {
//		Avatar Avatar = _client
//				.target(WEB_SERVICE_URI + "/1").request()
//				.accept("application/xml").get(Avatar.class);
//
//		assertEquals(1, Avatar.getId());
//		assertEquals("Sinnen", Avatar.getLastname());
//	}
//
//	/**
//	 * Tests that the Web service processes requests for all Avatars.
//	 */
//	@Test
//	public void queryAllAvatars() {
//		List<Avatar> Avatars = _client
//				.target(WEB_SERVICE_URI).request()
//				.accept("application/xml")
//				.get(new GenericType<List<Avatar>>() {
//				});
//
//		assertEquals(3, Avatars.size());
//	}

	/**
	 * Tests that the Web service can process requests for a particular
	 * Avatar's movements.
	 */
//	@Test
//	public void queryAvatarMovements() {
//		List<Movement> movementsForOliver = _client
//				.target(WEB_SERVICE_URI + "/1/movements")
//				.request().accept("application/xml")
//				.get(new GenericType<List<Movement>>() {
//				});
//
//		// Oliver has 3 recorded movements.
//		assertEquals(3, movementsForOliver.size());
//	}
}
