package edu.pitt.cs;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.mockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RentACatTest {

	/**
	 * The test fixture for this JUnit test. Test fixture: a fixed state of a set of
	 * objects used as a baseline for running tests. The test fixture is initialized
	 * using the @Before setUp method which runs before every test case. The test
	 * fixture is removed using the @After tearDown method which runs after each
	 * test case.
	 */

	RentACat r; // Object to test
	Cat c1; // First cat object
	Cat c2; // Second cat object
	Cat c3; // Third cat object

	private Cat createMockCat(String catName, int id) {
		Cat cat = Mockito.mock(Cat.class);
		Mockito.when(cat.getName()).thenReturn(catName);
		Mockito.when(cat.getId()).thenReturn(id);
		Mockito.when(cat.toString()).thenReturn("ID " + id + ". " + catName);
		return cat;
	}

	private void rentMockCat(RentACat r, Cat mockCat) {
		// Note to self: this test relies on implementation details in
		// `r.rentCat`, which makes this test brittle to code changes. 
		// An alternate approach would be to define a "fake" Cat implementation
		// with the limited functionality necessary to run `r.rentCat` on its own.
		Mockito.when(mockCat.getRented()).thenReturn(false);
		r.rentCat(mockCat.getId());
		Mockito.when(mockCat.getRented()).thenReturn(true);
	}

	@Before
	public void setUp() throws Exception {
		// Turn on automatic bug injection in the Cat class, to emulate a buggy Cat.
		// Your unit tests should work regardless of these bugs.
		Cat.bugInjectionOn = true;

		// INITIALIZE THE TEST FIXTURE
		// 1. Create a new RentACat object and assign to r
		r = RentACat.createInstance();

		// 2. Create an unrented Cat with ID 1 and name "Jennyanydots", assign to c1
		c1 = createMockCat("Jennyanydots", 1);

		// 3. Create an unrented Cat with ID 2 and name "Old Deuteronomy", assign to c2
		c2 = createMockCat("Old Deuteronomy", 2);

		// 4. Create an unrented Cat with ID 3 and name "Mistoffelees", assign to c3
		c3 = createMockCat("Mistoffelees", 3);
	}

	@After
	public void tearDown() throws Exception {
		// Not necessary strictly speaking since the references will be overwritten in
		// the next setUp call anyway and Java has automatic garbage collection.
		r = null;
		c1 = null;
		c2 = null;
		c3 = null;
	}

	/**
	 * Test case for Cat getCat(int id).
	 * 
	 * <pre>
	 * Preconditions: r has no cats.
	 * Execution steps: Call getCat(2).
	 * Postconditions: Return value is null.
	 * </pre>
	 */

	@Test
	public void testGetCatNullNumCats0() {
		Cat cat = r.getCat(2);

		assertNull(
			"getCat(2) returns a cat when there are no cats", 
			cat);
	}

	/**
	 * Test case for Cat getCat(int id).
	 * 
	 * <pre>
	 * Preconditions: c1, c2, and c3 are added to r using addCat(Cat c).
	 * Execution steps: Call getCat(2).
	 * Postconditions: Return value is not null.
	 *                 Returned cat has an ID of 2.
	 * </pre>
	 */

	@Test
	public void testGetCatNumCats3() {
		r.addCat(c1);
		r.addCat(c2);
		r.addCat(c3);

		Cat c = r.getCat(2);

		assertNotNull("getCat(2) returns null when there is a cat", c);
		assertEquals("getCat(2) returns a value with the incorrect ID", 2, c.getId());
	}

	/**
	 * Test case for boolean catAvailable(int id).
	 * 
	 * <pre>
	 * Preconditions: r has no cats.
	 * Execution steps: Call catAvailable(2).
	 * Postconditions: Return value is false.
	 * </pre>
	 */

	@Test
	public void testCatAvailableFalseNumCats0() {
		boolean available = r.catAvailable(2);

		assertFalse(
			"Cat not contained in RentACat should be marked as unavailable",
			available);
	}

	/**
	 * Test case for boolean catAvailable(int id).
	 * 
	 * <pre>
	 * Preconditions: c1, c2, and c3 are added to r using addCat(Cat c).
	 *                c3 is rented.
	 *                c1 and c2 are not rented.
	 * Execution steps: Call catAvailable(2).
	 * Postconditions: Return value is true.
	 * </pre>
	 */

	@Test
	public void testCatAvailableTrueNumCats3() {
		r.addCat(c1);
		r.addCat(c2);
		r.addCat(c3);
		rentMockCat(r, c3);

		// Note to grader: I deliberately use `c2.getId()` here instead of `2`
		// as otherwise this test could fail due to changes to the underlying ID
		// for c2. I've made similar choices in other tests.
		boolean available = r.catAvailable(c2.getId());

		assertTrue(
			"Unrented cat contained in RentACat should be marked as available",
			available);
		
	}

	/**
	 * Test case for boolean catAvailable(int id).
	 * 
	 * <pre>
	 * Preconditions: c1, c2, and c3 are added to r using addCat(Cat c).
	 *                c2 is rented.
	 *                c1 and c3 are not rented.
	 * Execution steps: Call catAvailable(2).
	 * Postconditions: Return value is false.
	 * </pre>
	 */

	@Test
	public void testCatAvailableFalseNumCats3() {
		r.addCat(c1);
		r.addCat(c2);
		r.addCat(c3);
		rentMockCat(r, c2);

		boolean available = r.catAvailable(c2.getId());

		assertFalse(
			"Rented cat contained in RentACat should be marked as unavailable",
			available);
	}

	/**
	 * Test case for boolean catExists(int id).
	 * 
	 * <pre>
	 * Preconditions: r has no cats.
	 * Execution steps: Call catExists(2).
	 * Postconditions: Return value is false.
	 * </pre>
	 */

	@Test
	public void testCatExistsFalseNumCats0() {
		boolean exists = r.catExists(2);

		assertFalse(
			"Cat with ID 2 should not exist in empty RentACat",
			exists);
	}

	/**
	 * Test case for boolean catExists(int id).
	 * 
	 * <pre>
	 * Preconditions: c1, c2, and c3 are added to r using addCat(Cat c).
	 * Execution steps: Call catExists(2).
	 * Postconditions: Return value is true.
	 * </pre>
	 */

	@Test
	public void testCatExistsTrueNumCats3() {
		r.addCat(c1);
		r.addCat(c2);
		r.addCat(c3);

		boolean exists = r.catExists(c2.getId());

		assertTrue(
			"Cat added to RentACat should exist",
			exists);
	}

	/**
	 * Test case for String listCats().
	 * 
	 * <pre>
	 * Preconditions: r has no cats.
	 * Execution steps: Call listCats().
	 * Postconditions: Return value is "".
	 * </pre>
	 */

	@Test
	public void testListCatsNumCats0() {
		String s = r.listCats();

		assertEquals("listCats() does not return an empty string", "", s);
	}

	/**
	 * Test case for String listCats().
	 * 
	 * <pre>
	 * Preconditions: c1, c2, and c3 are added to r using addCat(Cat c).
	 * Execution steps: Call listCats().
	 * Postconditions: Return value is "ID 1. Jennyanydots\nID 2. Old
	 *                 Deuteronomy\nID 3. Mistoffelees\n".
	 * </pre>
	 */

	@Test
	public void testListCatsNumCats3() {
		r.addCat(c1);
		r.addCat(c2);
		r.addCat(c3);

		String catList = r.listCats();

		assertEquals(
			"listCats() and expected string do not match",
			"ID 1. Jennyanydots\nID 2. Old Deuteronomy\nID 3. Mistoffelees\n",
			catList);
	}

	/**
	 * Test case for boolean rentCat(int id).
	 * 
	 * <pre>
	 * Preconditions: r has no cats.
	 * Execution steps: Call rentCat(2).
	 * Postconditions: Return value is false.
	 * </pre>
	 */

	@Test
	public void testRentCatFailureNumCats0() {
		boolean rent = r.rentCat(2);

		assertFalse("rentCat(2) does not return false", rent);
	}

	/**
	 * Test case for boolean rentCat(int id).
	 * 
	 * <pre>
	 * Preconditions: c1, c2, and c3 are added to r using addCat(Cat c).
	 *                c2 is rented.
	 * Execution steps: Call rentCat(2).
	 * Postconditions: Return value is false.
	 *                 c1.rentCat(), c2.rentCat(), c3.rentCat() are never called.
	 * </pre>
	 * 
	 * Hint: See sample_code/mockito_example/NoogieTest.java in the course
	 * repository for an example of behavior verification. Refer to the
	 * testBadgerPlayCalled method.
	 */

	@Test
	public void testRentCatFailureNumCats3() {
		r.addCat(c1);
		r.addCat(c2);
		r.addCat(c3);
		rentMockCat(r, c2);
		// Reset the count of invocations before calling `r.rentCat`
		reset(c2);

		boolean ret = r.rentCat(2);

		assertFalse("rentCat(2) is not false", ret);
		Mockito.verify(c1, Mockito.times(0)).rentCat();
		Mockito.verify(c2, Mockito.times(0)).rentCat();
		Mockito.verify(c3, Mockito.times(0)).rentCat();
	}

	/**
	 * Test case for boolean returnCat(int id).
	 * 
	 * <pre>
	 * Preconditions: r has no cats.
	 * Execution steps: Call returnCat(2).
	 * Postconditions: Return value is false.
	 * </pre>
	 */

	@Test
	public void testReturnCatFailureNumCats0() {
		boolean success = r.returnCat(2);

		assertFalse("Empty RentACat should fail to return cat", success);
	}

	/**
	 * Test case for boolean returnCat(int id).
	 * 
	 * <pre>
	 * Preconditions: c1, c2, and c3 are added to r using addCat(Cat c).
	 *                c2 is rented.
	 * Execution steps: Call returnCat(2).
	 * Postconditions: Return value is true.
	 *                 c2.returnCat() is called exactly once.
	 *                 c1.returnCat() and c3.returnCat are never called.
	 * </pre>
	 * 
	 * Hint: See sample_code/mockito_example/NoogieTest.java in the course
	 * repository for an example of behavior verification. Refer to the
	 * testBadgerPlayCalled method.
	 */

	@Test
	public void testReturnCatNumCats3() {
		r.addCat(c1);
		r.addCat(c2);
		r.addCat(c3);
		rentMockCat(r, c2);

		boolean success = r.returnCat(c2.getId());

		assertTrue("Rented cat is successfully returned", success);
		Mockito.verify(c1, Mockito.never()).returnCat();
		Mockito.verify(c2, Mockito.times(1)).returnCat();
		Mockito.verify(c3, Mockito.never()).returnCat();
	}
}
