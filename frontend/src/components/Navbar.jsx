import { Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

function Navbar() {
  const {
    user,
    logout,
    isAuthenticated,
  } = useAuth();

  return (
    <header className="navbar">
      <Link to="/" className="brand">
        AgriSeva
      </Link>

      <nav className="nav-links">
        <Link to="/">Home</Link>

        <Link to="/equipment">
          Equipment
        </Link>

        {isAuthenticated ? (
          <>
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