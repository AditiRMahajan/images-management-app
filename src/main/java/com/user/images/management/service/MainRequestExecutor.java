package com.user.images.management.service;

import java.util.concurrent.ExecutorService;

import com.user.images.management.service.impl.ImageService;
import com.user.images.management.util.JobUrlLists;
import com.user.images.management.util.URLToBase64;

public class MainRequestExecutor {

	URLToBase64 urlToBase64;
	ImageService imageService;
	JobUrlLists jobUrlLists;
	ExecutorService execService;

	public MainRequestExecutor(ExecutorService execService) {
		this.urlToBase64 = new URLToBase64();
		this.execService = execService;
	}

	public void mainExecutor(JobUrlLists jobUrlLists) {
		this.jobUrlLists = jobUrlLists;
		this.imageService = new ImageService(jobUrlLists);

		for (String link : jobUrlLists.getPending()) {
			RequestExecutor reqExec = new RequestExecutor(link, urlToBase64, imageService);
			execService.submit(reqExec);
		}

	}

}
