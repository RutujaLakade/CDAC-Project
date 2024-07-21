package com.onlinefood.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;

import com.onlinefood.dto.CommonApiResponse;
import com.onlinefood.dto.FoodAddRequest;
import com.onlinefood.dto.FoodDetailUpdateRequest;
import com.onlinefood.dto.FoodResponseDto;
import com.onlinefood.entity.Category;
import com.onlinefood.entity.Food;
import com.onlinefood.entity.User;
import com.onlinefood.exception.FoodSaveFailedException;
import com.onlinefood.service.CategoryService;
import com.onlinefood.service.FoodService;
import com.onlinefood.service.StorageService;
import com.onlinefood.service.UserService;
import com.onlinefood.utility.Constants.FoodStatus;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class FoodResource {

	private final Logger LOG = LoggerFactory.getLogger(FoodResource.class);

	@Autowired
	private FoodService foodService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private StorageService storageService;

	public ResponseEntity<CommonApiResponse> addFood(FoodAddRequest foodDto) {
	    LOG.info("request received for Food add");

	    CommonApiResponse response = new CommonApiResponse();

	    if (foodDto == null || foodDto.getCategoryId() == 0 || foodDto.getRestaurantId() == 0) {
	        response.setResponseMessage("missing input");
	        response.setSuccess(false);

	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }

	    Food food = FoodAddRequest.toEntity(foodDto);
	    food.setStatus(FoodStatus.ACTIVE.value());

	    User seller = this.userService.getUserById(foodDto.getRestaurantId());

	    if (seller == null) {
	        response.setResponseMessage("Restaurant not found");
	        response.setSuccess(false);

	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    Category category = this.categoryService.getCategoryById(foodDto.getCategoryId());

	    if (category == null) {
	        response.setResponseMessage("Category not found");
	        response.setSuccess(false);

	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    food.setRestaurant(seller);
	    food.setCategory(category);

	    String foodImageName1 = null;
	    String foodImageName2 = null;
	    String foodImageName3 = null;

	    try {
	        // Store food images
	        if (foodDto.getImage1() != null) {
	            foodImageName1 = storageService.store(foodDto.getImage1());
	        }
	        if (foodDto.getImage2() != null) {
	            foodImageName2 = storageService.store(foodDto.getImage2());
	        }
	        if (foodDto.getImage3() != null) {
	            foodImageName3 = storageService.store(foodDto.getImage3());
	        }
	    } catch (Exception e) {
	        // Handle file storage exception
	        LOG.error("Error storing food images: " + e.getMessage());
	        response.setResponseMessage("Failed to store food images");
	        response.setSuccess(false);
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    // Set image names in the food object
	    food.setImage1(foodImageName1);
	    food.setImage2(foodImageName2);
	    food.setImage3(foodImageName3);

	    // Add food to the database
	    Food savedFood = this.foodService.addFood(food);

	    if (savedFood == null) {
	        throw new FoodSaveFailedException("Failed to save the Food");
	    }

	    response.setResponseMessage("Food added successfully");
	    response.setSuccess(true);

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateFoodDetail(FoodDetailUpdateRequest request) {

		LOG.info("request received for update food");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Food food = this.foodService.getFoodById(request.getId());

		if (food == null) {
			response.setResponseMessage("food not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// it will update the category if changed
		if (food.getCategory().getId() != request.getCategoryId()) {
			Category category = this.categoryService.getCategoryById(request.getCategoryId());
			food.setCategory(category);
		}

		food.setDescription(request.getDescription());
		food.setName(request.getName());
		food.setPrice(request.getPrice());

		Food updatedFood = this.foodService.updateFood(food);

		if (updatedFood == null) {
			throw new FoodSaveFailedException("Failed to update the Food details");
		}

		response.setResponseMessage("Food added successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateFoodImage(FoodAddRequest request) {
	    LOG.info("request received for update food images");

	    CommonApiResponse response = new CommonApiResponse();

	    if (request == null || request.getId() == 0) {
	        response.setResponseMessage("Missing input: Food ID is required");
	        response.setSuccess(false);
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }

	    if (request.getImage1() == null || request.getImage2() == null || request.getImage3() == null) {
	        response.setResponseMessage("Image not provided for update");
	        response.setSuccess(false);
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }

	    Food food = this.foodService.getFoodById(request.getId());

	    if (food == null) {
	        response.setResponseMessage("Food not found for the given ID");
	        response.setSuccess(false);
	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    }

	    String existingImage1 = food.getImage1();
	    String existingImage2 = food.getImage2();
	    String existingImage3 = food.getImage3();

	    // Store updated food images
	    try {
	        String foodImageName1 = storageService.store(request.getImage1());
	        String foodImageName2 = storageService.store(request.getImage2());
	        String foodImageName3 = storageService.store(request.getImage3());

	        food.setImage1(foodImageName1);
	        food.setImage2(foodImageName2);
	        food.setImage3(foodImageName3);
	    } catch (Exception e) {
	        LOG.error("Error storing updated food images: " + e.getMessage());
	        response.setResponseMessage("Failed to update food images");
	        response.setSuccess(false);
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    // Update the food entity
	    Food updatedFood = this.foodService.updateFood(food);

	    if (updatedFood == null) {
	        response.setResponseMessage("Failed to update food information");
	        response.setSuccess(false);
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    // Delete the existing images
	    try {
	        this.storageService.delete(existingImage1);
	        this.storageService.delete(existingImage2);
	        this.storageService.delete(existingImage3);
	    } catch (Exception e) {
	        LOG.error("Error deleting existing food images: " + e.getMessage());
	        // Since the food information is updated, returning success with a warning
	        response.setResponseMessage("Food image updated successfully, but failed to delete existing images");
	        response.setSuccess(true); // Consider it a partial success
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

	    response.setResponseMessage("Food Image Updated Successfully");
	    response.setSuccess(true);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteFood(int foodId, int sellerId) {

		LOG.info("request received for deleting the food");

		CommonApiResponse response = new CommonApiResponse();

		if (foodId == 0 || sellerId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Food food = this.foodService.getFoodById(foodId);

		if (food == null) {
			response.setResponseMessage("food not found, failed to delete the food");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (food.getRestaurant().getId() != sellerId) {
			response.setResponseMessage("Food not owned by Restaurant, Can't Delete");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		food.setStatus(FoodStatus.DEACTIVATED.value());

		Food deletedFood = this.foodService.updateFood(food);

		if (deletedFood == null) {
			throw new FoodSaveFailedException("Failed to delete the Food");
		}

		response.setResponseMessage("Food Deleted Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<FoodResponseDto> fetchAllFoods() {

		LOG.info("request received for fetching all the foods");

		FoodResponseDto response = new FoodResponseDto();

		List<Food> foods = this.foodService
				.getAllFoodByStatusIn(Arrays.asList(FoodStatus.ACTIVE.value()));

		if (CollectionUtils.isEmpty(foods)) {
			response.setResponseMessage("No foods found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
		}

		response.setFoods(foods);
		response.setResponseMessage("Food fetched success");
		response.setSuccess(true);

		return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<FoodResponseDto> fetchAllRestaurantFoods(int sellerId) {

		LOG.info("request received for fetching all the Restaurant foods");

		FoodResponseDto response = new FoodResponseDto();

		if (sellerId == 0) {
			response.setResponseMessage("Restaurant not found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User seller = this.userService.getUserById(sellerId);

		if (seller == null) {
			response.setResponseMessage("Restaurant not found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Food> foods = this.foodService.getAllFoodByRestaurantAndStatusIn(seller,
				Arrays.asList(FoodStatus.ACTIVE.value()));

		if (CollectionUtils.isEmpty(foods)) {
			response.setResponseMessage("No foods found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
		}

		response.setFoods(foods);
		response.setResponseMessage("Food fetched success");
		response.setSuccess(true);

		return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<FoodResponseDto> searchFoodByName(String foodName) {

		LOG.info("request received for searching the foods");

		FoodResponseDto response = new FoodResponseDto();

		if (foodName == null) {
			response.setResponseMessage("missing input, food name");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Food> foods = this.foodService.searchFoodNameAndStatusIn(foodName,
				Arrays.asList(FoodStatus.ACTIVE.value()));

		if (CollectionUtils.isEmpty(foods)) {
			response.setResponseMessage("No foods found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
		}

		response.setFoods(foods);
		response.setResponseMessage("Food fetched success");
		response.setSuccess(true);

		return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<FoodResponseDto> searchRestaurantFoodsByName(String foodName, int sellerId) {

		LOG.info("request received for searching the seller foods");

		FoodResponseDto response = new FoodResponseDto();

		if (foodName == null || sellerId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User seller = this.userService.getUserById(sellerId);

		if (seller == null) {
			response.setResponseMessage("seller not found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Food> foods = this.foodService.searchFoodNameAndRestaurantAndStatusIn(foodName, seller,
				Arrays.asList(FoodStatus.ACTIVE.value()));

		if (CollectionUtils.isEmpty(foods)) {
			response.setResponseMessage("No foods found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
		}

		response.setFoods(foods);
		response.setResponseMessage("Food fetched success");
		response.setSuccess(true);

		return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<FoodResponseDto> fetchAllFoodsByCategory(int categoryId) {

		LOG.info("request received for fetching all the foods by category");

		FoodResponseDto response = new FoodResponseDto();

		if (categoryId == 0) {
			response.setResponseMessage("category id missing");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Category category = this.categoryService.getCategoryById(categoryId);

		if (category == null) {
			response.setResponseMessage("category not found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Food> foods = this.foodService.getAllFoodByCategoryAndStatusIn(category,
				Arrays.asList(FoodStatus.ACTIVE.value()));

		if (CollectionUtils.isEmpty(foods)) {
			response.setResponseMessage("No foods found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
		}

		response.setFoods(foods);
		response.setResponseMessage("Food fetched success");
		response.setSuccess(true);

		return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<FoodResponseDto> fetchFoodById(int foodId) {

		LOG.info("request received for searching the seller foods");

		FoodResponseDto response = new FoodResponseDto();

		if (foodId == 0) {
			response.setResponseMessage("missing food id");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Food food = this.foodService.getFoodById(foodId);

		if (food == null) {
			response.setResponseMessage("Food not found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		response.setFoods(Arrays.asList(food));
		response.setResponseMessage("Food fetched success");
		response.setSuccess(true);

		return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
	}

	public void fetchFoodImage(String foodImageName, HttpServletResponse resp) {
		Resource resource = storageService.load(foodImageName);
		if (resource != null) {
			try (InputStream in = resource.getInputStream()) {
				ServletOutputStream out = resp.getOutputStream();
				FileCopyUtils.copy(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ResponseEntity<FoodResponseDto> fetchAllRestaurantFoodsWithCategory(int sellerId, int categoryId) {

		LOG.info("request received for fetching all the Restaurant foods category wise");

		FoodResponseDto response = new FoodResponseDto();

		if (sellerId == 0 || categoryId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User seller = this.userService.getUserById(sellerId);

		if (seller == null) {
			response.setResponseMessage("Restaurant not found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Category category = this.categoryService.getCategoryById(categoryId);

		if (category == null) {
			response.setResponseMessage("category not found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Food> foods = this.foodService.getAllFoodByRestaurantAndCategoryAndStatusIn(seller, category,
				Arrays.asList(FoodStatus.ACTIVE.value()));

		if (CollectionUtils.isEmpty(foods)) {
			response.setResponseMessage("No foods found");
			response.setSuccess(false);

			return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
		}

		response.setFoods(foods);
		response.setResponseMessage("Food fetched success");
		response.setSuccess(true);

		return new ResponseEntity<FoodResponseDto>(response, HttpStatus.OK);
	}

}
