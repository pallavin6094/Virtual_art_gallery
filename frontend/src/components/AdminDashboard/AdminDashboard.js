
import React from "react";
import { Switch, Route } from "react-router-dom";
import Sidebar from "./Sidebar";
import Header from "./Header";
import Home from "./Home"; 
import AdminArtworks from "./AdminArtworks";
import Category from "./Category";

import AdminBuyerList from "./AdminBuyerList";
import OrderStatusPage from "./OrderStatusPage"; // Make sure path is correct
import Settings from "./Settings"; 
import './AdminDashboard.css';

function AdminDashboard() {
  return (
    <div className="grid-container">
      <Header />
      <Sidebar />
      <div className="main-container">
        <Switch>
          <Route exact path="/admin-dashboard" component={Home} />
          <Route path="/admin-dashboard/order-status" component={OrderStatusPage} />
          <Route path="/admin-dashboard/artworks" component={AdminArtworks}/>
          <Route path="/admin-dashboard/customer" component={AdminBuyerList}/>
          <Route path="/admin/settings" component={Settings} />
          <Route path="/admin-dashboard/themes" component={Category}/>



        </Switch>
      </div>
    </div>
  );
}

export default AdminDashboard;


