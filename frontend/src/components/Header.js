
import React, { useEffect, useState } from "react";
import { useHistory, useLocation,Link} from "react-router-dom";
import Register from "./auth/Register";
import Login from "./auth/Login";
import AboutUs from "./AboutUs";

import "./Header.css";

const Header = () => {
  const history = useHistory();
  const location = useLocation();
  const [openRegister, setOpenRegister] = useState(false);
  const [openLogin, setOpenLogin] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (token && role) {
      setIsAuthenticated(true);
      setUserRole(role.toLowerCase()); // Normalize to lowercase
    }
  }, [location.pathname]);

  const handleLogin = (token, userId, role) => {
    const normalizedRole = role.toLowerCase(); // ✅ normalize here

    localStorage.setItem("token", token);
    localStorage.setItem("userId", userId);
    localStorage.setItem("role", normalizedRole);

    setIsAuthenticated(true);
    setUserRole(normalizedRole);

    // Redirect based on role
    if (normalizedRole === "buyer") {
      history.push("/dashboard");
    } else if (normalizedRole === "artist") {
      history.push("/artist-dashboard");
    } else if (normalizedRole === "admin") {
      history.push("/admin-dashboard"); // ✅ Add this
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    setIsAuthenticated(false);
    setUserRole(null);
    history.push("/login");
  };

  return (
    <header className="tinted-header">
      <h1 className="header-title">Welcome To ArtFusion</h1>
      <div className="button-container">
       <Link to="/about" style={{ color: 'white', textDecoration: 'none' ,fontWeight: 'bold',fontSize: '18px'}}>
       About Us
       </Link>

       
        {isAuthenticated ? (
          <>
            <button
              className="auth-button"
              onClick={() =>
                history.push(userRole === "artist" ? "/artist-dashboard" : "/dashboard")
              }
            >
              Dashboard
            </button>
            <button className="auth-button logout" onClick={handleLogout}>
              Logout
            </button>
          </>
        ) : (
          <>
            <button className="auth-button" onClick={() => setOpenRegister(true)}>
              Register
            </button>
            <button className="auth-button" onClick={() => setOpenLogin(true)}>
              Login
            </button>
          </>
        )}
      </div>

      {/* Modals */}
      <Register open={openRegister} onClose={() => setOpenRegister(false)} />
      <Login open={openLogin} onClose={() => setOpenLogin(false)} onLogin={handleLogin} />
    </header>
  );
};

export default Header;
