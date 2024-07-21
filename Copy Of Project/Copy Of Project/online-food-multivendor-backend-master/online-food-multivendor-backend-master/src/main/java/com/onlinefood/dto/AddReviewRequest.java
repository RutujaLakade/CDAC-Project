package com.onlinefood.dto;

import lombok.Data;

@Data
public class AddReviewRequest {

	private int userId;

	private int foodId;

	private int star;

	private String review;

}
