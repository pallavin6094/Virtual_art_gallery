
import React from 'react';
import { BsPersonCircle } from 'react-icons/bs';
import './AdminDashboard.css';

function Header() {
  const titleStyle = {
    fontFamily: "'Poppins', sans-serif",
    fontSize: '23px',
    color: 'white',
    margin: 0,
    top:'10px',
  };

  const profileIconStyle = {
    fontSize: '30px',
    color: 'white',
    cursor: 'pointer',
    position: 'absolute',   // Make it positioned absolutely relative to nearest positioned ancestor
    top: '10px',           // Distance from the top
    right: '10px', 

  };

  return (
    <>
      <link
        href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap"
        rel="stylesheet"
      />

      <header className="header">
        <h1 style={titleStyle} top="10px">ADMIN DASHBOARD</h1>
        <BsPersonCircle style={profileIconStyle} title="Profile" />
      </header>
    </>
  );
}

export default Header;
