
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useStripe, useElements, CardElement } from '@stripe/react-stripe-js';
import axios from 'axios';

const CARD_ELEMENT_OPTIONS = {
  style: {
    base: {
      fontSize: '16px',
      color: '#ffffff',
      '::placeholder': {
        color: '#a0aec0',
      },
    },
    invalid: {
      color: '#e53e3e',
    },
  },
};

const OrderDetails = () => {
  const { orderId } = useParams();
  const numericOrderId = Number(orderId);
  const stripe = useStripe();
  const elements = useElements();

  const [payment, setPayment] = useState(null);
  const [clientSecret, setClientSecret] = useState('');
  const [loading, setLoading] = useState(true);
  const [paymentStatus, setPaymentStatus] = useState('');
  const [error, setError] = useState('');
  const [downloadUrl, setDownloadUrl] = useState(''); // <-- NEW state
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchPaymentIntentAndDetails = async () => {
      try {
        const intentRes = await axios.post(
          'http://localhost:8080/api/payments/create-payment-intent',
          { orderId: numericOrderId },
          { headers: { Authorization: `Bearer ${token}` } }
        );

        setClientSecret(intentRes.data.clientSecret);

        const paymentRes = await axios.get(
          `http://localhost:8080/api/payments/${orderId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        setPayment(paymentRes.data);
        setLoading(false);
      } catch (err) {
        console.error('Error during payment setup:', err);
        setError(
          err.response?.status === 403
            ? 'You are not authorized to view this order.'
            : 'Something went wrong. Please try again later.'
        );
        setLoading(false);
      }
    };

    fetchPaymentIntentAndDetails();
  }, [orderId, token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!stripe || !elements) return;

    const result = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement),
      },
    });

    if (result.error) {
      setError(result.error.message);
    } else {
      const status = result.paymentIntent.status;
      const mappedStatus =
        status === 'succeeded'
          ? 'COMPLETED'
          : status === 'processing'
          ? 'PENDING'
          : 'FAILED';

      try {
        await axios.post(
          'http://localhost:8080/api/payments/confirm',
          {
            transactionId: result.paymentIntent.id,
            status: mappedStatus,
          },
          { headers: { Authorization: `Bearer ${token}` } }
        );

        setPaymentStatus(
          mappedStatus === 'COMPLETED'
            ? 'Payment successful!'
            : 'Payment submitted but not completed.'
        );

        // 🔥 After payment success, fetch download URL
        if (mappedStatus === 'COMPLETED') {
          const downloadRes = await axios.get(
            `http://localhost:8080/api/artworks/download/${payment.artworkId}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          setDownloadUrl(downloadRes.data.downloadUrl); // <-- Set download URL
        }

      } catch (err) {
        console.error('Error confirming payment:', err);
        setError('Failed to confirm payment to server.');
      }
    }
  };

  if (loading) return <p className="text-center text-white mt-10">Loading...</p>;
  if (error) return <p className="text-center text-red-500 mt-10">{error}</p>;

  return (
    <div
      className="flex justify-center items-center min-h-screen w-full px-4"
      style={{
        padding: "20px",
        backgroundImage: `url("/artist.png")`,
        backgroundSize: "100% 100%",
        backgroundRepeat: "no-repeat",
        backgroundPosition: "center",
        backgroundAttachment: "fixed",
        minHeight: "100vh",
        width: "100vw",
        overflowX: "hidden",
      
      }}
    >
      <div className="w-full max-w-md bg-black bg-opacity-70 rounded-2xl shadow-xl p-8">
        <h2 style={{ color: 'white', fontSize: '24px', marginBottom: '20px' }}>Order Payment</h2>
  
        <div style={{ padding: '10px', marginBottom: '20px' }}>
          <p style={{ color: 'white' }}>
            <strong>Transaction ID:</strong> {payment.transactionId}
          </p>
          <p style={{ color: 'white' }}>
            <strong>Amount:</strong> ₹{payment.amount}
          </p>
          <p style={{ color: 'white' }}>
            <strong>ArtworkId:</strong> {payment.artworkId}
          </p>
        </div>
  
        <form onSubmit={handleSubmit}>
          <div className="p-4 border border-gray-300 rounded-md mb-4 bg-white">
            <CardElement options={CARD_ELEMENT_OPTIONS} />
          </div>
  
          <button
            type="submit"
            disabled={!stripe || !elements}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded transition duration-200"
          >
            Pay Now
          </button>
        </form>
  
        <p style={{ color: "gold", fontWeight: "bold" }}>
        {paymentStatus}
        </p>


  
        {/* Show Download Button if downloadUrl is available */}
        {downloadUrl && (
          <div style={{ marginTop: '24px', textAlign: 'center' }}>
            <a
              href={downloadUrl}
              download
              style={{
                display: 'inline-block',
                backgroundColor: '#16a34a',
                color: '#fff',
                fontWeight: 'bold',
                padding: '10px 24px',
                borderRadius: '8px',
                textDecoration: 'none',
                transition: 'background-color 0.2s ease-in-out'
              }}
            >
              Download Artwork
            </a>
          </div>
        )}
      </div>
    </div>
  );
};

export default OrderDetails;
