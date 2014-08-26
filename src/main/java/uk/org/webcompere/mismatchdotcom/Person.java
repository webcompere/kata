package uk.org.webcompere.mismatchdotcom;

import java.util.ArrayList;
import java.util.List;

/**
 * A person who might be matched
 */
public class Person {
	private String name;
	private List<String> likes = new ArrayList<String>();
	private List<String> dislikes = new ArrayList<String>();
	private int age;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getLikes() {
		return likes;
	}
	public void setLikes(List<String> likes) {
		this.likes = likes;
	}
	public List<String> getDislikes() {
		return dislikes;
	}
	public void setDislikes(List<String> dislikes) {
		this.dislikes = dislikes;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
