import React, { useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  IconButton,
  Snackbar,
  Alert,
  InputAdornment,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import axios from "axios";

const Login = ({ open = false, onClose = () => {}, onLogin = () => {} }) => {
  const [formData, setFormData] = useState({ username: "", password: "" });
  const [message, setMessage] = useState({ text: "", type: "error" });
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [showPassword, setShowPassword] = useState(false); // 👁️ Password visibility state

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleTogglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.username || !formData.password) {
      setMessage({ text: "❌ Please enter both username and password", type: "error" });
      setSnackbarOpen(true);
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/api/users/login", formData, {
        headers: {
          "Content-Type": "application/json",
        },
      });

      const { token, userId, role } = response.data;

      if (token) {
        localStorage.setItem("token", token);
        localStorage.setItem("userId", userId);
        localStorage.setItem("role", role);

        setMessage({ text: "✅ Login successful!", type: "success" });
        setSnackbarOpen(true);

        onLogin(token, userId, role);
      } else {
        setMessage({ text: "❌ Login failed: No token returned", type: "error" });
        setSnackbarOpen(true);
      }
    } catch (error) {
      console.error("🔴 Login API error:", error);
      const errorMsg = error?.response?.data?.message || "❌ Invalid credentials or server error.";
      setMessage({ text: errorMsg, type: "error" });
      setSnackbarOpen(true);
    }
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>
        Login
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{ position: "absolute", right: 8, top: 8 }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <TextField
          label="Username"
          name="username"
          type="text"
          fullWidth
          margin="dense"
          value={formData.username}
          onChange={handleChange}
          required
        />
        <TextField
          label="Password"
          name="password"
          type={showPassword ? "text" : "password"} // 👁️ Toggle between visible & hidden
          fullWidth
          margin="dense"
          value={formData.password}
          onChange={handleChange}
          required
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton
                  onClick={handleTogglePasswordVisibility}
                  edge="end"
                  aria-label="toggle password visibility"
                >
                  {showPassword ? <Visibility /> : <VisibilityOff />}
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="secondary">
          Cancel
        </Button>
        <Button onClick={handleSubmit} color="primary" variant="contained">
          Login
        </Button>
      </DialogActions>

      <Snackbar open={snackbarOpen} autoHideDuration={3000} onClose={() => setSnackbarOpen(false)}>
        <Alert onClose={() => setSnackbarOpen(false)} severity={message.type} sx={{ width: "100%" }}>
          {message.text}
        </Alert>
      </Snackbar>
    </Dialog>
  );
};

export default Login;

