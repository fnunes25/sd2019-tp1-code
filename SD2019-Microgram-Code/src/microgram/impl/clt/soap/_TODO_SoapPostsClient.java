package microgram.impl.clt.soap;

import java.net.URI;
import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.soap.SoapPosts;

//TODO Make this class concrete
public  class _TODO_SoapPostsClient extends SoapClient implements Posts {

	SoapPosts impl;
	
	public _TODO_SoapPostsClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public Result<Post> getPost(String postId) {
		return super.tryCatchResult(() -> impl().getPost(postId));
	}
	
	
	private SoapPosts impl() {
		if( impl == null ) {
			//TODO
		}
		return impl;
	}

	@Override
	public Result<String> createPost(Post post) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> deletePost(String postId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
}
