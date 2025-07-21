import React from "react";
import "./AboutUs.css"; // ✅ Add styles for About Us page

const AboutUs = () => {
  return (
    <div className="about-container">
      <h1>About ArtFusion</h1>
      <p>
        Welcome to <strong>ArtFusion</strong>, a one-of-a-kind virtual gallery that bridges the gap between artists and art lovers.  
        Our platform is designed to celebrate creativity, empower artists, and offer a seamless experience for buyers looking for unique artwork.
      </p>

      <h2>🌟 Our Mission</h2>
      <p>
        At ArtFusion, our mission is to support and inspire artists by providing them with a dynamic space to showcase their work and connect with a global audience.  
        We believe that every artist deserves a chance to be recognized, and every art lover deserves access to authentic, high-quality artwork.
      </p>

      <h2>🎨 What We Offer</h2>
      <ul>
        <li>🖌️ **Artist Marketplace** – A dedicated space where artists can create profiles, upload their artwork, and sell their pieces effortlessly.</li>
        <li>🛍️ **Seamless Buying Experience** – Art enthusiasts can explore a diverse range of paintings, digital art, sculptures, and more.</li>
        <li>🌎 **Global Community** – Connect with artists and buyers from around the world.</li>
        <li>💬 **Engagement & Inspiration** – Participate in discussions, attend virtual exhibitions, and share your artistic journey.</li>
      </ul>

      <h2>🚀 Why Choose ArtFusion?</h2>
      <p>
        ArtFusion is more than just an art marketplace; it's a thriving community that fosters artistic growth and collaboration.  
        We are committed to helping artists turn their passion into a profession while providing buyers with an immersive and inspiring art-buying experience.
      </p>

      <h2>📢 Join Us Today!</h2>
      <p>
        Whether you're an artist looking to showcase your talent or an art enthusiast searching for the perfect piece, ArtFusion is the place for you.  
        Sign up now and become part of an ever-growing community where art truly comes to life!
      </p>
    </div>
  );
};

export default AboutUs;
