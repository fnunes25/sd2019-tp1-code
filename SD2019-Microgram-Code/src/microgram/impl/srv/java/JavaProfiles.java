package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import discovery.Discovery;
import microgram.api.Profile;
import microgram.api.java.Result;
import microgram.api.java.Result.ErrorCode;
import microgram.impl.clt.rest.RestPostsClient;
import microgram.impl.clt.rest.RestProfilesClient;
import microgram.impl.srv.rest.PostsRestServer;
import microgram.impl.srv.rest.ProfilesRestServer;
import microgram.impl.srv.rest.RestResource;

public class JavaProfiles extends RestResource implements microgram.api.java.Profiles {

	protected ConcurrentMap<String, Profile> users = new ConcurrentHashMap<>();
	protected ConcurrentMap<String, Set<String>> followers = new ConcurrentHashMap<>();
	protected ConcurrentMap<String, Set<String>> following = new ConcurrentHashMap<>();

	URI[] uri;

	boolean firstTime = true;

	public JavaProfiles() {
		firstTime = false;

	}

	public void findPostsServer() {
		try {
			uri = Discovery.findUrisOf(PostsRestServer.SERVICE, 1);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		if (firstTime == false) {
			findPostsServer();
			firstTime = true;
		}

		Profile res = users.get(userId);
		if (res == null)
			return error(NOT_FOUND);

		RestPostsClient users = new RestPostsClient(uri[0]);

		res.setFollowers(followers.get(userId).size());
		res.setFollowing(following.get(userId).size());
		
		
		try {
			List<String> jjj = users.getPosts(userId).value();
			res.setPosts(jjj.size());
		} catch (Exception e) {
		}

		return ok(res);
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		Profile res = users.putIfAbsent(profile.getUserId(), profile);
		if (res != null)
			return error(CONFLICT);

		followers.put(profile.getUserId(), new HashSet<>());
		following.put(profile.getUserId(), new HashSet<>());
		return ok();
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		Profile res = users.get(userId);
		if (res == null)
			return error(NOT_FOUND);

		Set<String> listFollowers;
		Set<String> listFollowing;

		users.remove(userId);

		for (Map.Entry<String, Profile> entry : users.entrySet()) {

			listFollowers = followers.get(entry.getKey());
			if (listFollowers.contains(userId)) {
				listFollowers.remove(userId);
				res.setFollowers(listFollowers.size());
			}

			listFollowing = following.get(entry.getKey());
			if (listFollowing.contains(userId)) {
				listFollowing.remove(userId);
				res.setFollowing(listFollowing.size());
			}

		}

		return ok();

	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		return ok(users.values().stream().filter(p -> p.getUserId().startsWith(prefix)).collect(Collectors.toList()));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		Set<String> s1 = following.get(userId1);
		Set<String> s2 = followers.get(userId2);

		if (s1 == null || s2 == null)
			return error(NOT_FOUND);

		if (isFollowing) {
			boolean added1 = s1.add(userId2), added2 = s2.add(userId1);
			if (!added1 || !added2)
				return error(CONFLICT);
		} else {
			boolean removed1 = s1.remove(userId2), removed2 = s2.remove(userId1);
			if (!removed1 || !removed2)
				return error(NOT_FOUND);
		}
		return ok();
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {

		Set<String> s1 = following.get(userId1);
		Set<String> s2 = followers.get(userId2);

		if (s1 == null || s2 == null)
			return error(NOT_FOUND);
		else
			return ok(s1.contains(userId2) && s2.contains(userId1));
	}

	public Result<Set<String>> getAllFollowers(String userId) {

		return ok(following.get(userId));
	}
}
