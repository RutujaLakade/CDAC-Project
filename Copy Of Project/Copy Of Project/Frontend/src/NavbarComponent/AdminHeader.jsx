import { Link, useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const AdminHeader = ({ handleLogout }) => {
  const navigate = useNavigate();

  const adminLogout = () => {
    toast.success("Logged out successfully!", {
      position: "top-center",
      autoClose: 1000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
    });
    // Call the handleLogout function passed as props
    handleLogout();
    // Redirect to the homepage
    navigate("/");
  };

  return (
    <ul className="navbar-nav ms-auto mb-2 mb-lg-0 me-5">
      <li className="nav-item">
        <Link
          to="/user/admin/register"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">Register Admin</b>
        </Link>
      </li>
      <li className="nav-item">
        <Link
          to="/category/add"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color"> Add Category</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/admin/order/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">All Orders</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/admin/food/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">All Foods</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/admin/category/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">All Categories</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/admin/restaurant/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">All Restaurant</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/admin/delivery-person/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">All Deliveries</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to="/admin/customer/all"
          className="nav-link active"
          aria-current="page"
        >
          <b className="text-color">View Customers</b>
        </Link>
      </li>

      <li className="nav-item">
        <Link
          to=""
          className="nav-link active"
          aria-current="page"
          onClick={adminLogout}
        >
          <b className="text-color">Logout</b>
        </Link>
        <ToastContainer />
      </li>
    </ul>
  );
};

export default AdminHeader;
