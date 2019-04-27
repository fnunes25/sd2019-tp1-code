package microgram.impl.clt.rest;

import static microgram.api.java.Result.ErrorCode.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import microgram.api.Post;
import microgram.api.Profile;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.rest.RestPosts;

import microgram.impl.clt.rest.RestProfilesClient;

//CLASSE POR FAZER
public abstract class RestPostsClient extends RestClient implements Posts {

	Map<String, Post> posts = new HashMap<String, Post>();

	public RestPostsClient(URI serverUri) {
		super(serverUri, RestPosts.PATH);
	}

	public Result<String> createPost(Post post) {
		// NAO SEI TRABALHAR COM ESTA PORRA
		Response r = target.request().post(Entity.entity(post, MediaType.APPLICATION_JSON));

		return super.responseContents(r, Status.OK, new GenericType<String>() {
		});
	}

	/**
	 * Requests a post
	 * 
	 * @param postId the unique identifier of the requested post.
	 * @return (OK,Post), or NOT_FOUND
	 */
	public Result<Post> getPost(String postId) {

		Post post = posts.get(postId);
		if (post != null) {
			return Result.ok(post);
		} else {
			return Result.error(NOT_FOUND);
		}

	}

	/**
	 * Deletes a given Post.
	 * 
	 * @param postId the unique identifier of the post to be deleted
	 * @return (OK,), or NOT_FOUND if postId does not match an existing post
	 */
	public Result<Void> deletePost(String postId) {
		Post post = posts.get(postId);
		if (post != null) {
			posts.remove(postId);
			return Result.ok();
		} else {
			return Result.error(NOT_FOUND);
		}

	}

	/**
	 * Adds or removes a like to a post
	 * 
	 * @param postId  the identifier of the post
	 * @param userId  the identifier of the user
	 * @param isLiked a flag with true to add a like, false to remove the like
	 * @return (OK,) if the like was added/removed; NOT_FOUND if either the post or
	 *         the like being removed does not exist, CONFLICT if the like already
	 *         exists.
	 */
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		
		Post post = posts.get(postId);

		if (post != null) {
			if (post.hasLikeOf(userId)) {

				if (isLiked) {
					post.addLike(userId);
				} else {
					post.removeLike(userId);
				}
				return Result.ok();
			} else {
				return Result.error(NOT_FOUND);
			}
		} else {
			return Result.error(NOT_FOUND);
		}

	}

	/**
	 * Determines if a post is liked by a user.
	 * 
	 * @param postId the identifier of the post
	 * @param userId the identifier of the user
	 * @return (OK,Boolean), or NOT_FOUND if there is no Post with the given postId
	 */
	public Result<Boolean> isLiked(String postId, String userId) {
		Post post = posts.get(postId);
		if(post != null) {
		return Result.ok(post.hasLikeOf(userId));
		} else {
			return Result.error(NOT_FOUND);
		}
	}

	/**
	 * Retrieves the list of post identifiers of the posts published by the given
	 * user profile
	 * 
	 * @param userId the user profile that owns the requested posts
	 * @return (OK, List<PostId>|empty list) or NOT_FOUND if the user profile is not
	 *         known
	 */
	public Result<List<String>> getPosts(String userId) {
		List<String> postsFromProfile = new ArrayList<String>();
		// COMO VEJO SE O PERFIL EXISTE???
		// A ESTRUTURA DE DADOS ESTA NOUTRO LADO
		// RestProfilesClient profiles = new RestProfilesClient();

		for (Map.Entry<String, Post> entry : posts.entrySet()) {
			Post post = entry.getValue();
			if (post.getOwnerId().equals(userId)) {
				postsFromProfile.add(post.getPostId());
			}
		}

		return Result.ok(postsFromProfile);

	}

	/**
	 * Returns the feed of the user profile. The feed is the list of Posts made by
	 * user profiles followed by the userId profile.
	 * 
	 * @param userId user profile of the requested feed
	 * @return (OK,List<PostId>|empty list), NOT_FOUND if the user profile is not
	 *         known
	 */
	public Result<List<String>> getFeed(String userId) {
		
		return null;
	}

}
