
import React, { useState } from 'react';
import { useStripe, useElements, CardElement } from '@stripe/react-stripe-js';
import axios from 'axios';

const CARD_ELEMENT_OPTIONS = {
  style: {
    base: {
      fontSize: '16px',
      color: '#32325d',
      '::placeholder': {
        color: '#a0aec0',
      },
    },
    invalid: {
      color: '#e53e3e',
    },
  },
};

const CheckoutForm = ({ clientSecret, orderId, artworkId }) => {
  const stripe = useStripe();
  const elements = useElements();
  const token = localStorage.getItem('token');

  const [paymentStatus, setPaymentStatus] = useState('');
  const [error, setError] = useState('');
  const [downloadUrl, setDownloadUrl] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!stripe || !elements || !clientSecret) return;

    try {
      const result = await stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card: elements.getElement(CardElement),
        },
      });

      if (result.error) {
        console.error(result.error.message);
        setError(result.error.message);
        return;
      }

      const status = result.paymentIntent.status;
      const mappedStatus = status === 'succeeded' ? 'COMPLETED' : 'FAILED';

      await axios.post(
        'http://localhost:8080/api/payments/confirm',
        {
          transactionId: result.paymentIntent.id,
          status: mappedStatus,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (mappedStatus === 'COMPLETED') {
        setPaymentStatus('Payment successful!');
        fetchDownloadUrl();
      } else {
        setPaymentStatus('Payment failed. Try again.');
      }
    } catch (err) {
      console.error('Error confirming payment:', err);
      setError('Failed to confirm payment.');
    }
  };

  const fetchDownloadUrl = async () => {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/artworks/download/${artworkId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (res.data.downloadUrl) {
        setDownloadUrl(res.data.downloadUrl);
      } else {
        setError('Download URL not found.');
      }
    } catch (err) {
      console.error('Error fetching download URL:', err);
      setError('Unable to fetch download link.');
    }
  };

  return (
    <>
      <form onSubmit={handleSubmit}>
        <div className="p-4 border border-gray-300 rounded-md mb-4 bg-white">
          <CardElement options={CARD_ELEMENT_OPTIONS} />
        </div>

        <button
          type="submit"
          disabled={!stripe || !elements}
          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Pay Now
        </button>
      </form>

      {error && (
        <p className="mt-4 text-center text-red-500 font-semibold">{error}</p>
      )}

      <p style={{ color: "gold", fontWeight: "bold" }}>
        {paymentStatus}
        </p>

{paymentStatus === 'Payment successful!' && downloadUrl && (
  <div className="text-center mt-6">
    <a
      href={downloadUrl}
      className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded inline-block"
      download
      target="_blank"
      rel="noopener noreferrer"
    >
      Download Artwork
    </a>
  </div>
)}




    </>
  );
};

export default CheckoutForm;
