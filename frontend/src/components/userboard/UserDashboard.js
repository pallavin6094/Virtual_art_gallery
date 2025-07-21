
import React, { useEffect, useState } from "react";
import axios from "axios";
import { FaUserCircle, FaShoppingCart, FaImages } from "react-icons/fa";
import { useHistory } from "react-router-dom";
import "./UserDashboard.css";

const UserDashboard = () => {
  const [artworks, setArtworks] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedArtwork, setSelectedArtwork] = useState(null);
  const [artistDetails, setArtistDetails] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showInstructions, setShowInstructions] = useState(false);

  const history = useHistory();

  useEffect(() => {
    const fetchArtworks = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get("http://localhost:8080/api/artworks", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setArtworks(response.data);
      } catch (error) {
        console.error("Error fetching artworks:", error);
      }
    };

    fetchArtworks();
  }, []);

  const handleImageClick = (artwork) => {
    setSelectedArtwork(artwork);
    setArtistDetails({
      name: artwork.artistName || artwork.artist?.user?.username || "Unknown",
      rating: artwork.artistRating || artwork.artist?.rating || "N/A",
      specialization: artwork.artistSpecialization || artwork.artist?.specialization || "N/A",
    });
    setShowModal(true);
  };

  const handleAddToCart = async (artwork) => {
    try {
      const token = localStorage.getItem("token");
      await axios.post(
        "http://localhost:8080/api/cart/add",
        { artworkId: artwork.id },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      alert("✅ Artwork added to cart!");
      history.push("/cart");
    } catch (error) {
      console.error("Error adding to cart:", error);
      const errorMessage =
        typeof error?.response?.data === "string"
          ? error.response.data
          : error?.response?.data?.message;
  if (error?.response?.status === 403) {
  alert("❌ Failed to add to cart. Please read the instructions.");
}  else {
  alert("❌ Please try again or check your internet connection.");
}
    }
  };

  const filteredArtworks = artworks.filter(
    (art) =>
      art.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      art.artistName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      art.artist?.user?.username?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="page">
      {/* Profile and Cart Icons */}
      <div className="profile-cart-header">
        <div className="profile-cart-container">
           {/* ✅ Instruction Link beside Purchased Art */}
          <span
            onClick={() => setShowInstructions(true)}
            style={{ marginLeft: "10px", cursor: "pointer", color: "white", textDecoration: "underline" }}
          >
            Instructions
          </span>

          <div className="tooltip" style={{ marginLeft: "10px"}}></div>
          <FaImages size={30} className="icon" onClick={() => history.push("/mypurchases")} />

          <div className="tooltip" style={{ marginLeft: "10px" }}></div>
          <FaShoppingCart size={30} className="icon" onClick={() => history.push("/cart")} />

          <div className="tooltip"></div>
          <FaUserCircle size={32} className="icon" onClick={() => history.push("/userprofile")} />
        </div>

        <h1 className="explore-title">Explore Artworks</h1>
      </div>

      {/* Search Bar */}
      <div style={{ marginBottom: "2rem" }}>
        <input
          type="text"
          placeholder="Search by title or artist"
          className="search"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {/* Artwork Grid */}
      <div className="art-grid">
        {filteredArtworks.map((artwork, index) => (
          <div
            key={artwork.id || index}
            style={{
              background: "white",
              color: "black",
              borderRadius: "10px",
              overflow: "hidden",
              boxShadow: "0 4px 15px gold",
              height: "200px",
              width: "300px",
              display: "flex",
              alignItems: "center",
              flexDirection: "column",
              justifyContent: "center",
            }}
          >
            <img
              src={artwork.imageUrl}
              alt={artwork.title}
              className="card-img"
              onClick={() => handleImageClick(artwork)}
              style={{ cursor: "pointer" }}
            />
          </div>
        ))}

        {filteredArtworks.length === 0 && (
          <p className="no-data">No artworks found.</p>
        )}
      </div>

      {/* Modal */}
      {showModal && selectedArtwork && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <img
              src={selectedArtwork.imageUrl}
              alt={selectedArtwork.title}
              className="modal-img"
            />
            <h2 className="modal-title">{selectedArtwork.title}</h2>
            <p className="modal-desc">{selectedArtwork.description}</p>
            <p className="modal-price">Price: ₹{selectedArtwork.price?.toFixed(2)}</p>

            {artistDetails && (
              <>
                <p><strong>Artist:</strong> {artistDetails.name}</p>
                <p><strong>Rating:</strong> {artistDetails.rating} ⭐</p>
              </>
            )}

            {selectedArtwork?.status ? (
              selectedArtwork.status === "SOLD" ? (
                <p style={{ color: "red", fontWeight: "bold" }}>
                  This artwork has already been sold.
                </p>
              ) : (
                <button className="btn" onClick={() => handleAddToCart(selectedArtwork)}>
                  Add to Cart
                </button>
              )
            ) : (
              <p style={{ color: "orange" }}>Loading status...</p>
            )}

            <button className="close-btn" onClick={() => setShowModal(false)}>
              Close
            </button>
          </div>
        </div>
      )}

      {/* ✅ Instructions Modal */}
      {showInstructions && (
        <div className="modal-overlay" onClick={() => setShowInstructions(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2>📝 Instructions</h2>
            <p>1️⃣ First create your buyer profile before adding to cart.</p>
            <p>2️⃣ If “Failed to add to cart” shows up:</p>
            <ul>
              <li>✅ you have'nt created your profile.
              <span
              style={{ color: "gold", textDecoration: "underline", cursor: "pointer" }}
              onClick={() => {
              history.push("/userprofile");
              setShowInstructions(false); // optional: close modal
               }}
               >
               Click here to create profile.
               </span></li>
              <li>✅ It might be already in your cart.
                <span 
                style={{ color: "gold", textDecoration: "underline", cursor: "pointer" }}
                onClick={() => {
              history.push("/cart");
              setShowInstructions(false); // optional: close modal
               }}
               >Click here for cart page </span>
              </li>
              <li>✅ Or check your internet and try again.</li>
            </ul>
            <p>3️⃣ After you purchase your artwork, you can see it in the Purchased Artwork page and download it.
              <span
              style={{ color: "gold", textDecoration: "underline", cursor: "pointer" }}
                onClick={() => {
              history.push("/mypurchases");
              setShowInstructions(false); // optional: close modal
               }}
               >Click here </span>

            </p>
            <button className="close-btn" onClick={() => setShowInstructions(false)}>
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default UserDashboard;
