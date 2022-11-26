package com.user.images.management.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.user.images.management.entity.AuthenticationRequest;
import com.user.images.management.entity.AuthenticationResponse;
import com.user.images.management.entity.JobIdObject;
import com.user.images.management.entity.JobStatusObject;
import com.user.images.management.entity.UrlObject;
import com.user.images.management.entity.User;
import com.user.images.management.repository.DatabaseMap;
import com.user.images.management.service.CustomUserDetailsService;
import com.user.images.management.service.UrlUploadRequestService;
import com.user.images.management.service.UserService;
import com.user.images.management.util.JwtUtil;

@RestController
public class AppController {

	// private static final Logger logger =
	// LoggerFactory.getLogger(AppController.class);

	@Autowired
	private UserService userService;

	@Autowired
	JwtUtil jwtTokenUtil;

	@Autowired
	CustomUserDetailsService myUserDetailsService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private DatabaseMap databaseMap;

	@Autowired
	private UrlUploadRequestService asyncRequestService;

	@GetMapping("/home")
	public String defaultPage() {
		return "Welcome to Image Management System for Users!!";
	}

	// Register a User
//	@PostMapping(value = { "/add-user" }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
//	public String addUser(@RequestPart("user") User user, @RequestPart("imageFile") MultipartFile[] file) {
//		String result = "";
//		try {
//			User dbUser = userService.findUserByEmail(user.getEmail());
//			if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
//				result = "error=Enter valid first name";
//			} else if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
//				result = "error=Enter valid last name";
//			} else if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
//				result = "error=Enter valid email";
//			} else if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
//				result = "error=Enter valid password";
//			}
//			if (dbUser == null) {
//				Set<Image> images = this.uploadImage(file);
//				user.setImages(images);
//				userService.saveUser(user);
//			} else {
//				result = "User Already Exists!";
//			}
//
//		} catch (Exception e) {
//			logger.debug(e.getMessage());
//
//		}
//		return result;
//	}

	// Register a User
	@PostMapping("/add-user")
	public String addUser(@RequestBody User user) {
		String result = "";
		User dbUser = userService.findUserByEmail(user.getEmail());
		if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
			result = "error=Enter valid first name";
		} else if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
			result = "error=Enter valid last name";
		} else if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			result = "error=Enter valid email";
		} else if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			result = "error=Enter valid password";
		}
		if (dbUser == null) {
			userService.saveUser(user);
		} else {
			result = "User Already Exists!";
		}

		return result;
	}

//	public Set<Image> uploadImage(MultipartFile[] multiPartFiles) throws IOException {
//		Set<Image> images = new HashSet<>();
//
//		for (MultipartFile file : multiPartFiles) {
//			Image image = new Image(file.getOriginalFilename(), file.getContentType(), file.getBytes());
//			images.add(image);
//		}
//		return images;
//	}

	// upload image
	@PostMapping("/upload")
	public ResponseEntity<JobIdObject> uploadJob(@RequestBody UrlObject urlObject) {

		CompletableFuture<JobIdObject> responseJobId = asyncRequestService.getJobIdForUrl(urlObject);
		try {
			return new ResponseEntity<>(responseJobId.get(), HttpStatus.CREATED);
		} catch (InterruptedException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (ExecutionException e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@GetMapping("/images")
	public ResponseEntity<UrlObject> findAllJobs() {
		UrlObject urlObject = new UrlObject();
		urlObject.setUrls(databaseMap.getAllUploadedLinks());

		return new ResponseEntity<>(urlObject, HttpStatus.OK);
	}

	@GetMapping("/images/{jobId}")
	public ResponseEntity<JobStatusObject> findByJobId(@PathVariable Long jobId) {
		Optional<JobStatusObject> jobStatusObject = databaseMap.getJobStatusById(jobId);

		if (jobStatusObject.isPresent()) {
			return new ResponseEntity<>(jobStatusObject.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// Read All users
	@GetMapping("/users")
	public List<User> fetchAllUsers() {
		return userService.fetchUsers();
	}

	// Retrieve the details of a specific user
	@GetMapping("/user/{userId}")
	private User getUser(@PathVariable("userId") Long userId) {
		return userService.getUserById(userId);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect UserName or Password !", e);
		}

		final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}

}
