package com.user.images.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long imageId;
	private String imageName;
	private String type;
	@Column(length = 50000000)
	private byte[] picByte;

	public Image(String imageName, String type, byte[] picByte) {
		super();
		this.imageName = imageName;
		this.type = type;
		this.picByte = picByte;
	}

}
