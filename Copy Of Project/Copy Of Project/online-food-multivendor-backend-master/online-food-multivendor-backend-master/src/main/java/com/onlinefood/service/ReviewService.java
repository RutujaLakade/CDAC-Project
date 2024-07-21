package com.onlinefood.service;

import java.util.List;

import com.onlinefood.entity.Food;
import com.onlinefood.entity.Review;
import com.onlinefood.entity.User;

public interface ReviewService {
	
	Review addReview(Review review);
	
	List<Review> fetchFoodReviews(List<Food> products);

	List<Review> fetchRestaurantFoodReview(User restaurant);
	
}
