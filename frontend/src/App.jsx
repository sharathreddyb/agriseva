import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";
import Navbar from "./components/Navbar";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import EquipmentPage from "./pages/EquipmentPage";
import ProductsPage from "./pages/ProductsPage";
import RentEquipmentPage from "./pages/RentEquipmentPage";
import MyRentalsPage from "./pages/MyRentalsPage";
import OwnerRentalRequestsPage from "./pages/OwnerRentalRequestsPage";
import OrderProductPage from "./pages/OrderProductPage";
import MyOrdersPage from "./pages/MyOrdersPage";
import ReceivedOrdersPage from "./pages/ReceivedOrdersPage";
import { useAuth } from "./context/useAuth";
import "./App.css";

function ProtectedRoute({ children }) {
  const { isAuthenticated } =
    useAuth();

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  return children;
}

function EquipmentOwnerRoute({
  children,
}) {
  const {
    user,
    isAuthenticated,
  } = useAuth();

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  const isEquipmentOwner =
    user?.roles?.includes(
      "EQUIPMENT_OWNER"
    );

  if (!isEquipmentOwner) {
    return (
      <Navigate
        to="/"
        replace
      />
    );
  }

  return children;
}

function ProductSellerRoute({
  children,
}) {
  const {
    user,
    isAuthenticated,
  } = useAuth();

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  const isProductSeller =
    user?.roles?.includes(
      "PRODUCT_SELLER"
    );

  if (!isProductSeller) {
    return (
      <Navigate
        to="/"
        replace
      />
    );
  }

  return children;
}

function App() {
  return (
    <BrowserRouter>
      <Navbar />

      <Routes>
        <Route
          path="/"
          element={<HomePage />}
        />

        <Route
          path="/equipment"
          element={<EquipmentPage />}
        />

        <Route
          path="/products"
          element={<ProductsPage />}
        />

        <Route
          path="/products/:productId/order"
          element={
            <ProtectedRoute>
              <OrderProductPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/orders"
          element={
            <ProtectedRoute>
              <MyOrdersPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/seller/orders"
          element={
            <ProductSellerRoute>
              <ReceivedOrdersPage />
            </ProductSellerRoute>
          }
        />

        <Route
          path="/equipment/:equipmentId/rent"
          element={
            <ProtectedRoute>
              <RentEquipmentPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/rentals"
          element={
            <ProtectedRoute>
              <MyRentalsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/owner/rentals"
          element={
            <EquipmentOwnerRoute>
              <OwnerRentalRequestsPage />
            </EquipmentOwnerRoute>
          }
        />

        <Route
          path="/login"
          element={<LoginPage />}
        />

        <Route
          path="/register"
          element={<RegisterPage />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;