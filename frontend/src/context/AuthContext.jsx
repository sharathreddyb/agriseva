import { useState } from "react";
import { AuthContext } from "./authContext";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const storedUser =
      localStorage.getItem("user");

    return storedUser
      ? JSON.parse(storedUser)
      : null;
  });

  function login(authResponse) {
    localStorage.setItem(
      "accessToken",
      authResponse.accessToken
    );

    const userData = {
      userId: authResponse.userId,
      fullName: authResponse.fullName,
      email: authResponse.email,
      phoneNumber:
        authResponse.phoneNumber,
      roles: authResponse.roles,
    };

    localStorage.setItem(
      "user",
      JSON.stringify(userData)
    );

    setUser(userData);
  }

  function updateRoles(roles) {
    setUser((currentUser) => {
      if (!currentUser) {
        return currentUser;
      }

      const updatedUser = {
        ...currentUser,
        roles,
      };

      localStorage.setItem(
        "user",
        JSON.stringify(updatedUser)
      );

      return updatedUser;
    });
  }

  function logout() {
    localStorage.removeItem(
      "accessToken"
    );
    localStorage.removeItem("user");
    setUser(null);
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        logout,
        updateRoles,
        isAuthenticated:
          Boolean(user),
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}