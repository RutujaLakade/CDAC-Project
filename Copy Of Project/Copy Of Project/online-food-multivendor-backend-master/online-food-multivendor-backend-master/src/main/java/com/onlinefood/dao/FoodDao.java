package com.onlinefood.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.onlinefood.entity.Category;
import com.onlinefood.entity.Food;
import com.onlinefood.entity.User;

@Repository
public interface FoodDao extends JpaRepository<Food, Integer> {

	List<Food> findByStatusIn(List<String> status);

	List<Food> findByRestaurantAndStatusIn(User restaurant, List<String> status);

	List<Food> findByRestaurantAndCategoryAndAndStatusIn(User restaurant, Category category, List<String> status);

	List<Food> findByCategoryAndStatusIn(Category category, List<String> status);

	Long countByStatusIn(List<String> status);

	Long countByStatusInAndRestaurant(List<String> status, User restaurant);

	List<Food> findByNameContainingIgnoreCaseAndStatusIn(String foodName, List<String> status);

	List<Food> findByNameContainingIgnoreCaseAndRestaurantAndStatusIn(String foodName, User restaurant, List<String> status);
	
}
