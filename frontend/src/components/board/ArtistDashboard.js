import React, { useEffect, useState } from "react";
import axios from "axios";
import { useHistory } from "react-router-dom";

const ArtistDashboard = () => {
  const [artworks, setArtworks] = useState([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [showProfileDropdown, setShowProfileDropdown] = useState(false);
  const [selectedArtwork, setSelectedArtwork] = useState(null);
  const [isUpdateMode, setIsUpdateMode] = useState(false);
  const [formData, setFormData] = useState({
    title: "",
    category: "",
    description: "",
    imageUrl: "",
    price: "",
  });
  const [earnings, setEarnings] = useState(null);
  const [isEarningsDialogOpen, setIsEarningsDialogOpen] = useState(false);
  const [showInstructions, setShowInstructions] = useState(false);

  const history = useHistory();

  useEffect(() => {
    fetchArtworks();
  }, []);

  const fetchArtworks = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return;
      const response = await axios.get("http://localhost:8080/api/artworks/my-artworks", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setArtworks(response.data);
    } catch (error) {
      console.error("🔴 Error fetching artworks:", error);
    }
  };

  const fetchEarnings = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return;
      const response = await axios.get("http://localhost:8080/api/orders/earnings", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setEarnings(response.data);
      setIsEarningsDialogOpen(true);
    } catch (error) {
      console.error("🔴 Error fetching earnings:", error);
    }
  };

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    if (!token) return;

    try {
      if (isUpdateMode && selectedArtwork) {
        await axios.put(`http://localhost:8080/api/artworks/${selectedArtwork.id}`, formData, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        alert("✅ Artwork updated successfully!");
      } else {
        await axios.post("http://localhost:8080/api/artworks/upload", formData, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        alert("✅ Artwork uploaded successfully!");
      }

      fetchArtworks();
      setFormData({ title: "", category: "", description: "", imageUrl: "", price: "" });
      setIsDialogOpen(false);
      setIsUpdateMode(false);
      setSelectedArtwork(null);
    } catch (error) {
      console.error("🔴 Error submitting artwork:", error);
    }
  };

  const handleDeleteArtwork = async () => {
    if (!selectedArtwork) return;
    const confirmDelete = window.confirm("Are you sure you want to delete this artwork?");
    if (!confirmDelete) return;

    try {
      if (selectedArtwork.status?.toLowerCase() === "sold") {
        alert("❌ Cannot delete. This artwork is already sold.");
        return;
      }

      const token = localStorage.getItem("token");
      await axios.delete(`http://localhost:8080/api/artworks/${selectedArtwork.id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert("✅ Artwork deleted successfully!");
      setArtworks((prev) => prev.filter((art) => art.id !== selectedArtwork.id));
      setSelectedArtwork(null);
    } catch (error) {
      console.error("🔴 Error deleting artwork:", error);
    }
  };

  const handleEditArtwork = () => {
    if (selectedArtwork) {
      setFormData({
        title: selectedArtwork.title,
        category: selectedArtwork.category,
        description: selectedArtwork.description,
        imageUrl: selectedArtwork.imageUrl,
        price: selectedArtwork.price,
      });
      setIsUpdateMode(true);
      setIsDialogOpen(true);
    }
  };

  return (
    <div
      style={{
        backgroundImage: `url(${process.env.PUBLIC_URL}/midnight_blue_wall_bg.png)`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        minHeight: "100vh",
        padding: "20px",
        opacity: 0.9,
      }}
    >
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1 style={{ color: "white", fontSize: "28px", fontWeight: "bold" }}>
          Welcome to Art Gallery!
        </h1>
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <span
            onClick={() => setShowInstructions(true)}
            style={{
              marginLeft: "5px",
              color: "white",
              textDecoration: "underline",
              cursor: "pointer",
            }}
          >
            Instructions
          </span>
          <button
            onClick={() => {
              setIsDialogOpen(true);
              setIsUpdateMode(false);
              setFormData({ title: "", category: "", description: "", imageUrl: "", price: "" });
            }}
            style={{
              backgroundColor: "#007bff",
              color: "white",
              padding: "10px 15px",
              borderRadius: "5px",
              border: "none",
              cursor: "pointer",
            }}
          >
            Sell Your Art
          </button>

          <button
            onClick={fetchEarnings}
            style={{
              backgroundColor: "#28a745",
              color: "white",
              padding: "10px 15px",
              borderRadius: "5px",
              border: "none",
              cursor: "pointer",
            }}
          >
            My Earnings
          </button>

          <div style={{ position: "relative" }}>
            <button
              style={{
                backgroundColor: "transparent",
                border: "none",
                cursor: "pointer",
                fontSize: "20px",
              }}
              onClick={() => history.push("/profile")}
            >
              🧑
            </button>
           
          </div>
        </div>
      </div>

      {showInstructions && (
        <div
          style={{
            position: "fixed",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: "400px",
            background: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0px 4px 8px rgba(0,0,0,0.2)",
            zIndex: 1000,
            color: "black",
          }}
        >
          <h2>📝 Instructions</h2>
          <p>1️⃣ Create your artist profile before selling your art.</p>
          <p>2️⃣ You can update or delete your artwork anytime, unless it is sold.</p>
          <button
            onClick={() => setShowInstructions(false)}
            style={{
              background: "#dc3545",
              color: "white",
              padding: "8px",
              borderRadius: "5px",
              border: "none",
              marginTop: "10px",
              width: "100%",
            }}
          >
            Close
          </button>
        </div>
      )}

      {isEarningsDialogOpen && (
        <div
          style={{
            position: "fixed",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: "400px",
            background: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0px 4px 8px rgba(0,0,0,0.2)",
            zIndex: 1000,
            color: "black",
          }}
        >
          <h2>Your Total Earnings</h2>
          <p style={{ fontSize: "24px", fontWeight: "bold", color: "black" }}>₹{earnings}</p>
          <button
            onClick={() => setIsEarningsDialogOpen(false)}
            style={{
              background: "#dc3545",
              color: "white",
              padding: "8px",
              borderRadius: "5px",
              border: "none",
              width: "100%",
            }}
          >
            Close
          </button>
        </div>
      )}

      <div
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: "20px",
          marginTop: "20px",
        }}
      >
        {artworks.map((art) => (
          <div
            key={art.id}
            style={{
              position: "relative",
              width: "280px",
              borderRadius: "10px",
              overflow: "hidden",
              cursor: "pointer",
            }}
            onClick={() => setSelectedArtwork(art)}
          >
            <img
              src={art.imageUrl}
              alt={art.title}
              style={{
                width: "90%",
                height: "200px",
                objectFit: "cover",
                borderRadius: "10px",
                boxShadow: "0px 4px 15px rgba(255, 215, 0, 0.8)",
                border: "2px solid gold",
              }}
            />
          </div>
        ))}
      </div>

      {isDialogOpen && (
        <div
          style={{
            position: "fixed",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: "400px",
            background: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0px 4px 8px rgba(0,0,0,0.2)",
            zIndex: 1000,
            color: "black",
          }}
        >
          <h2>{isUpdateMode ? "Update Artwork" : "Sell Your Art"}</h2>
          <form
            onSubmit={handleSubmit}
            style={{ display: "flex", flexDirection: "column", gap: "10px" }}
          >
            <input
              type="text"
              name="title"
              placeholder="Title"
              value={formData.title}
              onChange={handleInputChange}
              required
            />
            <input
              type="text"
              name="category"
              placeholder="Category"
              value={formData.category}
              onChange={handleInputChange}
              required
            />
            <textarea
              name="description"
              placeholder="Description"
              value={formData.description}
              onChange={handleInputChange}
              required
            />
            <input
              type="text"
              name="imageUrl"
              placeholder="Image URL"
              value={formData.imageUrl}
              onChange={handleInputChange}
              required
            />
            <input
              type="number"
              name="price"
              placeholder="Price"
              value={formData.price}
              onChange={handleInputChange}
              required
            />
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <button
                type="submit"
                style={{
                  background: "#28a745",
                  color: "white",
                  padding: "8px",
                  borderRadius: "5px",
                  border: "none",
                }}
              >
                {isUpdateMode ? "Update" : "Upload"}
              </button>
              <button
                type="button"
                onClick={() => {
                  setIsDialogOpen(false);
                  setIsUpdateMode(false);
                  setSelectedArtwork(null);
                }}
                style={{
                  background: "#dc3545",
                  color: "white",
                  padding: "8px",
                  borderRadius: "5px",
                  border: "none",
                }}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {selectedArtwork && !isDialogOpen && (
        <div
          style={{
            position: "fixed",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            width: "400px",
            background: "white",
            padding: "20px",
            borderRadius: "10px",
            boxShadow: "0px 4px 8px rgba(0,0,0,0.2)",
            zIndex: 1000,
            color: "black",
          }}
        >
          <h3>{selectedArtwork.title}</h3>
          <p>
            <strong>Category:</strong> {selectedArtwork.category}
          </p>
          <p>
            <strong>Description:</strong> {selectedArtwork.description}
          </p>
          <p>
            <strong>Status:</strong> {selectedArtwork.status}
          </p>
          <p style={{ fontWeight: "bold", fontSize: "18px" }}>
            Price: ₹{selectedArtwork.price}
          </p>
          <div
            style={{ display: "flex", justifyContent: "space-between", marginTop: "10px" }}
          >
            <button
              onClick={() => setSelectedArtwork(null)}
              style={{
                background: "#dc3545",
                color: "white",
                padding: "8px",
                borderRadius: "5px",
              }}
            >
              Close
            </button>
            {selectedArtwork?.status?.toLowerCase() !== "sold" && (
              <button
                onClick={handleEditArtwork}
                style={{
                  background: "#ffc107",
                  color: "black",
                  padding: "8px",
                  borderRadius: "5px",
                  cursor: "pointer",
                }}
              >
                Update
              </button>
            )}
            {selectedArtwork?.status?.toLowerCase() !== "sold" && (
              <button
                onClick={handleDeleteArtwork}
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
        </div>
      )}
    </div>
  );
};

export default ArtistDashboard;
