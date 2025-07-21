
import React, { useState, useEffect } from "react";
import axios from "axios";
import { FaDownload, FaTrash } from "react-icons/fa";
import { useHistory } from "react-router-dom"; // ✅ for navigation (React Router v5)

const CartPage = () => {
  const [cartItems, setCartItems] = useState([]);
  const [purchasedIndexes, setPurchasedIndexes] = useState([]);
  const history = useHistory(); // ✅ initialize history

  useEffect(() => {
    const fetchCartItems = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/cart/items", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        setCartItems(response.data);
      } catch (error) {
        console.error("Failed to fetch cart items:", error);
      }
    };

    fetchCartItems();
  }, []);

  const handleBuyNow = async (item, index) => {
  const token = localStorage.getItem("token");

  try {
    const response = await axios.post(
      `http://localhost:8080/api/orders/checkout/single/${item.cartItemId}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    console.log("Order placed:", response.data);
    setPurchasedIndexes([...purchasedIndexes, index]);
    alert(`Order placed successfully for "${item.artworkTitle}"`);

    // ✅ Redirect to Order Details page
    const { orderId } = response.data;
    history.push(`/orders/${orderId}`);

  } catch (error) {
  console.log("🛑 Full error object:", error);
  console.log("📦 error.response:", error.response);
  console.log("📄 error.response.data:", error.response?.data);

  const status = error.response?.status;

  if (status === 403) {
    alert("This artwork is already sold. You cannot place an order for it.");
  } else {
    alert("Failed to place order. Please try again.");
  }
}

  };

  const handleDownload = (imageUrl, title) => {
    const link = document.createElement("a");
    link.href = imageUrl;
    link.download = `${title || "artwork"}.jpg`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleCancel = async (cartItemId, index) => {
    const token = localStorage.getItem("token");

    if (!token) {
      console.error("No token found in localStorage");
      return;
    }

    try {
      const response = await axios.delete(`http://localhost:8080/api/cart/remove/${cartItemId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      console.log("Delete response:", response.data);

      const updatedItems = cartItems.filter((_, idx) => idx !== index);
      setCartItems(updatedItems);
      setPurchasedIndexes(purchasedIndexes.filter((i) => i !== index));
    } catch (error) {
      console.error("Error removing item from cart:", error.response || error);
    }
  };

  if (!cartItems || cartItems.length === 0) {
    return <h2 style={{ textAlign: "center", marginTop: "50px" }}>Your cart is empty.</h2>;
  }

  return (
    <div
      style={{
        padding: "20px",
        backgroundImage: `url("/website.png")`,
        backgroundSize: "100% 100%",
        backgroundRepeat: "no-repeat",
        backgroundPosition: "center",
        backgroundAttachment: "fixed",
        minHeight: "100vh",
        width: "100vw",
        overflowX: "hidden",
      }}
    >
      <h2 style={{ marginBottom: "30px", textAlign: "left",color:"midnightblue" }}>Shopping Cart</h2>

      <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-start", gap: "30px" }}>
        {cartItems.map((item, index) => {
          return (
            <div
              key={index}
              style={{
                width: "280px",
                padding: "20px",
                borderRadius: "10px",
                backgroundColor: "#f9f9f9",
                boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                color:"midnightblue"
              }}
            >
              {item.imageUrl ? (
                <img
                  src={item.imageUrl}
                  alt={item.artworkTitle || `Art ${index}`}
                  style={{
                    width: "100%",
                    height: "200px",
                    objectFit: "cover",
                    borderRadius: "8px",
                    marginBottom: "10px",
                  }}
                />
              ) : (
                <div
                  style={{
                    width: "100%",
                    height: "200px",
                    backgroundColor: "#e0e0e0",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    borderRadius: "8px",
                    marginBottom: "10px",
                    color: "#666",
                    fontSize: "14px",
                  }}
                >
                  No Image Available
                </div>
              )}

              <h3 style={{ margin: "10px 0 5px" }}>{item.artworkTitle || "Artwork"}</h3>

              {!purchasedIndexes.includes(index) ? (
  item.ordered ? (
    <button
      disabled
      style={{
        padding: "10px 20px",
        fontSize: "16px",
        backgroundColor: "gray",
        color: "white",
        border: "none",
        borderRadius: "5px",
        marginBottom: "10px",
        cursor: "not-allowed",
      }}
    >
      Already Ordered
    </button>
  ) : (
    <button
      onClick={() => handleBuyNow(item, index)}
      style={{
        padding: "10px 20px",
        fontSize: "16px",
        backgroundColor: "#28a745",
        color: "white",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer",
        marginBottom: "10px",
      }}
    >
      Order
    </button>
  )

              ) : (
                <button
                  onClick={() => handleDownload(item.imageUrl, item.artworkTitle)}
                  style={{
                    padding: "10px 20px",
                    fontSize: "16px",
                    backgroundColor: "#ffcc00",
                    color: "black",
                    border: "none",
                    borderRadius: "5px",
                    display: "flex",
                    alignItems: "center",
                    gap: "8px",
                    cursor: "pointer",
                    marginBottom: "10px",
                  }}
                >
                  <FaDownload /> Download
                </button>
              )}

              <button
                onClick={() => handleCancel(item.cartItemId, index)}
                style={{
                  padding: "6px 12px",
                  fontSize: "14px",
                  backgroundColor: "#dc3545",
                  color: "white",
                  border: "none",
                  borderRadius: "5px",
                  cursor: "pointer",
                  display: "flex",
                  alignItems: "center",
                  gap: "6px",
                }}
              >
                <FaTrash />
                Cancel
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default CartPage;
