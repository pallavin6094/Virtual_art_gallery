
 # 🎨 Virtual Art Gallery

## 📖 About

**Virtual Art Gallery** is a full-stack web application that connects artists and buyers on a single platform.

- 🎨 **Artists:** Can register, create profiles, upload and showcase their artworks, set prices, and manage their listings through a dedicated dashboard.
- 🛍️ **Buyers:** Can browse artwork by category or artist, view details, add art to their cart, make secure payments, and manage their orders.
- 🔐 **Admin:** (Optional, if you have) Manages users, artworks, and transactions for smooth platform operation.

The project includes:
- **Frontend:** A modern React.js web interface for a smooth user experience.
- **Backend:** A secure Spring Boot REST API to handle user data, artwork listings, orders, and payments.
- **Payments:** Integrated with **Stripe** for secure online transactions.
- **Storage:** Uses **Cloudinary** for image hosting and delivery.

This platform makes it easy for artists to reach buyers online and for buyers to discover and purchase unique art from anywhere.

## 📁 Project Structure


- **`frontend/`**: Contains the client-side code built with React.js.
- **`backend/`**: Contains the server-side code built with Spring Boot.

---

## 🚀 Features

✅ Artist Dashboard  
✅ Buyer Dashboard  
✅ Admin Dashboard
✅ Secure Stripe Payment Integration  
✅ Cloudinary Artwork Uploads  
✅ User Authentication  
✅ Order Management

---

## 🛠️ Tech Stack

| Layer      | Technology              |
|------------|-------------------------|
| Frontend   | React.js, HTML, CSS     |
| Backend    | Spring Boot, Java       |
| Database   | (e.g., MySQL, HeidiSQL) |
| Payments   | Stripe                  |
| Storage    | Cloudinary              |


## ⚙️ Setup & Run Locally
Follow these steps to run the Virtual Art Gallery on your local machine.
->Run the Backend (Spring Boot):
  mvn spring-boot:run

->Run the Frontend (React)
  $env:NODE_OPTIONS="--openssl-legacy-provider"
  npm start

📄 License
   This project is licensed under the MIT License.
   See the LICENSE file for details.