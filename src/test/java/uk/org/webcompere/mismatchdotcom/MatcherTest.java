package uk.org.webcompere.mismatchdotcom;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.org.webcompere.mismatchdotcom.Matcher;
import uk.org.webcompere.mismatchdotcom.Person;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


/**
 * Tests for the matcher
 */
public class MatcherTest {
	private Matcher matcher;
	
	@Before
	public void before() {
		matcher = new Matcher();
	}
	
	@Test
	public void noMatchesWhenNobodyAvailable() {
		Person person = createTestPerson("Bill", 32, null, null);
		
		List<Person> matches = 	matcher.findMatches(person);
		assertThat(matches.size(), is(0));
	}
	
	@Test
	public void oneMatchWhenIdealMatePresent() {
		Person person = createTestPerson("Bill", 32, null, null);
		Person idealMate = createTestPerson("Belinda", 32, null, null);
		
		matcher.add(idealMate);
		
		List<Person> matches = 	matcher.findMatches(person);
		
		assertThat(matches, contains(idealMate));
	}
	
	@Test
	public void noMatchWhenAgeDifferenceTooGreat() {
		Person person = createTestPerson("Bill", 32, null, null);
		Person other = createTestPerson("Belinda", 18, null, null);
		
		matcher.add(other);
		
		List<Person> matches = 	matcher.findMatches(person);
		
		assertThat(matches.size(), is(0));
	}
	
	@Test
	public void choosesOneMatchFromMultiplePeopleBasedOnAge() {
		Person person = createTestPerson("Bill", 32, null, null);
		Person youngOther = createTestPerson("Jemima", 18, null, null);
		Person sameAgePerson = createTestPerson("Belinda", 32, null, null);
		
		matcher.add(youngOther);
		matcher.add(sameAgePerson);
		
		List<Person> matches = 	matcher.findMatches(person);
		
		assertThat(matches, contains(sameAgePerson));
	}
	
	@Test
	public void ageThresholds() {
		checkAgeMatch(32, 32, true);
		checkAgeMatch(32, 31, true);
		checkAgeMatch(32, 28, true);
		checkAgeMatch(28, 32, true);
		checkAgeMatch(32, 18, false);
		checkAgeMatch(32, 25, false);
		checkAgeMatch(18, 17, true);
		checkAgeMatch(17, 18, true);
		checkAgeMatch(17, 24, false);
		checkAgeMatch(18, 21, true);
	}
	
	@Test
	public void matchesPeopleBasedOnSameLikes() {
		// same likes
		checkLikeMatch(Arrays.asList("wine", "music"), Arrays.asList("wine", "music"), true);
		// same likes, different order
		checkLikeMatch(Arrays.asList("wine", "music"), Arrays.asList("music", "wine"), true);
		
		// no likes in common
		checkLikeMatch(Arrays.asList("wine", "music"), Arrays.asList("dogs", "reading"), false);
		// should be commutative
		checkLikeMatch(Arrays.asList("dogs", "reading"), Arrays.asList("wine", "music"), false);

		// not enough likes in common
		checkLikeMatch(Arrays.asList("wine", "music"), null, false);
		
		// just enough likes in common
		checkLikeMatch(Arrays.asList("wine", "music"), Arrays.asList("dogs", "wine", "music"), true);

		// person two needs more likes from person 1
		checkLikeMatch(Arrays.asList("wine", "music"), Arrays.asList("dogs", "wine", "music", "shipping forecast"), true);
	}
	
	@Test
	public void noMoreThanHalfOfLikesCanBeDislikedByOther() {
		// they both like the same things and person 2 has no dislikes that person 1 likes
		checkDislikeMatch(Arrays.asList("wine", "music"), null, true);

		// person two dislikes one thing that person 1 likes
		checkDislikeMatch(Arrays.asList("wine", "music"), Arrays.asList("shopping"), true);

		// person two dislikes two things that person 1 likes - meaning they hate half of what that person likes
		checkDislikeMatch(Arrays.asList("wine", "music"), Arrays.asList("shopping", "magic"), false);

		// person two dislikes two things that person 1 likes - but that's not half
		checkDislikeMatch(Arrays.asList("wine", "music", "eating in"), Arrays.asList("shopping", "magic"), true);
	}

	private void checkDislikeMatch(List<String> commonLikes, List<String> person2Dislikes, boolean shouldMatch) {
		matcher = new Matcher();
		
		Person person = createTestPerson("Bill", 32, commonLikes, null);
		Person other = createTestPerson("Belinda", 32, commonLikes, null);
		
		if (person2Dislikes!=null) {
			// person likes these things that person 2 dislikes
			person.getLikes().addAll(person2Dislikes);
			
			// other hates these things that person likes
			other.getDislikes().addAll(person2Dislikes);
		}
		
		matcher.add(other);
		
		List<Person> matches = 	matcher.findMatches(person);
		
		assertThat(matches.size(), is(shouldMatch ? 1 : 0));		
	}

	private void checkLikeMatch(List<String> person1Likes, List<String> person2Likes, boolean shouldMatch) {
		matcher = new Matcher();
		
		Person person = createTestPerson("Bill", 32, person1Likes, null);
		Person other = createTestPerson("Belinda", 32, person2Likes, null);
		
		matcher.add(other);
		
		List<Person> matches = 	matcher.findMatches(person);
		
		assertThat(matches.size(), is(shouldMatch ? 1 : 0));		
	}

	private void checkAgeMatch(int manAge, int womanAge, boolean shouldMatch) {
		matcher = new Matcher();
		
		Person person = createTestPerson("Bill", manAge, null, null);
		Person other = createTestPerson("Belinda", womanAge, null, null);
		
		matcher.add(other);
		
		List<Person> matches = 	matcher.findMatches(person);
		
		assertThat(matches.size(), is(shouldMatch ? 1 : 0));
	}

	private Person createTestPerson(String name, int age, 
			List<String> likes,
			List<String> dislikes) {
		Person person = new Person();
		person.setName(name);
		person.setAge(age);
		if (likes!=null)
			person.getLikes().addAll(likes);
		if (dislikes!=null)
			person.getDislikes().addAll(dislikes);
		
		return person;
	}
}
