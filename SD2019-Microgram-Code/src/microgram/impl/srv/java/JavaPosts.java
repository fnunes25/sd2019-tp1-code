package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.ErrorCode.NOT_IMPLEMENTED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import microgram.api.Post;
import microgram.api.Profile;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.java.Result.ErrorCode;
import utils.Hash;

public class JavaPosts implements Posts {

	protected Map<String, Post> posts = new HashMap<>();
	protected Map<String, Set<String>> likes = new HashMap<>();
	protected Map<String, Set<String>> userPosts = new HashMap<>();

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

		return Result.ok();

	}

	@Override
	public Result<String> createPost(Post post) {
		String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
		if (posts.putIfAbsent(postId, post) == null) {

			likes.put(postId, new HashSet<>());

			Set<String> posts = userPosts.get(post.getOwnerId());
			if (posts == null)
				userPosts.put(post.getOwnerId(), posts = new LinkedHashSet<>());

			posts.add(postId);
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
		
		
		JavaProfiles profiles = new JavaProfiles();
		Profile profile = profiles.users.get(userId);
		if(profile == null) {
			return Result.error(NOT_FOUND);
		}
		
		List<String> feed = new LinkedList<String>();
		for ( String entry : profiles.following.get(profile.getUserId())) {
			feed.addAll(userPosts.get(entry));
		}
		
		return Result.ok(feed);
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
