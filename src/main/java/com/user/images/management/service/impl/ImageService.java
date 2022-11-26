package com.user.images.management.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import com.user.images.management.exception.UploadException;
import com.user.images.management.util.CustomResponseHandler;
import com.user.images.management.util.JobUrlLists;
import com.user.images.management.util.ResponseObject;
import com.user.images.management.util.TimeZone;
import com.user.images.management.util.URLToBase64;

@Service
public class ImageService {

	public static final String CLIENT_ID = "88b02dc2c5034e3";
	public static final String IMGUR_URL = "https://api.imgur.com/3/image";

	JobUrlLists jobUrlLists;

	public ImageService(JobUrlLists jobUrlLists) {
		this.jobUrlLists = jobUrlLists;
	}

	public JobUrlLists getJobUrlLists() {
		return jobUrlLists;
	}

	URLToBase64 urlToBase64;

	public void uploadImage(String base64String, String imageLink) throws UploadException, IOException {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPostRequest = new HttpPost(IMGUR_URL);
		httpPostRequest.setHeader("Authorization", "Client-ID " + CLIENT_ID);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("image", base64String));
		CustomResponseHandler customResponseHandler = new CustomResponseHandler();
		int status = -1;
		try {
			httpPostRequest.setEntity(new UrlEncodedFormEntity(params));
			@SuppressWarnings("unchecked")
			ResponseObject responseBody = (ResponseObject) httpClient.execute(httpPostRequest, customResponseHandler);
			// LOGGER.info("----------------------------------------");
			// LOGGER.info(responseBody);

			status = responseBody.getStatusCode();
			if (status >= 200 && status < 300) {
				jobUrlLists.getPending().remove(imageLink);
				jobUrlLists.getCompleted().add(responseBody.getLink());
				if (jobUrlLists.getPending().isEmpty()) {
					TimeZone dateTime = new TimeZone();
					jobUrlLists.setFinished(dateTime.getTimeNow());
					jobUrlLists.setStatus("processed");
				}
				// LOGGER.info("Adding the imgur link for " + imageLink + " to completed
				// list.");
			} else {
				jobUrlLists.getPending().remove(imageLink);
				jobUrlLists.getFailed().add(imageLink);
				// LOGGER.info("Adding the link " + imageLink + " to failed list");
			}

		} catch (IOException e) {
			throw new UploadException(e, status);
		} finally {
			httpClient.close();
		}

	}

}
