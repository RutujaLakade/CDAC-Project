import { Link, useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const HeaderUser = ({ handleLogout }) => {
  const navigate = useNavigate();

  const userLogout = () => {
    sessionStorage.removeItem("active-customer");
    sessionStorage.removeItem("customer-jwtToken");
    handleLogout(); // Call the handleLogout function passed as props
    toast.success("Logged out successfully!", {
      position: "top-center",
      autoClose: 1000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
    });
    navigate("/"); // Redirect to the homepage
  };

  return (
    <ul className="navbar-nav ms-auto mb-2 mb-lg-0 me-5">
      <li className="nav-item">
        <Link
          to="/customer/cart"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">My Cart</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/customer/order"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">My Order</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/"
          className="nav-link active"
          aria-current="page"
          onClick={userLogout} // Call userLogout when logout link is clicked
        >
          <b className="text-color">Logout</b>
        </Link>
        <ToastContainer />
      </li>
    </ul>
  );
};

export default HeaderUser;
