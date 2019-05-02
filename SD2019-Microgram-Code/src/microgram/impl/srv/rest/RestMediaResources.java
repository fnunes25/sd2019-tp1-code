package microgram.impl.srv.rest;

import java.io.IOException;
import javax.ws.rs.WebApplicationException;

import microgram.api.java.Result;
import microgram.api.rest.RestMediaStorage;
import microgram.impl.srv.java.JavaMedia;

public class RestMediaResources extends RestResource implements RestMediaStorage {

	final String baseUri;
	final JavaMedia impl;

	public RestMediaResources(String baseUri) {
		this.baseUri = baseUri + RestMediaStorage.PATH;
		this.impl = new JavaMedia();
	}

	@Override
	public String upload(byte[] bytes) {
		Result<String> result = impl.upload(bytes);
		if (result.isOK())
			return baseUri + "/" + result.value();
		else
			throw new WebApplicationException(super.statusCode(result));
	}

	@Override
	public byte[] download(String id) {
		Result<byte[]> result = impl.download(id);
		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(super.statusCode(result));
	}

	@Override
	public void delete(String id) throws IOException {
		Result<Void> result = impl.delete(id);
		if (!result.isOK())
			throw new WebApplicationException(super.statusCode(result));

	}

}
