package com.user.images.management.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

@SuppressWarnings("rawtypes")
public class CustomResponseHandler implements ResponseHandler {

	@Override
	public ResponseObject handleResponse(final HttpResponse response) throws IOException {
		int status = response.getStatusLine().getStatusCode();
		ResponseObject rspObject = new ResponseObject();
		rspObject.setStatusCode(status);

		if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			Header headerEncoding = response.getEntity().getContentEncoding();

			Charset enocodedCharset = (headerEncoding == null) ? StandardCharsets.UTF_8
					: Charsets.toCharset(headerEncoding.toString());
			String jsonResponse = EntityUtils.toString(entity, enocodedCharset);
			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONObject dataObject = (JSONObject) jsonObject.get("data");
			rspObject.setLink((String) dataObject.get("link"));
			rspObject.setId((String) dataObject.get("id"));
			rspObject.setImgType((String) dataObject.get("type"));

		}

		return rspObject;
	}

}
