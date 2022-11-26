package com.user.images.management.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.user.images.management.entity.JobStatusObject;
import com.user.images.management.entity.LinkStatusObject;
import com.user.images.management.entity.UrlObject;
import com.user.images.management.util.IdGenerator;
import com.user.images.management.util.JobUrlLists;
import com.user.images.management.util.TimeZone;

/*
* Data store class. Stores a map of job id
* and JobUrlLists object to serve as a repository.
* This class also performs data cleaning
* i.e. remove duplicates, remove non-image
* links etc.
* */

@Repository
public class DatabaseMap {

	@Autowired
	private IdGenerator idGenerator;

	public static final Map<Long, JobUrlLists> jobs = new ConcurrentHashMap<>();
	private List<String> validExt;

	public Map<Long, JobUrlLists> getJobs() {
		return jobs;
	}

	public DatabaseMap() {
		validExt = new ArrayList<>();
		validExt.add("png");
		validExt.add("gif");
		validExt.add("tif");
		validExt.add("jpg");
		validExt.add("bmp");
		validExt.add("jpeg");
	}

	public JobUrlLists create(UrlObject urlObject) {
		JobUrlLists jobUrlListsObj = new JobUrlLists();
		Long id = idGenerator.getNextId();
		jobUrlListsObj.setId(String.valueOf(id));
		jobUrlListsObj.setStatus("in-progress");

		// removing duplicates
		Set<String> hashSet = new LinkedHashSet<>(urlObject.getUrls());

		// remove non-image links
		Iterator<String> setIterator = hashSet.iterator();
		while (setIterator.hasNext()) {
			String linkToBeChecked = setIterator.next();
			String ext = FilenameUtils.getExtension(linkToBeChecked);
			if (!validExt.contains(ext.toLowerCase())) {
				setIterator.remove();
//                LOGGER.info("removing " + linkToBeChecked + " as it does not conform to our supported " +
//                        "image extensions (jpg/tif/bmp/png/gif)");
			}
		}

		jobUrlListsObj.getPending().addAll(hashSet);

		TimeZone dateTime = new TimeZone();
		jobUrlListsObj.setCreated(dateTime.getTimeNow());

		jobs.put(id, jobUrlListsObj);
		return jobUrlListsObj;
	}

	public Optional<JobStatusObject> getJobStatusById(Long id) {
		JobStatusObject jobStatusObject = null;
		if (jobs.containsKey(id)) {
			JobUrlLists jobListObject = jobs.get(id);

			jobStatusObject = new JobStatusObject();
			jobStatusObject.setId(jobListObject.getId());
			jobStatusObject.setCreated(jobListObject.getCreated());
			jobStatusObject.setStatus(jobListObject.getStatus());

			LinkStatusObject linkStatusObject = new LinkStatusObject();
			if (!jobListObject.getPending().isEmpty()) {
				linkStatusObject.setPending(jobListObject.getPending());
			} else {
				jobStatusObject.setFinished(jobListObject.getFinished());
			}
			if (!jobListObject.getCompleted().isEmpty()) {
				linkStatusObject.setComplete(jobListObject.getCompleted());
			}
			if (!jobListObject.getFailed().isEmpty()) {
				linkStatusObject.setFailed(jobListObject.getFailed());
			}
			jobStatusObject.setUploaded(linkStatusObject);
		}
		return Optional.ofNullable(jobStatusObject);
	}

	public List<String> getAllUploadedLinks() {
		List<String> uploadedUrlsList = new ArrayList<String>();
		for (Map.Entry<Long, JobUrlLists> entry : jobs.entrySet()) {
			uploadedUrlsList.addAll(entry.getValue().getCompleted());
		}
		return uploadedUrlsList;

	}
}
