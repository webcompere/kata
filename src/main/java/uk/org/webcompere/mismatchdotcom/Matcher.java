package uk.org.webcompere.mismatchdotcom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Matcher {
	private List<Person> people = new ArrayList<Person>();
	
	public List<Person> findMatches(Person person) {
		List<Person> matches = new ArrayList<Person>();
		
		// filter the list down according to whether they're of similar age
		for(int i=0; i<people.size(); i++) {
			int meanAge = (people.get(i).getAge() + person.getAge()) / 2;
			if ((double)Math.abs(person.getAge()-meanAge) / person.getAge() < 0.1 &&
				(double)Math.abs(person.getAge()-meanAge) / person.getAge() < 0.1) {
				matches.add(people.get(i));
			}
		}
		
		// filter the list according to whether people like the same thing
		List<Person> likeMatches = new ArrayList<Person>();
		for(Person p:matches) {
			Set<String> personLikes = new HashSet<String>();
			personLikes.addAll(person.getLikes());
			
			Set<String> pLikes = new HashSet<String>();
			pLikes.addAll(p.getLikes());
			
			personLikes.retainAll(pLikes);
			
			int commonLikes = personLikes.size();
			
			if ((commonLikes == person.getLikes().size() && commonLikes == p.getLikes().size()) ||
				(double)commonLikes / person.getLikes().size() >= 0.5 &&
					(double)commonLikes / p.getLikes().size() >= 0.5) {
				likeMatches.add(p);
			}
		}
		
		// there would be a bad relationship if half or more of someone's likes are disliked by the other
		List<Person> noConflictMatches = new ArrayList<Person>();
		for(Person p:likeMatches) {
			int dislike1 = getDislikesInLikes(p, person);
			int dislike2 = getDislikesInLikes(person, p);
			
			if ((dislike1 == 0 || (double)dislike1 / person.getLikes().size() < 0.5) &&
				(dislike2 == 0 || (double)dislike2 / p.getLikes().size() < 0.5)) {
				noConflictMatches.add(p);
			}
		}
		
		return noConflictMatches;
	}

	private int getDislikesInLikes(Person disliker, Person person) {
		Set<String> dislikes = new HashSet<String>();
		dislikes.addAll(disliker.getDislikes());
		
		Set<String> likes = new HashSet<String>();
		likes.addAll(person.getLikes());
		
		// get the commonality of likes and dislikes
		likes.retainAll(dislikes);
		
		return likes.size();
	}

	public void add(Person person) {
		people.add(person);
	}

}
