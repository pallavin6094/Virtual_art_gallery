// components/admin/Settings.js
import React from 'react';
import { useHistory } from 'react-router-dom';

const Settings = () => {
  const history = useHistory();

  const handleLogout = () => {
    localStorage.removeItem('token'); // remove token
    history.push('/login'); // redirect to login
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>Settings</h2>
      <p>History: You logged in at {new Date().toLocaleString()}</p>
      <p 
        onClick={handleLogout}
        style={{
          color: 'blue',
          textDecoration: 'underline',
          cursor: 'pointer',
          marginTop: '10px'
        }}
      >
        Logout
      </p>
    </div>
  );
};

export default Settings;
