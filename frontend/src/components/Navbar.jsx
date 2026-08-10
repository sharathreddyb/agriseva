import { Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

function Navbar() {
  const {
    user,
    logout,
    isAuthenticated,
  } = useAuth();

  const isEquipmentOwner =
    user?.roles?.includes(
      "EQUIPMENT_OWNER"
    );

  const isProductSeller =
    user?.roles?.includes(
      "PRODUCT_SELLER"
    );

  return (
    <header className="navbar">
      <Link
        to="/"
        className="brand"
      >
        AgriSeva
      </Link>

      <nav className="nav-links">
        <Link to="/">
          Home
        </Link>

        <Link to="/equipment">
          Equipment
        </Link>

        <Link to="/products">
          Products
        </Link>

        {isAuthenticated ? (
          <>
            <Link to="/rentals">
              My Rentals
            </Link>

            <Link to="/orders">
              My Orders
            </Link>

            {isEquipmentOwner && (
              <Link to="/owner/rentals">
                Owner Requests
              </Link>
            )}

            {isProductSeller && (
              <Link to="/seller/orders">
                Received Orders
              </Link>
            )}

            <span className="welcome-text">
              Hi, {user.fullName}
            </span>

            <button
              type="button"
              className="logout-button"
              onClick={logout}
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login">
              Login
            </Link>

            <Link
              to="/register"
              className="register-link"
            >
              Register
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}

export default Navbar;