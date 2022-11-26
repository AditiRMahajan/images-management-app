package com.user.images.management.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkStatusObject {

	List<String> pending;
	List<String> complete;
	List<String> failed;

}
