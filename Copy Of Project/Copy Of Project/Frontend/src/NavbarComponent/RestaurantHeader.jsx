import { Link, useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const RestaurantHeader = ({ handleLogout }) => {
  let navigate = useNavigate();

  const restaurantLogout = () => {
    handleLogout(); // Call the handleLogout function provided as a prop
    navigate("/"); // Redirect to the homepage
  };

  const handleLogoutClick = () => {
    restaurantLogout();
    toast.success("Logged out successfully!", {
      position: "top-center",
      autoClose: 1000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
    });
  };

  return (
    <ul className="navbar-nav ms-auto mb-2 mb-lg-0 me-5">
      <li className="nav-item">
        <Link
          to="/restaurant/order/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">Restaurant Orders</b>
        </Link>
      </li>
      <li className="nav-item">
        <Link
          to="/restaurant/delivery/register"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">Register Delivery</b>
        </Link>
      </li>
      <li className="nav-item">
        <Link
          to="/restaurant/delivery-person/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">View Delivery Persons</b>
        </Link>
      </li>
      <li className="nav-item">
        <Link to="/food/add" className="nav-link active" aria-current="page">
          <b className="text-color">Add Food</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/restaurant/food/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">View My Foods</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/"
          className="nav-link active"
          aria-current="page"
          onClick={handleLogoutClick}
        >
          <b className="text-color">Logout</b>
        </Link>
        <ToastContainer />
      </li>
    </ul>
  );
};

export default RestaurantHeader;
