import React from "react";
import "./BackgroundVideo.css"; // Import CSS for styling

const BackgroundVideo = () => {
  return (
    <div className="video-container">
      <video autoPlay loop muted className="background-video">
        <source src="/videos/6214509-uhd_3840_2160_25fps.mp4" type="video/mp4" />
        Your browser does not support the video tag.
      </video>
      <div className="content">
        <h2>Art is not what you see,but what makes others see</h2>
        <p>Explore amazing content!</p>
      </div>
    </div>
  );
};

export default BackgroundVideo;
