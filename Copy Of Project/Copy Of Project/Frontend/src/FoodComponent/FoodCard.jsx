import { Link } from "react-router-dom";
import CategoryNavigator from "../CategoryComponent/CategoryNavigator";

const FoodCard = (food) => {
  const descriptionToShow = (description, maxLength) => {
    if (description.length <= maxLength) {
      return description;
    } else {
      const truncatedText = description.substring(0, maxLength);
      return truncatedText + "...";
    }
  };

  return (
    <div className="col">
      <div class="card food-card rounded-card h-100 shadow-lg">
        <img
          src={"http://localhost:8080/api/food/" + food.item.image1}
          class="card-img-top rounded"
          alt="img"
          style={{
            maxHeight: "300px", // Adjust the maximum height as needed
            margin: "0 auto",
          }}
        />

        <div class="card-body text-color">
          <h5>
            Category:{" "}
            <CategoryNavigator
              item={{
                id: food.item.category.id,
                name: food.item.category.name,
              }}
            />
          </h5>
          <h5 class="card-title d-flex justify-content-between text-color-second">
            <div>
              <b>{food.item.name}</b>
            </div>
          </h5>
          <p className="card-text">
            <b>{descriptionToShow(food.item.description, 50)}</b>
          </p>
        </div>
        <div class="card-footer">
          <div className="d-flex justify-content-between mt-2">
            <Link
              to={`/food/${food.item.id}/category/${food.item.category.id}`}
              className="btn bg-color custom-bg-text"
            >
              Add to Cart
            </Link>

            <div className="text-color-second">
              <p>
                <span>
                  <h4>Price : &#8377;{food.item.price}</h4>
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FoodCard;
