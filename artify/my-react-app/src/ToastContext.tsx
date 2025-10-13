import React, { createContext, useContext, useState, ReactNode } from "react";

type Toast = {
  id: number;
  message: string;
  type: "success" | "danger";
};

type ToastContextType = {
  toasts: Toast[];
  addToast: (toast: string | Toast, type: "success" | "danger") => void;
  removeToast: (id: number) => void;
};

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export const ToastProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [toasts, setToasts] = useState<Toast[]>([]);

  // Add toast function
  const addToast = (toast: string | Toast, type: "success" | "danger") => {
    let newToast: Toast;

    if (typeof toast === "string") {
      newToast = { id: Date.now(), message: toast, type };
    } else {
      newToast = { ...toast, type };
    }

    setToasts((prevToasts) => {
      const updatedToasts = [newToast, ...prevToasts];
      return updatedToasts;
    });

    setTimeout(() => {
      removeToast(newToast.id);
    }, 3000);
  };

  const removeToast = (id: number) => {
    setToasts((prevToasts) => {
      const updatedToasts = prevToasts.filter((toast) => toast.id !== id);
      return updatedToasts;
    });
  };

  return (
    <ToastContext.Provider value={{ toasts, addToast, removeToast }}>
      {children}
    </ToastContext.Provider>
  );
};

export const useToast = (): ToastContextType => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error("useToast err");
  }
  return context;
};
