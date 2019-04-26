package microgram.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user Profile
 * 
 * A user Profile has an unique userId; a comprises: the user's full name; and,
 * a photo, stored at some photourl. This information is immutable. The profile
 * also gathers the user's statistics: ie., the number of posts made, the number
 * of profiles the user is following, the number of profiles following this
 * user. All these are mutable.
 * 
 * @author smd
 *
 */
public class Profile {

	String userId;
	String fullName;
	String photoUrl;

	int posts;
	int following;
	int followers;
	List<Profile> allFollowing;
	List<Profile> allFollowers;

	public Profile() {
	}

	public Profile(String userId, String fullName, String photoUrl, int posts, int following, int followers) {
		this.userId = userId;
		this.fullName = fullName;
		this.photoUrl = photoUrl;
		this.posts = posts;
		this.following = following;
		this.followers = followers;
		allFollowing = new ArrayList<Profile>();
		allFollowers = new ArrayList<Profile>();

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public int getPosts() {
		return posts;
	}

	public void setPosts(int posts) {
		this.posts = posts;
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	// NOVO
	public void startFollowing(Profile profile) {
		allFollowing.add(profile);
	}

	// NOVO
	public void newFollower(Profile profile) {
		allFollowers.add(profile);
	}

	// NOVO
	public void stopFollowing(Profile profile) {
		allFollowing.remove(profile);
	}

	// NOVO
	public void loseFollower(Profile profile) {
		allFollowers.remove(profile);
	}

	// NOVO
	public List<Profile> allFollowers() {
		return allFollowers;
	}
	
	// NOVO
	public List<Profile> allFollowing() {
		return allFollowing;
	}
	
	//NOVO
	public boolean isFolowing(Profile profile) {
		return allFollowers.contains(profile);
	}

}
