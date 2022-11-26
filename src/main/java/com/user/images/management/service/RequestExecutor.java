package com.user.images.management.service;

import java.io.IOException;

import com.user.images.management.exception.UploadException;
import com.user.images.management.service.impl.ImageService;
import com.user.images.management.util.JobUrlLists;
import com.user.images.management.util.URLToBase64;

public class RequestExecutor implements Runnable {

	private String imageLink;
	URLToBase64 urlToBase64;
	ImageService imageService;

	public RequestExecutor(String imageLink, URLToBase64 urlToBase64, ImageService imageService) {
		this.imageLink = imageLink;
		this.imageService = imageService;
		this.urlToBase64 = urlToBase64;
	}

	@Override
	public void run() {

		try {
			// download the image from provided URL and convert to Base64 string
			String base64String = urlToBase64.getBase64String(imageLink);

			// upload the image to imgur using the provided base 64 string
			imageService.uploadImage(base64String, imageLink);
		} catch (UploadException | IOException e) {
			// move the pending urls to failed list
			JobUrlLists jobUrlLists = imageService.getJobUrlLists();
			jobUrlLists.getPending().remove(imageLink);
			jobUrlLists.getFailed().add(imageLink);
//            LOGGER.error("Adding the link " + imageLink +" to failed list");
//            LOGGER.error("**[ERROR]** encountered following error: " + e.getStatus());
		}

	}

}
