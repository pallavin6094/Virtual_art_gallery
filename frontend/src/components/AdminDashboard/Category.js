import React, { useEffect, useState } from 'react';
import axios from 'axios';

const Category = () => {
  const [artworks, setArtworks] = useState([]);

  useEffect(() => {
    const token = localStorage.getItem("token");

    axios.get("http://localhost:8080/api/admin/dashboard/artworks", {
      headers: {
        'Authorization': `Bearer ${token}`,
      }
    })
    .then(response => {
      setArtworks(response.data);
    })
    .catch(error => {
      console.error('Error fetching artworks:', error);
    });
  }, []);

  return (
    <div style={{ padding: '20px', color: 'white', fontFamily: 'Arial, sans-serif' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '20px', fontSize: '22px', fontWeight: 'bold' }}>
        Artwork Categories
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
            <th align="left">ArtworkId</th>
            <th align="left">Title</th>
            <th align="left">Category</th>
          </tr>
        </thead>
        <tbody>
          {artworks.map((art, index) => (
            <tr
              key={art.id}
              style={{
                backgroundColor: index % 2 === 0 ? '#1e1e1e' : '#2d2d2d',
              }}
            >
              <td>{art.id}</td>
              <td>{art.title}</td>
              <td>{art.category}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Category;
