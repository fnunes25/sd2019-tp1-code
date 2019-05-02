package microgram.impl.clt.java;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;

//CLASS POR FAZER

public class RetryPostsClient extends RetryClient implements Posts {

	final Posts impl;

	public RetryPostsClient(Posts impl) {
		this.impl = impl;
	}

	@Override
	public Result<Post> getPost(String postId) {
		return reTry(() -> impl.getPost(postId));
	}

	@Override
	public Result<String> createPost(Post post) {
		return reTry(() -> impl.createPost(post));
	}

	@Override
	public Result<Void> deletePost(String postId) {
		return reTry(() -> impl.deletePost(postId));
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		return reTry(() -> impl.deletePost(postId));
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		return reTry(() -> impl.isLiked(postId, userId));
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		return reTry(() -> impl.getPosts(userId));
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		return reTry(() -> impl.getFeed(userId));
	}

}
