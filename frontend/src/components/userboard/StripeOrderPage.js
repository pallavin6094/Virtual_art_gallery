// StripeOrderPage.js
import React from "react";
import { Elements } from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import OrderDetails from "./OrderDetails"; // adjust path if needed

const stripePromise = loadStripe("pk_test_51RB7Q3PppH26spkJocAyzrOKd83SMUt3w85qgTVpCmVHeUpG13kWK5uc2S3hbHTufmV6lGVidDLA1gOTMNZIvA7A001CfPo6NA");

const StripeOrderPage = (props) => {
  return (
    <Elements stripe={stripePromise}>
      <OrderDetails {...props} />
    </Elements>
  );
};

export default StripeOrderPage;
