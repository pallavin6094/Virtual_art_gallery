import axios from "axios";

const API_URL = "http://localhost:8080/api/users"; // Correcting API URL

export const registerUser = async (userData) => {
  return await axios.post(`${API_URL}/register`, userData);
};

export const loginUser = async (credentials) => {
  const response = await axios.post(`${API_URL}/login`, credentials);
  return response.data; // ✅ Return full response data (including token & role)
};

export const getUserProfile = async (token, username) => {
  return await axios.get(`${API_URL}/profile/${username}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
};  // <-- Missing closing brace was added here ✅

export const updateUserProfile = async (token, userData) => {
  return await axios.put(`${API_URL}/profile`, userData, {
    headers: { Authorization: `Bearer ${token}` },
  });
};


