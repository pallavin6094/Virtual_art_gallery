
import React, { useEffect, useState } from "react";
import axios from "axios";

const PurchasedArtwork = () => {
  const [artworks, setArtworks] = useState([]);
  const [error, setError] = useState("");
  const [selectedImage, setSelectedImage] = useState(null);

  useEffect(() => {
    const fetchPurchasedArtworks = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get("http://localhost:8080/api/orders/buyer/purchased-artworks", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setArtworks(response.data);
      } catch (err) {
        console.error(err);
        setError("Failed to fetch purchased artworks.");
      }
    };

    fetchPurchasedArtworks();
  }, []);

  const handleDownload = (art) => {
    setSelectedImage(art);
  };

  return (
    <div style={{ padding: "24px" }}>
      <h2 style={{ textAlign: "center", marginBottom: "24px", color: "white" }}>My Purchased Artworks</h2>

      {error && <p style={{ color: "red", textAlign: "center" }}>{error}</p>}

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))", gap: "16px" }}>
        {artworks.map((art) => (
          <div key={art.artworkId} style={{ border: "1px solid #ccc", padding: "16px", borderRadius: "8px" }}>
            <img
              src={art.imageUrl}
              alt={art.title}
              style={{ width: "100%", height: "200px", objectFit: "cover", borderRadius: "4px" }}
            />
            <h3 style={{ color: 'white' }}>{art.title}</h3>
            <p style={{ color: 'white' }}><strong>Artwork ID:</strong> {art.artworkId}</p>
            <p style={{ color: 'white' }}><strong>Purchased Date:</strong> {new Date(art.purchaseDate).toLocaleString()}</p>
            <button
              onClick={() => handleDownload(art)}
              style={{
                marginTop: "10px",
                backgroundColor: "#3498db",
                color: "white",
                border: "none",
                padding: "8px 12px",
                borderRadius: "4px",
                cursor: "pointer",
              }}
            >
              View & Download
            </button>
          </div>
        ))}
      </div>

      {/* Modal */}
      {selectedImage && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            zIndex: 9999,
            width: "100vw",
            height: "100vh",
            backgroundColor: "rgba(0,0,0,0.7)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
          onClick={() => setSelectedImage(null)}
        >
          <div
            style={{
              backgroundColor: "white",
              padding: "20px",
              borderRadius: "8px",
              maxWidth: "90%",
              maxHeight: "90%",
              overflowY: "auto",
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <h2 style={{ marginBottom: "16px" }}>{selectedImage.title}</h2>
            <img
              src={selectedImage.imageUrl}
              alt={selectedImage.title}
              style={{ width: "100%", maxHeight: "500px", objectFit: "contain", marginBottom: "12px" }}
            />
            <p><strong>Image URL:</strong> <a href={selectedImage.imageUrl} target="_blank" rel="noreferrer">{selectedImage.imageUrl}</a></p>
            
          </div>
        </div>
      )}
    </div>
  );
};

export default PurchasedArtwork;

