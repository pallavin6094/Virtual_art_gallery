
import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import axios from "axios";

const Profile = () => {
  const history = useHistory();

  const [profileImage, setProfileImage] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [bio, setBio] = useState("");
  const [address, setAddress] = useState("");
  const [ratings, setRatings] = useState("");
  const [experience, setExperience] = useState("");
  const [specialization, setSpecialization] = useState("");
  const [isEdit, setIsEdit] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem("token");
        const userId = localStorage.getItem("userId");

        if (!token || !userId) return;

        const response = await axios.get(`http://localhost:8080/api/artists/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const data = response.data;

        if (data) {
          setEmail(data.email || "");
          setPhone(data.phoneNumber || "");
          setBio(data.bio || "");
          setAddress(data.location || "");
          setRatings(data.rating || "");
          setExperience(data.experience || "");
          setSpecialization(data.specialization || "");
          setProfileImage(data.profileImage || "");
          setIsEdit(true);
        }
      } catch (error) {
        console.log("No existing profile. You can create one.");
        setIsEdit(false);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const validateEmail = (email) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  const validatePhone = (phone) =>
    /^[6-9][0-9]{9}$/.test(phone);

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setProfileImage(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();

    // --- FRONTEND VALIDATION ---
    if (!email || !validateEmail(email)) {
      alert("Please enter a valid email.");
      return;
    }

    if (!phone || !validatePhone(phone)) {
      alert("Please enter a valid 10-digit phone number starting with 6-9.");
      return;
    }

    if (!profileImage || !bio || !address || !ratings || !experience || !specialization) {
      alert("All fields including profile image are required.");
      return;
    }

    const artistData = {
      email,
      phoneNumber: phone,
      bio,
      location: address,
      rating: ratings,
      experience,
      specialization,
      profileImage,
    };

    try {
      const token = localStorage.getItem("token");
      const userId = localStorage.getItem("userId");

      if (!token || !userId) {
        alert("Session expired. Please login again.");
        return;
      }

      if (isEdit) {
        await axios.put(`http://localhost:8080/api/artists/update/${userId}`, artistData, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
        alert("Artist profile updated successfully!");
      } else {
        await axios.post("http://localhost:8080/api/artists/create", artistData, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
        alert("Artist profile created successfully!");
        setIsEdit(true);
      }
    } catch (error) {
      console.error("Error saving profile:", error);
      alert("Failed to save profile. Please try again.");
    }
  };

  if (loading) return <div style={{ padding: "40px" }}>Loading profile...</div>;

  return (
    <div style={{ display: "flex", minHeight: "100vh", padding: "20px", backgroundColor: "#f1f1f1", color: "black" }}>
      {isEdit && (
        <div style={cardStyle}>
          <img src={profileImage} alt="Profile" style={profileImageStyle} />
          <h3>{email}</h3>
          <p><strong>Phone:</strong> {phone}</p>
          <p><strong>Bio:</strong> {bio}</p>
          <p><strong>Address:</strong> {address}</p>
          <p><strong>Ratings:</strong> {ratings} / 5</p>
          <p><strong>Experience:</strong> {experience}</p>
          <p><strong>Specialization:</strong> {specialization}</p>
        </div>
      )}

      <form onSubmit={handleSave} style={formStyle}>
        <h2 style={{ marginBottom: "20px" }}>{isEdit ? "Update Artist Profile" : "Create Artist Profile"}</h2>

        <label>Upload Profile Image:</label>
        <input type="file" accept="image/*" onChange={handleImageChange} style={inputStyle} />
        {profileImage && <img src={profileImage} alt="Preview" style={previewImageStyle} />}

        <label>Email:</label>
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} style={inputStyle} required />

        <label>Phone Number:</label>
        <input type="tel" value={phone} onChange={(e) => setPhone(e.target.value)} style={inputStyle} required />

        <label>Bio:</label>
        <textarea value={bio} onChange={(e) => setBio(e.target.value)} style={{ ...inputStyle, height: "80px" }} required />

        <label>Address:</label>
        <input type="text" value={address} onChange={(e) => setAddress(e.target.value)} style={inputStyle} required />

        <label>Ratings (1 to 5):</label>
        <input type="number" min="1" max="5" value={ratings} onChange={(e) => setRatings(e.target.value)} style={inputStyle} required />

        <label>Experience (e.g. 2 years):</label>
        <input type="text" value={experience} onChange={(e) => setExperience(e.target.value)} style={inputStyle} required />

        <label>Specialization:</label>
        <input type="text" value={specialization} onChange={(e) => setSpecialization(e.target.value)} style={inputStyle} required />

        <button type="submit" style={buttonStyle}>
          {isEdit ? "Update Profile" : "Save Profile"}
        </button>
        <button
          type="button"
          onClick={() => history.push("/artist-dashboard")}
          style={{ ...buttonStyle, backgroundColor: "#888", marginLeft: "10px" }}
        >
          Back to Dashboard
        </button>
        <button
          type="button"
          onClick={() => history.push("/login")}
          style={{ ...buttonStyle, backgroundColor: "red", marginLeft: "10px" }}
        >
          Logout
        </button>
      </form>
    </div>
  );
};

// Styles
const cardStyle = {
  width: "300px",
  backgroundColor: "#fff",
  borderRadius: "10px",
  padding: "20px",
  boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
  marginRight: "30px",
  textAlign: "center",
};

const profileImageStyle = {
  width: "100px",
  height: "100px",
  borderRadius: "50%",
  objectFit: "cover",
  marginBottom: "15px",
};

const previewImageStyle = {
  width: "80px",
  height: "80px",
  borderRadius: "50%",
  objectFit: "cover",
  marginBottom: "20px",
};

const formStyle = {
  flex: 1,
  backgroundColor: "white",
  borderRadius: "10px",
  padding: "30px",
  boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
};

const inputStyle = {
  width: "100%",
  padding: "10px",
  marginBottom: "20px",
  borderRadius: "5px",
  border: "1px solid #ccc",
};

const buttonStyle = {
  padding: "10px 20px",
  backgroundColor: "#007bff",
  color: "white",
  border: "none",
  borderRadius: "5px",
  cursor: "pointer",
};

export default Profile;

