
import React, { useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  Button, TextField, MenuItem, IconButton, Snackbar, Alert, InputAdornment
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import axios from "axios";
import { useHistory } from "react-router-dom";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api";

const Register = ({ open, onClose }) => {
  const history = useHistory();

  const initialFormState = {
    username: "",
    password: "",
    role: "BUYER",
  };

  const [formData, setFormData] = useState(initialFormState);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ text: "", type: "success" });
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleTogglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const validatePassword = (password) => {
    const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,20}$/;
    return pattern.test(password);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    if (!formData.username || !formData.password) {
      setMessage({ text: "❌ All fields are required!", type: "error" });
      setSnackbarOpen(true);
      setLoading(false);
      return;
    }

    if (!validatePassword(formData.password)) {
      setMessage({
        text: "❌ Password must be 8-20 characters and include uppercase, lowercase, number, and special character.",
        type: "error"
      });
      setSnackbarOpen(true);
      setLoading(false);
      return;
    }

    try {
      await axios.post(`${API_BASE_URL}/users/register`, formData);
      setMessage({ text: "✅ Registration successful! Redirecting to login...", type: "success" });
      setSnackbarOpen(true);
      setTimeout(() => {
        history.push("/login");
      }, 2000);
    } catch (error) {
      console.error("Registration error:", error);
      setMessage({
        text: error.response?.data?.message || "❌ Registration failed. Try again.",
        type: "error"
      });
      setSnackbarOpen(true);
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setFormData(initialFormState);
    setMessage({ text: "", type: "success" });
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>
        Register
        <IconButton
          aria-label="close"
          onClick={handleClose}
          sx={{ position: "absolute", right: 8, top: 8, color: (theme) => theme.palette.grey[500] }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent>
        <TextField
          label="Username"
          name="username"
          fullWidth
          margin="dense"
          value={formData.username}
          onChange={handleChange}
        />
        <TextField
          label="Password"
          name="password"
          type={showPassword ? "text" : "password"}
          fullWidth
          margin="dense"
          value={formData.password}
          onChange={handleChange}
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton
                  aria-label="toggle password visibility"
                  onClick={handleTogglePasswordVisibility}
                  edge="end"
                >
                  {showPassword ? <Visibility /> : <VisibilityOff />}
                </IconButton>
              </InputAdornment>
            ),
          }}
          helperText="Must be 8–20 characters, include uppercase, lowercase, number & special character"
        />
        <TextField
          select
          label="Role"
          name="role"
          value={formData.role}
          fullWidth
          margin="dense"
          onChange={handleChange}
        >
          <MenuItem value="BUYER">Buyer</MenuItem>
          <MenuItem value="ARTIST">Artist</MenuItem>
          <MenuItem value="ADMIN">Admin</MenuItem>
        </TextField>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="secondary">Cancel</Button>
        <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading}>
          {loading ? "Registering..." : "Sign Up"}
        </Button>
      </DialogActions>

      <Snackbar open={snackbarOpen} autoHideDuration={3000} onClose={() => setSnackbarOpen(false)}>
        <Alert onClose={() => setSnackbarOpen(false)} severity={message.type} sx={{ width: '100%' }}>
          {message.text}
        </Alert>
      </Snackbar>
    </Dialog>
  );
};

export default Register;


