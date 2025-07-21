
import React, { useState } from 'react';
import { Link, useHistory } from 'react-router-dom';
import {
  BsBrush, BsGrid1X2Fill, BsFillArchiveFill, BsFillGrid3X3GapFill,
  BsPeopleFill, BsListCheck, BsMenuButtonWideFill, BsFillGearFill
} from 'react-icons/bs';

function Sidebar({ openSidebarToggle, OpenSidebar }) {
  const [showLogout, setShowLogout] = useState(false);
  const history = useHistory();

  const handleLogout = () => {
    localStorage.removeItem('token');
    history.push('/login');
  };

  return (
    <aside id="sidebar" className={openSidebarToggle ? "sidebar-responsive" : ""}>
      <div className='sidebar-title'>
        <div className='sidebar-brand'>
          <BsBrush className='icon_header' /> Art Fusion
        </div>
        <span className='icon close_icon' onClick={OpenSidebar}>X</span>
      </div>

      <ul className='sidebar-list'>
        <li className='sidebar-list-item'>
          <Link to="/admin-dashboard">
            <BsGrid1X2Fill className='icon' /> Dashboard
          </Link>
        </li>
        <li className='sidebar-list-item'>
          <Link to="/admin-dashboard/artworks">
            <BsFillArchiveFill className='icon' /> Artwork
          </Link>
        </li>
        <li className='sidebar-list-item'>
          <Link to="/admin-dashboard/themes">
            <BsFillGrid3X3GapFill className='icon' /> Category
          </Link>
        </li>
        <li className='sidebar-list-item'>
          <Link to="/admin-dashboard/customer">
            <BsPeopleFill className='icon' /> Customers
          </Link>
        </li>
        <li className='sidebar-list-item'>
          <Link to="/admin-dashboard/order-status">
            <BsListCheck className='icon' /> Order Status
          </Link>
        </li>
        

        {/* Settings Toggle with Logout Option */}
        <li
          className='sidebar-list-item'
          style={{ cursor: 'pointer' }}
          onClick={() => setShowLogout(!showLogout)}
        >
          <BsFillGearFill className='icon' /> Settings
        </li>
        {showLogout && (
          <p
            style={{ margin: '5px 20px', cursor: 'pointer', color: 'white' }}
            onClick={handleLogout}
          >
            Logout
          </p>
        )}
      </ul>
    </aside>
  );
}

export default Sidebar;

