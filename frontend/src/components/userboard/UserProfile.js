
import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import axios from "axios";

const UserProfile = () => {
  const history = useHistory();
  const [email, setEmail] = useState("");
  const [contactNumber, setContactNumber] = useState("");
  const [address, setAddress] = useState("");
  const [saved, setSaved] = useState(false);
  const [isExistingProfile, setIsExistingProfile] = useState(false);

  const [emailError, setEmailError] = useState("");
  const [phoneError, setPhoneError] = useState("");

  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    if (!token || !userId) {
      alert("You are not logged in.");
      return;
    }

    axios
      .get(`http://localhost:8080/api/buyers/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        const data = response.data;
        setEmail(data.email);
        setContactNumber(data.contactNumber);
        setAddress(data.address);
        setSaved(true);
        setIsExistingProfile(true);
      })
      .catch((error) => {
        console.log("No existing profile found. Creating new.");
      });
  }, [token, userId]);

  const getInitials = (text) => {
    if (!text) return "U";
    return text.charAt(0).toUpperCase();
  };

  const validate = () => {
    let valid = true;

    // Reset errors
    setEmailError("");
    setPhoneError("");

    if (!email) {
      setEmailError("Email is required");
      valid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setEmailError("Invalid email format");
      valid = false;
    }

    if (!contactNumber) {
      setPhoneError("Phone number is required");
      valid = false;
    } else if (!/^[6-9][0-9]{9}$/.test(contactNumber)) {
      setPhoneError("Phone number must be exactly 10 digits and start with 6 or 9");
      valid = false;
    }

    return valid;
  };

  const handleSave = async (e) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    try {
      let response;

      if (isExistingProfile) {
        response = await axios.put(
          `http://localhost:8080/api/buyers/update/${userId}`,
          {
            email,
            contactNumber,
            address,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
      } else {
        response = await axios.post(
          `http://localhost:8080/api/buyers/create`,
          {
            email,
            contactNumber,
            address,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setIsExistingProfile(true);
      }

      if (response.status === 200) {
        setSaved(true);
        alert("Profile saved successfully!");
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Failed to save profile.");
    }
  };

  return (
    <div
      style={{
        display: "flex",
        minHeight: "100vh",
        backgroundColor: "#f0f0f0",
        padding: "20px",
      }}
    >
      {saved && (
        <div
          style={{
            width: "300px",
            backgroundColor: "#fff",
            borderRadius: "10px",
            padding: "20px",
            boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
            marginRight: "30px",
            textAlign: "center",
            color: "black",
          }}
        >
          <div
            style={{
              width: "100px",
              height: "100px",
              borderRadius: "50%",
              backgroundColor: "#007bff",
              color: "white",
              fontSize: "36px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              margin: "0 auto 15px",
            }}
          >
            {getInitials(email)}
          </div>
          <h3>{email}</h3>
          <p>{contactNumber}</p>
          <p>{address}</p>
        </div>
      )}

      <form
        onSubmit={handleSave}
        style={{
          flex: 1,
          backgroundColor: "white",
          borderRadius: "10px",
          padding: "30px",
          boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
        }}
      >
        <h2 style={{ marginBottom: "20px", color: "black" }}>
          {isExistingProfile ? "Update Buyer Profile" : "Create Buyer Profile"}
        </h2>

        <label style={{ color: "black" }}>Email:</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          style={inputStyle}
        />
        {emailError && <p style={errorStyle}>{emailError}</p>}

        <label style={{ color: "black" }}>Phone Number:</label>
        <input
          type="tel"
          value={contactNumber}
          onChange={(e) => setContactNumber(e.target.value)}
          style={inputStyle}
        />
        {phoneError && <p style={errorStyle}>{phoneError}</p>}

        <label style={{ color: "black" }}>Address:</label>
        <input
          type="text"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          style={inputStyle}
        />

        <button type="submit" style={saveBtnStyle}>
          {isExistingProfile ? "Update Profile" : "Save Profile"}
        </button>
        <button
          type="button"
          onClick={() => history.push("/dashboard")}
          style={backBtnStyle}
        >
          Back to Dashboard
        </button>
        <button
          type="button"
          onClick={() => history.push("/login")}
          style={backBtnStyle}
        >
          Logout
        </button>
      </form>
    </div>
  );
};

const inputStyle = {
  width: "100%",
  padding: "10px",
  marginBottom: "10px",
  borderRadius: "5px",
  border: "1px solid #ccc",
};

const errorStyle = {
  color: "red",
  marginBottom: "15px",
  fontSize: "14px",
};

const saveBtnStyle = {
  padding: "10px 20px",
  backgroundColor: "#007bff",
  color: "white",
  border: "none",
  borderRadius: "5px",
  cursor: "pointer",
};

const backBtnStyle = {
  marginLeft: "10px",
  padding: "10px 20px",
  backgroundColor: "#ccc",
  border: "none",
  borderRadius: "5px",
  cursor: "pointer",
};

export default UserProfile;

