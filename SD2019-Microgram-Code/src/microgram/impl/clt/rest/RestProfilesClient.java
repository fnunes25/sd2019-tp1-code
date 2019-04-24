package microgram.impl.clt.rest;

import static microgram.api.java.Result.ErrorCode.CONFLICT;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.java.Result.ErrorCode;
import microgram.api.rest.RestProfiles;

//CLASSE POR FAZER
public abstract class RestProfilesClient extends RestClient implements Profiles {

	Map<String,Profile> profiles = new HashMap<String,Profile>();
	
	public RestProfilesClient(URI serverUri) {
		super(serverUri, RestProfiles.PATH);
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		
		//NAO SEI TRABALHAR COM ESTA PORRA
		Response r = target.path(userId)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();
		
		return super.responseContents(r, Status.OK, new GenericType<Profile>() {});
	}
	
	

	/**
	 * Creates a profile
	 * 
	 * @param profile to be created
	 * @return result of (OK,), or CONFLICT
	 */
	public Result<Void> createProfile(Profile profile){
		
		String userId = profile.getUserId();
		//verificar se ja existe no mapa o user
		if(profiles.containsKey(userId)) {
			return Result.error(CONFLICT);
		}else {
			profiles.put(userId, profile);
			return Result.ok();
		}
		
		
	}

	/**
	 * Delete a profile
	 * 
	 * @param userId identifier of the profile to be deleted
	 * @return result of (OK,), or NOT_FOUND
	 */
	public Result<Void> deleteProfile(String userId){
		
		if(profiles.remove(userId) != null) {
			return Result.ok();
		}else {
			return Result.error(NOT_FOUND);
		}
		
	}

	/**
	 * Searches for profiles by prefix of the profile identifier
	 * 
	 * @param prefix - the prefix used to match identifiers
	 * @return result of (OK, List<Profile>); an empty list if the search yields no
	 *         profiles
	 */
	public Result<List<Profile>> search(String prefix){
		List<Profile> profilesPrefix =new ArrayList<Profile>(); 
		for (Map.Entry<String, Profile> entry : profiles.entrySet()) {
			Profile profile = entry.getValue();
		   if (profile.getUserId().startsWith(prefix)) {
			   profilesPrefix.add(profile);
		   }
		}
		return Result.ok(profilesPrefix);
		
		
	}

	/**
	 * Causes a profile to follow or stop following another.
	 * 
	 * @param userId1     the profile that will follow or cease to follow the
	 *                    followed profile
	 * @param userId2     the followed profile
	 * @param isFollowing flag that indicates the desired end status of the
	 *                    operation
	 * @return (OK,), NOT_FOUND if any of the profiles does not exist
	 */
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing){
		Profile profile1 = profiles.get(userId1);
		Profile profile2 = profiles.get(userId2); 
		
		if(profile1 != null && profile2 != null) {
			//NAO TENHO DE TER UMA LISTA DE QUEM SEGUE??
			int following = profile1.getFollowing();
			int followers = profile2.getFollowers();
			if(isFollowing) {
				profile1.setFollowing(following + 1);
				profile2.setFollowers(followers + 1);
			} else {
				profile1.setFollowing(following - 1);
				profile2.setFollowers(followers - 1);
			}
			return Result.ok();
		} else {
			return Result.error(NOT_FOUND);
		}
	}

	/**
	 * Checks if a profile is following another or not
	 * 
	 * @param userId1 the follower profile
	 * @param userId2 the followed profile
	 * @return (OK,Boolean), NOT_FOUND if any of the profiles does not exist
	 */
	public Result<Boolean> isFollowing(String userId1, String userId2){
		//TENHO DE TER UMA LISTA DE FOLLOWERS??
		Profile profile1 = profiles.get(userId1);
		Profile profile2 = profiles.get(userId2); 
		
		if(profile1 != null && profile2 != null) {
			if() {
				
				
			}
			return Result.ok();
		} else {
			return Result.error(NOT_FOUND);
		}
		
	}
	
	
	
	
	
	
	
}
