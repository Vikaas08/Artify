import "bootstrap/dist/css/bootstrap.min.css";
import "./index.css";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import Login from "./pages/Login.tsx";
import Register from "./pages/Register.tsx";
import Favourites from "./pages/Favourites.tsx";
import Logout from "./pages/Logout.tsx";
import { AuthProvider } from "./AuthContext.tsx";
import { ToastProvider } from "./ToastContext.tsx";

import { createBrowserRouter, RouterProvider } from "react-router-dom";

const router = createBrowserRouter([
  { path: "/", element: <App /> },
  { path: "/login", element: <Login /> },
  { path: "/register", element: <Register /> },
  { path: "/favourites", element: <Favourites /> },
  { path: "*", element: <div>404 Not Found</div> },
  { path: "/logout", element: <Logout /> },
]);

createRoot(document.getElementById("root")!).render(
  <ToastProvider>
    <AuthProvider>
      <StrictMode>
        <RouterProvider router={router} />
      </StrictMode>
    </AuthProvider>
  </ToastProvider>
);
