import React from "react";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Redirect,
  useLocation
} from "react-router-dom";
import Header from "./components/Header";
import BackgroundVideo from "./components/BackgroundVideo";
import AboutUs from "./components/AboutUs";
import ArtistDashboard from "./components/board/ArtistDashboard";
import Dashboard from "./components/userboard/UserDashboard";
import AdminDashboard from "./components/AdminDashboard/AdminDashboard";
import UserProfile from "./components/userboard/UserProfile";
import PurchasedArtwork from "./components/userboard/PurchasedArtwork";
import CartPage from "./components/userboard/CartPage";
import Profile from "./components/board/Profile";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import OrderStatusPage from "./components/AdminDashboard/OrderStatusPage"; 
import AdminBuyerList from "./components/AdminDashboard/AdminBuyerList"; 
import './App.css'; 
import ProtectedRoute from "./components/ProtectedRoute";
import StripeOrderPage from "./components/userboard/StripeOrderPage";

function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}

function AppContent() {
  const location = useLocation();
  const token = localStorage.getItem("token");
  const userRole = localStorage.getItem("role");

  return (
    <div className="app-container">
      {/* Show background video only on login or register */}
      {(location.pathname === "/login" || location.pathname === "/register") && (
        <BackgroundVideo />
      )}

      {/* /* Show header only on certain pages */ }
      
      {!location.pathname.startsWith("/artist-dashboard") &&
       !location.pathname.startsWith("/dashboard") &&
       !location.pathname.startsWith("/admin-dashboard") &&
       !location.pathname.startsWith("/admin-dashboard/artworks")&&
       !location.pathname.startsWith("/admin-dashboard/customer")&&
       !location.pathname.startsWith("/userprofile") &&
       !location.pathname.startsWith("/orders/") && 
       !location.pathname.startsWith("/profile")&&
       !location.pathname.startsWith("/about")&&
       !location.pathname.startsWith("/mypurchases")&&

       !location.pathname.startsWith("/cart")&&(

  <Header />
)}

      <Switch>
        <Route exact path="/">
          {token ? (
            userRole === "artist" ? (
              <Redirect to="/artist-dashboard" />
            ) : userRole === "admin" ? (
              <Redirect to="/admin-dashboard" />
            ) : (
              <Redirect to="/dashboard" />
            )
          ) : (
            <Redirect to="/login" />
          )}
        </Route>

        <Route exact path="/login" render={(props) => <Login {...props} />} />
        <Route exact path="/register" render={(props) => <Register {...props} />} />
        <Route exact path="/about" component={AboutUs} />


        {/* Protected Routes */}
        <ProtectedRoute
          
          path="/artist-dashboard"
          component={ArtistDashboard}
          requiredRole="artist"
        />

        <ProtectedRoute
          
          path="/dashboard"
          component={Dashboard}
          requiredRole="buyer"
        />
        <ProtectedRoute
           path="/admin-dashboard"
           component={AdminDashboard}
           requiredRole="admin"
        />

        <ProtectedRoute exact path="/profile" component={Profile} />
        <ProtectedRoute
          path="/userprofile"
          component={UserProfile}
          requiredRole="buyer"
        />
        <ProtectedRoute
          path="/mypurchases"
          component={PurchasedArtwork}
          requiredRole="buyer"
        />
        <ProtectedRoute
          path="/cart"
          component={CartPage}
          requiredRole="buyer"
        />
        <ProtectedRoute
          path="/orders/:orderId"
          component={StripeOrderPage}
          requiredRole="buyer"
        />

        {/* 404 Page */}
        <Route path="*">
          <h2 className="text-center text-white mt-10 text-xl">404 - Page Not Found</h2>
        </Route>
      </Switch>
    </div>
  );
}

export default App;

