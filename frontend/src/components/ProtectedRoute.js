// src/components/ProtectedRoute.js
import React from "react";
import { Route, Redirect } from "react-router-dom";

const ProtectedRoute = ({ component: Component, requiredRole, ...rest }) => {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  return (
    <Route
      {...rest}
      render={(props) => {
        if (!token) {
          return <Redirect to="/login" />;
        }

        // Check for role if required
        if (requiredRole && role !== requiredRole) {
          return <Redirect to="/login" />;
        }

        return <Component {...props} />;
      }}
    />
  );
};

export default ProtectedRoute;
