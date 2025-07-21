// AdminArtworks.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';

const AdminArtworks = () => {
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

  const handleDelete = async (artworkId) => {
    try {
      const token = localStorage.getItem("token");

      await axios.delete(`http://localhost:8080/api/admin/dashboard/artworks/${artworkId}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        }
      });

      // Update local state to reflect the deleted item
      setArtworks(prev => prev.filter(art => art.id !== artworkId));
    } catch (error) {
      console.error('Error deleting artwork:', error);
      alert("Failed to delete artwork.since it has been sold");
    }
  };

  return (
    <div>
      <h2>All Artworks</h2>
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))", gap: "16px" }}>
        {artworks.map(art => (
          <div key={art.id} style={{ border: "1px solid #ccc", padding: "16px", borderRadius: "8px" }}>
            <img src={art.imageUrl} alt={art.title} style={{ width: "100%", height: "200px", objectFit: "cover" }} />
            <h3>{art.title}</h3>
            <p><strong>ArtworkId:</strong>{art.id}</p>
            <p><strong>Price:</strong> ₹{art.price}</p>
            <p>{art.description}</p>
            <p><strong>Status:</strong>{art.status}</p>
            <p><strong>Created At:</strong>{art.createdAt}</p>
            {/* <button onClick={() => handleDelete(art.id)} style={{ backgroundColor: "red", color: "white", border: "none", padding: "8px", borderRadius: "4px" }}>
              Delete
            </button> */}
            {art?.status?.trim().toLowerCase() === "available" && (
  <button
    onClick={() => handleDelete(art.id)} // ✅ Correct
    style={{
      backgroundColor: "red",
      color: "white",
      padding: "8px",
      border: "none",
      borderRadius: "5px",
      cursor: "pointer",
    }}
  >
    Delete
  </button>
)}




          </div>
        ))}
      </div>
    </div>
  );
};

export default AdminArtworks;
