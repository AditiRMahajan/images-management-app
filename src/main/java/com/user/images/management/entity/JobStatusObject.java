package com.user.images.management.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobStatusObject {

	String id;
	String created;
	String finished;
	String status;
	LinkStatusObject uploaded;

}
