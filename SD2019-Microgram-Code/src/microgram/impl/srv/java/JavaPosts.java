package microgram.impl.srv.java;

import static microgram.api.java.Result.error;

import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.ErrorCode.NOT_IMPLEMENTED;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import discovery.Discovery;
import microgram.api.Post;
import microgram.api.Profile;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.java.Result.ErrorCode;
import microgram.impl.clt.rest.RestProfilesClient;
import microgram.impl.srv.rest.ProfilesRestServer;
import utils.Hash;

public class JavaPosts implements Posts {

	protected ConcurrentMap<String, Post> posts = new ConcurrentHashMap<>();
	protected ConcurrentMap<String, Set<String>> likes = new ConcurrentHashMap<>();
	protected ConcurrentMap<String, Set<String>> userPosts = new ConcurrentHashMap<>();
	URI[] uri;


	public JavaPosts() {
		findProfileServer();

	}

	public void findProfileServer() {
		try {
			uri = Discovery.findUrisOf(ProfilesRestServer.SERVICE, 1);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}
	}

	@Override
	public Result<Post> getPost(String postId) {
		Post res = posts.get(postId);
		if (res != null)
			return ok(res);
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Void> deletePost(String postId) {

		Post post = posts.get(postId);

		if (post == null) {
			return Result.error(NOT_FOUND);
		}

		String owner = post.getOwnerId();
		likes.remove(postId);
		userPosts.get(owner).remove(postId);
		posts.remove(postId);

		Result<Profile> user = new RestProfilesClient(uri[0]).getProfile(post.getOwnerId());

		Profile p = user.value();
		p.setPosts(userPosts.get(owner).size());

		return Result.ok();

	}

	@Override
	public Result<String> createPost(Post post) {
		String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());

		
		RestProfilesClient users = new RestProfilesClient(uri[0]);
		Result<Profile> user =users.getProfile(post.getOwnerId());

		if (!user.isOK()) {
			return Result.error(NOT_FOUND);
		}
		if (posts.putIfAbsent(postId, post) == null) {

			likes.put(postId, new HashSet<>());

			Set<String> postsUser = userPosts.get(post.getOwnerId());
			if (postsUser == null)
				userPosts.put(post.getOwnerId(), postsUser = new LinkedHashSet<>());

			postsUser.add(postId);
			
			//users.deleteProfile(post.getOwnerId());
			//users.createProfile(u);
		
		}
		return ok(postId);

	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		Set<String> res = likes.get(postId);
		if (res == null)
			return error(NOT_FOUND);

		if (isLiked) {
			if (!res.add(userId))
				return error(CONFLICT);
		} else {
			if (!res.remove(userId))
				return error(NOT_FOUND);
		}

		getPost(postId).value().setLikes(res.size());
		return ok();
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		Set<String> res = likes.get(postId);

		if (res != null)
			return ok(res.contains(userId));
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		Set<String> res = userPosts.get(userId);
		if (res != null)
			return ok(new ArrayList<>(res));
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<List<String>> getFeed(String userId) {

		RestProfilesClient users = new RestProfilesClient(uri[0]);

		Result<Profile> user = users.getProfile(userId);

		if (!user.isOK()) {
			return Result.error(NOT_FOUND);
		}

		List<String> feed = new LinkedList<String>();
				
	
		Set<String> followers = users.getAllFollowers(userId).value();
		for(String entry : followers) {
			feed.addAll(userPosts.get(entry));
		}

		return Result.ok(feed);

	}
	
	

}
