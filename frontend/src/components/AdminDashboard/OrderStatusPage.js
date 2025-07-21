
import React, { useEffect, useState } from 'react';
import axios from 'axios';

function OrderStatusPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8080/api/admin/dashboard/orders', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setOrders(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching orders:', error);
    }
  };

  const updateStatus = async (orderId, newStatus) => {
    try {
      const token = localStorage.getItem('token');
      const res = await axios.put(
        `http://localhost:8080/api/admin/dashboard/orders/${orderId}/status/${newStatus}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      alert(`Order status updated to ${newStatus}`);
      fetchOrders(); // Refresh data
    } catch (error) {
      console.error('Failed to update status:', error);
      alert('Failed to update order status.');
    }
  };

  if (loading) return <p className="text-center mt-4">Loading orders...</p>;

  return (
    <div style={{ padding: '20px', color: 'white', fontFamily: 'Arial, sans-serif' }}>
      <h2 style={{ fontSize: '22px', fontWeight: 'bold', marginBottom: '20px' }}>Order Status</h2>

      <table border="3" cellPadding="10" cellSpacing="0" style={{ width: '100%', backgroundColor: '#2d2d2d', borderCollapse: 'collapse' }}>
        <thead style={{ backgroundColor: '#444' }}>
          <tr>
            <th align="left">ID</th>
            <th align="left">Order Status</th>
            <th align="left">Order Date</th>
            <th align="left">Total Price</th>
            <th align="left">Change Status</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order, index) => (
            <tr key={order.id} style={{ backgroundColor: index % 2 === 0 ? '#1e1e1e' : '#2d2d2d' }}>
              <td>{order.id}</td>
              <td>{order.orderStatus}</td>
              <td>{order.orderDate}</td>
              <td>₹{order.totalPrice}</td>
              <td>
  <select
    onChange={(e) => updateStatus(order.id, e.target.value)}
    defaultValue=""
    disabled={order.orderStatus === 'COMPLETED'}
    style={{
      backgroundColor: order.orderStatus === 'COMPLETED' ? '#777' : '#555',
      color: 'white',
      padding: '5px 10px',
      borderRadius: '4px',
      border: '1px solid #888',
      cursor: order.orderStatus === 'COMPLETED' ? 'not-allowed' : 'pointer',
    }}
  >
    <option value="" disabled>
      {order.orderStatus === 'COMPLETED' ? 'Finalized' : 'Update'}
    </option>
    <option value="PENDING">Pending</option>
    <option value="COMPLETED">Completed</option>
    <option value="CANCELLED">Cancelled</option>
  </select>
   </td>
   </tr>
     ))}
    </tbody>
    </table>
    </div>
  );
}

export default OrderStatusPage;
