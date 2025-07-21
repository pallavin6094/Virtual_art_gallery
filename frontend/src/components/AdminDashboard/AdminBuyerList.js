
import React, { useEffect, useState } from 'react';
import axios from 'axios';

const AdminBuyerList = () => {
  const [users, setUsers] = useState([]);
  const token = localStorage.getItem('token'); // or however you're storing it

  useEffect(() => {
    axios.get("http://localhost:8080/api/admin/dashboard/users", {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    .then(response => setUsers(response.data))
    .catch(error => console.error('Error fetching users:', error));
  }, [token]);

  const handleDelete = (userId) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      axios.delete(`http://localhost:8080/api/admin/dashboard/users/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      .then(() => {
        setUsers(prev => prev.filter(user => user.id !== userId));
      })
      .catch(error => console.error('Error deleting user:', error));
    }
  };

 return (
  <div style={{ padding: '20px', color: 'white', fontFamily: 'Arial, sans-serif' }}>
    <h2 style={{ textAlign: 'center', marginBottom: '20px', fontSize: '22px', fontWeight: 'bold' }}>
      Customers
    </h2>

    <table
      style={{
        width: '100%',
        borderCollapse: 'collapse',
        backgroundColor: '#2d2d2d',
      }}
      border="3"
      cellPadding="10"
      cellSpacing="0"
    >
      <thead style={{ backgroundColor: '#444' }}>
        <tr>
          <th align="left">Id</th>
          <th align="left">Username</th>
          <th align="left">Role</th>
          <th align="left">Last Login</th>
          <th align="left">Action</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user, index) => (
          <tr
            key={user.id}
            style={{
              backgroundColor: index % 2 === 0 ? '#1e1e1e' : '#2d2d2d',
            }}
          >
            <td>{user.id}</td>
            <td>{user.username}</td>
            <td>{user.role}</td>
            <td>{user.lastLogin || 'N/A'}</td>
            <td>
              <button
                onClick={() => handleDelete(user.id)}
                style={{
                  backgroundColor: '#e74c3c',
                  color: 'white',
                  border: 'none',
                  padding: '6px 12px',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                Delete
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);

};

export default AdminBuyerList;

