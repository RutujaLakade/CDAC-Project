import { useState } from "react";
import AdminHeader from "./AdminHeader";
import DeliveryHeader from "./DeliveryHeader";
import HeaderUser from "./HeaderUser";
import NormalHeader from "./NormalHeader";
import RestaurantHeader from "./RestaurantHeader";

const RoleNav = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // Function to check session storage for active user
  const checkSessionStorage = () => {
    const user = JSON.parse(sessionStorage.getItem("active-customer"));
    const admin = JSON.parse(sessionStorage.getItem("active-admin"));
    const deliveryPerson = JSON.parse(
      sessionStorage.getItem("active-delivery")
    );
    const restaurant = JSON.parse(sessionStorage.getItem("active-restaurant"));

    // Update isLoggedIn state based on active user
    setIsLoggedIn(user || admin || deliveryPerson || restaurant);
  };

  // Call checkSessionStorage on component mount
  useState(() => {
    checkSessionStorage();
  }, []);

  // Function to handle logout
  const handleLogout = () => {
    // Clear session storage
    sessionStorage.clear();
    // Update isLoggedIn state to false
    setIsLoggedIn(false);
  };

  // Render based on the logged-in user role or default to NormalHeader
  if (isLoggedIn) {
    const user = JSON.parse(sessionStorage.getItem("active-customer"));
    const admin = JSON.parse(sessionStorage.getItem("active-admin"));
    const deliveryPerson = JSON.parse(
      sessionStorage.getItem("active-delivery")
    );
    const restaurant = JSON.parse(sessionStorage.getItem("active-restaurant"));

    if (user) {
      return <HeaderUser handleLogout={handleLogout} />;
    } else if (admin) {
      return <AdminHeader handleLogout={handleLogout} />;
    } else if (restaurant) {
      return <RestaurantHeader handleLogout={handleLogout} />;
    } else if (deliveryPerson) {
      return <DeliveryHeader handleLogout={handleLogout} />;
    }
  }

  return <NormalHeader />;
};

export default RoleNav;
