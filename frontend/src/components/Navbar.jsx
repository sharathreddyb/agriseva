import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

function Navbar() {
  const {
    user,
    logout,
    isAuthenticated,
  } = useAuth();

  const [menuOpen, setMenuOpen] =
    useState(false);

  const isEquipmentOwner =
    user?.roles?.includes(
      "EQUIPMENT_OWNER"
    );

  const isProductSeller =
    user?.roles?.includes(
      "PRODUCT_SELLER"
    );

  function closeMenu() {
    setMenuOpen(false);
  }

  function handleLogout() {
    logout();
    closeMenu();
  }

  return (
    <header className="navbar">
      <Link
        to="/"
        className="brand"
        onClick={closeMenu}
      >
        AgriSeva
      </Link>

      <button
        type="button"
        className="nav-menu-button"
        onClick={() =>
          setMenuOpen(
            (current) => !current
          )
        }
        aria-expanded={menuOpen}
        aria-label="Toggle navigation menu"
      >
        {menuOpen ? "Close" : "Menu"}
      </button>

      <nav
        className={
          menuOpen
            ? "nav-links nav-links-open"
            : "nav-links"
        }
      >
        <Link
          to="/"
          onClick={closeMenu}
        >
          Home
        </Link>

        <Link
          to="/equipment"
          onClick={closeMenu}
        >
          Equipment
        </Link>

        <Link
          to="/products"
          onClick={closeMenu}
        >
          Products
        </Link>

        {isAuthenticated ? (
          <>
            <Link
              to="/rentals"
              onClick={closeMenu}
            >
              My Rentals
            </Link>

            <Link
              to="/orders"
              onClick={closeMenu}
            >
              My Orders
            </Link>

            <Link
              to="/provider"
              onClick={closeMenu}
            >
              Sell or Rent
            </Link>

            {isEquipmentOwner && (
              <>
                <Link
                  to="/provider/equipment"
                  onClick={closeMenu}
                >
                  My Equipment
                </Link>

                <Link
                  to="/owner/rentals"
                  onClick={closeMenu}
                >
                  Owner Requests
                </Link>
              </>
            )}

            {isProductSeller && (
              <>
                <Link
                  to="/provider/products"
                  onClick={closeMenu}
                >
                  My Products
                </Link>

                <Link
                  to="/seller/orders"
                  onClick={closeMenu}
                >
                  Received Orders
                </Link>
              </>
            )}

            <span className="welcome-text">
              Hi, {user.fullName}
            </span>

            <button
              type="button"
              className="logout-button"
              onClick={handleLogout}
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <Link
              to="/login"
              onClick={closeMenu}
            >
              Login
            </Link>

            <Link
              to="/register"
              className="register-link"
              onClick={closeMenu}
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