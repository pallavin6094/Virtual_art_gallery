
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
  BsFillArchiveFill, BsFillGrid3X3GapFill, BsPeopleFill, BsFillBellFill
} from 'react-icons/bs';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend,
  ResponsiveContainer, LineChart, Line
} from 'recharts';

function Home() {
  const [stats, setStats] = useState({});
  const [orders, setOrders] = useState([]);
  const [earningsData, setEarningsData] = useState([]);

  // Fetch dashboard stats
  useEffect(() => {
    const token = localStorage.getItem('token');
    axios.get('http://localhost:8080/api/admin/dashboard/stats', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(response => setStats(response.data))
      .catch(error => console.error('Error fetching stats:', error));
  }, []);

  // Fetch orders and prepare earnings data for line chart
  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8080/api/admin/dashboard/orders', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const aggregated = {};

        response.data.forEach(order => {
          const date = new Date(order.orderDate).toISOString().split('T')[0];
          if (!aggregated[date]) {
            aggregated[date] = 0;
          }
          aggregated[date] += order.totalPrice;
        });

        const chartFormattedData = Object.entries(aggregated).map(([date, earnings]) => ({
          date,
          earnings,
        }));

        setOrders(response.data);
        setEarningsData(chartFormattedData);
      } catch (error) {
        console.error('Error fetching orders:', error);
      }
    };

    fetchOrders();
  }, []);

  const barChartData = [
    { name: 'Completed Orders', value: stats.totalCompletedOrders || 0 },
    { name: 'Failed Orders', value: stats.totalFailedOrders || 0 },
    { name: "Today's Orders", value: stats.artworksSoldToday || 0 }
  ];

  return (
    <main className="main-container">
      <div className="main-title">
        <h3>REPORT</h3>
      </div>

      {/* Stat Cards */}
      <div className="main-cards">
        <div className="card">
          <div className="card-inner">
            <h3>Artworks Uploaded Today</h3>
            <BsFillArchiveFill className="card_icon" />
          </div>
          <h1>{stats.artworksUploadedToday || 0}</h1>
        
        </div>

        <div className="card">
          <div className="card-inner">
            <h3>Users LoggedIn Today</h3>
            <BsFillGrid3X3GapFill className="card_icon" />
          </div>
          <h1>{stats.usersLoggedInToday || 0}</h1>
        </div>

        <div className="card">
          <div className="card-inner">
            <h3>Orders Today</h3>
            <BsPeopleFill className="card_icon" />
          </div>
          <h1>{stats.artworksSoldToday || 0}</h1>
        </div>

        <div className="card">
          <div className="card-inner">
            <h3>Failed Orders</h3>
            <BsFillBellFill className="card_icon" />
          </div>
          <h1>{stats.totalFailedOrders || 0}</h1>
        </div>
      </div>

      {/* Charts Section */}
      <div className="charts" style={{ display: 'flex', gap: '2rem', flexWrap: 'wrap' }}>
        {/* Bar Chart */}
        <div style={{ flex: '1 1 48%' }}>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={barChartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="value" fill="#FFC107" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Line Chart */}
        <div style={{ flex: '1 1 48%' }}>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={earningsData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="date" />
              <YAxis />
              <Tooltip formatter={(value) => [`₹${value}`, 'Earnings'] } />
              <Legend />
              <Line type="monotone" dataKey="earnings" stroke="#82ca9d" activeDot={{ r: 8 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </main>
  );
}

export default Home;
